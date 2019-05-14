package com.github.honoluluhenk.gcmonitor.gc;

import java.io.Serializable;
import java.util.Map;

import com.github.honoluluhenk.gcmonitor.memory.Memory;
import com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType;

import static java.util.Objects.requireNonNull;

/**
 * {@link Memory} data extracted from a garbage collection.
 */
public class GCEvent implements Serializable {
	private static final long serialVersionUID = 3368361816542995335L;

	private final GCCollection collection;
	private final Map<MemoryPoolType, Memory> memoryAfterGC;

	public GCEvent(GCCollection collection, Map<MemoryPoolType, Memory> memoryAfterGC) {
		this.collection = requireNonNull(collection);
		this.memoryAfterGC = requireNonNull(memoryAfterGC);
	}

	public GCCollection getCollection() {
		return collection;
	}

	public Map<MemoryPoolType, Memory> getMemoryAfterGC() {
		return memoryAfterGC;
	}
}
