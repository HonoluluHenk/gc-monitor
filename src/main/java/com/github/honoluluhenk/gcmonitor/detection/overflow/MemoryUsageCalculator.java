package com.github.honoluluhenk.gcmonitor.detection.overflow;

import java.io.Serializable;
import java.util.Optional;

import com.github.honoluluhenk.gcmonitor.memory.Memory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static java.util.Objects.requireNonNull;

public class MemoryUsageCalculator implements Serializable {
	private static final long serialVersionUID = 7462279127482755064L;

	private final Memory memory;

	public MemoryUsageCalculator(Memory memory) {
		this.memory = requireNonNull(memory);
	}

	public Optional<Double> calculatePct() {
		Optional<Double> usagePct = calculateRate()
				.map(d -> d * 100.0d);

		return usagePct;
	}

	@SuppressFBWarnings(value = "OPM_OVERLY_PERMISSIVE_METHOD", justification = "this is a library project")
	public Optional<Double> calculateRate() {
		Optional<Double> usagePct = memory.getMax()
				.map(max -> memory.getUsed() / (double) max);

		return usagePct;
	}

}
