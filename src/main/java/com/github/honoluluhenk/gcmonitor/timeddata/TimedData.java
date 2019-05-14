package com.github.honoluluhenk.gcmonitor.timeddata;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Add a timestamp to some data.
 *
 * @param <T> the payload data type.
 */
public class TimedData<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = -1657726356066610508L;

	private final ZonedDateTime timestamp;

	private final T data;

	public TimedData(ZonedDateTime timestamp, T data) {
		this.timestamp = Objects.requireNonNull(timestamp);
		this.data = Objects.requireNonNull(data);
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public T getData() {
		return data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof TimedData)) {
			return false;
		}
		TimedData<?> timedData = (TimedData<?>) o;
		return getTimestamp().equals(timedData.getTimestamp())
				&& getData().equals(timedData.getData());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTimestamp(), getData());
	}

	@Override
	public String toString() {
		return "TimedData{timestamp=" + timestamp + ", data=" + data + '}';
	}
}
