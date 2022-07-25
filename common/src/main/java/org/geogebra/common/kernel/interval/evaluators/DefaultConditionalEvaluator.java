package org.geogebra.common.kernel.interval.evaluators;


import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.kernel.interval.samplers.ConditionalSampler;
import org.geogebra.common.util.debug.Log;

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
		joinContinousPieces(result);
		return result;
	}

	private void joinContinousPieces(IntervalTupleList result) {
		List<Integer> borders = pieceBorders(result);
		for (int i = 0; i < borders.size(); i++) {
			int tupleIndex = borders.get(i);
			IntervalTuple tuple = result.get(tupleIndex);
			int piece = tuple.piece();
			int startIndex = tupleIndex + 1;
			IntervalTuple startTuple = result.get(startIndex);
			startTuple.y().setLow(evaluateWithSampler(new Interval(startTuple.x().getLow()), piece).getLow());
			int endIndex = i + 1 < borders.size() ? borders.get(i + 1) : result.count();

			for (int j = startIndex; j < endIndex; j++) {
				result.get(j).setPiece(piece);
			}
		}
	}

	private Interval evaluateWithSampler(Interval x, int index) {
		return samplers.get(index).evaluatedValue(x);
	}

	private List<Integer> pieceBorders(IntervalTupleList tuples) {
		List<Integer> list = new ArrayList<>();
		for (int i = 1; i < tuples.count(); i++) {
			IntervalTuple prev = tuples.get(i - 1);
			IntervalTuple current = tuples.get(i);
			if (prev.piece() != current.piece() && isClose(prev, current)) {
				Log.debug(prev.y() + " " + current.y());
				Log.debug("diff: " + (current.y().subtract(prev.y())));
				list.add(i - 1);
			}
		}
		return list;
	}

	private IntervalTuple evaluateTuple(Interval x) {
		IntervalTuple prev = null;
		IntervalTuple current = null;
		for (int i = 0; i < samplers.size(); i++) {
			ConditionalSampler sampler = samplers.get(i);
			if (sampler.isAccepted(x)) {
				return sampler.evaluateTuple(x);
			}
		}
		return IntervalTuple.NULL_TUPLE;
	}


	private boolean isClose(IntervalTuple prev, IntervalTuple current) {
		if (prev == null) {
			return false;
		}
		Interval y = new Interval(current.y());
		Interval subtract = y.subtract(prev.y());
		return subtract.getLow() < 1E-7;
	}
}


