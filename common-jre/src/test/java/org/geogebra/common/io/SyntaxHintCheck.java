package org.geogebra.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.editor.SyntaxHint;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class SyntaxHintCheck {

	private static MathFieldCommon mfc;

	/**
	 * Reset LaTeX factory
	 */
	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@Before
	public void setUp() {
		mfc = new MathFieldCommon(new MetaModel(), null);
	}

	@Test
	public void readPlaceholdersInitial() {
		KeyboardInputAdapter.onKeyboardInput(mfc.getInternal(), "FitPoly(<Points>, <Degree>)");
		SyntaxHint hint = mfc.getInternal().getSyntaxHint();
		assertEquals("FitPoly(", hint.getPrefix());
		assertEquals("Points", hint.getActive());
		assertEquals(", Degree)", hint.getSuffix());
	}

	@Test
	public void readPlaceholdersAfterComma() {
		KeyboardInputAdapter.onKeyboardInput(mfc.getInternal(), "FitPoly(<Points>, <Degree>)");
		EditorTyper typer = new EditorTyper(mfc);
		typer.type("{(1,1)},");
		SyntaxHint hint = mfc.getInternal().getSyntaxHint();
		assertEquals("FitPoly(Points, ", hint.getPrefix());
		assertEquals("Degree", hint.getActive());
		assertEquals(")", hint.getSuffix());
	}

	@Test
	public void nonCommandInput() {
		KeyboardInputAdapter.onKeyboardInput(mfc.getInternal(), "\"Hello, there!\"");
		EditorTyper typer = new EditorTyper(mfc);
		typer.type("{(1,1)},");
		SyntaxHint hint = mfc.getInternal().getSyntaxHint();
		assertTrue(hint.isEmpty());
	}

	@Test
	public void changeFunctionName() {
		String input = "FitPoly(<Points>, <Degree>)";
		KeyboardInputAdapter.onKeyboardInput(mfc.getInternal(), input);
		SyntaxHint hint = mfc.getInternal().getSyntaxHint();
		assertEquals("FitPoly(", hint.getPrefix());
		assertEquals("Points", hint.getActive());
		assertEquals(", Degree)", hint.getSuffix());
		EditorTyper typer = new EditorTyper(mfc);
		typer.repeatKey(JavaKeyCodes.VK_LEFT, input.length() - 3);
		typer.typeKey(JavaKeyCodes.VK_DELETE);
		assertTrue(mfc.getInternal().getSyntaxHint().isEmpty());
	}

}
