package com.github.honoluluhenk.gcmonitor.expiry;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Objects;

import com.github.honoluluhenk.gcmonitor.timeddata.TimeSeries;

/**
 * Expire all data from {@link TimeSeries} that is older than {@code expireAfter} (a {@link TemporalAmount}).
 */
public class DurationExpiry<T extends Serializable> implements Expiry<T>, Serializable {
	private static final long serialVersionUID = -2298525974951823363L;

	private final TemporalAmount expireAfter;

	public DurationExpiry(TemporalAmount expireAfter) {
		this.expireAfter = Objects.requireNonNull(expireAfter);
	}

	@Override
	public boolean isExpired(Params<T> params) {
		ZonedDateTime expireAt = getNow().minus(expireAfter);

		boolean expired = params.getTimedData().getTimestamp().compareTo(expireAt) <= 0;

		return expired;
	}

	/**
	 * Allow unit tests to not depend on static ZonedDateTime.now().
	 */
	/* default */ ZonedDateTime getNow() {
		return ZonedDateTime.now(ZoneId.systemDefault());
	}

}
