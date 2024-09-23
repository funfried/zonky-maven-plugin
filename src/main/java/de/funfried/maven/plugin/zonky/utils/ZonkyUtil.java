package de.funfried.maven.plugin.zonky.utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

/**
 * Zonky utility class.
 *
 * @author fbahle
 */
public class ZonkyUtil {
	public static EmbeddedPostgres start(int port, File workingDirectory, File dataDirectory) throws IOException {
		return EmbeddedPostgres.builder().setCleanDataDirectory(false).setOverrideWorkingDirectory(workingDirectory).setDataDirectory(dataDirectory).setPort(port).start();
	}

	public static void stop(EmbeddedPostgres embeddedPostgres, File workingDirectory, File dataDirectory) throws IOException, InterruptedException, TimeoutException {
		if (embeddedPostgres != null) {
			int port = embeddedPostgres.getPort();

			IOUtils.closeQuietly(embeddedPostgres);

			ProcessUtil.killProcessByPort(port);

			FileUtils.deleteQuietly(workingDirectory);
			FileUtils.deleteQuietly(dataDirectory);
		}
	}
}
