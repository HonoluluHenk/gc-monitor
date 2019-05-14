package com.github.honoluluhenk.gcmonitor.eventsource.openjdk;

import java.util.List;

import org.junit.jupiter.api.Test;

import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.CMS_OLD_GEN;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.G1_EDEN_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.G1_OLD_GEN;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.G1_SURVIVOR_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PAR_EDEN_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PAR_SURVIVOR_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PS_EDEN_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PS_OLD_GEN;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PS_SURVIVOR_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.SupportedGC.CMS;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.SupportedGC.G1_OLD_GENERATION;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.SupportedGC.G1_YOUNG_GENERATION;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.SupportedGC.PARNEW;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.SupportedGC.PS_MARKSWEEP;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.SupportedGC.PS_SCAVENGE;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class SupportedGCTest {
	@SuppressWarnings("PackageVisibleField")
	public static class Data {
		final SupportedGC gc;
		final String name;
		final MemoryPool[] pools;

		public Data(SupportedGC gc, String name, List<MemoryPool> pools) {
			this.gc = gc;
			this.name = name;
			//noinspection ZeroLengthArrayAllocation
			this.pools = pools.toArray(new MemoryPool[0]);
		}
	}

	private final List<Data> testInput = asList(
			new Data(PARNEW, "ParNew", asList(PAR_EDEN_SPACE, PAR_SURVIVOR_SPACE)),
			new Data(CMS, "ConcurrentMarkSweep", asList(PAR_EDEN_SPACE, PAR_SURVIVOR_SPACE,
					CMS_OLD_GEN)),

			new Data(G1_YOUNG_GENERATION, "G1 Young Generation", asList(G1_EDEN_SPACE, G1_SURVIVOR_SPACE)),
			new Data(G1_OLD_GENERATION, "G1 Old Generation", asList(G1_EDEN_SPACE, G1_SURVIVOR_SPACE,
					G1_OLD_GEN)),

			new Data(PS_SCAVENGE, "PS Scavenge", asList(PS_EDEN_SPACE, PS_SURVIVOR_SPACE)),
			new Data(PS_MARKSWEEP, "PS MarkSweep", asList(PS_EDEN_SPACE, PS_SURVIVOR_SPACE, PS_OLD_GEN))

	);

	@Test
	public void testParsing() {
		for (Data data : testInput) {
			assertThat(SupportedGC.fromGCName(data.name))
					.as("gc: " + data.name)
					.isPresent()
					.containsSame(data.gc)
					.get()
					.as("properties of " + data.name)
					.returns(data.name, SupportedGC::getGcName)
					.as("methods of " + data.name)
					.satisfies(gc -> assertThat(gc.getMemoryPools()).containsExactly(data.pools));
		}
	}

	@Test
	public void testParsing_unknown_name() {
		assertThat(SupportedGC.fromGCName("unknown name")).isNotPresent();
	}
}