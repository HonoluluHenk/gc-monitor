package com.github.honoluluhenk.gcmonitor.eventsource.openjdk;

import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;

import com.github.honoluluhenk.gcmonitor.memory.Memory;
import com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenJDKEventSourceTest {

	private Map<String, MemoryUsage> sample = null;

	//	private class OpenJDK8CompatibleTestHelper extends OpenJDKEventSource {
	//		public OpenJDK8CompatibleTestHelper(Expiry<Memory> expiry) {
	//			super(expiry);
	//		}
	//
	//		@Override
	//		Map<String, MemoryUsage> getMemoryUsageAfterGc(GarbageCollectionNotificationInfo info) {
	//			return sample;
	//		}
	//	}

	@BeforeEach
	void beforeEach() {
		// Sample MemoryUsage taken via debugger from a running system
		// {
		// G1 Old Gen=
		// 		init = 100663296(98304K)
		// 		used = 437481072(427227K)
		// 		committed = 495976448(484352K)
		// 		max = 524288000(512000K),
		// Code Cache=
		// 		init = 2555904(2496K)
		// 		used = 2136448(2086K)
		// 		committed = 2555904(2496K)
		// 		max = 251658240(245760K),
		// G1 Survivor Space=
		// 		init = 0(0K)
		// 		used = 0(0K)
		// 		committed = 0(0K)
		// 		max = -1(-1K),
		// Compressed Class Space=
		// 		init = 0(0K)
		// 		used = 754888(737K)
		// 		committed = 917504(896K)
		// 		max = 1073741824(1048576K),
		// Metaspace=
		// 		init = 0(0K)
		// 		used = 6484248(6332K)
		// 		committed = 6946816(6784K)
		// 		max = -1(-1K),
		// G1 Eden Space=
		// 		init = 27262976(26624K)
		// 		used = 0(0K)
		// 		committed = 28311552(27648K)
		// 		max = -1(-1K)
		// }
		sample = new HashMap<>();
		sample.put("G1 Old Gen", new MemoryUsage(100663296, 437481072, 495976448, 524288000));
		sample.put("Code Cache", new MemoryUsage(2555904, 2136448, 2555904, 251658240));
		sample.put("G1 Survivor Space", new MemoryUsage(0, 0, 0, -1));
		sample.put("Compressed Class Space", new MemoryUsage(0, 754888, 917504, 1073741824));
		sample.put("Metaspace", new MemoryUsage(0, 6484248, 6484248, -1));
		sample.put("G1 Eden Space", new MemoryUsage(27262976, 0, 28311552, -1));

	}

	@Test
	void parseMemory() {
		Map<MemoryPoolType, Memory> usages = new OpenJDKEventSource()
				.parseMemory(sample);

		assertThat(usages.keySet())
				.containsExactlyInAnyOrder(MemoryPoolType.OLD, MemoryPoolType.CODE_CACHE, MemoryPoolType.SURVIVOR,
						MemoryPoolType.COMPRESSED_CLASS_SPACE, MemoryPoolType.METASPACE, MemoryPoolType.YOUNG);
		assertThat(usages.get(MemoryPoolType.OLD).getUsed()).isEqualTo(437481072);
		assertThat(usages.get(MemoryPoolType.CODE_CACHE).getUsed()).isEqualTo(2136448);
		assertThat(usages.get(MemoryPoolType.SURVIVOR).getUsed()).isEqualTo(0);
		assertThat(usages.get(MemoryPoolType.COMPRESSED_CLASS_SPACE).getUsed()).isEqualTo(754888);
		assertThat(usages.get(MemoryPoolType.METASPACE).getUsed()).isEqualTo(6484248);
		assertThat(usages.get(MemoryPoolType.YOUNG).getUsed()).isEqualTo(0);
	}

}