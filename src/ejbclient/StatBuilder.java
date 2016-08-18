/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbclient;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import stateless.PersistBeanRemote;

/**
 *
 * @author iskra
 */
public class StatBuilder extends Thread {

    private boolean stop;
    private final long startTime;
    private TextField sentField;
    private TextField dbField;
    private TextField timeField;
    private long totalMsg;
    private long count;
    private PersistBeanRemote persistBean;

    public StatBuilder(TextField sentCount,TextField dbCount,TextField timePerMsg,PersistBeanRemote p) {
        this.startTime = System.currentTimeMillis();
        this.stop = false;
        this.sentField=sentCount;
        this.dbField=dbCount;
        this.timeField=timePerMsg;
        this.totalMsg=0;
        this.persistBean=p;
    }

    @Override
    public void run() {
        long initDbrecords = persistBean.getDbCount();
        while (!stop || count!=totalMsg) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
            totalMsg = persistBean.getDbCount() - initDbrecords;
            if(totalMsg==0) continue;
            long mlsPerMsg = (System.currentTimeMillis() - startTime) / totalMsg;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    sentField.setText(String.valueOf(count));
                    dbField.setText(String.valueOf(totalMsg));
                    timeField.setText(String.valueOf(mlsPerMsg));
                }
            });
//            System.out.println(initDbrecords);
        }
        System.out.println("Stat thread stopped!");
    }

    private void hult() {
        this.stop = true;
    }

    

    public void setStop(boolean stop) {
        this.stop = stop;
    }
    public void setCount(long count){
        this.count=count;
    }
}
