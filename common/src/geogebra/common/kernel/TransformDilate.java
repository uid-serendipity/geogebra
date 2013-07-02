package geogebra.common.kernel;

import geogebra.common.kernel.algos.AlgoDilate;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Dilation
 * 
 * @author kondr
 * 
 */
public class TransformDilate extends Transform {

	protected NumberValue ratio;
	protected GeoPointND center;

	/**
	 * @param cons construction
	 * @param ratio dilation ratio
	 */
	public TransformDilate(Construction cons,NumberValue ratio) {
		this.ratio = ratio;
		this.cons = cons;
	}

	/**
	 * @param cons construction
	 * @param ratio dilation ratio
	 * @param center dilation center
	 */
	public TransformDilate(Construction cons,NumberValue ratio, GeoPointND center) {
		this.ratio = ratio;
		this.center = center;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoDilate algo = new AlgoDilate(cons, geo, ratio, center);
		return algo;
	}

}
