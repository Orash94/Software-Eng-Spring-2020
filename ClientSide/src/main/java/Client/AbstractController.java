package Client;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import UtilClasses.DataElements;
import UtilClasses.DataElements.ClientToServerOpcodes;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

public abstract class AbstractController {

	protected Alert alert = new Alert(Alert.AlertType.ERROR);

	protected final String ERROR_TITLE_SERVER = "An error occurred while retrieving data from server";

	protected final String ERROR_TITLE_Client = "An error occurred while the system was hanaling your actions";

	private static boolean msgRecived = false;

	static Timer timer;

	static void msgRecieved() {
		msgRecived = true;
	}
	
	protected void sendRequest(ClientToServerOpcodes op, Object data) {
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				try {
					GetDataFromDB(op, data);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		timer = new Timer("RefreshingTimer");

		timer.scheduleAtFixedRate(timerTask, 30, 10000);
	}

	/**
	 * getDataFromServer(DataElements) The function calls the
	 * ClientMain.sendMessageToServer(Object) function
	 * 
	 * @param DataElements with opcode and data
	 * @return -1 for fail
	 */
	protected synchronized int sendRequestForDataFromServer(DataElements de) {
		int status;
		try {
			status = ClientMain.sendMessageToServer(de);
		} catch (IOException e) {
			status = -1;
			String errorMessage = "The system could not receive data from server. please reconnect and try again";
			popError(ERROR_TITLE_SERVER, errorMessage);
			e.printStackTrace();
		}

		return status;
	}

	/**
	 * Creating request to get data from server
	 * 
	 * @param op   - what type of request do we want (Enum)
	 * @param data - the date we want to send to server
	 * @return
	 * @throws InterruptedException Pause the main GUI thread
	 */
	public synchronized void GetDataFromDB(ClientToServerOpcodes op, Object data) throws InterruptedException {
		String initErrors = "";
		ClientMain.addController(this.getClass().toString().split("Client.")[1], this);
		ClientMain.setCurrController(this.getClass().toString().split("Client.")[1]);
		int dbStatus = sendRequestForDataFromServer(new DataElements(op, data));
		if ((dbStatus == -1)) {
			initErrors += "The system cannot retrieve studies from server\n";
		}

		if (!initErrors.isEmpty())
			popError(ERROR_TITLE_SERVER, initErrors);
		while (!msgRecived) {
			Thread.onSpinWait();
		}
		msgRecived = false;
	}

	void switchMainPanel(String Sfxml) {
		Platform.runLater(() -> {
			((mainController) ClientService.getController("mainController")).setMainPanel(Sfxml);
		});
	}

	public void initialize() {

	}
	
	

	/**
	 * Invokes an info alert message Mostly for a success create of objects on
	 * server
	 * 
	 * @param title-   Window title
	 * @param content- detailed info about the message
	 */
	void showMsg(String title, String content) {
		Platform.runLater(() -> {
			Alert info = new Alert(Alert.AlertType.INFORMATION);
			info.setTitle(title);
			info.setHeaderText(content);
			info.showAndWait();
		});
	}

	/**
	 * Error message
	 * 
	 * @param title        - main content of the error
	 * @param errorMessage - the detailed content of error
	 */
	public void popError(String title, String errorMessage) {
		Platform.runLater(() -> {
			alert.setHeaderText(title);
			alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(errorMessage)));
			alert.showAndWait();
		});
	}
	
	public static void stopTimer() {
		timer.cancel();
	}
}
