package org.geogebra.web.html5.util;

public interface AttributeProvider {

	String getAttr(String attribute);

	boolean hasAttr(String attribute);

	void removeAttribute(String attribute);

	void setAttribute(String attribute, String value);
}
