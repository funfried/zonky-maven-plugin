/*
 * Copyright (c) 2022 Airtango.
 * All rights reserved.
 */

package de.funfried.maven.plugin.zonky.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 *
 * @author fbahle
 */
public class ProcessUtil {
	private static final Pattern PROCESS_ID_PATTERN = Pattern.compile("([0-9]{1,5}$)");

	public static final long DEFAULT_PROCESS_TIMEOUT = 5000L;

	public static Set<Integer> getProcessIdsByPort(int port) throws IOException, TimeoutException, InterruptedException {
		return ProcessUtil.getProcessIdsByPort(port, DEFAULT_PROCESS_TIMEOUT);
	}

	public static Set<Integer> getProcessIdsByPort(int port, long timeout) throws IOException, TimeoutException, InterruptedException {
		Process p = startProcessOnPortProcess(port);

		boolean finished = p.waitFor(timeout, TimeUnit.MILLISECONDS);
		if (!finished) {
			throw new TimeoutException("Fetching process ID(s) for port " + port + " ran into timeout");
		} else {
			int result = p.exitValue();
			if (result != 0) {
				throw new IOException("Unexpected return code: " + result);
			}
		}

		List<String> outputs;
		try (InputStream is = p.getInputStream()) {
			outputs = IOUtils.readLines(is, StandardCharsets.UTF_8);
		}

		Set<Integer> processIdsOnPort = new HashSet<>();

		for (String output : outputs) {
			Matcher matcher = PROCESS_ID_PATTERN.matcher(output);

			while (matcher.find()) {
				Integer pid;

				try {
					pid = Integer.valueOf(matcher.group(1));
				} catch (NumberFormatException ex) {
					pid = null;
				}

				if (pid != null) {
					processIdsOnPort.add(pid);
				}
			}
		}

		return processIdsOnPort;
	}

	public static void killProcessById(int processId) throws IOException, TimeoutException, InterruptedException {
		killProcessById(processId, DEFAULT_PROCESS_TIMEOUT);
	}

	public static void killProcessById(int processId, long timeout) throws IOException, TimeoutException, InterruptedException {
		Process p = startKillProcess(processId);

		boolean finished = p.waitFor(timeout, TimeUnit.MILLISECONDS);
		if (finished) {
			int result = p.exitValue();
			if (result != 0) {
				throw new IOException("Unexpected return code: " + result);
			}
		}

		throw new TimeoutException("Killinng process ID " + processId + " ran into timeout");
	}

	public static void killProcessByPort(int port) throws IOException, TimeoutException, InterruptedException {
		killProcessByPort(port, DEFAULT_PROCESS_TIMEOUT);
	}

	public static void killProcessByPort(int port, long timeout) throws IOException, TimeoutException, InterruptedException {
		Set<Integer> processIds = getProcessIdsByPort(port, timeout);
		for (Integer pid : processIds) {
			killProcessById(pid, timeout);
		}
	}

	private static Process startKillProcess(int processId) throws IOException {
		ProcessBuilder pb;

		if (SystemUtils.IS_OS_WINDOWS) {
			pb = new ProcessBuilder("taskkill", "/F", "/PID " + Integer.toString(processId));
		} else {
			pb = new ProcessBuilder("kill", "-kill", Integer.toString(processId));
		}

		return pb.start();
	}

	private static Process startProcessOnPortProcess(int port) throws IOException {
		if (SystemUtils.IS_OS_WINDOWS) {
			ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", "netstat -ano | findstr :" + port);
			return pb.start();
		}

		ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", "lsof -n -i :" + port + " | grep LISTEN | awk '{print  $2}'");
		return pb.start();
	}
}
