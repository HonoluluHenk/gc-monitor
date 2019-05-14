package com.github.honoluluhenk.gcmonitor.detection.overflow;

import java.io.Serializable;

import static java.util.Objects.requireNonNull;

/**
 * The result of the {@link OverflowDetector}.
 * Indicates if and why (or why not)a possible  overflow got detected.
 */
public class Overflow implements Serializable {
	private static final long serialVersionUID = 7559117468370513304L;

	private final Status status;
	private final String reason;

	public Overflow(Status status, String reason) {
		this.status = requireNonNull(status);
		this.reason = requireNonNull(reason);
	}

	/**
	 * Build an {@link Status#OK} instance.
	 */
	@SuppressWarnings("PMD.ShortMethodName")
	public static Overflow ok(String reason) {
		return new Overflow(Status.OK, reason);
	}

	/**
	 * Build an Overflow error instance.
	 */
	public static Overflow overflow(String reason) {
		return new Overflow(Status.OVERFLOW, requireNonNull(reason));
	}

	public Status getStatus() {
		return status;
	}

	public String getReason() {
		return reason;
	}

	@Override
	public String toString() {
		return String.format("Overflow{status=%s, reason='%s'}",
				status, reason);
	}
}
