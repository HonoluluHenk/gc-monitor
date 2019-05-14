package com.github.honoluluhenk.gcmonitor.eventsource.openjdk;

import java.util.Optional;

import com.github.honoluluhenk.gcmonitor.gc.GCCollection;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GCActionTest {

	@Test
	void fromNotificationText_end_of_major() {
		Optional<GCAction> actual = GCAction.fromNotificationText("end of major GC");

		assertThat(actual)
				.isPresent()
				.containsSame(GCAction.END_OF_MAJOR_GC);
	}

	@Test
	void fromNotificationText_end_of_minor() {
		Optional<GCAction> actual = GCAction.fromNotificationText("end of minor GC");

		assertThat(actual)
				.isPresent()
				.containsSame(GCAction.END_OF_MINOR_GC);
	}

	@Test
	void fromNotificationText_unsupported() {
		Optional<GCAction> actual = GCAction.fromNotificationText("some unknown value");

		assertThat(actual)
				.isEmpty();
	}

	@Test
	void collection_type_maps_correctly() {
		Assertions.assertThat(GCAction.END_OF_MAJOR_GC.getCollection())
				.isSameAs(GCCollection.MAJOR);
		Assertions.assertThat(GCAction.END_OF_MINOR_GC.getCollection())
				.isSameAs(GCCollection.MINOR);
	}
}