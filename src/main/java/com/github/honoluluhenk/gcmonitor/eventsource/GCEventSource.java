package com.github.honoluluhenk.gcmonitor.eventsource;

import com.github.honoluluhenk.gcmonitor.gc.GCEvent;
import com.github.honoluluhenk.gcmonitor.timeddata.TimedData;

/**
 * Base class to mark requirements for Garbage Collector events ({@link GCEvent}).
 */
public interface GCEventSource extends EventSource<TimedData<GCEvent>> {
}
