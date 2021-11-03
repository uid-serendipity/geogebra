package org.geogebra.web.html5.main;

import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Default error handler
 */
public class ErrorHandlerW implements ErrorHandler {
	private final AppW app;

	/**
	 * @param app
	 *            application
	 */
	public ErrorHandlerW(AppW app) {
		this.app = app;
	}

	@Override
	public void showError(String msg) {
		if (!app.isErrorDialogsActive()) {
			return;
		}
		DialogData data = new DialogData("Error", null, "OK");
		showErrorDialog(data, msg, null);
	}

	@Override
	public void resetError() {
		// do nothing
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return app.getGuiManager().checkAutoCreateSliders(string, callback);
	}

	@Override
	public void showCommandError(final String command, String message) {
		if (!app.isErrorDialogsActive()) {
			return;
		}
		DialogData data = new DialogData(app.getLocalization().getError("Error"),
				"Close", "ShowOnlineHelp");
		showErrorDialog(data, message, () -> openCommandHelp(command));
	}

	private void showErrorDialog(DialogData data, String message, Runnable posBtnAction) {
		ComponentDialog dialog = new ComponentDialog(app, data, false, true);
		FlowPanel messagePanel = new FlowPanel();
		String[] lines = message.split("\n");
		for (String item : lines) {
			messagePanel.add(new Label(item));
		}
		dialog.addDialogContent(messagePanel);
		if (posBtnAction != null) {
			dialog.setOnPositiveAction(posBtnAction::run);
		}
		dialog.show();
	}

	/**
	 * @param command
	 *            command name
	 */
	protected void openCommandHelp(String command) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().openCommandHelp(command);
		}
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}
}
