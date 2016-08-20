/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbclient;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import stateless.PersistBeanRemote;

//отвечает за обновление данных view
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
        this.count=0;
    }

    @Override
    public void run() {
        //получаем начальное количество записей в таблице
        long initDbrecords = persistBean.getDbCount();
        //пока нет команды стоп либо не все сообщения сохранениы в БД
        while (!stop || count!=totalMsg) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
            totalMsg = persistBean.getDbCount() - initDbrecords;
            if(totalMsg==0) continue;
            long mlsPerMsg = (System.currentTimeMillis() - startTime) / totalMsg;
//            обновляем view
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    sentField.setText(String.valueOf(count));
                    dbField.setText(String.valueOf(totalMsg));
                    timeField.setText(String.valueOf(mlsPerMsg));
                }
            });
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
    public void incCount(){
        this.count++;
    }
}
