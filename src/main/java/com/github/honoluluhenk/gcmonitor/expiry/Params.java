package com.github.honoluluhenk.gcmonitor.expiry;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.github.honoluluhenk.gcmonitor.timeddata.TimedData;

/**
 * Parameter object for {@link Expiry#isExpired(Params)}.
 */
public class Params<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = -2805182892701495212L;

	private final TimedData<T> timedData;
	private final List<? extends TimedData<? super T>> allData;

	public Params(TimedData<T> timedData, List<? extends TimedData<? super T>> allData) {
		this.timedData = Objects.requireNonNull(timedData);
		this.allData = Objects.requireNonNull(allData);
	}

	/**
	 * The current TimeData to inspect.
	 */
	public TimedData<T> getTimedData() {
		return timedData;
	}

	/**
	 * For expiries that need to compare agains the whole collection of TimedData in a TimeSeries: a readonly view of
	 * the complete TimeSeries data <strong>at the time the expiry process started</strong>.
	 * <p>Please note: some data might already be removed by previous calls to {@link Expiry#isExpired(Params)}!</p>
	 * <p>Ordering of the list: latest entry at index 0</p>
	 */
	public List<? extends TimedData<? super T>> getAllData() {
		return allData;
	}
}
