package org.geogebra.common.kernel.interval.asymptotes;

import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.kernel.interval.samplers.FunctionSampler;

public class AsymptoteDetector {

	private final FunctionSampler sampler;
	private List<IntervalTuple> asymptotes;

	public AsymptoteDetector(GeoFunction function, EuclidianViewBounds bounds) {
		IntervalTuple range = new IntervalTuple(bounds.domain(), bounds.range());
		sampler = new FunctionSampler(function, range, bounds);
		sampler.disableAsymtoteProcessing();
		process();
	}

	public void process() {
		asymptotes = getAsymptotes(sampler.result());
	}

	public int size() {
		return asymptotes.size();
	}

	private List<IntervalTuple> getAsymptotes(IntervalTupleList tuples) {
		return tuples.stream().filter(t -> t.y().hasInfinity()).collect(Collectors.toList());
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
