package de.funfried.maven.plugin.zonky.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

/**
 * Zonky utility class.
 *
 * @author fbahle
 */
public class MavenProjectUtil {
	public static final String PROP_HOST = "zonky.host";

	public static final String PROP_PORT = "zonky.port";

	public static final String PROP_DATABASE = "zonky.database";

	public static final String PROP_USERNAME = "zonky.username";

	public static final String PROP_PASSWORD = "zonky.password";

	public static final String PROP_JDBC_URL = "zonky.jdbcUrl";

	public static final String PROP_WORK_DIRECTORY = "zonky.work.directory";

	public static final String PROP_DATA_DIRECTORY = "zonky.data.directory";

	public static final String PROP_DB_INSTANCE = "zonky";

	public static final Set<String> PROPS_ALL = Set.of(PROP_HOST, PROP_PORT, PROP_DATABASE, PROP_USERNAME, PROP_PASSWORD, PROP_JDBC_URL, PROP_WORK_DIRECTORY, PROP_DATA_DIRECTORY, PROP_DB_INSTANCE);

	private MavenProjectUtil() {
	}

	public static <E extends Object> E getProjectProperty(MavenProject project, List<MavenProject> reactorProjects, String key) {
		E value = getProjectProperty(project, key);
		if (value == null) {
			for (MavenProject p : reactorProjects) {
				E v = getProjectProperty(p, key);
				if (v != null) {
					value = v;

					break;
				}
			}
		}

		return value;
	}

	private static <E extends Object> E getProjectProperty(MavenProject project, String key) {
		return (E) project.getProperties().get(key);
	}

	public static void putProjectProperty(MavenProject project, List<MavenProject> reactorProjects, Map<String, Object> properties) {
		for (Map.Entry<String, Object> item : properties.entrySet()) {
			project.getProperties().put(item.getKey(), item.getValue());
		}

		for (MavenProject p : reactorProjects) {
			for (Map.Entry<String, Object> item : properties.entrySet()) {
				p.getProperties().put(item.getKey(), item.getValue());
			}
		}
	}

	public static void removeProjectProperty(MavenProject project, List<MavenProject> reactorProjects, String propertyKey) {
		project.getProperties().remove(propertyKey);

		for (MavenProject p : reactorProjects) {
			p.getProperties().remove(propertyKey);
		}
	}

	public static void removeProjectProperties(MavenProject project, List<MavenProject> reactorProjects, Set<String> propertyKeys) {
		for (String key : propertyKeys) {
			project.getProperties().remove(key);
		}

		for (MavenProject p : reactorProjects) {
			for (String key : propertyKeys) {
				p.getProperties().remove(key);
			}
		}
	}

	public static void removeAllProjectProperties(MavenProject project, List<MavenProject> reactorProjects) {
		removeProjectProperties(project, reactorProjects, PROPS_ALL);
	}
}
