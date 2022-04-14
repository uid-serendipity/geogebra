package org.geogebra.common.main.restriction;

public class RestrictExamImpl implements RestrictExam {

	private ExamRestrictionModel model;

	public RestrictExamImpl(ExamRestrictionModel model) {
		this.model = model;
	}

	@Override
	public void lock() {

	}

	@Override
	public void unlock() {

	}

	@Override
	public boolean hasSubApp(String appCode) {
		return model.isAppRestricted(appCode);
	}
}
