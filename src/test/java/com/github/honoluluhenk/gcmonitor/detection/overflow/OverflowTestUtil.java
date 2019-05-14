package com.github.honoluluhenk.gcmonitor.detection.overflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OverflowTestUtil {

	public static void assertOverflow(Overflow o, Status status, String reason) {
		assertAll(
				() -> assertThat(o.getStatus()).describedAs("detect").isEqualTo(status),
				() -> assertThat(o.getReason()).describedAs("reason").isEqualTo(reason)
		);
	}
}
