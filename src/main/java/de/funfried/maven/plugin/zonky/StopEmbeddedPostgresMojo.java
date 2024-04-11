package de.funfried.maven.plugin.zonky;

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
	 * Stops the embedded postgres database.
	 *
	 * @throws MojoExecutionException if an error occurs
	 */
	@Override
	public void execute() throws MojoExecutionException {
		Object obj = project.getProperties().get("zonky");
		if (obj != null && obj instanceof EmbeddedPostgres) {
			EmbeddedPostgres pg = (EmbeddedPostgres) obj;

			ZonkyUtil.stop(pg);
		}
	}
}
