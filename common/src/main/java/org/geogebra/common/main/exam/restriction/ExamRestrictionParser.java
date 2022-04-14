package org.geogebra.common.main.exam.restriction;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.main.Localization;

public class ExamRestrictionParser {
	private Localization loc;

	public ExamRestrictionParser(Localization loc) {

		this.loc = loc;
	}

	public List<String> getAppCodes() {
		return Arrays.asList("cas", "3d");
	}
}
