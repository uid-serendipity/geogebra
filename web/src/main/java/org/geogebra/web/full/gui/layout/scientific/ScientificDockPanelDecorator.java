package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.toolbarpanel.tableview.StickyValuesTable;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Adds the scientific header to AV panel.
 */
public final class ScientificDockPanelDecorator implements DockPanelDecorator {

	// TODO to find out where is this come from.
	public static final int TAB_HEIGHT_DIFFERENCE = 40;
	public static final int TABLE_HEIGHT_DIFFERENCE = 64;
	private FlowPanel main;
	private Widget tableTab;
	private StickyTable<?> table;

	@Override
	public Panel decorate(Panel wrapper, AppW appW) {
		main = new FlowPanel();
		main.setWidth("100%");
		main.add(wrapper);
		main.addStyleName("algebraPanel");
		return buildAndStylePanel(appW);
	}

	private Panel buildAndStylePanel(AppW app) {
		FlowPanel panel = new FlowPanel();
		stylePanel(panel);
		panel.add(main);
		main.addStyleName("algebraPanelScientific");

		ScientificScrollHandler scrollController = new ScientificScrollHandler(
				app, panel);
		panel.addDomHandler(scrollController, MouseDownEvent.getType());
		panel.addBitlessDomHandler(scrollController, TouchStartEvent.getType());
		return panel;
	}

	private static void stylePanel(Panel panel) {
		panel.setHeight("100%");
	}

	@Override
	public void onResize(AlgebraViewW aView, int offsetHeight) {
		toggleSmallScreen(main, aView.getApp().getAppletFrame()
				.shouldHaveSmallScreenLayout());
	}

	private void toggleSmallScreen(Widget w, boolean smallScreen) {
		Dom.toggleClass(w, "algebraPanelScientificSmallScreen",
				"panelScientificDefaults", smallScreen);
	}

	@Override
	public void resizeTable(int tabHeight) {
		table.setHeight(tabHeight - TABLE_HEIGHT_DIFFERENCE);
		tableTab.setHeight((tabHeight + TAB_HEIGHT_DIFFERENCE) + "px");
		toggleSmallScreen(tableTab, false);
	}

	@Override
	public void resizeTableSmallScreen(int tabHeight) {
		resizeTable(tabHeight);
		toggleSmallScreen(tableTab, true);
	}

	@Override
	public void decorateTableTab(Widget tab, StickyTable<?> table) {
		tableTab = tab;
		this.table = table;
		tab.addStyleName("panelScientificDefaults");
		disableShadedColumns((StickyValuesTable) table);
		table.addStyleName("scientific");
	}

	@Override
	public int getTabHeight(int tabHeight) {
		return tabHeight - TAB_HEIGHT_DIFFERENCE;
	}

	private void disableShadedColumns(StickyValuesTable table) {
		table.disableShadedColumns();
	}
}