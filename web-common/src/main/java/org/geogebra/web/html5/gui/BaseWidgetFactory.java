package org.geogebra.web.html5.gui;

import org.geogebra.web.html5.util.sliderPanel.SliderW;

import org.gwtproject.user.client.ui.Button;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.ListBox;

public class BaseWidgetFactory {
	/**
	 * @return flow panel; to be mocked
	 */
	public FlowPanel newPanel() {
		return new FlowPanel();
	}

	/**
	 * @return button, to be mocked
	 */
	public Button newButton() {
		return new Button();
	}

	/**
	 * @return button, to be mocked
	 */
	public Label newLabel() {
		return new Label();
	}

	/**
	 * @return list box, to be mocked
	 */
	public ListBox newListBox() {
		return new ListBox();
	}

	public SliderW newSlider(int i, int j) {
		return new SliderW(i, j);
	}
}
