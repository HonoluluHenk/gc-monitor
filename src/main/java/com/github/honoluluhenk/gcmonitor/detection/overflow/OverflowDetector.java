package com.github.honoluluhenk.gcmonitor.detection.overflow;

import com.github.honoluluhenk.gcmonitor.detection.Detector;
import com.github.honoluluhenk.gcmonitor.memory.Memory;

/**
 * Implementations should detect a memory overflow situation based on memory information.
 */
public interface OverflowDetector extends Detector<Overflow, Memory> {

	default void validate(int expectedReadings) {
		// ignored
	}
}
