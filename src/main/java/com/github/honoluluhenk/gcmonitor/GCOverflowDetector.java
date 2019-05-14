package com.github.honoluluhenk.gcmonitor;

import com.github.honoluluhenk.gcmonitor.detection.overflow.Overflow;
import com.github.honoluluhenk.gcmonitor.detection.overflow.OverflowDetector;
import com.github.honoluluhenk.gcmonitor.eventsource.GCEventSource;
import com.github.honoluluhenk.gcmonitor.expiry.Expiry;
import com.github.honoluluhenk.gcmonitor.gc.GCEvent;
import com.github.honoluluhenk.gcmonitor.memory.Memory;
import com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType;
import com.github.honoluluhenk.gcmonitor.timeddata.TimeSeries;
import com.github.honoluluhenk.gcmonitor.timeddata.TimedData;

import static java.util.Objects.requireNonNull;

/**
 * Warns about possible future GC endless loops and memory heap exhaustion.
 */
public class GCOverflowDetector {
	private final GCEventSource eventSource;
	private final Expiry<Memory> expiry;
	private final OverflowDetector detector;

	private final TimeSeries<Memory> timeSeries = new TimeSeries<>();

	public GCOverflowDetector(
			GCEventSource eventSource,
			Expiry<Memory> expiry,
			OverflowDetector detector
	) {
		this.eventSource = requireNonNull(eventSource, "eventSource");
		this.eventSource.addEventListener(this::handleGCEvent);
		this.expiry = requireNonNull(expiry, "expiry");

		expiry.getExpectedReadings().ifPresent(detector::validate);
		this.detector = requireNonNull(detector, "detector");
	}

	/* default */ void handleGCEvent(TimedData<GCEvent> event) {
		requireNonNull(event);

		if (!event.getData().getCollection().isMajor()) {
			return;
		}

		Memory memory = event.getData().getMemoryAfterGC().get(MemoryPoolType.OLD);
		if (memory == null) {
			return;
		}

		timeSeries.add(new TimedData<>(event.getTimestamp(), memory));
		timeSeries.expire(expiry);

	}

	public void start() {
		eventSource.start();
	}

	public void stop() {
		eventSource.stop();
	}

	public Overflow detect() {
		Overflow overflow = detector.detect(timeSeries);

		return overflow;
	}

	/**
	 * testing only!
	 */
	/* default */ TimeSeries<Memory> getTimeSeries() {
		return timeSeries;
	}
}
