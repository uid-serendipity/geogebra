package org.geogebra.common.euclidian.draw;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
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
		gp.reset();
		detector.update();
	}

	public void draw(GGraphics2D g2) {
		List<IntervalTuple> asymptotes = detector.getAsymptotes();
		for (IntervalTuple tuple: asymptotes) {
			Interval y = tuple.y();
			if (!y.isWhole()) {
				if (y.isInverted()) {
					extendInvertedAsyptote(tuple);
				} else {
					extendAsyptote(tuple);
				}
			}
		}

		g2.setPaint(GColor.RED);
		g2.draw(gp);
		g2.setPaint(geo.getObjectColor());
	}

	private void extendInvertedAsyptote(IntervalTuple tuple) {
		Interval lowInterval = tuple.y().extractLow();
		Interval highInterval = tuple.y().extractHigh();
		if (!lowInterval.isInfiniteSingleton()) {
			drawBottom(tuple.x(), lowInterval);
		}
		if (!highInterval.isInfiniteSingleton()) {
			drawTop(tuple.x(), highInterval);
		}
	}

	private void drawBottom(Interval x, Interval y) {
		double sx = view.toScreenCoordXd(x.getLow());
		double sy = view.toScreenCoordYd(y.getHigh());
		gp.moveTo(sx, sy);
		gp.lineTo(sx, view.getMaxYScreen());
	}

	private void drawTop(Interval x, Interval y) {
		double sx = view.toScreenCoordXd(x.getLow());
		double sy = view.toScreenCoordYd(y.getHigh());
		gp.moveTo(sx, sy);
		gp.lineTo(sx, 0);
	}

	private void extendAsyptote(IntervalTuple tuple) {
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
}
