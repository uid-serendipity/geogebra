package org.geogebra.web.html5.gui.util;

import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.event.dom.client.DoubleClickEvent;
import org.gwtproject.event.dom.client.DoubleClickHandler;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseDownHandler;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseMoveHandler;
import org.gwtproject.event.dom.client.MouseOutEvent;
import org.gwtproject.event.dom.client.MouseOutHandler;
import org.gwtproject.event.dom.client.MouseOverEvent;
import org.gwtproject.event.dom.client.MouseOverHandler;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.MouseUpHandler;
import org.gwtproject.event.dom.client.TouchCancelEvent;
import org.gwtproject.event.dom.client.TouchCancelHandler;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.dom.client.TouchEndHandler;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchMoveHandler;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.event.dom.client.TouchStartHandler;

/**
 * Use this class when you ONLY need stopPropagation() & preventDefault(), being
 * static, this might spare creations of small no-name inline classes
 * 
 * @author Arpad
 */
public final class CancelEvents implements MouseDownHandler,
		MouseUpHandler, MouseOverHandler, MouseOutHandler, MouseMoveHandler,
		ClickHandler, DoubleClickHandler, TouchStartHandler, TouchEndHandler,
		TouchMoveHandler, TouchCancelHandler {

	public static final CancelEvents INSTANCE = new CancelEvents();

	private CancelEvents() {
		super();
	}

	@Override
	public void onClick(ClickEvent ce) {
		ce.preventDefault();
		ce.stopPropagation();
	}

	@Override
	public void onDoubleClick(DoubleClickEvent me) {
		me.preventDefault();
		me.stopPropagation();
	}

	@Override
	public void onMouseDown(MouseDownEvent me) {
		me.preventDefault();
		me.stopPropagation();
	}

	@Override
	public void onMouseUp(MouseUpEvent mue) {
		mue.preventDefault();
		mue.stopPropagation();
	}

	@Override
	public void onMouseOver(MouseOverEvent me) {
		me.preventDefault();
		me.stopPropagation();
	}

	@Override
	public void onMouseOut(MouseOutEvent mue) {
		mue.preventDefault();
		mue.stopPropagation();
	}

	@Override
	public void onMouseMove(MouseMoveEvent mue) {
		mue.preventDefault();
		mue.stopPropagation();
	}

	@Override
	public void onTouchStart(TouchStartEvent tse) {
		tse.preventDefault();
		tse.stopPropagation();
	}

	@Override
	public void onTouchEnd(TouchEndEvent tee) {
		tee.preventDefault();
		tee.stopPropagation();
	}

	@Override
	public void onTouchMove(TouchMoveEvent tee) {
		tee.preventDefault();
		tee.stopPropagation();
	}

	@Override
	public void onTouchCancel(TouchCancelEvent tce) {
		tce.preventDefault();
		tce.stopPropagation();
	}
}
