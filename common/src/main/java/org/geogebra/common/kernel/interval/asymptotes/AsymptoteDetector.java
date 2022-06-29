package org.geogebra.common.kernel.interval.asymptotes;

import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.kernel.interval.samplers.FunctionSampler;
import org.geogebra.common.util.debug.Log;

public class AsymptoteDetector {

	public static final double MAX_INVERTED_LENGHT = 1E7;
	private final FunctionSampler sampler;
	private List<IntervalTuple> asymptotes;
	private EuclidianViewBounds bounds;

	public AsymptoteDetector(GeoFunction function, EuclidianViewBounds bounds) {
		this.bounds = bounds;
		IntervalTuple range = new IntervalTuple(bounds.domain(), bounds.range());
		sampler = new FunctionSampler(function, range, bounds);
		sampler.disableAsymtoteProcessing();
		update();
	}

	public void update() {
		IntervalTuple range = new IntervalTuple(bounds.domain(), bounds.range());
		sampler.update(range);
		asymptotes = filterAsymptotes(sampler.result());
		Log.debug(this.toString());
	}

	public int size() {
		return asymptotes.size();
	}

	private List<IntervalTuple> filterAsymptotes(IntervalTupleList tuples) {
		return tuples.stream()
				.filter(tuple -> possibleAsymtote(tuple.y()))
				.collect(Collectors.toList());
	}

	private boolean possibleAsymtote(Interval y) {
		return y.hasInfinity() && !y.isInfiniteSingleton()
				&& !(y.isHalfPositiveInfinity() || y.isHalfNegativeInfinity())
				|| (y.isInverted() && y.getLength() < bounds.getYmax() - bounds.getYmin());
	}

	public List<IntervalTuple> getAsymptotes() {
		return asymptotes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		asymptotes.forEach(t -> {
			sb.append(t.toString());
			sb.append("\n");
		});
		return sb.toString();
	}
}
