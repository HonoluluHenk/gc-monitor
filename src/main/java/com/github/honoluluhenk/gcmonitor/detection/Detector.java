package com.github.honoluluhenk.gcmonitor.detection;

import java.io.Serializable;

import com.github.honoluluhenk.gcmonitor.timeddata.TimeSeries;

/**
 * Implementations determine if the given timeSeries matches a certain condition (e.g.: some overflow).
 */
@FunctionalInterface
public interface Detector<R extends Serializable, T extends Serializable> {
	/**
	 * The actual algorithm.
	 *
	 * Please note that the detector <b>must</b> return a not null value!
	 */
	R detect(TimeSeries<T> timeSeries);
}
