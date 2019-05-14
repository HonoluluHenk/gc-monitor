package com.github.honoluluhenk.gcmonitor.detection.overflow;

import org.junit.jupiter.api.Test;

import static com.github.honoluluhenk.gcmonitor.detection.overflow.OverflowTestUtil.assertOverflow;
import static org.assertj.core.api.Assertions.assertThat;

public class OverflowTest {

	@Test
	void ok() {
		Overflow o = Overflow.ok("no reason");

		assertOverflow(o, Status.OK, "no reason");
	}

	@Test
	void fail() {
		Overflow o = Overflow.overflow("a reason");

		assertOverflow(o, Status.OVERFLOW, "a reason");
	}

	@Test
	void toString_text() {
		assertThat(Overflow.overflow("a reason").toString())
				.isEqualTo("Overflow{status=OVERFLOW, reason='a reason'}");
	}

}