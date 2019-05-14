package com.github.honoluluhenk.gcmonitor.expiry;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NullExpiryTest {

	@Test
	void expired() {
		assertThat(new NullExpiry<>().isExpired(null)).isFalse();
	}
}