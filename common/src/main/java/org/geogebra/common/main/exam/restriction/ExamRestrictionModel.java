package org.geogebra.common.main.exam.restriction;

import java.util.ArrayList;
import java.util.List;

public class ExamRestrictionModel {
	List<String> appCodes = new ArrayList<>();

	public ExamRestrictionModel(ExamRestrictionParser parser) {
		this.appCodes = parser.getAppCodes();
	}

	public boolean isAppRestricted(String appCode) {
		return appCodes.contains(appCode);
	}
}
