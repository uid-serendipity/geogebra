package org.geogebra.web.html5.gui.util;

import org.gwtproject.dom.client.Document;
import org.gwtproject.event.dom.client.ChangeHandler;
import org.gwtproject.event.dom.client.DomEvent;
import org.gwtproject.event.dom.client.HasChangeHandlers;
import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.HasValue;
import org.gwtproject.user.client.ui.Label;

public class SliderPanel extends FlowPanel implements HasChangeHandlers,
		HasValue<Integer>, SliderInputHandler {

	private Slider slider;
	private Label sliderLabel;

	public SliderPanel() {
		this(0, 100);
	}

	/**
	 * @param min
	 *            slider min
	 * @param max
	 *            slider max
	 */
	public SliderPanel(int min, int max) {
		slider = new Slider(min, max);
		add(slider);
		sliderLabel = new Label();
		sliderLabel.setText(this.getValue() + "");
		add(sliderLabel);
		sliderLabel.addStyleName("popupSliderLabel");
		setStyleName("optionsSlider");
		Slider.addInputHandler(slider.getElement(), this);
	}

	@Override
	public Integer getValue() {
		return slider.getValue();
	}

	/**
	 * @param min
	 *            range minimum
	 */
	public void setMinimum(int min) {
		slider.setMinimum(min);
	}

	/**
	 * @param max
	 *            range maximum
	 */
	public void setMaximum(int max) {
		slider.setMaximum(max);
	}

	public void setTickSpacing(int step) {
		slider.setTickSpacing(step);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
	        ValueChangeHandler<Integer> handler) {
		return slider.addValueChangeHandler(handler);
	}

	@Override
	public void onSliderInput() {
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(),
				this.slider);
		sliderLabel.setText(this.getValue() + "");
	}

	@Override
	public void setValue(Integer value) {
		slider.setValue(value, false);
		sliderLabel.setText(this.getValue() + "");
	}

	@Override
	public void setValue(Integer value, boolean fireEvents) {
		slider.setValue(value, fireEvents);
		sliderLabel.setText(this.getValue() + "");
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return slider.addChangeHandler(handler);
	}
}
