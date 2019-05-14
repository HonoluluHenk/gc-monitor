package com.github.honoluluhenk.gcmonitor.detection.overflow;

import java.util.List;
import java.util.Optional;

import com.github.honoluluhenk.gcmonitor.memory.Memory;
import com.github.honoluluhenk.gcmonitor.timeddata.TimeSeries;
import com.github.honoluluhenk.gcmonitor.timeddata.TimedData;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

/**
 * If all of the last {@code numMeasures} {@link Memory} readings are above the {@code thresholdPct},
 * an {@link Overflow} with status {@link Status#OVERFLOW} is detected.
 */
public class UsageAboveThreshold implements OverflowDetector {
	private final int numMeasures;
	private final double thresholdPct;

	/**
	 * Convenience: calls {@link #UsageAboveThreshold(int, double)} with numMeasures = 1.
	 */
	public UsageAboveThreshold(double thresholdPct) {
		this(1, thresholdPct);
	}

	/**
	 * @param numMeasures determines the amount of readings that are taken into account.
	 * @param thresholdPct sets the alarm threshold in percent (e.g.: 90.0d means 90 percent).
	 */
	@SuppressFBWarnings(value = "OPM_OVERLY_PERMISSIVE_METHOD", justification = "this is a library project")
	public UsageAboveThreshold(int numMeasures, double thresholdPct) {
		if (numMeasures <= 0) {
			throw new IllegalArgumentException("numMeasures must be > 0 but was: " + numMeasures);
		}
		this.numMeasures = numMeasures;

		if (thresholdPct <= 0.0 || thresholdPct > 100.0) {
			throw new IllegalArgumentException("thresholdPct must be 0 < x <= 100 but was: " + thresholdPct);
		}
		this.thresholdPct = thresholdPct;
	}

	@Override
	public Overflow detect(TimeSeries<Memory> timeSeries) {
		List<TimedData<Memory>> timedData = timeSeries.getTimedData();
		int readingCount = timedData.size();
		if (readingCount < numMeasures) {
			return Overflow.ok(format("not enough readings, have: %d but need: %d", readingCount, numMeasures));
		}

		List<Double> relevantReadings = timeSeries.stream()
				.limit(numMeasures)
				.map(entry -> new MemoryUsageCalculator(entry.getData()).calculatePct())
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(toList());

		long thresholdExceeded = relevantReadings.stream()
				.filter(usage -> usage >= thresholdPct)
				.count();

		boolean overflow = thresholdExceeded >= numMeasures;

		return overflow
				? Overflow.overflow(format("The last %d readings were above threshold of %s: %s",
				numMeasures, thresholdPct, relevantReadings))
				: Overflow.ok(format("have %d readings, only %d of %d exceede threshold of %s%%: %s",
				readingCount, thresholdExceeded, numMeasures, thresholdPct, relevantReadings));
	}

	@Override
	public void validate(int expectedReadings) {
		if (expectedReadings < numMeasures) {
			throw new IllegalArgumentException(format(
					"cannot get enough data because expectedReadings < numMeasures: %s < %s",
					expectedReadings, numMeasures));
		}
	}

	public int getNumMeasures() {
		return numMeasures;
	}

	public double getThresholdPct() {
		return thresholdPct;
	}
}
