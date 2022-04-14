package org.geogebra.common.main.exam.restriction;

public interface Restrictable {
	void restrict(ExamRestrictionModel model);
	void permit();
}
