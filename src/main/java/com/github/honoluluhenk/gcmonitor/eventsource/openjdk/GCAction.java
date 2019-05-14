package com.github.honoluluhenk.gcmonitor.eventsource.openjdk;

import java.util.Arrays;
import java.util.Optional;

import com.github.honoluluhenk.gcmonitor.gc.GCCollection;
import com.sun.management.GarbageCollectionNotificationInfo;

/**
 * A GC action as delivered by {@link GarbageCollectionNotificationInfo#getGcAction()}.
 */
public enum GCAction {
	END_OF_MINOR_GC("end of minor GC", GCCollection.MINOR),
	END_OF_MAJOR_GC("end of major GC", GCCollection.MAJOR);

	private final String actionText;
	private final GCCollection collection;

	GCAction(String actionText, GCCollection collection) {
		this.actionText = actionText;
		this.collection = collection;
	}

	public String getActionText() {
		return actionText;
	}

	public GCCollection getCollection() {
		return collection;
	}

	/**
	 * @param actionText see {@link GarbageCollectionNotificationInfo#getGcAction()}.
	 */
	public static Optional<GCAction> fromNotificationText(String actionText) {
		return Arrays.stream(GCAction.values())
				.filter(a -> a.getActionText().equals(actionText))
				.findAny();
	}
}
