package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.LinkedList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolverInterface;
import org.geogebra.common.kernel.Kernel;
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
	private ArrayList<MySpecialDouble> multiplyCoeffs = new ArrayList<>();

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
				multiplyCoeffs.add((MySpecialDouble) node.getLeft());
				if (node.getRight() instanceof MySpecialDouble) {
					numberValues += 1;
					multiplyCoeffs.add((MySpecialDouble) node.getRight());
				}
			}
			if (node.getLeft() instanceof ExpressionNode) {
				if (node.getRight() instanceof ExpressionNode) {
					return isFactored((ExpressionNode) node.getLeft()) && isFactored(
							(ExpressionNode) node.getRight());
				} else if (node.getRight() instanceof MySpecialDouble) {
					numberValues += 1;
					multiplyCoeffs.add((MySpecialDouble) node.getRight());
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
				boolean isSameSign = true;
				if (factorList != null) {
					// check coefficients of polynomial
					for (PolyFunction pf : factorList) {
						pf.updateCoeffValues();
						coeffs = pf.getCoeffsCopy();
						// return false for e.g. 2x-2
						if (Kernel.gcd(coeffs) != 1) {
							return false;
						}
						// return false for e.g. x^2+x
						if (coeffs.length > 2) {
							if (coeffs[0] == 0) {
								return false;
							}
						}
						if (canBeSimplified(node, coeffs)) {
							return false;
						}
						value = coeffs[coeffs.length - 1];
						for (double c : coeffs) {
							if (!(((int) value ^ (int) c) >= 0)) {
								isSameSign = false;
								break;
							}
						}
					}
					if (value < 0 && isSameSign) {
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

	private boolean canBeSimplified(ExpressionNode node, double[] coeffs) {
		ArrayList<Double> coeffsNotInt = new ArrayList<>();
		for (double c : coeffs) {
			if ((int) c != c) {
				coeffsNotInt.add(c);
			}
		}
		if (coeffsNotInt.isEmpty()) {
			return false;
		}
		double product = 1;
		for (MySpecialDouble d : multiplyCoeffs) {
			product *= d.getDouble();
		}
		boolean allInteger = true;
		for (double c : coeffs) {
			double cMultiplied = c * product;
			if ((int) cMultiplied != cMultiplied) {
				allInteger = false;
				break;
			}
		}
		return allInteger;
	}
}
