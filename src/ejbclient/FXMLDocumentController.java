/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbclient;

import java.net.URL;
import java.util.Date;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

/**
 *
 * @author iskra
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TextField timePerMsg;
    @FXML
    private TextField sentCount;
    @FXML
    private TextField dbCount;
    private MsgSender sender;

    @FXML
    private void handleStart(ActionEvent event) {
        sender = new MsgSender(sentCount, dbCount, timePerMsg);
        sender.start();
    }

    @FXML
    private void handleStop(ActionEvent event) {
        sender.getStatBuider().setStop(true);
        sender.setStop(true);
    }

    @FXML
    private void handleCleanTable() {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }
}
