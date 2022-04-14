package org.geogebra.common.main.restriction;

public interface RestrictExam {
	void lock();
	void unlock();
	boolean hasSubApp(String appCode);
}
