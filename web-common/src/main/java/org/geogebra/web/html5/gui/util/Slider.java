package org.geogebra.web.html5.gui.util;

import org.geogebra.common.util.StringUtil;

import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.InputElement;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.event.dom.client.ChangeHandler;
import org.gwtproject.event.dom.client.HasChangeHandlers;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseDownHandler;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseMoveHandler;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.MouseUpHandler;
import org.gwtproject.event.logical.shared.ValueChangeEvent;
import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.ui.FocusWidget;
import org.gwtproject.user.client.ui.HasValue;

public class Slider extends FocusWidget implements HasChangeHandlers,
        HasValue<Integer>, MouseDownHandler, MouseUpHandler, MouseMoveHandler {

	private InputElement range;
	private boolean valueChangeHandlerInitialized;
	private Integer valueOnDragStart;

	/**
	 * Create a new slider.
	 * 
	 * @param min
	 *            slider min
	 * @param max
	 *            slider max
	 */
	public Slider(int min, int max) {
		range = Document.get().createTextInputElement();
		range.setAttribute("type", "range");
		range.setAttribute("min", String.valueOf(min));
		range.setAttribute("max", String.valueOf(max));
		range.setValue(String.valueOf(min));
		setElement(range);
		addMouseDownHandler(this);
		// addMouseMoveHandler(this);
		addMouseUpHandler(this);
	}

	public static void addInputHandler(Element el, SliderInputHandler handler) {
		Dom.addEventListener(el, "input", evt -> handler.onSliderInput());
	}

	@Override
	public Integer getValue() {
		return StringUtil.empty(range.getValue()) ? Integer.valueOf(0)
				: Integer.valueOf(range.getValue());
	}

	public void setMinimum(int min) {
		range.setAttribute("min", String.valueOf(min));
	}

	public void setMaximum(int max) {
		range.setAttribute("max", String.valueOf(max));
	}

	public void setTickSpacing(int step) {
		range.setAttribute("step", String.valueOf(step));
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
	        ValueChangeHandler<Integer> handler) {
		if (!valueChangeHandlerInitialized) {
			valueChangeHandlerInitialized = true;
			addChangeHandler(event -> ValueChangeEvent.fire(this, getValue()));
		}
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public void setValue(Integer value) {
		setValue(value, false);
	}

	@Override
	public void setValue(Integer value, boolean fireEvents) {
		setSliderValue(String.valueOf(value));
	}

	private void setSliderValue(String value) {
		range.setValue(value);
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		ValueChangeEvent.fireIfNotEqual(this, valueOnDragStart, getValue());

	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		valueOnDragStart = getValue();
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		event.stopPropagation();
	}

}
