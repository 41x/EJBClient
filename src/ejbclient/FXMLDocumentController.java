/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbclient;

import java.net.URL;
import java.util.Date;
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
    private TextField queueMax;
    @FXML
    private TextField dbCount;
    @FXML
    private TextField queueCount;
    @FXML
    private TextField sentCount;
    @FXML
    private Label sliderCount;
    @FXML
    private Slider slider;

    @FXML
    private void handleStart(ActionEvent event) {
        try {
            int intervals=(int)slider.getValue()-1;
            
            
            long start = System.currentTimeMillis();
            System.out.println(new Date() + "\n");

            Thread.sleep(1000);
            System.out.println(new Date() + "\n");

            long end = System.currentTimeMillis();
            long diff = end - start;
            System.out.println("Difference is : " + diff);
        } catch (Exception e) {
            System.out.println("Got an exception!");
        }

    }

    @FXML
    private void handleStop(ActionEvent event) {

    }

    @FXML
    private void handleDrag() {
    }

    @FXML
    private void handleDone() {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

    public Label getSliderCount() {
        return sliderCount;
    }

    public Slider getSlider() {
        return slider;
    }

}
