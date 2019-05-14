package com.github.honoluluhenk.gcmonitor.gc;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

class GCCollectionTest {

	@Test
	void major() {
		assertAll(
				() -> Assertions.assertThat(GCCollection.MAJOR.isMajor()).isTrue(),
				() -> Assertions.assertThat(GCCollection.MINOR.isMajor()).isFalse()
		);
	}
}