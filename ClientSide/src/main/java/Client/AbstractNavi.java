package Client;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public abstract class AbstractNavi {
	void switchMainPanel(String Sfxml) {
		((mainController) ClientService.getController("mainController")).setMainPanel(Sfxml);
	}

	@FXML
	void logout(MouseEvent event) {
		ClientMain.removeAllControllers();
		mainController.loggedOut();
		try {
			App.changeStage("loginController", "login");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
