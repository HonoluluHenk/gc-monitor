package com.github.honoluluhenk.gcmonitor.expiry;

import java.time.ZonedDateTime;
import java.util.OptionalInt;

import com.github.honoluluhenk.gcmonitor.timeddata.TimedData;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpiryTest {

	private static final TimedData<String> IGNORED_TIMED_DATA
			= new TimedData<>(ZonedDateTime.now(), "Hello");
	private static final Params<String> IGNORED = new Params<>(IGNORED_TIMED_DATA, emptyList());

	private static final Expiry<String> TRUE = (params) -> true;
	private static final Expiry<String> FALSE = (params) -> false;

	@Test
	void and_true_true() {
		assertTrue(TRUE.and(TRUE).isExpired(IGNORED));
	}

	@Test
	void and_true_false() {
		assertFalse(TRUE.and(FALSE).isExpired(IGNORED));
	}

	@Test
	void and_false_true() {
		assertFalse(FALSE.and(TRUE).isExpired(IGNORED));
	}

	@Test
	void and_false_false() {
		assertFalse(FALSE.and(FALSE).isExpired(IGNORED));
	}

	@Test
	void or_true_true() {
		assertTrue(TRUE.or(TRUE).isExpired(IGNORED));
	}

	@Test
	void or_true_false() {
		assertTrue(TRUE.or(FALSE).isExpired(IGNORED));
	}

	@Test
	void or_false_true() {
		assertTrue(FALSE.or(TRUE).isExpired(IGNORED));
	}

	@Test
	void or_false_false() {
		assertFalse(FALSE.or(FALSE).isExpired(IGNORED));
	}

	@Test
	void negate_true() {
		assertFalse(TRUE.negate().isExpired(IGNORED));
	}

	@Test
	void negate_false() {
		assertTrue(FALSE.negate().isExpired(IGNORED));
	}

	@Test
	void expectedReadings() {
		assertThat(TRUE.getExpectedReadings()).isEqualTo(OptionalInt.empty());
	}
}