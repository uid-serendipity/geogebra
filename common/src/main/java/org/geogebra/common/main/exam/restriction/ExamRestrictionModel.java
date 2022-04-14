package org.geogebra.common.main.exam.restriction;

import java.util.Arrays;
import java.util.List;

public class ExamRestrictionModel {
	private List<String> appCodes;
	void setAppCodes(String... list) {
		appCodes = Arrays.asList(list);
	}

	public boolean isAppRestricted(String appCode) {
		return appCodes.contains(appCode);
	}

	public String getDefaultAppCode() {
		return appCodes.get(0);
	}
}
