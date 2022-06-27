package org.geogebra.common.euclidian.draw;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.asymptotes.AsymptoteDetector;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.util.DoubleUtil;

public class DrawExtendedAsymptotes {
	private final GeoFunction geo;
	private final GeneralPathClipped gp;
	private EuclidianView view;
	private final AsymptoteDetector detector;

	public DrawExtendedAsymptotes(GeoFunction geo, EuclidianView view,
			AsymptoteDetector detector) {
		this.geo = geo;
		gp = new GeneralPathClipped(view);
		this.view = view;
		this.detector = detector;
	}

	public void update() {
		detector.update();
	}

	public void draw(GGraphics2D g2) {
		List<IntervalTuple> asymptotes = detector.getAsymptotes();
		for (IntervalTuple tuple: asymptotes) {
			double sx = view.toScreenCoordXd(tuple.x().getLow());
			if (DoubleUtil.isEqual(tuple.y().getLow(), Double.NEGATIVE_INFINITY)) {
				double sy = view.toScreenCoordYd(tuple.y().getHigh());
				gp.moveTo(sx, sy);
				gp.lineTo(sx, view.getMaxYScreen());
			} else {
				double sy = view.toScreenCoordYd(tuple.y().getLow());
				gp.moveTo(sx, sy);
				gp.lineTo(sx, 0);
			}
		}
		g2.setPaint(GColor.RED);
		g2.draw(gp);
		g2.setPaint(geo.getObjectColor());
	}
}
