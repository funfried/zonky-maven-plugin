package de.funfried.maven.plugin.zonky.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

/**
 *
 * @author fbahle
 */
public class ZonkyUtil {
	public static EmbeddedPostgres start(int port, String workingDirectory, String dataDirectory) throws IOException {
		return EmbeddedPostgres.builder().setCleanDataDirectory(false).setOverrideWorkingDirectory(new File(workingDirectory)).setDataDirectory(dataDirectory).setPort(port).start();
	}

	public static void stop(EmbeddedPostgres embeddedPostgres) {
		if (embeddedPostgres != null) {
			IOUtils.closeQuietly(embeddedPostgres);
		}
	}
}
