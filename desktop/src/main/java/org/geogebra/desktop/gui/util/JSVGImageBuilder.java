

package org.geogebra.desktop.gui.util;

import static org.geogebra.desktop.gui.util.JSVGConstants.BLANK_SVG;
import static org.geogebra.desktop.gui.util.JSVGConstants.HEADER;
import static org.geogebra.desktop.gui.util.JSVGConstants.NO_URI;
import static org.geogebra.desktop.gui.util.JSVGConstants.UNSUPPORTED_SVG;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.geogebra.desktop.util.ImageManagerD;
import org.geogebra.desktop.util.UtilD;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import io.sf.carte.echosvg.anim.dom.SAXSVGDocumentFactory;
import io.sf.carte.echosvg.bridge.BridgeContext;
import io.sf.carte.echosvg.bridge.DocumentLoader;
import io.sf.carte.echosvg.bridge.GVTBuilder;
import io.sf.carte.echosvg.bridge.UserAgent;
import io.sf.carte.echosvg.bridge.UserAgentAdapter;
import io.sf.carte.echosvg.dom.util.SAXIOException;
import io.sf.carte.echosvg.gvt.GraphicsNode;

/**
 * Class to load and paint SVGs.
 * Note that links within SVG are replaced to blank images for security reasons.
 */
public final class JSVGImageBuilder {

	private static String content;
	private static int loopGuard = 0;
	private static JSVGImage blankImage = null;
	private static JSVGImage unsupportedImage = null;
	private JSVGImageBuilder() {
		// utility class
	}

	/**
	 * Create {@link JSVGImage} from file
	 *
	 * @param file of the svg.
	 * @return the new {@link JSVGImage}.
	 * @throws IOException if there is some I/O issue.
	 */
	public static JSVGImage fromFile(File file) throws IOException {
		FileInputStream is = new FileInputStream(file);
		String content = UtilD.loadIntoString(is);
		is.close();
		return fromContent(content);
	}

	/**
	 * Create {@link JSVGImage} from SVG string content.
	 * @param content of the SVG.
	 * @return the new {@link JSVGImage}.
	 */
	public static JSVGImage fromContent(String content) {
		JSVGImageBuilder.content = content;
		Reader reader = new StringReader(content);
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
		SVGDocument doc;
		try {
			doc = f.createSVGDocument(NO_URI, reader);
		} catch (SAXIOException se) {
			return fromContent(fixHeader(content));
		} catch (IOException e) {
			return blankImage();
		}
		return newImage(doc);
	}

	private static JSVGImage blankImage() {
		if (blankImage == null) {
			blankImage = fromContent(BLANK_SVG);
		}
		return blankImage;
	}

	private static JSVGImage newImage(SVGDocument doc) {
		loopGuard++;
		try {
			return build(doc);
		} catch (Exception e) {
			if (loopGuard > 2) {
				loopGuard = 0;
				return unsupportedImage();
			}
			return fromContent(fixHeader(ImageManagerD.fixSVG(content)));
		}
	}

	private static JSVGImage unsupportedImage() {
		if (unsupportedImage == null) {
			unsupportedImage = fromContent(UNSUPPORTED_SVG);
		}
		return unsupportedImage;
	}

	private static JSVGImage build(SVGDocument doc) {
		UserAgent userAgent = new UserAgentAdapter();

		DocumentLoader loader = new SVGDocumentLoaderNoError(userAgent);

		BridgeContext ctx = new BridgeContext(userAgent, loader);
		ctx.setDynamicState(BridgeContext.DYNAMIC);
		GVTBuilder builder = new GVTBuilder();
		GraphicsNode node = builder.build(ctx, doc);

		SVGSVGElement rootElement = doc.getRootElement();
		return new JSVGImage(node, rootElement.getWidth().getBaseVal().getValue(),
				rootElement.getHeight().getBaseVal().getValue());
	}

		private static String fixHeader(String content){
			int beginIndex = content.indexOf("<svg");
			if (beginIndex == -1) {
				return BLANK_SVG;
			}
			String body = content.substring(beginIndex);
			return HEADER + body;
		}

		/**
		 * Method to fetch the SVG image from an url
		 * @param url the url from which to fetch the SVG image
		 */
		public static JSVGImage fromUrl(URL url){
			SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
			try {
				SVGDocument doc = f.createSVGDocument(url.toString());
				return newImage(doc);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
}
