package com.github.honoluluhenk.gcmonitor.timeddata;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

class TimedDataTest {

	@Test
	void getTimestamp_should_return_the_timestamp() {
		ZonedDateTime now = ZonedDateTime.now();
		TimedData<String> out = new TimedData<>(now, "Hello");

		ZonedDateTime timestamp = out.getTimestamp();

		assertThat(timestamp).isEqualTo(now);
	}

	@Test
	void getData_should_return_the_data() {
		String hello = "Hello";
		TimedData<String> out = new TimedData<>(ZonedDateTime.now(), hello);

		String data = out.getData();

		assertThat(data).isSameAs(hello);
	}

	@Test
	void toString_prints_something_useful() {
		ZonedDateTime now = ZonedDateTime.now();
		TimedData<String> out = new TimedData<>(now, "Hello");
		String expected = String.format("TimedData{timestamp=%s, data=Hello}", now);

		String string = out.toString();

		assertThat(string).isEqualTo(expected);
	}

	@Nested
	class EqualsHashcodeTest {
		private final ZonedDateTime now = ZonedDateTime.now();
		private final ZonedDateTime then = now.plus(1, HOURS);

		public class Other extends TimedData<String> {
			private static final long serialVersionUID = 2434957647170408608L;

			public Other(ZonedDateTime timestamp, String data) {
				super(timestamp, data);
			}
		}

		public class Independent implements Serializable {
			private static final long serialVersionUID = -8278458747510183474L;
		}

		@Test
		void equal_on_same_ref() {
			TimedData<String> test = new TimedData<>(now, "Hello");

			assertThat(test).isEqualTo(test);
		}

		@Test
		void equal_on_sublass() {
			TimedData<String> a = new TimedData<>(now, "Hello");
			Other b = new Other(now, a.getData());

			assertThat(a).isEqualTo(b);
		}

		@Test
		void not_equal_on_independent_class() {
			TimedData<String> a = new TimedData<>(now, "Hello");
			Independent b = new Independent();

			assertThat(a).isNotEqualTo(b);
		}

		@Test
		void equal_on_same_timestamp_and_data() {
			TimedData<String> a = new TimedData<>(now, "Hello");
			TimedData<String> b = new TimedData<>(now, a.getData());

			assertThat(a).isEqualTo(b);
			assertThat(a.hashCode()).isEqualTo(b.hashCode());
		}

		@Test
		void differs_on_same_timestamp_and_differing_data() {
			TimedData<String> a = new TimedData<>(now, "Hello");
			TimedData<String> b = new TimedData<>(now, "World");

			assertThat(a).isNotEqualTo(b);
			assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
		}

		@Test
		void differs_on_differing_timestamp_and_differing_data() {
			TimedData<String> a = new TimedData<>(now, "Hello");
			TimedData<String> b = new TimedData<>(then, "World");

			assertThat(a).isNotEqualTo(b);
			assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
		}

		@Test
		void differs_on_differing_timestamp_and_same_data() {
			TimedData<String> a = new TimedData<>(now, "Hello");
			TimedData<String> b = new TimedData<>(then, a.getData());

			assertThat(a).isNotEqualTo(b);
			assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
		}

	}
}