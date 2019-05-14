package com.github.honoluluhenk.gcmonitor.expiry;

import java.util.List;
import java.util.OptionalInt;

import com.github.honoluluhenk.gcmonitor.PayloadFixture;
import com.github.honoluluhenk.gcmonitor.TestUtil;
import com.github.honoluluhenk.gcmonitor.timeddata.TimedData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionSizeExpiryTest {

	private static final CollectionSizeExpiry<PayloadFixture> EXPIRY
			= new CollectionSizeExpiry<>(2);

	private static final TimedData<PayloadFixture> FIRST
			= new TimedData<>(TestUtil.mkTime(1), new PayloadFixture("FIRST"));
	private static final TimedData<PayloadFixture> SECOND
			= new TimedData<>(TestUtil.mkTime(2), new PayloadFixture("SECOND"));
	private static final TimedData<PayloadFixture> THIRD
			= new TimedData<>(TestUtil.mkTime(3), new PayloadFixture("THIRD"));

	@Test
	void test_illegal_constructor_arg() {
		assertThrows(IllegalArgumentException.class,
				() -> new CollectionSizeExpiry<>(0));
		assertThrows(IllegalArgumentException.class,
				() -> new CollectionSizeExpiry<>(-1));
	}

	@Nested
	class ExpireTest {
		private final List<TimedData<PayloadFixture>> allData = asList(FIRST, SECOND, THIRD);

		@Test
		void test_expire_FIRST() {
			assertTrue(EXPIRY.isExpired(new Params<>(FIRST, allData)));
		}

		@Test
		void test_expire_SECOND() {
			assertFalse(EXPIRY.isExpired(new Params<>(SECOND, allData)));
		}

		@Test
		void test_expire_THIRD() {
			assertFalse(EXPIRY.isExpired(new Params<>(THIRD, allData)));
		}
	}

	@Nested
	class NoExpireTest {
		private final List<TimedData<PayloadFixture>> allData = asList(FIRST, SECOND);

		@Test
		void test_expire_nothing_on_small_list_FIRST() {
			boolean expired = EXPIRY.isExpired(new Params<>(FIRST, allData));
			assertFalse(expired);
		}

		@Test
		void test_expire_nothing_on_small_list_SECOND() {
			boolean expired = EXPIRY.isExpired(new Params<>(SECOND, allData));
			assertFalse(expired);
		}

	}

	@Nested
	public class ExpectedReadingsTest {
		@Test
		void returns_correct_value() {
			assertThat(new CollectionSizeExpiry<>(2).getExpectedReadings())
					.isEqualTo(OptionalInt.of(2));
			assertThat(new CollectionSizeExpiry<>(Integer.MAX_VALUE).getExpectedReadings())
					.isEqualTo(OptionalInt.of(Integer.MAX_VALUE));
		}
	}
}
