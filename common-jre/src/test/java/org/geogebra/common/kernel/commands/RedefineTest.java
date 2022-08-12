package org.geogebra.common.kernel.commands;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.test.EventAcumulator;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.TestStringUtil;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.geogebra.test.commands.ErrorAccumulator;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class RedefineTest extends BaseUnitTest {

	private static AlgebraProcessor ap;
	private static App app;

	/**
	 * Initialize app & algebra processor.
	 */
	@Before
	public void setAppAndAlgebraProcessor() {
		ap = getApp().getKernel().getAlgebraProcessor();
		app = getApp();
	}

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	private static void t(String input, String expected) {
		AlgebraTestHelper.checkSyntaxSingle(input, new String[] { expected }, ap,
				StringTemplate.xmlTemplate);
	}

	private static void t(String input, String expected, StringTemplate tpl) {
		AlgebraTestHelper.checkSyntaxSingle(input, new String[] { expected }, ap,
				tpl);
	}

	private void checkError(String s, String msg) {
		ErrorAccumulator errorStore = new ErrorAccumulator();
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(s, false, errorStore,
						false, null);
		assertEquals(msg, errorStore.getErrors());
	}

	@Test
	public void breakingTypeChangeShouldRaiseException() {
		t("A=(1,1)", "(1, 1)");
		t("B=(1,0)", "(1, 0)");
		t("C=(0,0)", "(0, 0)");
		t("D=(0,1)", "(0, 1)");
		t("poly1=Polygon[A,B,C,D]", new String[] { "1", "1", "1", "1", "1" });
		t("a", "1"); // polygon side
		app.getKernel().setUndoActive(true);
		app.getKernel().initUndoInfo();
		app.storeUndoInfo();
		checkError("A(x)=x", "Redefinition failed");
		checkError("A(x)=(", "Unbalanced brackets \nA(x)=( ");
		checkError("A(x)=1+", "Please check your input");
		t("A", "(1, 1)");
		t("poly1", "1");
		t("a", "1");
	}

	@Test
	public void testErrors() {
		t("f(x)=x", "x");
		checkError("f(x)=f(x)+x", "Circular definition");
		checkError("f(x)=y", "Invalid function:\n" + "Please enter an explicit function in x");
		checkError("f(t)=y", "Invalid function:\n" + "Please enter an explicit function in t");
		checkError("f(t)=x", "Invalid function:\n" + "Please enter an explicit function in t");
		checkError("f(x)=3/(x^2+y^2=1)", "Illegal division \n" + "3 /  x\u00B2 + y\u00B2 = 1 ");
		checkError("f(x)=3*(x^2+y^2=1)",
				"Illegal multiplication \n" + "3 *  x\u00B2 + y\u00B2 = 1 ");
		checkError("f(x)=3+(x^2+y^2=1)", "Illegal addition \n" + "3 +  x\u00B2 + y\u00B2 = 1 ");
		checkError("f(x)=3-(x^2+y^2=1)", "Illegal subtraction \n" + "3 -  x\u00B2 + y\u00B2 = 1 ");
		checkError("f(x)=3^(x^2+y^2=1)", "Illegal exponent \n" + "3 ^  x\u00B2 + y\u00B2 = 1 ");
		checkError("f(x)=sin(x^2+y^2=1)", "Illegal argument \n" + "sin(  x\u00B2 + y\u00B2 = 1 ) ");
		// error could be improved
		checkError("f(x)=sin(1,2,3)", "Unknown command : sin");
		checkError("{1,2,3}\\(1,2)", "Illegal list operation \n" + "{1, 2, 3} \\ (1, 2) ");
	}

	@Test
	public void testErrorUndefined() {
		checkError("Rename(f,\"fff\")",
				"Please check your input :\n" + "Undefined variable \n" + "f ");
	}

	@Test
	public void curlyBracketsShouldNotAffectRedefine() {
		t("r=1", "1");
		t("r_2=2*r", "2");
		t("r_3=3*r_2", "6");
		t("r_{2}=3*r", "3");

		t("a=7", "7");
		t("A=(1,1)", "(1, 1)");
		t("B=(1,0)", "(1, 0)");
		t("C=(0,0)", "(0, 0)");
		t("D=(0,1)", "(0, 1)");
		t("poly1=Polygon[A,B,C,D]", new String[] { "1", "1", "1", "1", "1" });
		assertEquals("a_1 = Segment(A, B, poly1)",
				lookup("a_1").getDefinitionForInputBar());
		t("a_{1} = Segment(A, B, poly1)", new String[0]);
		ap.changeGeoElement(lookup("a_1"),
				"a_{1} = Segment(A, B, poly1)", true, true,
				TestErrorHandler.INSTANCE, null);
	}

	@Test
	public void undoShouldNotRandomize() {
		app.setRandomSeed(42);
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		t("a=random()", "0.7275636800328681");

		app.storeUndoInfo();
		t("1", "1");
		app.storeUndoInfo();
		app.getKernel().undo();

		t("a", "0.7275636800328681");
	}

	@Test
	public void randomizeUpdateConstruction() {
		app.setRandomSeed(42);
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		t("b=100", "100");
		t("a=randomUniform(0,b)", "72.75636800328681");
		((GeoNumeric) lookup("b")).setValue(10);
		((GeoNumeric) lookup("b")).resetDefinition();
		app.getKernel().updateConstruction(false);
		t("a", "10");
	}

	@Test
	public void setValueShouldChangeRandom() {
		app.setRandomSeed(42);
		t("a=random()", "0.7275636800328681");
		t("SetValue(a,0.5)", new String[0]);
		t("a", "0.5");
	}

	@Test
	public void setValueShouldChangeRandomSequence() {
		app.setRandomSeed(42);
		add("a=Sequence(RandomBetween(1,k),k,1,5)");
		t("SetValue(a,{3,-2,3,2,5})", new String[0]);
		t("a", "{1, 1, 3, 2, 5}");
	}

	@Test
	public void setValueShouldChangeShuffle() {
		app.setRandomSeed(42);
		t("L_1=Shuffle(1..10)", "{8, 7, 3, 2, 6, 10, 4, 1, 5, 9}");
		t("SetValue(L_1, {1, 2, 3, 4, 5, 6, 7, 11, 9, 10})", new String[0]);
		t("L_1", "{1, 2, 3, 4, 5, 6, 7, 9, 10, 8}");
	}

	@Test
	public void undoShouldNotRandomizeShufle() {
		app.setRandomSeed(42);
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		t("L_1=Shuffle(1..10)", "{8, 7, 3, 2, 6, 10, 4, 1, 5, 9}");

		app.storeUndoInfo();
		t("1", "1");
		app.storeUndoInfo();
		app.getKernel().undo();

		t("L_1", "{8, 7, 3, 2, 6, 10, 4, 1, 5, 9}");
	}

	@Test
	public void setValueShouldChangeRandomElement() {
		app.setRandomSeed(42);
		t("P=RandomElement((1..10,1..10))", "(8, 8)");
		t("SetValue(P, (7, 7))", new String[0]);
		t("P", "(7, 7)");
	}

	@Test
	public void cmdRename() {
		checkError("Rename[ 6*7, \"$7\" ]",
				"Command Rename:\nIllegal argument: Text \"$7\"\n\n"
						+ "Syntax:\nRename( <Object>, <Name> )");
	}

	@Test
	public void functionLHSShouldRemainConic() {
		t("f(x,y)=xx+y", "x^(2) + y");
		t("a:f(x,y)=0", TestStringUtil.unicode("x^2 + y = 0"));
		assertEquals(lookup("a").getGeoClassType(), GeoClass.CONIC);
		reload();
		hasType("a", GeoClass.CONIC);
	}

	@Test
	public void rigidPolygonShouldSurviveReload() {
		add("A=(1,1)");
		add("B=(1,2)");
		add("C=(2,1)");
		add("t1=Polygon(A,B,C)");
		add("t2=RigidPolygon(t1)");
		reload();
		assertEquals("t2 = Polygon(D, E, F)", lookup("t2").getDefinitionForInputBar());
	}

	private void hasType(String label, GeoClass geoClass) {
		assertEquals(lookup(label).getGeoClassType(), geoClass);
	}

	@Test
	public void copyOfConicShouldNotBeCellRange() {

		t("B20:x^2+y=0", TestStringUtil.unicode("x^2 + y = 0"));
		t("D20=B20", TestStringUtil.unicode("x^2 + y = 0"));
		assertEquals(
				lookup("D20").getGeoClassType(),
				GeoClass.CONIC);
		reload();
		assertEquals(
				lookup("D20").getGeoClassType(),
				GeoClass.CONIC);
	}

	@Test
	public void pointOnSplineShouldMove() {
		t("A=(1, 1)", "(1, 1)");
		t("b:Spline({(0, 1),A,(1, 0)})", TestStringUtil.unicode(
				"(If(t < 0.5, -2t^3 + 2.5t, 2t^3 - 6t^2 + 5.5t - 0.5),"
						+ " If(t < 0.5, -2t^3 + 0.5t + 1, 2t^3 - 6t^2 + 3.5t + 0.5))"),
				StringTemplate.editTemplate);
		t("B:ClosestPoint(A, b)", "(1, 1)");
		t("A=(0, 0)", "(0, 0)");
		t("B", "(0, 0)");
	}

	@Test
	public void pointOnFnShouldNotStayUndefined() {
		t("a=1", "1");
		t("f=axx", "(1 * x^(2))");
		t("A=Point[f]", "(0, 0)");
		t("a=?", "NaN");
		t("a=1", "1");
		t("A", "(0, 0)");
	}

	@Test
	public void pointOnPartialFunctionShouldStayUndefined() {
		t("ZoomIn[0,0,100,100]", new String[0]);
		t("a=.9", "0.9");
		// undefined for most onscreen points
		t("f=If(x==0, 1, ?)",
				"If[x " + Unicode.QUESTEQ + " 0, 1, NaN]");
		t("A=Point[f, a]", "(NaN, NaN)");
		t("a=.8", "0.8");
		t("A", "(NaN, NaN)");
	}

	@Test
	public void anonymousLineShouldStayLine() {
		app.getEuclidianView3D();
		app.setActiveView(App.VIEW_EUCLIDIAN3D);
		t("c=Circle((0,0,0),1,x=0)", "X = (0, 0, 0) + (0, - cos(t), sin(t))",
				StringTemplate.editTemplate);
		app.setActiveView(App.VIEW_EUCLIDIAN);
		t("d=Circle((0,0,0),1,x=0)", "X = (0, 0, 0) + (0, - cos(t), sin(t))",
				StringTemplate.editTemplate);

		reload();
		t("d", "X = (0, 0, 0) + (0, - cos(t), sin(t))",
				StringTemplate.editTemplate);
		t("c", "X = (0, 0, 0) + (0, - cos(t), sin(t))",
				StringTemplate.editTemplate);
	}

	@Test
	public void updateImplicitCurve() {
		add("a=2");
		t("c:y^2 = (x^2-a^2)/x^2", "y^(2) = (x^(2) - 2^(2)) / x^(2)");
		assertFalse("Implicit curve with var should be dependent.",
				lookup("c").isIndependent());
		t("c1:y^2 = (x^2-2^2)/x^2", "y^(2) = (x^(2) - 2^(2)) / x^(2)");
		assertTrue("Implicit curve without vars should be independent.",
				lookup("c1").isIndependent());
		assertEquals(
				TestStringUtil.unicode("c: y^2 = (x^2 - 2^2) / x^2"),
				lookup("c").getAlgebraDescriptionTextOrHTMLDefault(
						new IndexHTMLBuilder(true)));
		t("a=3", "3");
		assertEquals(
				TestStringUtil.unicode("c: y^2 = (x^2 - 3^2) / x^2"),
				lookup("c").getAlgebraDescriptionTextOrHTMLDefault(
						new IndexHTMLBuilder(true)));
	}

	@Test
	public void derivativeShouldNotThrowCircularException() {
		t("f(x)=x^2", "x^(2)");
		t("f'(x)=f'", "(2 * x)");
		ap.changeGeoElement(lookup("f'"), "f'(x)", true, true,
				TestErrorHandler.INSTANCE, new AsyncOperation<GeoElementND>() {
					@Override
					public void callback(GeoElementND obj) {
						// no callback
					}
				});
		t("f'(x)", "(2 * x)");
	}

	@Test
	public void redefinitionShouldNotMakeUnfixed() {
		add("b:Circle(O,1)");
		add("c:xx+yy=2");
		add("d:xx+yy");
		app.setNewExam();
		app.startExam();
		assertFalse(lookup("b").isLocked());
		assertTrue(lookup("c").isLocked());
		assertFalse(lookup("d").isLocked());
		add("d:xx+yy=2");
		assertTrue(lookup("d").isLocked());
	}

	@Test
	public void cubeShouldNotVanish() {
		add("A=O");
		add("a=1");
		add("Segment(A,a)");
		add("cb=Cube(A,B)");
		assertThat(lookup("cb"), isDefined());
		add("SetValue(a,-1)");
		assertThat(lookup("cb"), not(isDefined()));
		add("SetValue(a,1)");
		assertThat(lookup("cb"), isDefined());
	}

	@Test
	public void setValueShouldKeepDefinition() {
		t("a=1", "1");
		t("A=(1, 1/a)", "(1, 1)");
		add("SetValue(a, 0)");
		t("A", "(NaN, NaN)");
		add("SetValue(a, 1)");
		t("A", "(1, 1)");
	}

	@Test
	public void functionShouldStayInequality() {
		// old format: only NaN
		app.getGgbApi().evalXML("<expression label=\"studans\" "
				+ "exp=\"studans: NaN\" type=\"inequality\"/>\n"
				+ "<element type=\"function\" label=\"studans\">\n"
				+ "\t<show object=\"false\" label=\"false\" ev=\"4\"/>\n"
				+ "</element>");
		assertThat(lookup("studans"), isForceInequality());
		// new format: includes function variables
		app.getGgbApi().evalXML("<expression label=\"studans2\" "
				+ "exp=\"studans2(x) = ?\" type=\"inequality\"/>\n"
				+ "<element type=\"function\" label=\"studans2\">\n"
				+ "\t<show object=\"false\" label=\"false\" ev=\"4\"/>\n"
				+ "</element>");
		assertThat(lookup("studans2"), isForceInequality());
	}

	@Test
	public void avRedefineShouldChangeInequalityToFunction() {
		add("f:x>3");
		add("f(x)=x+3");
		assertThat(lookup("f"), not(isForceInequality()));
	}

	@Test
	public void minMaxShouldWorkForUndefinedFunctions() {
		add("a:-3<x<6");
		add("b:x<3");
		t("min=Min(a)", "-3");
		t("max=Max(a)", "6");
		t("minB=Min(b)", "-Infinity");
		t("maxB=Max(b)", "3");
		add("SetValue(a,?)");
		add("SetValue(b,?)");
		add("c=a(3)");
		add("d=b(3)");
		assertThat(lookup("d"), hasValue("false"));
		assertThat(lookup("c"), hasValue("false"));
		reload();
		assertThat(lookup("c"), hasValue("false"));
	}

	@Test
	public void redefineShouldUpdateOrder() {
		add("numA:Element(1..5, 5)");
		add("numA0:2numA");
		add("numA2:1");
		add("qrow:{numA + \"\"}");
		add("numA3=numA0 - numA2");
		add("row11:{Element(qrow, 1)}");
		add("qrow:{numA3 + \"\"}");
		assertEquals("{numA3 + \"\"}",
				lookup("qrow").getRedefineString(false, false));
	}

	@Test
	public void reloadShouldNotLabelIntersectionPaths() {
		add("a = Cube((-1, 1, 0), (1, 1, 0), Vector((0, 0, 1)))");
		add("l1 = {3x + y + z = 1, x - 3y - z = -7}");
		add("l2 = Zip(IntersectPath(P, a), P, l1)");
		reload();
		List<String> labels = getKernel().getConstruction().getGeoSetConstructionOrder()
				.stream().filter(a -> !a.isAuxiliaryObject()).map(GeoElement::getLabelSimple)
				.collect(Collectors.toList());
		assertEquals(Arrays.asList("a", "l1", "l2"), labels);
		assertEquals("Zip(IntersectPath(P, a), P, l1)",
				lookup("l2").getDefinition(StringTemplate.defaultTemplate));
	}

	@Test
	public void maskShouldStayMask() {
		GeoPolygon mask = add("q1=Polygon((0,0),(0,1),(1,1),(1,0))");
		mask.setIsMask(true);
		GeoPolygon redefined = add("q1=Polygon((0,0),(0,2),(1,2),(1,0))");
		assertTrue("rectangle should still be a mask", redefined.isMask());
		assertEquals(1, redefined.getAlphaValue(), 1E-5);
	}

	@Test
	public void curveShouldBeDefinedAfterInputUpdate() {
		add("b=?");
		add("f(x)=?");
		GeoElement curve = add("Curve(f(t),f(t),t,-b,b)");
		add("SetValue(b,1)");
		add("SetValue(f,sin(x))");
		assertThat(curve, isDefined());
	}

	@Test
	public void matrixShouldBeDefinedAfterRedefine() {
		add("m1={?}");
		add("m2=Transpose(m1)");
		add("m1={{1,2},{3,4}}");
		assertThat(lookup("m2"), hasValue("{{1, 3}, {2, 4}}"));
	}

	@Test
	public void simpleRedefinitionsShouldBeSoft() {
		add("A=(1,1)");
		GeoElement m = add("m=Line(A,(1,3))");
		GeoElement redefinedM = add("m=Line(A,(1,3))");
		assertEquals(m, redefinedM); // no-op redefinition
		redefinedM = add("m=Line(A,(1,2))");
		assertEquals(m, redefinedM); // soft redefinition
		redefinedM = add("m=Line(A,Vector((1,3)))");
		assertNotEquals(m, redefinedM);
	}

	@Test
	public void softRedefineShouldUpdateSiblings() {
		add("c=Cone((0,0,0),(0,0,1),2)");
		EventAcumulator listener = new EventAcumulator();
		getApp().getEventDispatcher().addEventListener(listener);
		add("c=Cone((0,0,0),(0,0,1),4)");
		assertEquals(Arrays.asList("UPDATE c", "UPDATE d", "UPDATE a"),
				listener.getEvents());
	}

	@Test
	public void pointsOnLocusShouldReload() {
		add("stroke1=PolyLine((0,0), (1,0), (2,0), true)");
		add("pts=Sequence(Point(stroke1, i), i, 0, 1, 0.5)");
		// only testing that it reloads OK, actual values seem a bit off
		assertThat(lookup("pts"), hasValue("{(0, 0), (1.5, 0), (?, ?)}"));
		reload();
		assertThat(lookup("pts"), hasValue("{(0, 0), (1.5, 0), (?, ?)}"));
	}

	@Test
	public void selectionAllowedShouldStay() {
		GeoPoint pt = add("A=(1,2)");
		pt.setSelectionAllowed(false);
		GeoElement redefined = add("A:x=y");
		assertEquals("A", redefined.getLabelSimple());
		assertFalse("Selection should stay disabled",
				redefined.isSelectionAllowed(null));
		GeoElement transformed = add("Rotate(A,90deg)");
		assertTrue("Selection should not be copied",
				transformed.isSelectionAllowed(null));
	}

	@Test
	public void eigenvectorsNotChangedOnReload() {
		add("c:x^2+y^2=1");
		add("c':Reflect(c, x+2y=5)");
		GeoElement p = add("P=Point(c')");
		assertThat(p, hasValue("(1.55, 3.11)"));
		reload();
		p = lookup("P");
		assertThat(p, hasValue("(1.55, 3.11)"));
	}

	/**
	 * @return matcher for inequalities
	 */
	public static TypeSafeMatcher<GeoElementND> isForceInequality() {
		return new TypeSafeMatcher<GeoElementND>() {
			@Override
			protected boolean matchesSafely(GeoElementND item) {
				return item instanceof GeoFunction && ((GeoFunction) item).isForceInequality();
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("Forced inequality");
			}
		};
	}

}
