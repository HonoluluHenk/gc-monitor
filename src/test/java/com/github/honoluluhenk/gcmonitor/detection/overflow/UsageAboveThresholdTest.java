package com.github.honoluluhenk.gcmonitor.detection.overflow;

import java.time.ZonedDateTime;

import com.github.honoluluhenk.gcmonitor.memory.Memory;
import com.github.honoluluhenk.gcmonitor.timeddata.TimeSeries;
import com.github.honoluluhenk.gcmonitor.timeddata.TimedData;
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

public class UsageAboveThresholdTest {
	private static final double THRESHOLD_PCT = 90.0d;

	private static final ZonedDateTime FIRST_TIME = mkTime(1);
	private static final ZonedDateTime SECOND_TIME = mkTime(2);
	private static final ZonedDateTime THIRD_TIME = mkTime(3);

	private static final TimedData<Memory> FIRST_LOW = new TimedData<>(
			FIRST_TIME,
			new Memory(10, 50, 100, 100));
	private static final TimedData<Memory> SECOND_LOW = new TimedData<>(
			SECOND_TIME,
			new Memory(10, 51, 100, 100));
	private static final TimedData<Memory> THIRD_LOW = new TimedData<>(
			THIRD_TIME,
			new Memory(10, 52, 100, 100));

	private static final TimedData<Memory> FIRST_HIGH = new TimedData<>(
			FIRST_TIME,
			new Memory(10, 90, 100, 100));
	private static final TimedData<Memory> SECOND_HIGH = new TimedData<>(
			SECOND_TIME,
			new Memory(10, 91, 100, 100));
	private static final TimedData<Memory> THIRD_HIGH = new TimedData<>(
			THIRD_TIME,
			new Memory(10, 96, 100, 100));

	@Nested
	class ConstructorValidationsTest {
		@Test
		void convenienceConstructor() {
			assertThat(new UsageAboveThreshold(92).getNumMeasures())
					.isEqualTo(1);
		}

		@ParameterizedTest
		@ValueSource(ints = { 1, 2, 99, Integer.MAX_VALUE })
		void validNumReadings(int numReadings) {
			int actual = new UsageAboveThreshold(numReadings, 99.0).getNumMeasures();

			assertThat(actual).isEqualTo(numReadings);
		}

		@ParameterizedTest
		@ValueSource(ints = { Integer.MIN_VALUE, -1, 0 })
		void invalidNumReadings(int numMeasures) {
			IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
					() -> new UsageAboveThreshold(numMeasures, 99.0));

			assertThat(iae).hasMessage("numMeasures must be > 0 but was: " + numMeasures);
		}

		@ParameterizedTest
		@ValueSource(doubles = { 0.01, 50, 99.99, 100.0 })
		void validThresholdPct(double thresholdPct) {
			double actual = new UsageAboveThreshold(5, thresholdPct).getThresholdPct();

			assertThat(actual).isEqualTo(thresholdPct);
		}

		@ParameterizedTest
		@ValueSource(doubles = {
				Double.NEGATIVE_INFINITY,
				-0.01, 0.0, 100.00001,
				Double.MAX_VALUE, Double.POSITIVE_INFINITY })
		void invalidThresholdPct(double thresholdPct) {
			IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
					() -> new UsageAboveThreshold(5, thresholdPct));

			assertThat(iae).hasMessage("thresholdPct must be 0 < x <= 100 but was: " + thresholdPct);
		}
	}

	@Nested
	class DetectionTest {
		private final TimeSeries<Memory> ts = new TimeSeries<>();

		@Nested
		class NotEnoughReadingsIsOKTest {

			@Test
			void x_0_of_1() {
				Overflow o = new UsageAboveThreshold(1, THRESHOLD_PCT)
						.detect(ts);

				assertOverflow(o, Status.OK, "not enough readings, have: 0 but need: 1");
			}

			@Test
			void detect_OK_not_enough_readings_0_of_100() {
				Overflow o = new UsageAboveThreshold(100, THRESHOLD_PCT)
						.detect(ts);

				assertOverflow(o, Status.OK, "not enough readings, have: 0 but need: 100");
			}

			@Test
			void detect_OK_not_enough_readings_1_of_100() {
				ts.add(FIRST_HIGH);

				Overflow o = new UsageAboveThreshold(100, THRESHOLD_PCT)
						.detect(ts);

				assertOverflow(o, Status.OK, "not enough readings, have: 1 but need: 100");
			}
		}

		@Nested
		class OverflowOKTest {

			@Test
			void detect_OK_1_of_1_reading() {
				ts.add(FIRST_LOW);

				Overflow o = new UsageAboveThreshold(1, THRESHOLD_PCT)
						.detect(ts);

				assertOverflow(o, Status.OK, "have 1 readings, only 0 of 1 exceede threshold of 90.0%: [50.0]");
			}

			@Test
			void detect_OK_1_of_2_reading() {
				ts.add(FIRST_LOW);
				ts.add(SECOND_LOW);

				Overflow o = new UsageAboveThreshold(1, THRESHOLD_PCT)
						.detect(ts);

				assertOverflow(o, Status.OK, "have 2 readings, only 0 of 1 exceede threshold of 90.0%: [50.0]");
			}

			@Test
			void detect_OK_2_of_2_reading() {
				ts.add(FIRST_LOW);
				ts.add(SECOND_LOW);

				Overflow o = new UsageAboveThreshold(2, THRESHOLD_PCT)
						.detect(ts);

				assertOverflow(o, Status.OK, "have 2 readings, only 0 of 2 exceede threshold of 90.0%: [50.0, 51.0]");
			}

			@Test
			void detect_OK_2_of_3_reading() {
				ts.add(FIRST_LOW);
				ts.add(SECOND_LOW);
				ts.add(THIRD_LOW);

				Overflow o = new UsageAboveThreshold(2, THRESHOLD_PCT)
						.detect(ts);

				assertOverflow(o, Status.OK, "have 3 readings, only 0 of 2 exceede threshold of 90.0%: [50.0, 51.0]");
			}

			@Test
			void detect_OK_lohi_reading_with_one_relevant() {
				ts.add(FIRST_LOW);
				ts.add(SECOND_HIGH);

				Overflow o = new UsageAboveThreshold(1, THRESHOLD_PCT)
						.detect(ts);

				assertOverflow(o, Status.OK, "have 2 readings, only 0 of 1 exceede threshold of 90.0%: [50.0]");
			}

			@Test
			void detect_OK_lohi_reading_with_first_two_relevant() {
				ts.add(FIRST_LOW);
				ts.add(SECOND_HIGH);

				Overflow o = new UsageAboveThreshold(2, THRESHOLD_PCT)
						.detect(ts);

				assertOverflow(o, Status.OK, "have 2 readings, only 1 of 2 exceede threshold of 90.0%: [50.0, 91.0]");
			}

			@Test
			void detect_OK_lohi_reading_with_first_three_relevant() {
				ts.add(FIRST_LOW);
				ts.add(SECOND_LOW);
				ts.add(THIRD_HIGH);

				Overflow o = new UsageAboveThreshold(3, THRESHOLD_PCT)
						.detect(ts);

				assertOverflow(o, Status.OK, "have 3 readings, only 1 of 3 exceede threshold of 90.0%: [50.0, 51.0, 96"
						+ ".0]");
			}
		}

		@Nested
		class OverflowOccursTest {

			@Test
			void overflow_one_reading() {
				ts.add(FIRST_HIGH);

				Overflow o = new UsageAboveThreshold(1, THRESHOLD_PCT)
						.detect(ts);

				assertOverflow(o, Status.OVERFLOW, "The last 1 readings were above threshold of 90.0: [90.0]");
			}

			@Test
			void overflow_two_readings_with_first_one_relevant() {
				ts.add(FIRST_HIGH);
				ts.add(SECOND_HIGH);

				Overflow o = new UsageAboveThreshold(1, THRESHOLD_PCT)
						.detect(ts);

				assertOverflow(o, Status.OVERFLOW, "The last 1 readings were above threshold of 90.0: [90.0]");
			}

			@Test
			void overflow_three_readings_with_first_two_relevant() {
				ts.add(FIRST_HIGH);
				ts.add(SECOND_HIGH);
				ts.add(THIRD_LOW);

				Overflow o = new UsageAboveThreshold(2, THRESHOLD_PCT)
						.detect(ts);

				assertOverflow(o, Status.OVERFLOW, "The last 2 readings were above threshold of 90.0: [90.0, 91.0]");
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
			UsageAboveThreshold detector = new UsageAboveThreshold(detectorMeasures, 50.0d);

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
			UsageAboveThreshold detector = new UsageAboveThreshold(detectorMeasures, 50.0d);

			assertThrows(IllegalArgumentException.class,
					() -> detector.validate(expiryMeasures));
		}
	}

}