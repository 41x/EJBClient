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

    public StatBuilder(TextField sentCount,TextField dbCount,TextField timePerMsg) {
        this.startTime = System.currentTimeMillis();
        this.stop = false;
        this.sentField=sentCount;
        this.dbField=dbCount;
        this.timeField=timePerMsg;
        this.totalMsg=0;
    }

    @Override
    public void run() {
        InitialContext ctx = createContext("http-remoting://localhost:8080", "jmsMe", "pass");
        if (ctx == null) {
            System.out.println("createContext error");
            hult();
        }

        PersistBeanRemote persistBean = null;
        try {
            persistBean = (PersistBeanRemote) ctx.lookup("EJBServer/PersistBean!stateless.PersistBeanRemote");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            hult();
        }

        long initDbrecords = persistBean.getDbCount();
        while (!stop || count!=totalMsg) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
            totalMsg = persistBean.getDbCount();// - initDbrecords;
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
            System.out.println(initDbrecords);
        }
        System.out.println("Stat thread stopped!");
    }

    private void hult() {
        this.stop = true;
    }

    private InitialContext createContext(String url, String login, String password) {
        Properties jndiProps = new Properties();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        jndiProps.put(Context.PROVIDER_URL, url);
        jndiProps.put(Context.SECURITY_PRINCIPAL, login);
        jndiProps.put(Context.SECURITY_CREDENTIALS, password);
        jndiProps.put("jboss.naming.client.ejb.context", true);
        try {
            return new InitialContext(jndiProps);
        } catch (NamingException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
    public void setCount(long count){
        this.count=count;
    }
}
