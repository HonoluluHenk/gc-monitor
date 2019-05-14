package com.github.honoluluhenk.gcmonitor.eventlistener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

class EventListenersTest {
	private final EventListeners<Object> listeners = new EventListeners<>();
	@SuppressWarnings("unchecked")
	private final EventListener<Object> listenerSpy = Mockito.spy(EventListener.class);

	@Nested
	class EmtpyTest {

		@Test
		void addListener_adds() {
			listeners.addListener(listenerSpy);

			assertThat(listeners.getListeners()).containsExactly(listenerSpy);
		}

		@Test
		void removeListener_remove_works_on_empty() {
			boolean removed = listeners.removeListener(listenerSpy);

			assertThat(removed).isFalse();
			assertThat(listeners.getListeners()).isEmpty();
		}

		@Test
		void removeAll_clears_on_empty() {
			listeners.removeAllListeners();

			assertThat(listeners.getListeners()).isEmpty();
		}

	}

	@Nested
	class OneListenerTest {
		@SuppressWarnings("unchecked")
		private final EventListener<Object> otherSpy = Mockito.spy(EventListener.class);

		@BeforeEach
		void beforeEach() {
			Mockito.doAnswer((ignored) -> Void.class)
					.when(listenerSpy).accept(any());

			listeners.addListener(listenerSpy);
		}

		@Test
		void addListener_adds() {
			listeners.addListener(otherSpy);
			assertThat(listeners.getListeners()).containsExactly(listenerSpy, otherSpy);
		}

		@Test
		void removeListener_removes_one() {
			boolean removed = listeners.removeListener(listenerSpy);

			assertThat(removed).isTrue();
			assertThat(listeners.getListeners()).isEmpty();
		}

		@Test
		void removeAll_clears() {
			listeners.removeAllListeners();

			assertThat(listeners.getListeners()).isEmpty();
		}

		@Test
		void notifyListeners() {
			Mockito.doAnswer((ignored) -> Void.class)
					.when(listenerSpy).accept(any());

			Object arg = new Object();
			listeners.notifyListeners(arg);

			Mockito.verify(listenerSpy, times(1))
					.accept(arg);
		}
	}

	@Nested
	class MultipleListenersTest {
		@SuppressWarnings("unchecked")
		private final EventListener<Object> otherSpy = Mockito.spy(EventListener.class);

		@BeforeEach
		void beforeEach() {
			Mockito.doAnswer((ignored) -> Void.class)
					.when(listenerSpy).accept(any());
			Mockito.doAnswer((ignored) -> Void.class)
					.when(otherSpy).accept(any());

			listeners.addListener(listenerSpy);
			listeners.addListener(otherSpy);
		}

		@Test
		void removeListener_removes_one() {
			boolean removed = listeners.removeListener(listenerSpy);

			assertThat(removed).isTrue();
			assertThat(listeners.getListeners()).containsExactly(otherSpy);
		}

		@Test
		void removeListener_removes_other() {
			boolean removed = listeners.removeListener(otherSpy);

			assertThat(removed).isTrue();
			assertThat(listeners.getListeners()).containsExactly(listenerSpy);
		}

		@Test
		void removeAll_clears() {
			listeners.removeAllListeners();

			assertThat(listeners.getListeners()).isEmpty();
		}

		@Test
		void notifyListeners() {

			Object arg = new Object();
			listeners.notifyListeners(arg);

			Mockito.verify(listenerSpy, times(1))
					.accept(arg);
			Mockito.verify(otherSpy, times(1))
					.accept(arg);
		}

	}

	@Nested
	class AddListenerTwiceTest {

		@BeforeEach
		void beforeEach() {
			Mockito.doAnswer((ignored) -> Void.class)
					.when(listenerSpy).accept(any());

			listeners.addListener(listenerSpy);
			listeners.addListener(listenerSpy);
		}

		@Test
		void addListener_adds_the_same_twice() {
			assertThat(listeners.getListeners()).containsExactly(listenerSpy, listenerSpy);
		}

		@Test
		void removeListener_removes_one() {
			boolean removed = listeners.removeListener(listenerSpy);

			assertThat(removed).isTrue();
			assertThat(listeners.getListeners()).containsExactly(listenerSpy);
		}

		@Test
		void removeAll_clears() {
			listeners.removeAllListeners();

			assertThat(listeners.getListeners()).isEmpty();
		}

		@Test
		void notifyListeners() {

			Object arg = new Object();
			listeners.notifyListeners(arg);

			Mockito.verify(listenerSpy, times(2))
					.accept(arg);
		}
	}

	@Nested
	class GeneralTest {

		@Test
		void addListener_does_not_call_accept() {
			listeners.addListener(listenerSpy);

			Mockito.verify(listenerSpy, times(0))
					.accept(any());
		}

	}

	@Nested
	class ExceptionHandlingTest {

		@Test
		@SuppressWarnings("unchecked")
		void continue_if_throwing() {
			EventListener<Object> listenerBefore = Mockito.spy(EventListener.class);
			EventListener<Object> listenerAfter = Mockito.spy(EventListener.class);

			Mockito.doAnswer(ignored -> Void.class)
					.when(listenerBefore).accept(any());
			listeners.addListener(listenerBefore);
			Mockito.doThrow(new IllegalStateException("test mock throws"))
					.when(listenerSpy).accept(any());
			listeners.addListener(listenerSpy);
			Mockito.doAnswer(ignored -> Void.class)
					.when(listenerAfter).accept(any());
			listeners.addListener(listenerAfter);

			String event = "Hello World";
			listeners.notifyListeners(event);

			Mockito.verify(listenerBefore, times(1))
					.accept(event);
			Mockito.verify(listenerSpy, times(1))
					.accept(event);
			Mockito.verify(listenerAfter, times(1))
					.accept(event);

		}
	}
}