import java.util.HashMap;
import java.util.Map;

import com.github.honoluluhenk.gcmonitor.GCOverflowDetector;
import com.github.honoluluhenk.gcmonitor.detection.overflow.AverageUsageAboveThreshold;
import com.github.honoluluhenk.gcmonitor.eventsource.openjdk.OpenJDKEventSource;
import com.github.honoluluhenk.gcmonitor.expiry.CollectionSizeExpiry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tester {

	private static final Logger LOG = LoggerFactory.getLogger(Tester.class);

	private static final Map<Integer, Object> LEAK = new HashMap<>();

	@SuppressWarnings("CallToSystemGC")
	public static void main(String[] args) {
		long quarter = Runtime.getRuntime().maxMemory() / 4;

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
			LEAK.put(i, new Object());
		}
	}

	private static void fillMem(long numBytes) {
		int offset = LEAK.size() + 1;
		if (numBytes > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Cannot allocate " + numBytes + " bytes");
		}
		//noinspection NumericCastThatLosesPrecision
		LEAK.put(offset, new byte[(int) numBytes]);
	}
}
