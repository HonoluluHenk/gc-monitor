package com.github.honoluluhenk.gcmonitor.expiry;

import java.io.Serializable;

/**
 * Useful for debugging, else <strong>this might create a memory leak</strong>!
 * Never expire any data.
 */
public class NullExpiry<T extends Serializable> implements Expiry<T> {
	@Override
	public boolean isExpired(Params<T> params) {
		return false;
	}
}
