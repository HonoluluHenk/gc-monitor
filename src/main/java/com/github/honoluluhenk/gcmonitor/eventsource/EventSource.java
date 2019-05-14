package com.github.honoluluhenk.gcmonitor.eventsource;

import com.github.honoluluhenk.gcmonitor.eventlistener.EventListener;

public interface EventSource<T> {

	void start();

	void stop();

	void addEventListener(EventListener<T> listener);

	boolean removeEventListener(EventListener<T> listener);
}
