package org.geogebra.common.kernel.interval.samplers;

import static org.geogebra.common.euclidian.plot.interval.PlotterUtils.hdBounds;
import static org.geogebra.common.euclidian.plot.interval.PlotterUtils.newRange;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.junit.Test;

public class ConditionalPiecesTest extends BaseUnitTest {

	@Test
	public void conditionalsWithNoCommonPointsShouldBeMorePieces() {
		pieceShouldBe(2, "a=If(x < 0, -4, x^2)");
		pieceShouldBe(3, "a=If(x < 0, x, 1 < x < 4, tan(x), x > 4.5, sin(x))");
	}

	@Test
	public void conditionalsWithCommonEndPointsShouldBeOnePiece() {
//		pieceShouldBe(1, "a=If(x < 0, x, x)");
		pieceShouldBe(1, "a=If(x < 1, x, x^2)");
	}

	@Test
	public void conditionalsWithNonCommonEndPointsShouldBeTwoPiece() {
		pieceShouldBe(2, "a=If(x < 1, x, x^2 + 2)");
	}

	private void pieceShouldBe(int expected, String command) {
		GeoFunction function = add(command);
		IntervalTuple range = newRange(-5, 5, -5, 5);
		IntervalFunctionSampler sampler
				= new ConditionalFunctionSampler(function, range, hdBounds(range));

		IntervalTupleList tuples = sampler.result();
		assertEquals(expected,	countOfPieces(tuples));
	}

	private long countOfPieces(IntervalTupleList tuples) {
		return tuples.stream().mapToInt(IntervalTuple::piece).distinct().count();
	}


}
