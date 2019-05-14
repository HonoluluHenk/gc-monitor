package com.github.honoluluhenk.gcmonitor.detection.overflow;

/**
 * The actual status of the {@link Overflow} detected by the {@link OverflowDetector}.
 */
public enum Status {
	/**
	 * No overflow, everything OK.
	 */
	OK,
	/**
	 * Overlow detected!
	 */
	OVERFLOW
}
