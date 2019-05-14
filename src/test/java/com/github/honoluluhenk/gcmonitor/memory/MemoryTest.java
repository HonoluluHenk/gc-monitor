package com.github.honoluluhenk.gcmonitor.memory;

import java.lang.management.MemoryUsage;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings({ "OptionalGetWithoutIsPresent", "ResultOfObjectAllocationIgnored" })
class MemoryTest {

	private static final long IGNORE = 123;

	@Test
	public void init_MemoryUsage_change_detection() {
		// just in case the implementation of MemoryUsage changes

		assertAll(
				() -> assertThat(new MemoryUsage(1, IGNORE, IGNORE, IGNORE).getInit()).isEqualTo(1),
				() -> assertThat(new MemoryUsage(0, IGNORE, IGNORE, IGNORE).getInit()).isEqualTo(0),
				() -> assertThat(new MemoryUsage(-1, IGNORE, IGNORE, IGNORE).getInit()).isEqualTo(-1),
				() -> assertThatThrownBy(() -> new MemoryUsage(-2, IGNORE, IGNORE, IGNORE))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("init parameter = -2 is negative but not -1.")
		);
	}

	@Test
	public void init_behaviour() {
		assertAll(
				() -> assertThat(new Memory(1, 0, 0, 0).getInit().get())
						.isEqualTo(1),
				() -> assertThat(new Memory(0, 0, 0, 0).getInit().get())
						.isEqualTo(0),
				() -> assertThat(new Memory(-1, 0, 0, 0).getInit().isPresent())
						.isFalse()
		);
	}

	@Test
	public void used_change_detection() {

		assertAll(
				() -> assertThat(new Memory(IGNORE, 1, IGNORE, IGNORE).getUsed()).isEqualTo(1),
				() -> assertThat(new Memory(IGNORE, 0, IGNORE, IGNORE).getUsed()).isEqualTo(0),
				() -> assertThatThrownBy(() -> new Memory(IGNORE, -1, IGNORE, IGNORE))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("used parameter = -1 is negative."),
				() -> assertThatThrownBy(() -> new Memory(IGNORE, -2, IGNORE, IGNORE))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("used parameter = -2 is negative.")
		);
	}

	@Test
	public void used_behaviour() {
		assertAll(
				() -> assertThat(new Memory(IGNORE, 1, IGNORE, IGNORE).getUsed())
						.isEqualTo(1),
				() -> assertThat(new Memory(IGNORE, 0, IGNORE, IGNORE).getUsed())
						.isEqualTo(0)
		);
	}

	@Test
	public void committed_change_detection() {

		assertAll(
				() -> assertThat(new Memory(IGNORE, 0, 1, IGNORE).getCommitted()).isEqualTo(1),
				() -> assertThat(new Memory(IGNORE, 0, 0, IGNORE).getCommitted()).isEqualTo(0),
				() -> assertThatThrownBy(() -> new Memory(IGNORE, 0, -1, IGNORE))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("committed parameter = -1 is negative."),
				() -> assertThatThrownBy(() -> new Memory(IGNORE, 0, -2, IGNORE))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("committed parameter = -2 is negative.")
		);
	}

	@Test
	public void committed_behaviour() {
		assertAll(
				() -> assertThat(new Memory(IGNORE, 0, 1, IGNORE).getCommitted())
						.isEqualTo(1),
				() -> assertThat(new Memory(IGNORE, 0, 0, IGNORE).getCommitted())
						.isEqualTo(0)
		);
	}

	@Test
	public void max_change_detection() {

		assertAll(
				() -> assertThat(new Memory(IGNORE, 0, 0, 1).getMax()).contains(1L),
				() -> assertThat(new Memory(IGNORE, 0, 0, 0).getMax()).contains(0L),
				() -> assertThat(new Memory(IGNORE, 0, 0, -1).getMax()).isNotPresent(),
				() -> assertThatThrownBy(() -> new Memory(IGNORE, 0, 0, -2))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("max parameter = -2 is negative but not -1.")
		);
	}

	@Test
	public void max_behaviour() {
		assertAll(
				() -> assertThat(new Memory(IGNORE, 0, 0, 1).getMax().get())
						.isEqualTo(1),
				() -> assertThat(new Memory(IGNORE, 0, 0, 0).getMax().get())
						.isEqualTo(0),
				() -> assertThat(new Memory(IGNORE, 0, 0, -1).getMax().isPresent())
						.isFalse()
		);
	}

	@Test
	public void toString_should_print_pretty() {
		assertThat(new Memory(123, 234, 345, 456).toString())
				.isEqualTo("Memory{init = 123(0K) used = 234(0K) committed = 345(0K) max = 456(0K)}");
	}
}