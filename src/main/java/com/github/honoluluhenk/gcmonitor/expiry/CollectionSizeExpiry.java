package com.github.honoluluhenk.gcmonitor.expiry;

import java.io.Serializable;
import java.util.List;
import java.util.OptionalInt;

import com.github.honoluluhenk.gcmonitor.timeddata.TimeSeries;
import com.github.honoluluhenk.gcmonitor.timeddata.TimedData;

/**
 * Expire data from the {@link TimeSeries} if more than {@code maxCollectionSize} entries exist.
 * <p>Older entries are expired first.</p>
 */
public class CollectionSizeExpiry<T extends Serializable> implements Expiry<T>, Serializable {
	private static final long serialVersionUID = 460414477181441179L;

	private final int maxCollectionSize;

	/**
	 * @param maxCollectionSize must be &gt; 0.
	 * @throws IllegalArgumentException if input validation fails.
	 */
	public CollectionSizeExpiry(int maxCollectionSize) {
		if (maxCollectionSize <= 0) {
			throw new IllegalArgumentException("maxCollectionSize must be >= 0 but was: " + maxCollectionSize);
		}
		this.maxCollectionSize = maxCollectionSize;
	}

	@Override
	public boolean isExpired(Params<T> params) {
		List<? extends TimedData<? super T>> allData = params.getAllData();
		int firstAllowedIndex = allData.size() - maxCollectionSize;
		int idx = allData.indexOf(params.getTimedData());
		return idx < firstAllowedIndex;
	}

	@Override
	public OptionalInt getExpectedReadings() {
		return OptionalInt.of(maxCollectionSize);
	}
}
