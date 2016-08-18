/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbclient;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import stateless.PersistBeanRemote;

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
    private PersistBeanRemote persistBean;

    @FXML
    private void handleStart(ActionEvent event) {
        if (persistBean == null) {
            try {
                getStBean();
            } catch (NamingException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (persistBean == null) {
                System.out.println("Could not get bean");
                return;
            }
        }

        sender = new MsgSender(sentCount, dbCount, timePerMsg, persistBean);
        sender.start();
    }

    @FXML
    private void handleStop(ActionEvent event) {
        sender.getStatBuider().setStop(true);
        sender.setStop(true);
    }

    @FXML
    private void handleCleanTable() {
        if (persistBean == null) {
            try {
                getStBean();
            } catch (NamingException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (persistBean == null) {
                System.out.println("Could not get bean");
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

    private PersistBeanRemote getStBean() throws NamingException {
        InitialContext ctx = createContext("http-remoting://localhost:8080", "jmsMe", "pass");
        if (ctx == null) {
            System.out.println("createContext error");
            return null;
        }
        persistBean = null;
        try {
            persistBean = (PersistBeanRemote) ctx.lookup("EJBServer/PersistBean!stateless.PersistBeanRemote");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
        return persistBean;
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
}
