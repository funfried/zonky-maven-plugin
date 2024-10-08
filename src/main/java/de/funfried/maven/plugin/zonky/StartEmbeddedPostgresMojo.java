package de.funfried.maven.plugin.zonky;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import javax.sql.DataSource;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import de.funfried.maven.plugin.zonky.utils.AlreadyStartedPolicy;
import de.funfried.maven.plugin.zonky.utils.MavenProjectUtil;
import de.funfried.maven.plugin.zonky.utils.ZonkyUtil;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

/**
 * Goal which starts an embedded postgres database.
 */
@Mojo(name = "start", defaultPhase = LifecyclePhase.INITIALIZE, requiresProject = true, threadSafe = true)
public class StartEmbeddedPostgresMojo extends AbstractMojo {
	/**
	 * The port on which the database will be accessible. A value less than or equal to 0 means auto detect a free port. The port is available through the property ${zonky.port}.
	 */
	@Parameter(defaultValue = "0", property = "port")
	private int port;

	/**
	 * If {@code true}, a create database statement with the given database name will be executed on startup.
	 */
	@Parameter(defaultValue = "true", property = "createDatabase")
	private boolean createDatabase;

	/**
	 * The database name to write your data. Should not be postgres!
	 */
	@Parameter(defaultValue = "data", property = "databaseName")
	private String databaseName;

	/**
	 * Define what should be done when the database is already started and the start goal is called again. Choose between:
	 * <ul>
	 * <li>fail (lets the build fail)</li>
	 * <li>reinit (drops the database and if "createDatabase" is true recreates the database again)</li>
	 * <li>restart (stops the database and starts it again)</li>
	 * <li>ignore (just keeps the current database and does not start a new one)</li>
	 * </ul>
	 */
	@Parameter(defaultValue = "reinit", property = "onAlreadyStarted")
	private AlreadyStartedPolicy onAlreadyStarted;

	/**
	 * The working directory for the embedded database.
	 */
	@Parameter(defaultValue = "${project.build.directory}/embedded-postgres/work", property = "workingDirectory")
	private String workingDirectory;

	/**
	 * The data directory for the embedded database.
	 */
	@Parameter(defaultValue = "${project.build.directory}/embedded-postgres/data", property = "dataDirectory")
	private String dataDirectory;

	/**
	 * The maven project.
	 */
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	/**
	 * Contains the full list of projects in the reactor.
	 */
	@Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
	private List<MavenProject> reactorProjects;

	/**
	 * Starts the embedded postgres database.
	 *
	 * @throws MojoExecutionException if an error occurs
	 */
	@Override
	public void execute() throws MojoExecutionException {
		EmbeddedPostgres pg = MavenProjectUtil.getProjectProperty(project, reactorProjects, MavenProjectUtil.PROP_DB_INSTANCE);
		if (pg != null) {
			if (AlreadyStartedPolicy.fail.equals(onAlreadyStarted)) {
				throw new MojoExecutionException("Embedded database already started.");
			}

			if (AlreadyStartedPolicy.reinit.equals(onAlreadyStarted)) {
				DataSource dataSource = pg.getDatabase("postgres", "postgres");
				try (Connection connection = dataSource.getConnection()) {
					try (Statement stmt = connection.createStatement()) {
						stmt.execute("DROP DATABASE \"" + databaseName + "\";");

						if (createDatabase) {
							stmt.execute("CREATE DATABASE \"" + databaseName + "\";");
						}
					}
				} catch (SQLException ex) {
					throw new MojoExecutionException("Failed to reset embedded database", ex);
				}

				String workDir = MavenProjectUtil.getProjectProperty(project, reactorProjects, MavenProjectUtil.PROP_WORK_DIRECTORY);
				String dataDir = MavenProjectUtil.getProjectProperty(project, reactorProjects, MavenProjectUtil.PROP_DATA_DIRECTORY);

				File workDirFile = new File(workDir);
				File dataDirFile = new File(dataDir);

				started(pg, workDirFile, dataDirFile);

				getLog().info("Embedded postgres database reinitialized");
			} else if (AlreadyStartedPolicy.restart.equals(onAlreadyStarted)) {
				try {
					String workDir = MavenProjectUtil.getProjectProperty(project, reactorProjects, MavenProjectUtil.PROP_WORK_DIRECTORY);
					String dataDir = MavenProjectUtil.getProjectProperty(project, reactorProjects, MavenProjectUtil.PROP_DATA_DIRECTORY);

					File workDirFile = new File(workDir);
					File dataDirFile = new File(dataDir);

					ZonkyUtil.stop(pg, workDirFile, dataDirFile);

					start(port, workDirFile, dataDirFile);
				} catch (IOException | InterruptedException | TimeoutException ex) {
					getLog().error("Failed to stop database", ex);
				}
			}
		} else {
			String subDir = UUID.randomUUID().toString();

			start(port, new File(workingDirectory, subDir), new File(dataDirectory, subDir));
		}
	}

	private EmbeddedPostgres start(int port, File workingDirectory, File dataDirectory) throws MojoExecutionException {
		EmbeddedPostgres pg;

		try {
			pg = ZonkyUtil.start(port, workingDirectory, dataDirectory);

			if (createDatabase) {
				DataSource dataSource = pg.getDatabase("postgres", "postgres");
				try (Connection connection = dataSource.getConnection()) {
					try (Statement stmt = connection.createStatement()) {
						stmt.execute("DROP DATABASE IF EXISTS \"" + databaseName + "\";");
						stmt.execute("CREATE DATABASE \"" + databaseName + "\";");
					}
				} catch (SQLException ex) {
					throw new MojoExecutionException("Failed to create embedded database '" + databaseName + "'", ex);
				}
			}

			started(pg, workingDirectory, dataDirectory);
		} catch (IOException ex) {
			throw new MojoExecutionException("Failed to start embedded database", ex);
		}

		return pg;
	}

	private void started(EmbeddedPostgres pg, File workingDirectory, File dataDirectory) {
		int pgPort = pg.getPort();
		String jdbcUrl = pg.getJdbcUrl("postgres", databaseName);

		getLog().info("Started embedded postgres database at port " + pgPort + " (JDBC URL: " + jdbcUrl + ")");

		Map<String, Object> properties = new HashMap<>();
		properties.put(MavenProjectUtil.PROP_HOST, "localhost");
		properties.put(MavenProjectUtil.PROP_PORT, pgPort);
		properties.put(MavenProjectUtil.PROP_DATABASE, databaseName);
		properties.put(MavenProjectUtil.PROP_USERNAME, "postgres");
		properties.put(MavenProjectUtil.PROP_PASSWORD, "postgres");
		properties.put(MavenProjectUtil.PROP_JDBC_URL, jdbcUrl);
		properties.put(MavenProjectUtil.PROP_WORK_DIRECTORY, workingDirectory.getAbsolutePath());
		properties.put(MavenProjectUtil.PROP_DATA_DIRECTORY, dataDirectory.getAbsolutePath());
		properties.put(MavenProjectUtil.PROP_DB_INSTANCE, pg);

		MavenProjectUtil.putProjectProperty(project, reactorProjects, properties);
	}
}
