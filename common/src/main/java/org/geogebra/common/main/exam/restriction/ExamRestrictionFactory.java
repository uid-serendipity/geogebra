package org.geogebra.common.main.exam.restriction;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;

import org.geogebra.common.main.Localization;

public class ExamRestrictionFactory {
	public static RestrictExam create(Localization loc) {
		ExamRestrictionModel model = createModel(loc);
		return new RestrictExamImpl(model);
	}

	private static ExamRestrictionModel createModel(Localization loc) {
		return createDummyRestrictionModel();
	}

	private static ExamRestrictionModel createDummyRestrictionModel() {
		ExamRestrictionModel model = new ExamRestrictionModel();
		model.setAppCodes(CAS_APPCODE, G3D_APPCODE);
		return model;
	}

}
