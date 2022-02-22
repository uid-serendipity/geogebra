package org.geogebra.common.gui.view.algebra.fiter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Allows every kind of output row.
 */
public class DefaultAlgebraOutputFilter implements AlgebraOutputFilter {

	@Override
	public boolean isAllowed(GeoElementND element) {
		boolean allowed = true;
		if (element instanceof GeoSymbolic
				&& element.getDefinition().getLeft() instanceof Command
				&& Commands.If.name()
				.equals(((Command) element.getDefinition().getLeft()).getName())) {
			allowed = false;
		}
		return allowed;
	}
}
