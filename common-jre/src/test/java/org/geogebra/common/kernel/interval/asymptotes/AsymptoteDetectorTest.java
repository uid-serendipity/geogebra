package org.geogebra.common.kernel.interval.asymptotes;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBoundsMock;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.util.debug.Log;
import org.junit.Test;

public class AsymptoteDetectorTest extends BaseUnitTest {
	private static AsymptoteDetector detector;

	@Test
	public void oneAsymptote() {
		shouldBeAsymptote("(1/x)-(1/x)", 1);
	}

	@Test
	public void shouldBeNoPeak() {
		shouldBeAsymptote("1/x * 1/(|csc(x)|)", 0);
	}

	private void shouldBeAsymptote(String desctription, int expected) {
		createDetectorFor(desctription);
		assertEquals(expected, detector.size());
	}

	private void createDetectorFor(String desctription) {
		GeoFunction function = add(desctription);
		IntervalTuple range =
				new IntervalTuple(new Interval(-10, 10), new Interval(-10, 10));
		EuclidianViewBounds evBounds =
				new EuclidianViewBoundsMock(range, 1920, 1024);
		detector = new AsymptoteDetector(function, evBounds);
		Log.debug(detector);
	}

	@Test
	public void multipleAsymptotes() {
		shouldBeAsymptote("x ln abs(cos(x))", 6);
	}
}
