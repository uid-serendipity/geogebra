package org.geogebra.common.main.exam.restriction;

public interface RestrictExam {
	void enable();
	void disable();
	void register(Restrictable item);
}
