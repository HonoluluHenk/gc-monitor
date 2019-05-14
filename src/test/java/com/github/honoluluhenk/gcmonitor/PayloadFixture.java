package com.github.honoluluhenk.gcmonitor;

import java.io.Serializable;
import java.util.Objects;

public class PayloadFixture implements Serializable {
	private static final long serialVersionUID = 2198661422575915824L;

	private final String name;

	public PayloadFixture(String name) {
		this.name = Objects.requireNonNull(name);
	}

	@Override
	public String toString() {
		return "PayloadFixture{name=" + name + '}';
	}
}
