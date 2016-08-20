/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbclient;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextField;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import stateless.PersistBeanRemote;

//отвечает за отправку сообщений
public class MsgSender extends Thread {

    private boolean stop;
    private TextField sentField;
    private TextField dbField;
    private TextField timeField;
    private StatBuilder statBuider;
    private PersistBeanRemote persistBean;

    public MsgSender(TextField sentCount, TextField dbCount, TextField timePerMsg,PersistBeanRemote p) {
        this.stop = false;
        this.sentField = sentCount;
        this.dbField = dbCount;
        this.timeField = timePerMsg;
        this.persistBean=p;
    }

    @Override
    public void run() {
        String msg = "{\"latitude\":100.0,\"longitude\":200.0,\"horizontalAccuracy\":300,\"speed\":75.0,\"course\":10,\"altitude\":20,\"verticalAccuracy\":5,\"actuality\":\"GPS\",\"timestamp\":1393337635000}";
        InitialContext ctx = createContext("http-remoting://127.0.0.1:8080", "jmsMe", "pass");
        if (ctx == null) {
            System.out.println("createContext error");
            hult();
        }

//        ищем очередь
        QueueSender sender = null;
        QueueSession session = null;
        QueueConnection connection = null;
        try {
            Queue queue = (Queue) ctx.lookup("jms/queue/ejb3Queue");
            QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
            connection = factory.createQueueConnection("jmsMe", "pass");
            session = connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
            sender = session.createSender(queue);

        } catch (NamingException | JMSException ex) {
            System.out.println(ex.getMessage());
            hult();
        }

//        запускаем поток отвечающий за обновление view
        statBuider = new StatBuilder(sentField, dbField, timeField,persistBean);
        statBuider.start();

//        непрерывно отправляем сообщения в очередь
        while (!stop) {
            try {
                ObjectMessage objectMessage = session.createObjectMessage(msg);
                sender.send(objectMessage);
                //обновляем счетчки отправленных сообщений
                statBuider.incCount();
            } catch (JMSException ex) {
                System.out.println(ex.getMessage());
                hult();
            }
        }

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Failed to close connection");
            Logger.getLogger(MsgSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Sending thread stopped!");
    }

    public void hult() {
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

    public StatBuilder getStatBuider() {
        return statBuider;
    }

}
