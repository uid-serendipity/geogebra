/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * MyError.java
 *
 * Created on 04. Oktober 2001, 09:29
 */

package geogebra.common.main;

/**
 * 
 * @author Markus
 * @version
 */
public class MyError extends java.lang.Error {

	private static final long serialVersionUID = 1L;
	
	protected AbstractApplication app;
	private String[] strs;
	private String commandName = null;

	/** Creates new MyError */
	public MyError(AbstractApplication app, String errorName) {
		// set localized message
		super(errorName);
		this.app = app;
	}

	public MyError(AbstractApplication app, String errorName, String commandName) {
		// set localized message
		super(errorName);
		this.app = app;
		this.commandName = commandName;
	}

	public MyError(AbstractApplication app, String[] strs) {
		this.app = app;
		// set localized message
		this.strs = strs;
	}

	public String getcommandName() {
		return commandName;
	}

	@Override
	public String getLocalizedMessage() {
		if (strs == null) {
			return app.getError(getMessage());
		}
		StringBuilder sb = new StringBuilder();
		sb.append(app.getError(strs[0]) + "\n");
		for (int i = 1; i < strs.length; i++) {
			sb.append(app.getError(strs[i]) + " ");
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(getClass().toString() + ": ");
		if (strs == null)
			sb.append(app.getError(getMessage()));
		else {
			for (int i = 0; i < strs.length; i++) {
				sb.append(app.getError(strs[i]) + " : ");
			}
		}
		return sb.toString();
	}

}
