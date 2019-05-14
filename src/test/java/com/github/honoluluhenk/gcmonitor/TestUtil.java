package com.github.honoluluhenk.gcmonitor;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class TestUtil {

	private TestUtil() {
		// utility class
	}

	public static ZonedDateTime mkTime(int day) {
		return ZonedDateTime
				.of(2018, 1, day, 0, 0, 0, 0, ZoneId.systemDefault());
	}
}
