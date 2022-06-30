package org.geogebra.common.kernel.interval.asymptotes;

import java.util.ArrayList;
import java.util.List;

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
	private GeoFunction function;
	private EuclidianViewBounds bounds;
	private IntervalTupleList result;

	public AsymptoteDetector(GeoFunction function, EuclidianViewBounds bounds) {
		this.function = function;
		this.bounds = bounds;
		IntervalTuple range = new IntervalTuple(bounds.domain(), bounds.range());
		sampler = new FunctionSampler(function, range, bounds);
		sampler.disableAsymtoteProcessing();
		update();
	}

	public void update() {
		IntervalTuple range = new IntervalTuple(bounds.domain(), bounds.range());
		sampler.update(range);
		result = sampler.result();
		asymptotes = filterAsymptotes(result);
		Log.debug(this.toString());
	}

	public int size() {
		return asymptotes.size();
	}

	private List<IntervalTuple> filterAsymptotes(IntervalTupleList tuples) {
		ArrayList<IntervalTuple> list = new ArrayList<>();
		for (int i = 0; i < tuples.count(); i++) {
			if (possibleAsymtote(i, tuples)) {
				list.add(tuples.get(i));
			}
		}
		return list;
	}

	private boolean possibleAsymtote(int index, IntervalTupleList tuples) {
		Interval y = tuples.get(index).y();
		return y.hasInfinity() && !y.isInfiniteSingleton()
				&& !(y.isHalfPositiveInfinity() || y.isHalfNegativeInfinity())
				|| (y.isInverted()); //&& !isDependencyAtAsymptote(index));
	}

	boolean isDependencyAtAsymptote(int index) {
		IntervalTuple origin = result.get(index);
		if (!origin.y().isInverted()) {
			return false;
		}

		for (int i = index - 1; i > Math.max(0, index - 10) ; i--) {
			if (hasDP(i)) {
				return true;
			}
		}
		for (int i = index + 1; i < Math.min(index + 10, result.count()) ; i++) {
			if (hasDP(i)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasDP(int i) {
		IntervalTuple tuple = result.get(i);
		if (tuple.isInverted() || tuple.isUndefined()) {
			return false;

		}
		double diffLow = Math.abs(function.value(tuple.x().getLow()) - tuple.y().getLow());
		double diffHigh = Math.abs(function.value(tuple.x().getHigh()) - tuple.y().getHigh());
		double dOd = Math.abs(diffHigh - diffLow);
		if (dOd > 1E-4 && dOd != Double.POSITIVE_INFINITY) {
			return true;
		}
		return false;
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
