package com.github.honoluluhenk.gcmonitor;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.OptionalInt;

import com.github.honoluluhenk.gcmonitor.detection.overflow.Overflow;
import com.github.honoluluhenk.gcmonitor.detection.overflow.OverflowDetector;
import com.github.honoluluhenk.gcmonitor.eventsource.GCEventSource;
import com.github.honoluluhenk.gcmonitor.expiry.Expiry;
import com.github.honoluluhenk.gcmonitor.gc.GCCollection;
import com.github.honoluluhenk.gcmonitor.gc.GCEvent;
import com.github.honoluluhenk.gcmonitor.memory.Memory;
import com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType;
import com.github.honoluluhenk.gcmonitor.timeddata.TimedData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GCOverflowDetectorTest {

	private GCOverflowDetector detector = null;

	private GCEventSource eventSourceMock = null;
	private OverflowDetector detectorMock = null;
	private Expiry<Memory> expiryMock = null;

	@BeforeEach
	void beforeEach() {
		eventSourceMock = mock(GCEventSource.class);
		@SuppressWarnings("unchecked")
		Expiry<Memory> memoryExpiry = mock(Expiry.class);
		expiryMock = memoryExpiry;
		detectorMock = mock(OverflowDetector.class);

		detector = new GCOverflowDetector(
				eventSourceMock,
				expiryMock,
				detectorMock
		);
	}

	@Test
	void init() {
		verify(eventSourceMock, only())
				.addEventListener(isNotNull());
	}

	@Test
	void start() {
		detector.start();

		verify(eventSourceMock, times(1))
				.start();
	}

	@Test
	void stop() {
		detector.stop();

		verify(eventSourceMock, times(1))
				.stop();
	}

	@Test
	void detector_validate_gets_called_if_expiry_knows_expected_readings() {
		when(expiryMock.getExpectedReadings())
				.thenReturn(OptionalInt.of(2));

		new GCOverflowDetector(
				eventSourceMock,
				expiryMock,
				detectorMock
		);

		verify(detectorMock, times(1))
				.validate(2);
	}

	@Test
	void detector_validate_gets_not_called_on_unknown_expected_readings() {
		when(expiryMock.getExpectedReadings())
				.thenReturn(OptionalInt.empty());

		new GCOverflowDetector(
				eventSourceMock,
				expiryMock,
				detectorMock
		);

		verify(detectorMock, never())
				.validate(2);
	}

	@Test
	void detect_has_been_called() {
		Overflow expected = Overflow.ok("testing");
		when(detectorMock.detect(any()))
				.thenReturn(expected);

		Overflow actual = detector.detect();

		assertThat(actual)
				.isSameAs(expected);
		verify(detectorMock, times(1)).detect(any());
	}

	@Nested
	class HandleGCEventTest {

		private ZonedDateTime now = null;
		private Memory memory = null;

		@Test
		void handleGCEvent_major() {
			TimedData<GCEvent> params = givenMemoryInPoolType(MemoryPoolType.OLD, GCCollection.MAJOR);

			Assertions.assertThat(detector.getTimeSeries().getTimedData())
					.isEmpty();

			detector.handleGCEvent(params);

			// TODO: spy on timeseries
			// as long as injecting into final fields is not suppported by Mockito
			// and thus more "direct" tests are not possible, try to workaround:
			// look at the number of entries in the TimeSeries
			// and see that expire() is called on the Expiry mock instance
			TimedData<Memory> expected = new TimedData<>(now, memory);
			Assertions.assertThat(detector.getTimeSeries().getTimedData())
					.containsExactly(expected);
			verify(expiryMock, times(1)).isExpired(any());
		}

		@Test
		void handleGCEvent_major_with_no_memory_for_wanted_poolType() {
			TimedData<GCEvent> params = givenMemoryInPoolType(MemoryPoolType.YOUNG, GCCollection.MAJOR);

			Assertions.assertThat(detector.getTimeSeries().getTimedData())
					.isEmpty();

			detector.handleGCEvent(params);

			// TODO: spy on timeseries
			// as long as injecting into final fields is not suppported by Mockito
			// and thus more "direct" tests are not possible, try to workaround:
			// look at the number of entries in the TimeSeries
			// and see that expire() is called on the Expiry mock instance
			Assertions.assertThat(detector.getTimeSeries().getTimedData())
					.isEmpty();
			verify(expiryMock, never()).isExpired(any());
		}

		@Test
		void handleGCEvent_major_with_minor_collection() {
			TimedData<GCEvent> params = givenMemoryInPoolType(MemoryPoolType.OLD, GCCollection.MINOR);

			Assertions.assertThat(detector.getTimeSeries().getTimedData())
					.isEmpty();

			detector.handleGCEvent(params);

			// TODO: spy on timeseries
			// as long as injecting into final fields is not suppported by Mockito
			// and thus more "direct" tests are not possible, try to workaround:
			// look at the number of entries in the TimeSeries
			// and see that expire() is called on the Expiry mock instance
			Assertions.assertThat(detector.getTimeSeries().getTimedData())
					.isEmpty();
			verify(expiryMock, never()).isExpired(any());
		}

		private TimedData<GCEvent> givenMemoryInPoolType(MemoryPoolType poolType, GCCollection collection) {
			this.now = ZonedDateTime.now();
			this.memory = new Memory(1, 2, 3, 4);
			Map<MemoryPoolType, Memory> memAfter = Collections.singletonMap(poolType, memory);
			return new TimedData<>(now, new GCEvent(collection, memAfter));
		}

	}
}