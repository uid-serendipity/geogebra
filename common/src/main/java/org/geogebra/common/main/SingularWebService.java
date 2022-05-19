package org.geogebra.common.main;

public interface SingularWebService {
	boolean isAvailable();

	String getLocusLib();

	String directCommand(String toString) throws Throwable;

	String getSingularVersionString();

	String getLocusCommand();

	String getTranslatedCASCommand(String s);
}
