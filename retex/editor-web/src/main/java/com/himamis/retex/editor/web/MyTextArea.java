package com.himamis.retex.editor.web;

import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.TextArea;

import elemental2.dom.Element;
import jsinterop.base.Js;

public class MyTextArea extends TextArea {

	/**
	 * @param element
	 *            wrapped element
	 */
	public MyTextArea(Element element) {
		super(Js.uncheckedCast(element));
	}

	/**
	 * Factory method
	 * 
	 * @param element
	 *            textarea element
	 * @return textarea widget
	 */
	public static MyTextArea wrap(Element element) {
		// Assert that the element is attached.

		MyTextArea textArea = new MyTextArea(element);

		// Mark it attached and remember it for cleanup.
		textArea.onAttach();
		RootPanel.detachOnWindowClose(textArea);

		return textArea;
	}

	/**
	 * @param handler
	 *            composition event handler
	 */
	public void addCompositionUpdateHandler(EditorCompositionHandler handler) {
		elemental2.dom.Element el = Js.uncheckedCast(getElement());
		el.addEventListener("compositionupdate", evt -> handler.onCompositionUpdate(evt));
	}

	public void addCompositionEndHandler(EditorCompositionHandler handler) {
		elemental2.dom.Element el = Js.uncheckedCast(getElement());
		el.addEventListener("compositionend", evt -> handler.onCompositionUpdate(evt));
	}
}
