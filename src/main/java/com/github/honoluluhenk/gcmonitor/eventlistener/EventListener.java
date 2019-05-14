package com.github.honoluluhenk.gcmonitor.eventlistener;

/**
 * Implementations should handle an event they registered for.
 */
@FunctionalInterface
public interface EventListener<E> {
	/**
	 * Throwing {@link RuntimeException} is allowed but not acted upon (just logged).
	 */
	void accept(E event);
}
