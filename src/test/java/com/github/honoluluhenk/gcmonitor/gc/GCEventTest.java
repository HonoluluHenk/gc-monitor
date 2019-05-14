package com.github.honoluluhenk.gcmonitor.gc;

import java.lang.management.MemoryUsage;
import java.util.EnumMap;
import java.util.Map;

import com.github.honoluluhenk.gcmonitor.memory.Memory;
import com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GCEventTest {
	private final Map<MemoryPoolType, Memory> memoryAfterGC = new EnumMap<>(MemoryPoolType.class);

	@Test
	void getCollection() {
		assertThat(new GCEvent(GCCollection.MAJOR, memoryAfterGC).getCollection()).isSameAs(GCCollection.MAJOR);
		assertThat(new GCEvent(GCCollection.MINOR, memoryAfterGC).getCollection()).isSameAs(GCCollection.MINOR);
	}

	@Test
	void getMemoryAfterGC_empty() {
		Assertions.assertThat(new GCEvent(GCCollection.MAJOR, memoryAfterGC).getMemoryAfterGC()).isEmpty();
	}

	@Test
	void getMemoryAfterGC_contains() {
		Memory fixture = Memory.wrap(new MemoryUsage(123, 234, 345, 456));

		memoryAfterGC.put(MemoryPoolType.OLD, fixture);

		Assertions.assertThat(new GCEvent(GCCollection.MAJOR, memoryAfterGC).getMemoryAfterGC())
				.containsOnlyKeys(MemoryPoolType.OLD)
				.containsValue(fixture);
	}
}