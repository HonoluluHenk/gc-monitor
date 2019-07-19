package com.github.honoluluhenk.gcmonitor.eventsource.openjdk;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

import com.github.honoluluhenk.gcmonitor.eventlistener.EventListener;
import com.github.honoluluhenk.gcmonitor.eventlistener.EventListeners;
import com.github.honoluluhenk.gcmonitor.eventsource.GCEventSource;
import com.github.honoluluhenk.gcmonitor.gc.GCCollection;
import com.github.honoluluhenk.gcmonitor.gc.GCEvent;
import com.github.honoluluhenk.gcmonitor.memory.Memory;
import com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType;
import com.github.honoluluhenk.gcmonitor.timeddata.TimedData;
import com.sun.management.GarbageCollectionNotificationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.GCAction.fromNotificationText;
import static com.sun.management.GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

/**
 * Produces {@link GCEvent}s for OpenJDK and compatible (Oracle, IBM, ...) JVMs.
 */
public class OpenJDKEventSource implements GCEventSource {
	private static final Logger LOG = LoggerFactory.getLogger(OpenJDKEventSource.class.getName());

	private final EventListeners<TimedData<GCEvent>> eventListeners = new EventListeners<>();

	private final NotificationListener notificationListener = this::handleGCNotification;
	private final NotificationFilter notificationFilter = this::isGCNotification;

	private boolean isGCNotification(Notification notification) {
		boolean equals = GARBAGE_COLLECTION_NOTIFICATION.equals(notification.getType());
		LOG.trace("filter activated: {}={}", notification.getType(), equals);

		return equals;
	}

	private void handleGCNotification(Notification notification, Object handback) {
		LOG.trace("Received notification: {}/{}", notification, handback);
		try {
			GarbageCollectionNotificationInfo info =
					GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());

			GCAction action = fromNotificationText(info.getGcAction())
					.orElseThrow(() -> new IllegalStateException(
							"Notification action not supported: " + info.getGcAction()));

			Map<MemoryPoolType, Memory> poolData = parseMemory(getMemoryUsageAfterGc(info));

			TimedData<GCEvent> event = buildEvent(action.getCollection(), poolData);

			LOG.trace("Event notification: {}", event);

			eventListeners.notifyListeners(event);
		} catch (@SuppressWarnings("ErrorNotRethrown") NoClassDefFoundError e) {
			// happens if class GarbageCollectionNotificationInfo is not in the classpath
			LOG.error("Cannot find a required class: {}", e.getMessage());
		} catch (RuntimeException rte) {
			LOG.error("Error processing GC notification, emit GCEvent, message: {}", notification.getMessage(), rte);
		}
	}

	private TimedData<GCEvent> buildEvent(GCCollection collection, Map<MemoryPoolType, Memory> poolData) {
		requireNonNull(collection);
		requireNonNull(poolData);

		return new TimedData<>(now(), new GCEvent(collection, poolData));
	}

	/**
	 * make testing easier.
	 */
	/* default */ ZonedDateTime now() {
		return ZonedDateTime.now(ZoneId.systemDefault());
	}

	/**
	 * make testing easier.
	 */
	/* default */ Map<String, MemoryUsage> getMemoryUsageAfterGc(GarbageCollectionNotificationInfo info) {
		return info.getGcInfo().getMemoryUsageAfterGc();
	}

	/* default */ Map<MemoryPoolType, Memory> parseMemory(Map<String, MemoryUsage> memoryAfterGC) {
		LOG.trace("parseMemory: {}", memoryAfterGC);

		Map<MemoryPoolType, Memory> result =
				memoryAfterGC.entrySet().stream()
						.map(this::memoryPoolFromName)
						.collect(toMap(
								e -> e.getKey().getPoolType(),
								e -> Memory.wrap(e.getValue()),
								this::combineBySummingUsed
						));

		return result;
	}

		private Memory combineBySummingUsed(Memory a, Memory b) {
		return new Memory(
				a.getInit().orElse(-1L),
				a.getUsed() + b.getUsed(),
				a.getCommitted(),
				a.getMax().orElse(-1L));
	}

	private Map.Entry<MemoryPool, MemoryUsage> memoryPoolFromName(Entry<String, MemoryUsage> e) {
		return MemoryPool.fromPoolName(e.getKey())
				.map(pool -> new SimpleImmutableEntry<>(pool, e.getValue()))
				.orElseThrow(() -> new IllegalArgumentException("No defined pool found for: " + e.getKey()));
	}

	@Override
	public void start() {
		LOG.info("{} starting", OpenJDKEventSource.class.getSimpleName());
		registerEventListeners();
		LOG.info("{} started", OpenJDKEventSource.class.getSimpleName());
	}

	@Override
	public void stop() {
		LOG.info("{} stopping", OpenJDKEventSource.class.getSimpleName());
		unregisterEventListener();
		LOG.info("{} stopped", OpenJDKEventSource.class.getSimpleName());
	}

	@Override
	public void addEventListener(EventListener<TimedData<GCEvent>> listener) {
		eventListeners.addListener(listener);
	}

	@Override
	public boolean removeEventListener(EventListener<TimedData<GCEvent>> listener) {
		return eventListeners.removeListener(listener);
	}

	private void registerEventListeners() {
		findSupportedGCEmitters()
				.forEach(this::addNotificationListener);
	}

	private void addNotificationListener(NotificationEmitter emitter) {
		LOG.trace("adding NotificationListener on {}", emitter);
		emitter.addNotificationListener(
				notificationListener,
				notificationFilter,
				null);
	}

	private void unregisterEventListener() {
		findSupportedGCEmitters()
				.forEach(this::removeNotificationListener);
	}

	private void removeNotificationListener(NotificationEmitter emitter) {
		LOG.trace("removing NotificationListener on {}", emitter);
		try {
			emitter.removeNotificationListener(
					notificationListener,
					notificationFilter,
					null);
		} catch (ListenerNotFoundException e) {
			LOG.warn("Could not unregister notificationListener. maybe called stop() without start()?", e);
		}
	}

	private List<NotificationEmitter> findSupportedGCEmitters() {
		List<NotificationEmitter> result = ManagementFactory.getGarbageCollectorMXBeans().stream()
				.map(this::notificationEmitterFromGCMXBean)
				.collect(Collectors.toList());

		return result;
	}

	private NotificationEmitter notificationEmitterFromGCMXBean(GarbageCollectorMXBean gc) {
		SupportedGC supportedGC = SupportedGC.fromGCName(gc.getName())
				.orElseThrow(() -> new UnsupportedOperationException("GC not supported: " + gc.getName()));
		LOG.debug("GC: {}, pools: {}", supportedGC, asList(gc.getMemoryPoolNames()));

		validateMemoryPools(gc);
		validateNotificationEmitter(gc);

		return (NotificationEmitter) gc;
	}

	private void validateMemoryPools(GarbageCollectorMXBean gc) {
		Arrays.stream(gc.getMemoryPoolNames())
				.filter(poolName -> !MemoryPool.isSupported(poolName))
				.findAny()
				.ifPresent(pool -> {
					throw new IllegalArgumentException(
							format("garbagecCollector/memory pool not supported: %s/%s", gc.getName(), pool));
				});
	}

	private void validateNotificationEmitter(GarbageCollectorMXBean gc) {
		requireNonNull(gc);

		if (!(gc instanceof NotificationEmitter)) {
			throw new IllegalArgumentException(
					format("GC does not support NotificationEmitter but we need this: %s/%s",
							gc.getName(), gc.getObjectName()));
		}
	}
}
