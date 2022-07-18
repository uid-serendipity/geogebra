package org.geogebra.common.kernel.interval.evaluators;

import java.util.List;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.kernel.interval.samplers.ConditionalSampler;

public class DefaultConditionalEvaluator implements IntervalEvaluatable {

	private List<ConditionalSampler> samplers;

	public DefaultConditionalEvaluator(List<ConditionalSampler> samplers) {
		this.samplers = samplers;
	}

	@Override
	public IntervalTupleList evaluate(double low, double high) {
		return evaluate(new Interval(low, high));
	}

	@Override
	public IntervalTupleList evaluate(Interval x) {
		for (ConditionalSampler sampler : samplers) {
			if (sampler.isAccepted(x)) {
				return sampler.evaluate(x);
			}
		}
		return IntervalTupleList.emptyList();
	}


	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		IntervalTupleList result = new IntervalTupleList();
		space.values().forEach(x -> result.add(evaluateTuple(x)));
		return result;
	}

	private IntervalTuple evaluateTuple(Interval x) {
		for (ConditionalSampler sampler : samplers) {
			if (sampler.isAccepted(x)) {
				return sampler.evaluateTuple(x);
			}
		}
		return IntervalTuple.NULL_TUPLE;
	}
}
