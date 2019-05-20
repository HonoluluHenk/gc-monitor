# gc-monitor

[![Build Status](https://travis-ci.org/HonoluluHenk/gcmonitor.svg?branch=develop)][Build Status Link]
[![Maven Central](
https://img.shields.io/maven-central/v/com.github.honoluluhenk.gcmonitor/gcmonitor.svg?label=Maven%20Central
)][Maven Central Link]

[Build Status Link]: https://travis-ci.org/HonoluluHenk/gcmonitor
[Maven Central Link]: https://search.maven.org/search?q=g:%22com.github.honoluluhenk.gcmonitor%22%20AND%20a:%22gcmonitor%22

Helps you detecting Garbage Collector overflows before they trash, crash or overwhelm the JVM.

# Usage

Maven dependency:
```xml
<dependency>
  <groupId>com.github.honoluluhenk.gcmonitor</groupId>
  <artifactId>gcmonitor</artifactId>
  <version>1.0.5</version>
</dependency>

```

```java
// this will collect data from an OpenJDK compatible JVM.
// Information of the last 5 GC collections will get
// used for overflow detection:
// If the last 3 memory usage measures are above 80%,
// detection will return an overflow.
GCOverflowDetector detector = new GCOverflowDetector(
    new OpenJDKEventSource(),
    new CollectionSizeExpiry<>(5),
    new UsageAboveThreshold(3, 80)
);

// actually start monitoring
detector.start();

// do some detection.
Overflow overflow = detector.detect();
// detect() may be called asynchronously and also multiple times.
// This allows implementing an external healthcheck easily.
if (overflow.getStatus() == Status.OVERFLOW) {
	System.out.println("Got overflow, reason: " + overflow.getReason());
}

// cleanup
detector.stop();

```

The detector also supports re-use by calling `detector.start()` and `detector.stop()` repeatedly.


# GCOverflowDetector

Class: `GCOverflowDetector`

This is the main workhorse.

Tries to detect JVM memory overflows before they actually happen.



Uses Event Sources for memory usage data acquisition, Expiries for data removal and Detectors for the actual detection of memory overflows.



# Event Sources

## OpenJDKEventSource

Class: `OpenJDKEventSource`

Supports OpenJDK and compatible (Oracle, IBM, ...) JVMs.



# Expiries

# CollectionSizeExpiry

Class: `CollectionSizeExpiry`

Retains a maximum amount of measures. Older measures get expired first.



## DurationExpiry

Class: `DurationExpiry`

Keeps measures that are younger than some `TemporalAmount`.



## NullExpiry

Class: `NullExpiry`

Never expires anything. Mainly used for development since never expiring anything means a memory leak!



# Detectors

# UsageAboveThreshold

Class: `UsageAboveThreshold`

Takes N (1 <= N <= Integer.MAX_VALUE) memory usage readings. If *all* of these readings are above a threshold, an overflow is detected.



## AverageUsageAboveThreshold

Class: `AverageUsageAboveThreshold`

Works like UsageAboveThreshold but averages the memory usage over N memory usage readings.

If the averaged usage is above a threshold, an overflow is detected.


# Information for developers
## Release checklist

* check/update dependency version in README.md

## Release

Bump version numbers, build with all checks enabled and - if successful - push everything:
```bash
mvn -U jgitflow:release-start jgitflow:release-finish
# and then wait for travis...
```
