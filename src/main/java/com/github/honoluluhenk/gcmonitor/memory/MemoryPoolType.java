package com.github.honoluluhenk.gcmonitor.memory;

import java.lang.management.GarbageCollectorMXBean;

/**
 * Memory pool types abstracted away from different JVM implementations.
 *
 * <p>
 * Different JVM vendors (Oracle, OpenJDK, IBM, ...) may specify memory pool names at will,
 * see {@link GarbageCollectorMXBean#getMemoryPoolNames()}.
 * </p>
 * <p>
 * Our library categorizes these pools based on longevity ({@link #YOUNG}/{@link #SURVIVOR}/
 * {@link #OLD}) and/or technical pools ({@link #METASPACE}/{@link #CODE_CACHE}/{@link #COMPRESSED_CLASS_SPACE}).
 * </p>
 */
public enum MemoryPoolType {
	/**
	 * GC young generation.
	 */
	YOUNG,
	/**
	 * GC survivor space.
	 */
	SURVIVOR,
	/**
	 * GC old generation.
	 */
	OLD,
	/**
	 * JVM metaspace/PermGen.
	 */
	METASPACE,
	/**
	 * JVM code cache.
	 */
	CODE_CACHE,
	/**
	 * JVM compressed class space.
	 */
	COMPRESSED_CLASS_SPACE
}
