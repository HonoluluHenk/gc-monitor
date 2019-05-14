# gc-monitor

Helps you detecting Garbage Collector overflows before they trash, crash or overwhelm the JVM.



# Usage

```java
// this will collect data from an OpenJDK compatible JVM.
// Information of the last 5 GC collections will get
// used for overflow detection:
// If the last 3 memory usage measures are above 80%,
// detection will return an overflow.
GCOverflowDetector detector = new GCOverflowDetector(
    new OpexnJDKEventSource(),
    new CollectionSizeExpiry<>(5),
    new UsageAboveThreshold(3, 80)
);

// actually start monitoring
detector.start();

// do some detection.
Overflow overflow = detector.detect();
// detect() may be called asynchronously and also multiple times.
// The tenabled implementing an external healthcheck easily.

// cleanup
detector.stop();

// the detector supports reuse by calling start again:
detector.start();
// ...
detector.stop();
```



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

