package com.github.honoluluhenk.gcmonitor.eventsource.openjdk;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.CMS_OLD_GEN;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.CODE_CACHE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.COMPRESSED_CLASS_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.G1_EDEN_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.G1_OLD_GEN;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.G1_SURVIVOR_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.METASPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PAR_EDEN_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PAR_SURVIVOR_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PS_EDEN_SPACE;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PS_OLD_GEN;
import static com.github.honoluluhenk.gcmonitor.eventsource.openjdk.MemoryPool.PS_SURVIVOR_SPACE;
import static com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType.OLD;
import static com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType.SURVIVOR;
import static com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType.YOUNG;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class MemoryPoolTest {

	@SuppressWarnings("PublicField")
	private static class Data {
		public final MemoryPool pool;
		public final String name;
		public final MemoryPoolType poolType;

		private Data(MemoryPool pool, String name, MemoryPoolType poolType) {
			this.pool = pool;
			this.name = name;
			this.poolType = poolType;
		}
	}

	private static final List<Data> DATA = asList(
			new Data(G1_OLD_GEN, "G1 Old Gen", OLD),
			new Data(G1_SURVIVOR_SPACE, "G1 Survivor Space", SURVIVOR),
			new Data(G1_EDEN_SPACE, "G1 Eden Space", YOUNG),
			//
			new Data(PS_EDEN_SPACE, "PS Eden Space", YOUNG),
			new Data(PS_SURVIVOR_SPACE, "PS Survivor Space", SURVIVOR),
			new Data(PS_OLD_GEN, "PS Old Gen", OLD),
			//
			new Data(PAR_EDEN_SPACE, "Par Eden Space", YOUNG),
			new Data(PAR_SURVIVOR_SPACE, "Par Survivor Space", SURVIVOR),
			new Data(CMS_OLD_GEN, "CMS Old Gen", OLD),
			//
			new Data(METASPACE, "Metaspace", MemoryPoolType.METASPACE),
			new Data(CODE_CACHE, "Code Cache", MemoryPoolType.CODE_CACHE),
			new Data(COMPRESSED_CLASS_SPACE, "Compressed Class Space", MemoryPoolType.COMPRESSED_CLASS_SPACE)
	);

	@Test
	public void testParsing_unsupported() {
		String invalid = "not a valid pool name";

		assertThat(MemoryPool.fromPoolName(invalid))
				.isNotPresent();
		assertThat(MemoryPool.isSupported(invalid))
				.isFalse();
	}

	@Test
	public void setup_contains_all_values() {
		assertThat(DATA.size()).isEqualTo(MemoryPool.values().length);
	}

	public static Stream<Arguments> params() {
		return DATA.stream()
				.map(e -> arguments(e.pool, e.name, e.poolType));
	}

	@ParameterizedTest
	@MethodSource("params")
	public void testParsing(MemoryPool pool, String poolName, MemoryPoolType poolType) {
		Optional<MemoryPool> actual = MemoryPool.fromPoolName(poolName);

		Assertions.assertThat(actual)
				.as("pool: " + pool.name())
				.isPresent()
				.containsSame(pool)
				.get()
				.as("properties of " + pool.name())
				.returns(poolName, MemoryPool::getPoolName)
				.returns(poolType, MemoryPool::getPoolType)
				.returns(true, p -> MemoryPool.isSupported(poolName));
	}

}