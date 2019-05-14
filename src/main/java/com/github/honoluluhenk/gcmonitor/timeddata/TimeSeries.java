package com.github.honoluluhenk.gcmonitor.timeddata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.github.honoluluhenk.gcmonitor.expiry.Expiry;
import com.github.honoluluhenk.gcmonitor.expiry.Params;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static java.util.Collections.unmodifiableList;

/**
 * A series of data ordered by timestamp (descending).
 * I.E.: entry with biggest timestamp comes first when iterating (e.g.: {@link #stream()} or via
 * {@link #getTimedData()}.
 *
 * @param <T> the payload data this TimeSeries is made of.
 */
public class TimeSeries<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = 2117342200468145148L;

	// LinkedList since insertion at an index close to one end
	// (99% expected use case) is cheap
	private final List<TimedData<T>> data = new ArrayList<>();

	public List<TimedData<T>> getTimedData() {
		return unmodifiableList(data);
	}

	public void add(TimedData<T> timedData) {
		Objects.requireNonNull(timedData);

		insertAtTimestamp(timedData);
	}

	public void expire(Expiry<T> expiry) {
		Objects.requireNonNull(expiry);

		synchronized (data) {
			List<TimedData<T>> allData = unmodifiableList(data);
			data.removeIf(entry -> expiry.isExpired(new Params<>(entry, allData)));
		}
	}

	// TODO: convert to some sorted collection, e.g.: SortedSet.
	@SuppressFBWarnings(value = "LII_LIST_INDEXED_ITERATING",
			justification = "needs converting to a sorted collection")
	private void insertAtTimestamp(TimedData<T> timedData) {
		synchronized (data) {
			int i = 0;
			for (i = 0; i < data.size(); i++) {
				TimedData<T> existing = data.get(i);

				if (existing.getTimestamp().isAfter(timedData.getTimestamp())) {
					break;
				}
			}

			this.data.add(i, timedData);
		}
	}

	public Stream<TimedData<T>> stream() {
		return getTimedData().stream();
	}

}
