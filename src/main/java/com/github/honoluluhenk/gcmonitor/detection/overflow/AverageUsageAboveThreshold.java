package com.github.honoluluhenk.gcmonitor.detection.overflow;

import java.util.List;

import com.github.honoluluhenk.gcmonitor.memory.Memory;
import com.github.honoluluhenk.gcmonitor.timeddata.TimeSeries;
import com.github.honoluluhenk.gcmonitor.timeddata.TimedData;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static java.lang.String.format;

/**
 * <p>
 * <strong>Detection principle:</strong>
 * </p>
 * <ul>
 * <li>if the average memory pool usage (usage / max)</li>
 * <li>over {@code numMeasures} collections</li>
 * <li>is &gt;= {@code alarmThresholdPct}</li>
 * </ul>
 * <p>
 * then the detection counts as {@link Overflow} with {@link Status#OVERFLOW}.
 * </p>
 * <p>
 * The detector ignores measurements where there is no max present (i.e.: no maximum memory pool size defined).<br>
 * Note from the author: I could not setup an Oracle/OpenJDK JVM to reproduce this szenario.
 * </p>
 */
public class AverageUsageAboveThreshold implements OverflowDetector {
	private final long numMeasures;
	private final double alarmThresholdPct;

	/**
	 * @param numMeasures calculate the average memory usage averaged over this amount of measures.
	 * Overflow warnings are only given if enough readings have been receievd.
	 * @param alarmThresholdPct If the average memory usage is &gt;= {@code alarmThresholdPct} (e.g.: &gt;= 90.0d),
	 * an overflow is detected.
	 */
	public AverageUsageAboveThreshold(long numMeasures, double alarmThresholdPct) {
		if (numMeasures <= 0) {
			throw new IllegalArgumentException("numMeasures must be > 0 but was: " + numMeasures);
		}
		this.numMeasures = numMeasures;

		if (alarmThresholdPct <= 0.0d || alarmThresholdPct > 100.0d) {
			throw new IllegalArgumentException(
					format("alarmThresholdPct must be 0.0 < x <= 100.0 but was: %s", alarmThresholdPct));
		}
		this.alarmThresholdPct = alarmThresholdPct;
	}

	@Override
	public Overflow detect(TimeSeries<Memory> timeSeries) {
		List<TimedData<Memory>> timedData = timeSeries.getTimedData();

		int measuresCount = timedData.size();
		if (measuresCount < this.numMeasures) {
			return Overflow.ok(
					format("Not enough measures, have: %d but need at least %d", measuresCount, this.numMeasures));
		}

		@SuppressFBWarnings("OI_OPTIONAL_ISSUES_USES_IMMEDIATE_EXECUTION")
		double usagePct = timedData.stream()
				.mapToDouble(data -> new MemoryUsageCalculator(data.getData()).calculatePct()
						.orElse(0.0d))
				.average()
				.orElse(0.0d);

		boolean ok = usagePct < alarmThresholdPct;
		if (ok) {
			return Overflow.ok(
					format("usagePct < alarmThresholdPct: %s < %s, have %s measures",
							usagePct, alarmThresholdPct, measuresCount));
		}

		return Overflow.overflow(format("Usage: %.5f%% >= %.5f%%", usagePct,
				alarmThresholdPct));
	}

	@Override
	public void validate(int expectedReadings) {
		if (expectedReadings < numMeasures) {
			throw new IllegalArgumentException(format(
					"can never enough data because expectedReadings < numMeasures: %s < %s",
					expectedReadings, numMeasures));
		}
	}

	public long getNumMeasures() {
		return numMeasures;
	}

	public double getAlarmThresholdPct() {
		return alarmThresholdPct;
	}
}
