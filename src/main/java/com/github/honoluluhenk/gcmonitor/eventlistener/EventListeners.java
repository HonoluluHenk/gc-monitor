package com.github.honoluluhenk.gcmonitor.eventlistener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.synchronizedList;
import static java.util.Collections.unmodifiableList;

/**
 * Helper class to manage a collection of {@link EventListener}s.
 */
public class EventListeners<E> {
	private static final Logger LOG = LoggerFactory.getLogger(EventListeners.class);

	private final List<EventListener<E>> listeners = synchronizedList(new ArrayList<>());

	public void addListener(EventListener<E> listener) {
		listeners.add(listener);
	}

	public boolean removeListener(EventListener<E> listener) {
		return listeners.remove(listener);
	}

	public void removeAllListeners() {
		listeners.clear();
	}

	public void notifyListeners(E event) {
		Objects.requireNonNull(event);

		listeners.forEach(l -> notifyListener(l, event));
	}

	public Collection<EventListener<E>> getListeners() {
		return unmodifiableList(listeners);
	}

	private void notifyListener(EventListener<E> listener, E event) {
		try {
			listener.accept(event);
		} catch (RuntimeException e) {
			LOG.warn("Eventistener failed on event {}: {}", event, listener, e);
		}
	}
}
