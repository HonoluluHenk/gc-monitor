package com.github.honoluluhenk.gcmonitor.expiry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import com.github.honoluluhenk.gcmonitor.PayloadFixture;

/**
 * Expire all data given in the constructor. See {@link Collection#contains(Object)} for comparison operation.
 */
public class FakeExpiry implements Expiry<PayloadFixture> {
	private final Collection<PayloadFixture> expired;

	public FakeExpiry(PayloadFixture... expired) {
		this.expired = Arrays.asList(Objects.requireNonNull(expired));
	}

	@Override
	public boolean isExpired(Params<PayloadFixture> params) {
		return expired.contains(params.getTimedData().getData());
	}
}
