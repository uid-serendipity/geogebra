package org.geogebra.web.simple;

import java.util.ArrayList;

import org.geogebra.web.cas.giac.CASFactoryW;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.SuperDevUncaughtExceptionHandler;

import com.google.gwt.core.client.EntryPoint;

import jsinterop.base.Js;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebSimple implements EntryPoint {

	/**
	 * set true if Google Api Js loaded
	 */
	@Override
	public void onModuleLoad() {
		exportGGBElementRenderer();

		// instead, load it immediately
		startGeoGebra(GeoGebraElement.getGeoGebraMobileTags());
		SuperDevUncaughtExceptionHandler.register();
		Stub3DFragment.load();
	}

	static void startGeoGebra(ArrayList<GeoGebraElement> geoGebraMobileTags) {
		GeoGebraFrameSimple.main(geoGebraMobileTags, new CASFactoryW());
	}

	private void exportGGBElementRenderer() {
		GeoGebraGlobal.setRenderGGBElement((el, callback) -> {
			GeoGebraFrameSimple.renderArticleElement(GeoGebraElement.as(Js.uncheckedCast(el)), callback,
					new CASFactoryW());
		});
		GeoGebraFrameW.renderGGBElementReady();
	}

}
