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
@Mojo(name = "stop", defaultPhase = LifecyclePhase.NONE)
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
	 * Stops the embedded postgres database.
	 *
	 * @throws MojoExecutionException if an error occurs
	 */
	@Override
	public void execute() throws MojoExecutionException {
		EmbeddedPostgres pg = MavenProjectUtil.getProjectProperty(project, reactorProjects, MavenProjectUtil.PROP_DB_INSTANCE);
		if (pg != null) {
			String workDir = MavenProjectUtil.getProjectProperty(project, reactorProjects, MavenProjectUtil.PROP_WORK_DIRECTORY);
			String dataDir = MavenProjectUtil.getProjectProperty(project, reactorProjects, MavenProjectUtil.PROP_DATA_DIRECTORY);

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
		getLog().info("Stopped embedded postgres database at port " + MavenProjectUtil.getProjectProperty(project, reactorProjects, MavenProjectUtil.PROP_PORT) + " (JDBC URL: "
				+ MavenProjectUtil.getProjectProperty(project, reactorProjects, MavenProjectUtil.PROP_JDBC_URL) + ")");

		MavenProjectUtil.removeAllProjectProperties(project, reactorProjects);
	}
}
