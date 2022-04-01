package com.himamis.retex.editor.share.editor;

import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;

public class SyntaxHintModel {
	private EditorState editorState = null;

	public void update(EditorState editorState) {
		this.editorState = editorState;
	}

	public SyntaxHint getHint() {
		if (!isAccepted()) {
			return null;
		}

		MathFunction fn = getMathFunction();
		if (fn.getName() == Tag.APPLY && !fn.getPlaceholders().isEmpty()) {
			int commas = editorState.countCommasBeforeCurrent();
			if (commas < fn.getPlaceholders().size()) {
				return new SyntaxHint(GeoGebraSerializer.serialize(fn.getArgument(0)),
						fn.getPlaceholders(), commas);
			}
		}
		return null;
	}

	private boolean isAccepted() {
		return isMathFunction() && isSingleParent();
	}

	private boolean isSingleParent() {
		return editorState.getCurrentField().getParentIndex() == 1;
	}

	private MathFunction getMathFunction() {
		return (MathFunction) editorState.getCurrentField().getParent();
	}

	private boolean isMathFunction() {
		return editorState.getCurrentField().getParent() instanceof MathFunction;
	}
}
