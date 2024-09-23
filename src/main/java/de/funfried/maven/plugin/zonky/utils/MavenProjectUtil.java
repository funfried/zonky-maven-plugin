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
		for (String key : properties.keySet()) {
			project.getProperties().put(key, properties.get(key));
		}

		for (MavenProject p : reactorProjects) {
			for (String key : properties.keySet()) {
				p.getProperties().put(key, properties.get(key));
			}
		}
	}

	public static void removeProjectProperty(MavenProject project, List<MavenProject> reactorProjects, Set<String> propertyKeys) {
		for (String key : propertyKeys) {
			project.getProperties().remove(key);
		}

		for (MavenProject p : reactorProjects) {
			for (String key : propertyKeys) {
				p.getProperties().remove(key);
			}
		}
	}
}
