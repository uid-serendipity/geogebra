package org.geogebra.common.kernel.algos;

import java.util.LinkedList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolverInterface;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.Operation;

public class AlgoIsFactored extends AlgoElement {

	private GeoElement inputGeo; // input
	private GeoBoolean outputBoolean; // output

	private FunctionVariable fv;

	protected EquationSolverInterface eqnSolver;
	private final Solution solution = new Solution();

	private int numberValues = 0;

	/**
	 * @param cons construction
	 * @param label label
	 * @param inputGeo function or number
	 */
	public AlgoIsFactored(Construction cons, String label, GeoElement inputGeo) {
		super(cons);
		this.inputGeo = inputGeo;

		outputBoolean = new GeoBoolean(cons);

		eqnSolver = cons.getKernel().getEquationSolver();
		solution.resetRoots();

		setInputOutput();
		compute();
		outputBoolean.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.IsFactored;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputGeo;

		super.setOutputLength(1);
		super.setOutput(0, outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return boolean result
	 */
	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		if (inputGeo instanceof GeoNumeric) {
			outputBoolean.setValue(true);
			return;
		}
		if (inputGeo instanceof GeoFunction) {
			if (!((GeoFunction) inputGeo).getFunction().isPolynomialFunction(true, false)) {
				outputBoolean.setUndefinedProverOnly();
				return;
			}
			fv = ((GeoFunction) inputGeo).getFunction().getFunctionVariable();
			ExpressionNode node = ((GeoFunction) inputGeo).getFunctionExpression();
			if (node != null) {
				boolean isFactored = isFactored(node);
				if (isFactored && numberValues > 1) {
					isFactored = false;
				}
				outputBoolean.setValue(isFactored);
			}
		}
	}

	private boolean isFactored(ExpressionNode node) {
		if (node.isOperation(Operation.MULTIPLY)) {
			if (node.getLeft() instanceof MySpecialDouble) {
				numberValues += 1;
				if (node.getRight() instanceof MySpecialDouble) {
					numberValues += 1;
				}
			}
			if (node.getLeft() instanceof ExpressionNode) {
				if (node.getRight() instanceof ExpressionNode) {
					return isFactored((ExpressionNode) node.getLeft()) && isFactored(
							(ExpressionNode) node.getRight());
				} else if (node.getRight() instanceof MySpecialDouble) {
					numberValues += 1;
				}
				return isFactored((ExpressionNode) node.getLeft());
			} else if (node.getRight() instanceof ExpressionNode) {
				return isFactored((ExpressionNode) node.getRight());
			}
			return true;
		}

		if (node.isOperation(Operation.POWER)) {
			if (node.getRight() instanceof MySpecialDouble) {
				if (node.getLeft() instanceof ExpressionNode) {
					return isFactored((ExpressionNode) node.getLeft());
				}
				return true;
			}
			return false;
		}
		return isFactoredPolynomial(node);
	}

	private boolean isFactoredPolynomial(ExpressionNode node) {
		GeoFunction geoFun = node.buildFunction(fv);
		Function fun = geoFun.getFunction();
		if (fun != null && fun.isPolynomialFunction(true, false)) {
			PolyFunction polyFun = fun.expandToPolyFunction(fun.getExpression(),
					false, false);
			if (polyFun != null) {
				LinkedList<PolyFunction> factorList = fun.getPolynomialFactors(true, true);
				double[] coeffs;
				double value = 1.0;
				boolean isSame = true;
				if (factorList != null) {
					// check coefficients of polynomial
					for (PolyFunction pf : factorList) {
						pf.updateCoeffValues();
						coeffs = pf.getCoeffsCopy();
						value = coeffs[coeffs.length - 1];
						for (double c : coeffs) {
							if ((int) c != c) {
								return false;
							}
							// return false for e.g. 2x-2
							if (Math.abs(c) != Math.abs(value)) {
								isSame = false;
							}
						}
					}
					if (isSame && value != 1.0) {
						return false;
					}
				}
				int degree = polyFun.getDegree();
				if (degree > 1) {
					// check roots
					AlgoRootsPolynomial
							.calcRootsMultiple(fun, 0, solution, eqnSolver);
					int numRoots = solution.curRealRoots;
					if (numRoots > 1) {
						for (int i = 0; i < numRoots; i++) {
							// take only integer roots
							if ((int) solution.curRoots[i] != i) {
								return false;
							}
						}
					}
					return !(numRoots == 1
							&& (int) solution.curRoots[0] == solution.curRoots[0]);
				}
				return true;
			}
		}
		return false;
	}
}
