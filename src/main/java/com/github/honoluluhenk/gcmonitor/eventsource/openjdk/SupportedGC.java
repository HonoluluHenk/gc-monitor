package com.github.honoluluhenk.gcmonitor.eventsource.openjdk;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.CMS_OLD_GEN;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.G1_EDEN_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.G1_OLD_GEN;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.G1_SURVIVOR_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PAR_EDEN_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PAR_SURVIVOR_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PS_EDEN_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PS_OLD_GEN;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PS_SURVIVOR_SPACE;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;

/**
 *
 */
public enum SupportedGC {
	/**
	 * Parallel New GC takes care for eden/young generation.
	 */
	PARNEW("ParNew", asList(PAR_EDEN_SPACE, PAR_SURVIVOR_SPACE)),
	/**
	 * Concurrent Mark Sweep old generation collector.
	 */
	CMS("ConcurrentMarkSweep", asList(PAR_EDEN_SPACE, PAR_SURVIVOR_SPACE, CMS_OLD_GEN)),

	/**
	 * Garbage First - young generation.
	 */
	G1_YOUNG_GENERATION("G1 Young Generation", asList(G1_EDEN_SPACE, G1_SURVIVOR_SPACE)),
	/**
	 * Garbage First - old generation.
	 */
	G1_OLD_GENERATION("G1 Old Generation", asList(G1_EDEN_SPACE, G1_SURVIVOR_SPACE, G1_OLD_GEN)),

	/**
	 * Parallel Sweep scavenge- young generation.
	 */
	PS_SCAVENGE("PS Scavenge", asList(PS_EDEN_SPACE, PS_SURVIVOR_SPACE)),
	/**
	 * Parallel Sweep Mark &amp; Sweep - handle old generation.
	 */
	PS_MARKSWEEP("PS MarkSweep", asList(PS_EDEN_SPACE, PS_SURVIVOR_SPACE, PS_OLD_GEN));

	private final String gcName;
	@SuppressWarnings("ImmutableEnumChecker") // unmodifiableCollection
	private final Collection<MemoryPool> memoryPools;

	SupportedGC(String gcName, Collection<MemoryPool> memoryPools) {
		this.gcName = requireNonNull(gcName);
		this.memoryPools = unmodifiableCollection(requireNonNull(memoryPools));
	}

	public String getGcName() {
		return gcName;
	}

	public Collection<MemoryPool> getMemoryPools() {
		return memoryPools;
	}

	public static Optional<SupportedGC> fromGCName(String gcName) {
		Optional<SupportedGC> result = Arrays.stream(values())
				.filter(gc -> gc.getGcName().equals(gcName))
				.findAny();

		return result;
	}
}
