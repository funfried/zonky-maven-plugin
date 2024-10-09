package de.funfried.maven.plugin.zonky;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import de.funfried.maven.plugin.zonky.utils.MavenProjectUtil;
import de.funfried.maven.plugin.zonky.utils.ZonkyUtil;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

/**
 * Goal which stops the embedded postgres database.
 */
@Mojo(name = "stop", defaultPhase = LifecyclePhase.PRE_CLEAN, threadSafe = false)
public class StopEmbeddedPostgresMojo extends AbstractMojo {
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
	 * If {@code true}, only one database instance is used for all multimodule projects, otherwise all projects get their own instance.
	 */
	@Parameter(defaultValue = "true", property = "singleInstance")
	private boolean singleInstance;

	/**
	 * Stops the embedded postgres database.
	 *
	 * @throws MojoExecutionException if an error occurs
	 */
	@Override
	public void execute() throws MojoExecutionException {
		EmbeddedPostgres pg = MavenProjectUtil.getProjectProperty(project, singleInstance ? reactorProjects : null, MavenProjectUtil.PROP_DB_INSTANCE);
		if (pg != null) {
			String workDir = MavenProjectUtil.getProjectProperty(project, singleInstance ? reactorProjects : null, MavenProjectUtil.PROP_WORK_DIRECTORY);
			String dataDir = MavenProjectUtil.getProjectProperty(project, singleInstance ? reactorProjects : null, MavenProjectUtil.PROP_DATA_DIRECTORY);

			File workDirFile = new File(workDir);
			File dataDirFile = new File(dataDir);

			try {
				ZonkyUtil.stop(pg, workDirFile, dataDirFile);

				stopped();
			} catch (IOException | InterruptedException | TimeoutException ex) {
				getLog().error("Failed to stop database", ex);
			}
		}
	}

	private void stopped() {
		getLog().info("Stopped embedded postgres database at port " + MavenProjectUtil.getProjectProperty(project, singleInstance ? reactorProjects : null, MavenProjectUtil.PROP_PORT) + " (JDBC URL: "
				+ MavenProjectUtil.getProjectProperty(project, singleInstance ? reactorProjects : null, MavenProjectUtil.PROP_JDBC_URL) + ")");

		MavenProjectUtil.removeAllProjectProperties(project, singleInstance ? reactorProjects : null);
	}
}
