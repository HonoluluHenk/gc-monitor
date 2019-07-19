package com.github.honoluluhenk.gcmonitor.eventsource.openjdk;

import java.util.Arrays;
import java.util.Optional;

import com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType.OLD;
import static com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType.SURVIVOR;
import static com.github.honoluluhenk.gcmonitor.memory.MemoryPoolType.YOUNG;
import static java.util.Objects.requireNonNull;

public enum MemoryPool {
	/**
	 * G1 Old Generation.
	 */
	G1_OLD_GEN("G1 Old Gen", OLD),
	/**
	 * G1 Survivor Space.
	 */
	G1_SURVIVOR_SPACE("G1 Survivor Space", SURVIVOR),
	/**
	 * G1 Eden Space (young generation).
	 */
	G1_EDEN_SPACE("G1 Eden Space", YOUNG),

	/**
	 * Parallel Sweep Eden Space (young generation).
	 */
	PS_EDEN_SPACE("PS Eden Space", YOUNG),
	/**
	 * Parallel Sweep Survivor Space.
	 */
	PS_SURVIVOR_SPACE("PS Survivor Space", SURVIVOR),
	/**
	 * Parallel Sweep Old Generation.
	 */
	PS_OLD_GEN("PS Old Gen", OLD),

	/**
	 * CMS/ParNew Eden Space.
	 */
	PAR_EDEN_SPACE("Par Eden Space", YOUNG),
	/**
	 * CMS/ParNew Eden Space.
	 */
	PAR_SURVIVOR_SPACE("Par Survivor Space", SURVIVOR),
	/**
	 * CMS/ParNew Eden Space.
	 */
	CMS_OLD_GEN("CMS Old Gen", OLD),

	/**
	 * Metaspace. Where Java bytecode lives.
	 */
	METASPACE("Metaspace", MemoryPoolType.METASPACE),
	/**
	 * Code Cache. Where compiled classes live.
	 */
	CODE_CACHE("Code Cache", MemoryPoolType.CODE_CACHE),
	CODE_HEAP_NON_NMETHODS("CodeHeap 'non-nmethods'", MemoryPoolType.CODE_CACHE),
	CODE_HEAP_NON_PROFILED_NMETHODS("CodeHeap 'non-profiled nmethods'", MemoryPoolType.CODE_CACHE),
	CODE_HEAP_PROFILED_NMETHODS("CodeHeap 'profiled nmethods'", MemoryPoolType.CODE_CACHE),
	/**
	 * If using CompressedOOPS, this is where the compressed bytecode lives.
	 */
	COMPRESSED_CLASS_SPACE("Compressed Class Space", MemoryPoolType.COMPRESSED_CLASS_SPACE);

	private final String poolName;
	private final MemoryPoolType poolType;

	MemoryPool(String poolName, MemoryPoolType poolType) {
		this.poolName = requireNonNull(poolName);
		this.poolType = requireNonNull(poolType);
	}

	public String getPoolName() {
		return poolName;
	}

	public MemoryPoolType getPoolType() {
		return poolType;
	}

	@SuppressFBWarnings(value = "OPM_OVERLY_PERMISSIVE_METHOD", justification = "This is a library project")
	public static Optional<MemoryPool> fromPoolName(String poolName) {
		Optional<MemoryPool> result = Arrays.stream(values())
				.filter(mp -> mp.getPoolName().equals(poolName))
				.findAny();

		return result;
	}

	public static boolean isSupported(String poolName) {
		return fromPoolName(poolName).isPresent();
	}

}
