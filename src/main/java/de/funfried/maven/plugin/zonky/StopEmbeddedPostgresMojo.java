package de.funfried.maven.plugin.zonky;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

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
		Object obj = project.getProperties().get("zonky");
		if (obj != null && obj instanceof EmbeddedPostgres) {
			EmbeddedPostgres pg = (EmbeddedPostgres) obj;

			try {
				ZonkyUtil.stop(pg);

				stopped();
			} catch (IOException | InterruptedException | TimeoutException ex) {
				getLog().error("Failed to stop database", ex);
			}
		}
	}

	private void stopped() {
		getLog().info("Stopped embedded postgres database at port " + project.getProperties().get("zonky.port") + " (JDBC URL: " + project.getProperties().get("zonky.jdbcUrl") + ")");

		project.getProperties().remove("zonky.host");
		project.getProperties().remove("zonky.port");
		project.getProperties().remove("zonky.database");
		project.getProperties().remove("zonky.username");
		project.getProperties().remove("zonky.password");
		project.getProperties().remove("zonky.jdbcUrl");
		project.getProperties().remove("zonky");

		for (MavenProject p : reactorProjects) {
			p.getProperties().remove("zonky.host");
			p.getProperties().remove("zonky.port");
			p.getProperties().remove("zonky.database");
			p.getProperties().remove("zonky.username");
			p.getProperties().remove("zonky.password");
			p.getProperties().remove("zonky.jdbcUrl");
			p.getProperties().remove("zonky");
		}
	}
}
