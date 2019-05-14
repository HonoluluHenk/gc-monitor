package com.github.honoluluhenk.gcmonitor.memory;

import java.io.Serializable;
import java.lang.management.MemoryUsage;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static java.util.Objects.requireNonNull;

/**
 * A wapper for {@link MemoryUsage} with easier accessor methods.
 */
@SuppressFBWarnings("OI_OPTIONAL_ISSUES_PRIMITIVE_VARIANT_PREFERRED")
public class Memory implements Serializable {
	private static final long serialVersionUID = -7179707467202958590L;

	private final long init;
	private final long used;
	private final long committed;
	private final long max;

	/**
	 * See {@link MemoryUsage} for more details.
	 */
	public Memory(long init, long used, long committed, long max) {
		this(new MemoryUsage(init, used, committed, max));
	}

	private Memory(MemoryUsage memoryUsage) {
		// MemoryUsage only contains valid data;
		this.init = memoryUsage.getInit();
		this.used = memoryUsage.getUsed();
		this.committed = memoryUsage.getCommitted();
		this.max = memoryUsage.getMax();
	}

	/**
	 * Create the wrapper..
	 */
	public static Memory wrap(MemoryUsage memoryUsage) {
		requireNonNull(memoryUsage);

		return new Memory(memoryUsage);
	}

	/**
	 * See {@link MemoryUsage#getInit()}, returns {@link Optional#empty()} instead of -1.
	 */
	public Optional<Long> getInit() {
		return minusOneAsEmpty(init);
	}

	/**
	 * See {@link MemoryUsage#getUsed()}.
	 */
	public long getUsed() {
		return used;
	}

	/**
	 * See {@link MemoryUsage#getMax()}, returns {@link Optional#empty()} instead of -1.
	 */
	public Optional<Long> getMax() {
		return minusOneAsEmpty(max);
	}

	/**
	 * See {@link MemoryUsage#getCommitted()}.
	 */
	public long getCommitted() {
		return committed;
	}

	@SuppressFBWarnings(value = "OI_OPTIONAL_ISSUES_PRIMITIVE_VARIANT_PREFERRED",
			justification = "OptionalLong does not support map() which is stupid")
	private Optional<Long> minusOneAsEmpty(long value) {
		if (value == -1) {
			return Optional.empty();
		}
		return Optional.of(value);
	}

	@Override
	public String toString() {
		return "Memory{" + new MemoryUsage(init, used, committed, max) + '}';
	}
}
