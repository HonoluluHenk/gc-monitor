package com.github.honoluluhenk.gcmonitor.detection.overflow;

import java.time.ZonedDateTime;

import com.github.honoluluhenk.gcmonitor.memory.Memory;
import com.github.honoluluhenk.gcmonitor.timeddata.TimeSeries;
import com.github.honoluluhenk.gcmonitor.timeddata.TimedData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.honoluluhenk.gcmonitor.TestUtil.mkTime;
import static com.github.honoluluhenk.gcmonitor.detection.overflow.OverflowTestUtil.assertOverflow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AverageUsageAboveThresholdTest {
	public static final double THRESHOLD_PCT = 75.0d;

	public static final ZonedDateTime FIRST_TIME = mkTime(1);
	public static final ZonedDateTime SECOND_TIME = mkTime(2);
	public static final ZonedDateTime THIRD_TIME = mkTime(3);

	public static final Memory LOW_MEMORY = new Memory(10, 50, 100, 100);
	public static final Memory FULL_MEMORY = new Memory(10, 100, 100, 100);

	public static final TimedData<Memory> FIRST_HIGH = new TimedData<>(
			FIRST_TIME,
			new Memory(10, 90, 100, 100));
	public static final TimedData<Memory> SECOND_HIGH = new TimedData<>(
			SECOND_TIME,
			new Memory(10, 91, 100, 100));
	public static final TimedData<Memory> THIRD_HIGH = new TimedData<>(
			THIRD_TIME,
			new Memory(10, 96, 100, 100));

	@Nested
	class ConstructorValidationsTest {
		@ParameterizedTest
		@ValueSource(ints = { 1, 60, Integer.MAX_VALUE })
		void valid_minMeasures(int minMeasures) {
			long actual = new AverageUsageAboveThreshold(minMeasures, 75).getNumMeasures();

			assertThat(actual).isEqualTo(minMeasures);
		}

		@ParameterizedTest
		@ValueSource(ints = { Integer.MIN_VALUE, -1, 0 })
		void invalid_minMeasures(int minMeasures) {
			IllegalArgumentException iae = assertThrows(
					IllegalArgumentException.class,
					() -> new AverageUsageAboveThreshold(minMeasures, 75));

			assertThat(iae).hasMessage("numMeasures must be > 0 but was: " + minMeasures);
		}

		@ParameterizedTest
		@ValueSource(doubles = { 0.1, 50, 75, 99.9, 100.0 })
		void valid_alarmThresholdPct(double threshold) {
			double actual = new AverageUsageAboveThreshold(5, threshold).getAlarmThresholdPct();

			assertThat(actual).isEqualTo(threshold);
		}

		@ParameterizedTest
		@ValueSource(doubles = {
				Double.NEGATIVE_INFINITY,
				-0.01, 0.0, 100.01,
				Double.MAX_VALUE, Double.POSITIVE_INFINITY })
		void invalid_alarmThresholdPct(double thresholdPct) {
			IllegalArgumentException iae = assertThrows(
					IllegalArgumentException.class,
					() -> new AverageUsageAboveThreshold(1, thresholdPct));

			assertThat(iae).hasMessage("alarmThresholdPct must be 0.0 < x <= 100.0 but was: " + thresholdPct);
		}
	}

	@Nested
	class TooFewMeasuresTest {
		private final AverageUsageAboveThreshold detector = new AverageUsageAboveThreshold(3, THRESHOLD_PCT);
		private final TimeSeries<Memory> gcInfo = new TimeSeries<>();

		@Test
		public void no_measures() {
			Overflow o = detector.detect(gcInfo);

			assertOverflow(o, Status.OK, "Not enough measures, have: 0 but need at least 3");
		}

		@Test
		public void too_few_measures_1() {
			gcInfo.add(FIRST_HIGH);

			Overflow o = detector.detect(gcInfo);

			assertOverflow(o, Status.OK, "Not enough measures, have: 1 but need at least 3");
		}

		@Test
		public void too_few_measures_2() {
			gcInfo.add(FIRST_HIGH);
			gcInfo.add(SECOND_HIGH);

			Overflow o = detector.detect(gcInfo);

			assertOverflow(o, Status.OK, "Not enough measures, have: 2 but need at least 3");
		}

		@Test
		public void no_max() {
			gcInfo.add(new TimedData<>(
					FIRST_TIME,
					new Memory(10, 90, 100, -1)));
			gcInfo.add(new TimedData<>(
					SECOND_TIME,
					new Memory(10, 90, 100, -1)));
			gcInfo.add(new TimedData<>(
					THIRD_TIME,
					new Memory(10, 90, 100, -1)));
			Overflow o = detector.detect(gcInfo);

			assertOverflow(o, Status.OK, "usagePct < alarmThresholdPct: 0.0 < 75.0, have 3 measures");
		}
	}

	@Nested
	class ComputationsTest {
		private final AverageUsageAboveThreshold detector = new AverageUsageAboveThreshold(3, THRESHOLD_PCT);
		private final TimeSeries<Memory> gcInfo = new TimeSeries<>();

		@Nested
		class AllHighTest {

			@BeforeEach
			void beforeEach() {
				gcInfo.add(FIRST_HIGH);
				gcInfo.add(SECOND_HIGH);
				gcInfo.add(THIRD_HIGH);
			}

			@Test
			public void triggers() {
				Overflow o = detector.detect(gcInfo);

				assertOverflow(o, Status.OVERFLOW, "Usage: 92.33333% >= 75.00000%");
			}

		}

		@Nested
		class LotsOfLowTest {

			@BeforeEach
			void beforeEach() {
				for (int i = 0; i < 100; i++) {
					TimedData<Memory> foo = new TimedData<>(FIRST_TIME.plusNanos(i), LOW_MEMORY);
					gcInfo.add(foo);
				}
			}

			@Test
			public void does_not_trigger() {

				Overflow o = detector.detect(gcInfo);

				assertOverflow(o, Status.OK, "usagePct < alarmThresholdPct: 50.0 < 75.0, have 100 measures");
			}

			@Test
			public void and_not_enough_high_does_not_trigger() {
				for (int i = 0; i < 99; i++) {
					gcInfo.add(new TimedData<>(FIRST_TIME.plusNanos(5000 + i), FULL_MEMORY));
				}

				Overflow o = detector.detect(gcInfo);

				assertOverflow(o, Status.OK, "usagePct < alarmThresholdPct: 74.87437185929649 < 75.0, have 199 "
						+ "measures");

			}

			@Test
			public void and_then_some_high_triggers() {
				for (int i = 0; i < 100; i++) {
					gcInfo.add(new TimedData<>(FIRST_TIME.plusNanos(5000 + i), FULL_MEMORY));
				}

				Overflow o = detector.detect(gcInfo);

				assertOverflow(o, Status.OVERFLOW, "Usage: 75.00000% >= 75.00000%");

			}
		}

	}

	@Nested
	public class DetectorValidationTest {
		@ParameterizedTest
		@CsvSource({
				"1, 1",
				"1, 2",
				"1, 99",
		})
		void validates_ok(int detectorMeasures, int expiryMeasures) {
			AverageUsageAboveThreshold detector = new AverageUsageAboveThreshold(detectorMeasures, 50.0d);

			assertThatCode(() -> detector.validate(expiryMeasures))
					.doesNotThrowAnyException();
		}

		@ParameterizedTest
		@CsvSource({
				"1, 0",
				"2, 1",
				"2, 0",
				"99, 1",
				"99, 98",
		})
		void validate_throws_on_illegal_input(int detectorMeasures, int expiryMeasures) {
			AverageUsageAboveThreshold detector = new AverageUsageAboveThreshold(detectorMeasures, 50.0d);

			assertThrows(IllegalArgumentException.class,
					() -> detector.validate(expiryMeasures));
		}
	}
}

