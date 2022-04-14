package org.geogebra.common.main.exam.restriction;

import java.util.ArrayList;
import java.util.List;

public class RestrictExamImpl implements RestrictExam {

	private ExamRestrictionModel model;
	private List<Restrictable> restrictables;

	public RestrictExamImpl(ExamRestrictionModel model) {
		this.model = model;
		restrictables = new ArrayList<>();
	}

	@Override
	public void enable() {
		restrictables.forEach(restrictable -> restrictable.restrict(model));
	}

	@Override
	public void disable() {
		restrictables.forEach(Restrictable::permit);

	}

	@Override
	public void register(Restrictable item) {
		restrictables.add(item);
	}
}
