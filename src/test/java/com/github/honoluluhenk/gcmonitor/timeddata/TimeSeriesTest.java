package com.github.honoluluhenk.gcmonitor.timeddata;

import java.util.List;
import java.util.stream.Collectors;

import com.github.honoluluhenk.gcmonitor.PayloadFixture;
import com.github.honoluluhenk.gcmonitor.TestUtil;
import com.github.honoluluhenk.gcmonitor.expiry.FakeExpiry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TimeSeriesTest {

	private static final PayloadFixture THREE = new PayloadFixture("three");
	private static final PayloadFixture FIVE = new PayloadFixture("five");
	private static final PayloadFixture SEVEN = new PayloadFixture("seven");
	private static final PayloadFixture FOO = new PayloadFixture("foo");

	private final TimeSeries<PayloadFixture> dot = new TimeSeries<>();

	@BeforeEach
	void setup() {
		dot.add(new TimedData<>(TestUtil.mkTime(3), THREE));
		dot.add(new TimedData<>(TestUtil.mkTime(5), FIVE));
		dot.add(new TimedData<>(TestUtil.mkTime(7), SEVEN));
	}

	@Nested
	class AddOrderingTest {
		@Test
		void testAdd_works() {
			assertThat(payloads()).containsExactly(THREE, FIVE, SEVEN);

		}

		@Test
		void testAdd_atBeginning() {
			dot.add(new TimedData<>(TestUtil.mkTime(1), FOO));

			assertThat(payloads()).containsExactly(FOO, THREE, FIVE, SEVEN);
		}

		@Test
		void testAdd_atMiddle() {
			dot.add(new TimedData<>(TestUtil.mkTime(4), FOO));

			assertThat(payloads()).containsExactly(THREE, FOO, FIVE, SEVEN);
		}

		@Test
		void testAdd_atEnd() {
			dot.add(new TimedData<>(TestUtil.mkTime(9), FOO));

			assertThat(payloads()).containsExactly(THREE, FIVE, SEVEN, FOO);
		}

		@Test
		void testAdd_MultipleOnSameDay() {
			// FOO is at the same day as FIVE
			dot.add(new TimedData<>(TestUtil.mkTime(5), FOO));

			assertThat(payloads()).containsExactly(THREE, FIVE, FOO, SEVEN);
		}
	}

	@Nested
	class ExpiryTest {
		@Test
		void testExpire_nothing_expired() {
			dot.expire(new FakeExpiry());

			assertThat(payloads()).containsExactly(THREE, FIVE, SEVEN);
		}

		@Test
		void testExpire_first_expired() {
			dot.expire(new FakeExpiry(THREE));

			assertThat(payloads()).containsExactly(FIVE, SEVEN);
		}

		@Test
		void testExpire_middle_expired() {
			dot.expire(new FakeExpiry(FIVE));

			assertThat(payloads()).containsExactly(THREE, SEVEN);
		}

		@Test
		void testExpire_last_expired() {
			dot.expire(new FakeExpiry(SEVEN));

			assertThat(payloads()).containsExactly(THREE, FIVE);
		}

		@Test
		void testExpire_all_expired() {
			dot.expire(new FakeExpiry(THREE, FIVE, SEVEN));

			assertThat(payloads()).containsExactly();
		}
	}

	private List<PayloadFixture> payloads() {
		List<PayloadFixture> expected = dot.getTimedData().stream()
				.map(TimedData::getData)
				.collect(Collectors.toList());

		return expected;
	}
}
