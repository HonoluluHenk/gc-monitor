package com.github.honoluluhenk.gcmonitor.detection.overflow;

import com.github.honoluluhenk.gcmonitor.memory.Memory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryUsageCalculatorTest {
	private static final int INIT = -1;
	private static final int COMMITTED = 200;

	@Test
	void calculateUsagePct_0pct() {
		MemoryUsageCalculator calc = new MemoryUsageCalculator(new Memory(INIT, 0, COMMITTED, 200));

		assertThat(calc.calculatePct())
				.isPresent()
				.contains(0.0d);
	}

	@Test
	void calculateUsagePct_100pct() {
		MemoryUsageCalculator calc = new MemoryUsageCalculator(new Memory(INIT, 200, COMMITTED, 200));

		assertThat(calc.calculatePct())
				.isPresent()
				.contains(100.0d);
	}

	@Test
	void calculateUsagePct_zero_used_empty() {
		MemoryUsageCalculator calc = new MemoryUsageCalculator(new Memory(INIT, 0, COMMITTED, -1));

		assertThat(calc.calculatePct())
				.isNotPresent();
	}

	@Test
	void calculateUsagePct_nonzero_used_empty() {
		MemoryUsageCalculator calc = new MemoryUsageCalculator( new Memory(INIT, 100, COMMITTED, -1));

		assertThat(calc.calculatePct())
				.isNotPresent();
	}
}
