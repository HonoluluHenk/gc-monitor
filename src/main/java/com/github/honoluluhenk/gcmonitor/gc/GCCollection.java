package com.github.honoluluhenk.gcmonitor.gc;

public enum GCCollection {
	MINOR(false),
	MAJOR(true);

	private final boolean major;

	GCCollection(boolean major) {
		this.major = major;
	}

	public boolean isMajor() {
		return major;
	}
}
