import java.util.ArrayList;
import java.util.List;

import com.github.honoluluhenk.gcmonitor.GCOverflowDetector;
import com.github.honoluluhenk.gcmonitor.detection.overflow.AverageUsageAboveThreshold;
import com.github.honoluluhenk.gcmonitor.eventsource.openjdk.OpenJDKEventSource;
import com.github.honoluluhenk.gcmonitor.expiry.CollectionSizeExpiry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tester {

	private static final Logger LOG = LoggerFactory.getLogger(Tester.class);

	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private static final List<Object> LEAK = new ArrayList<>();

	@SuppressWarnings("CallToSystemGC")
	public static void main(String[] args) {
		long maxMemory = Runtime.getRuntime().maxMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		LOG.info("memory, max: {}, free: {}", maxMemory, freeMemory);

		long quarter = maxMemory / 4;

		//		GCOverflowDetector mon = new GCOverflowDetector(2, 3, 60);
		GCOverflowDetector mon = new GCOverflowDetector(
				new OpenJDKEventSource(),
				new CollectionSizeExpiry<>(3),
				new AverageUsageAboveThreshold(2, 60)
		);

		mon.start();

		fillMem(3 * quarter);

		LOG.info("first gc");
		System.gc();

		LOG.info("====================== START =======================");
		LOG.info("Status: {}", mon.detect());

		//        fillMem();
		//		fillMem(quarter);

		LOG.info("second gc");
		System.gc();

		//		fillMem(quarter);

		LOG.info("second gc");
		System.gc();

		LOG.info("Status: {}", mon.detect());

		//		LOG.info("====================== clear =======================");
		//		LEAK.clear();
		System.gc();
		LOG.info("Status: {}", mon.detect());

		sleep(1000);

		LOG.info("Final Status: {}", mon.detect());

		mon.stop();

	}

	private static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignored
		}
	}

	private static void fillMem() {

		for (int i = 0; i < Integer.MAX_VALUE >>> 8; i++) {
			LEAK.add(i, new Object());
		}
	}

	private static void fillMem(long numBytes) {
		LOG.info("allocating: {}", numBytes);
		if (numBytes > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Cannot allocate " + numBytes + " bytes");
		}
		//noinspection NumericCastThatLosesPrecision
		LEAK.add(new byte[(int) numBytes]);
	}
}
