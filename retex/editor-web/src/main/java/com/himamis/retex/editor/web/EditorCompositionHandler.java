package com.himamis.retex.editor.web;

import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.model.Korean;
import com.himamis.retex.editor.share.util.JavaKeyCodes;

import elemental2.dom.Element;
import elemental2.dom.Event;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Composition handler for math input
 * 
 * @author Zbynek
 */
final class EditorCompositionHandler {

	private MathFieldW editor;
	private boolean insertOnEnd = false;

	boolean backspace = false;

	/**
	 * @param editor
	 *            equation editor
	 */
	public EditorCompositionHandler(MathFieldW editor) {
		this.editor = editor;
	}

	public void attachTo(MyTextArea inputTextArea) {
		Element el = Js.uncheckedCast(inputTextArea.getElement());
		inputTextArea.addCompositionUpdateHandler(this);
		inputTextArea.addCompositionEndHandler(this);
		el.addEventListener("keydown", this::onKeyDown);
		el.addEventListener("keyup", this::onKeyUp);
	}

	public void onCompositionUpdate(Event event) {
		if (backspace) {
			return;
		}

		// this works fine for Korean as the editor has support for
		// combining Korean characters
		// but for eg Japanese probably will need to hook into
		// compositionstart & compositionend events as well

		// in Chrome typing fast gives \u3137\uB450
		// instead of \u3137\u315C
		// so flatten the result and send just the last character
		CompositionEvent compEvent = Js.uncheckedCast(event);
		String data = Korean.flattenKorean(compEvent.data);
		// ^: fix for swedish
		if (!"^".equals(data) && data.length() > 0) {
			char inputChar = data.charAt(data.length() - 1);
			char lastChar = Korean.unmergeDoubleCharacterForEditor(inputChar);
			if (Korean.isCompatibilityChar(lastChar)
					|| Korean.isSingleKoreanChar(lastChar)) {
				editor.insertString("" + lastChar);
			} else {
				insertOnEnd = true;
			}

		}
	}

	public void onCompositionEnd(Event event) {
		if (insertOnEnd) {
			// inserted string should only depend on `compositionend`
			// in Safari the data in `cmpositionupdate` is just Latin chars
			CompositionEvent compositionEvent = Js.uncheckedCast(event);
			editor.insertString(compositionEvent.data);
			editor.getInternal().notifyAndUpdate(compositionEvent.data);
		}
	}

	private boolean composingBackspace(Event event) {
		JsPropertyMap<Object> nativeEvent = Js.asPropertyMap(event);
		return Js.isTruthy(nativeEvent.get("isComposing"))
			&& "Backspace".equals(nativeEvent.get("code"));
	}

	public void onKeyDown(Event event) {
		if (composingBackspace(event)) {
			editor.getKeyListener().onKeyPressed(new KeyEvent(JavaKeyCodes.VK_BACK_SPACE));
			backspace = true;
		}
	}

	public void onKeyUp(Event event) {
		if (composingBackspace(event)) {
			editor.clearState();
			backspace = false;
		}
	}
}