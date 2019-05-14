package com.github.honoluluhenk.gcmonitor.expiry;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import com.github.honoluluhenk.gcmonitor.PayloadFixture;
import com.github.honoluluhenk.gcmonitor.TestUtil;
import com.github.honoluluhenk.gcmonitor.timeddata.TimedData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class DurationExpiryTest {

	private static final int NOW_DAY = 10;
	private static final int EXPIRY_DAY = 9;
	private static final Duration DURATION = Duration.of(NOW_DAY - EXPIRY_DAY, ChronoUnit.DAYS);

	private static final DurationExpiry<PayloadFixture> EXPIRY = new DurationExpiry<PayloadFixture>(DURATION) {
		private static final long serialVersionUID = 295246575272479240L;

		@Override
		ZonedDateTime getNow() {
			return TestUtil.mkTime(NOW_DAY);
		}
	};

	private static final List<TimedData<PayloadFixture>> DONT_CARE = Collections.emptyList();

	@Test
	void expires_very_old_data() {
		boolean expired = EXPIRY.isExpired(new Params<>(new TimedData<>(TestUtil.mkTime(NOW_DAY - 5),
				new PayloadFixture(
				"expired")), DONT_CARE));

		assertTrue(expired);
	}

	@Test
	void expires_old_data_on_threshold() {
		boolean expired = EXPIRY.isExpired(new Params<>(new TimedData<>(TestUtil.mkTime(EXPIRY_DAY),
				new PayloadFixture(
				"expired")), DONT_CARE));

		assertTrue(expired);
	}

	@Test
	void does_not_expire_data_past_threshold() {
		// boundary test: choose a datetime veeery close to the expire threshold
		boolean expired = EXPIRY.isExpired(new Params<>(new TimedData<>(TestUtil.mkTime(EXPIRY_DAY).plusNanos(1),
				new PayloadFixture("not expired")), DONT_CARE));

		assertFalse(expired);
	}

	@Test
	void does_not_expire_data_on_now() {
		boolean expired = EXPIRY.isExpired(new Params<>(new TimedData<>(TestUtil.mkTime(NOW_DAY), new PayloadFixture(
				"not "
				+ "expired")), DONT_CARE));

		assertFalse(expired);
	}

	@Test
	void does_not_expire_future_data() {
		boolean expired = EXPIRY.isExpired(new Params<>(new TimedData<>(TestUtil.mkTime(NOW_DAY + 5),
				new PayloadFixture("not "
				+ "expired")), DONT_CARE));

		assertFalse(expired);
	}

	@Test
	void getNow_provieds_a_value() {
		assertNotNull(new DurationExpiry<>(DURATION).getNow());
	}
}