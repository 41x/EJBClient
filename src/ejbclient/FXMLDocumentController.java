/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbclient;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import stateless.PersistBeanRemote;

public class FXMLDocumentController implements Initializable {

    @FXML
    private TextField timePerMsg;
    @FXML
    private TextField sentCount;
    @FXML
    private TextField dbCount;
    private MsgSender sender;
    private PersistBeanRemote persistBean;

    
    @FXML
    private void handleStart(ActionEvent event) {
//        ищем по jndi stateless bean отвечающий за запросы в БД
        if (persistBean == null) {
            try {
                initSBean();
            } catch (NamingException ex) {
                System.out.println(ex.getMessage()+"\nCould not get bean");
                return;
            }
        }
//        запускаем поток отвечающий за отправку
        sender = new MsgSender(sentCount, dbCount, timePerMsg, persistBean);
        sender.start();
    }

    @FXML
    private void handleStop(ActionEvent event) {
//        закрываем потоки отв за отправку и вывод на экран
        sender.getStatBuider().setStop(true);
        sender.setStop(true);
    }

//    очищает таблицу в БД
    @FXML
    private void handleCleanTable() {
        if (persistBean == null) {
            try {
                initSBean();
            } catch (NamingException ex) {
                System.out.println(ex.getMessage()+"\nCould not get bean");
                return;
            }
        }
        try {
            persistBean.cleanTable();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

    public void setPersistBean(PersistBeanRemote persistBean) {
        this.persistBean = persistBean;
    }

    
    private PersistBeanRemote initSBean() throws NamingException {
        InitialContext ctx = createContext("http-remoting://127.0.0.1:8080", "jmsMe", "pass");
        persistBean = null;
        persistBean = (PersistBeanRemote) ctx.lookup("EJBServer/PersistBean!stateless.PersistBeanRemote");
        return persistBean;
    }

    private InitialContext createContext(String url, String login, String password) throws NamingException {
        Properties jndiProps = new Properties();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        jndiProps.put(Context.PROVIDER_URL, url);
        jndiProps.put(Context.SECURITY_PRINCIPAL, login);
        jndiProps.put(Context.SECURITY_CREDENTIALS, password);
        jndiProps.put("jboss.naming.client.ejb.context", true);
        return new InitialContext(jndiProps);
    }
}
