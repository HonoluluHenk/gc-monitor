package com.github.honoluluhenk.gcmonitor.expiry;

import java.io.Serializable;
import java.util.OptionalInt;

import com.github.honoluluhenk.gcmonitor.timeddata.TimeSeries;

/**
 * Expires data from {@link TimeSeries}.
 */
@FunctionalInterface
public interface Expiry<T extends Serializable> {
	/**
	 * Implementations should determine if the currently processed entry (see {@link Params#getTimedData()})
	 * must be removed from a time series.
	 * {@link Params#getAllData()} contains the complete liste <strong>before any expiry happened</strong> for this
	 * run.
	 */
	boolean isExpired(Params<T> params);

	/**
	 * If known, the implementation must return a <strong>lower bound</strong>
	 * of the number of readings that will get kept in the TimeSeries after expiration.
	 */
	default OptionalInt getExpectedReadings() {
		return OptionalInt.empty();
	}

	default Expiry<T> and(Expiry<T> other) {
		return (params) -> isExpired(params) && other.isExpired(params);
	}

	@SuppressWarnings("PMD.ShortMethodName")
	default Expiry<T> or(Expiry<T> other) {
		return (params) -> isExpired(params) || other.isExpired(params);
	}

	default Expiry<T> negate() {
		return (params) -> !isExpired(params);
	}

}
