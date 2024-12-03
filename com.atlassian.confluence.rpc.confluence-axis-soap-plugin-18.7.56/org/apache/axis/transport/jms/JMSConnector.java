/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jms.BytesMessage
 *  javax.jms.Connection
 *  javax.jms.ConnectionFactory
 *  javax.jms.Destination
 *  javax.jms.ExceptionListener
 *  javax.jms.JMSException
 *  javax.jms.Message
 *  javax.jms.MessageConsumer
 *  javax.jms.MessageProducer
 *  javax.jms.Session
 */
package org.apache.axis.transport.jms;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.axis.components.jms.JMSVendorAdapter;
import org.apache.axis.transport.jms.InvokeException;
import org.apache.axis.transport.jms.InvokeTimeoutException;
import org.apache.axis.transport.jms.JMSConnectorManager;
import org.apache.axis.transport.jms.JMSEndpoint;
import org.apache.axis.transport.jms.JMSURLHelper;
import org.apache.axis.transport.jms.MapUtils;
import org.apache.axis.transport.jms.Subscription;

public abstract class JMSConnector {
    protected int m_numRetries;
    protected long m_connectRetryInterval;
    protected long m_interactRetryInterval;
    protected long m_timeoutTime;
    protected long m_poolTimeout;
    protected AsyncConnection m_receiveConnection;
    protected SyncConnection m_sendConnection;
    protected int m_numSessions;
    protected boolean m_allowReceive;
    protected JMSVendorAdapter m_adapter;
    protected JMSURLHelper m_jmsurl;

    public JMSConnector(ConnectionFactory connectionFactory, int numRetries, int numSessions, long connectRetryInterval, long interactRetryInterval, long timeoutTime, boolean allowReceive, String clientID, String username, String password, JMSVendorAdapter adapter, JMSURLHelper jmsurl) throws JMSException {
        this.m_numRetries = numRetries;
        this.m_connectRetryInterval = connectRetryInterval;
        this.m_interactRetryInterval = interactRetryInterval;
        this.m_timeoutTime = timeoutTime;
        this.m_poolTimeout = timeoutTime / (long)numRetries;
        this.m_numSessions = numSessions;
        this.m_allowReceive = allowReceive;
        this.m_adapter = adapter;
        this.m_jmsurl = jmsurl;
        javax.jms.Connection sendConnection = this.createConnectionWithRetry(connectionFactory, username, password);
        this.m_sendConnection = this.createSyncConnection(connectionFactory, sendConnection, this.m_numSessions, "SendThread", clientID, username, password);
        this.m_sendConnection.start();
        if (this.m_allowReceive) {
            javax.jms.Connection receiveConnection = this.createConnectionWithRetry(connectionFactory, username, password);
            this.m_receiveConnection = this.createAsyncConnection(connectionFactory, receiveConnection, "ReceiveThread", clientID, username, password);
            this.m_receiveConnection.start();
        }
    }

    public int getNumRetries() {
        return this.m_numRetries;
    }

    public int numSessions() {
        return this.m_numSessions;
    }

    public ConnectionFactory getConnectionFactory() {
        return this.getSendConnection().getConnectionFactory();
    }

    public String getClientID() {
        return this.getSendConnection().getClientID();
    }

    public String getUsername() {
        return this.getSendConnection().getUsername();
    }

    public String getPassword() {
        return this.getSendConnection().getPassword();
    }

    public JMSVendorAdapter getVendorAdapter() {
        return this.m_adapter;
    }

    public JMSURLHelper getJMSURL() {
        return this.m_jmsurl;
    }

    protected javax.jms.Connection createConnectionWithRetry(ConnectionFactory connectionFactory, String username, String password) throws JMSException {
        javax.jms.Connection connection = null;
        int numTries = 1;
        while (connection == null) {
            try {
                connection = this.internalConnect(connectionFactory, username, password);
            }
            catch (JMSException jmse) {
                if (!this.m_adapter.isRecoverable(jmse, 1) || numTries == this.m_numRetries) {
                    throw jmse;
                }
                try {
                    Thread.sleep(this.m_connectRetryInterval);
                }
                catch (InterruptedException ie) {
                    // empty catch block
                }
            }
            ++numTries;
        }
        return connection;
    }

    public void stop() {
        JMSConnectorManager.getInstance().removeConnectorFromPool(this);
        this.m_sendConnection.stopConnection();
        if (this.m_allowReceive) {
            this.m_receiveConnection.stopConnection();
        }
    }

    public void start() {
        this.m_sendConnection.startConnection();
        if (this.m_allowReceive) {
            this.m_receiveConnection.startConnection();
        }
        JMSConnectorManager.getInstance().addConnectorToPool(this);
    }

    public void shutdown() {
        this.m_sendConnection.shutdown();
        if (this.m_allowReceive) {
            this.m_receiveConnection.shutdown();
        }
    }

    public abstract JMSEndpoint createEndpoint(String var1) throws JMSException;

    public abstract JMSEndpoint createEndpoint(Destination var1) throws JMSException;

    protected abstract javax.jms.Connection internalConnect(ConnectionFactory var1, String var2, String var3) throws JMSException;

    protected abstract SyncConnection createSyncConnection(ConnectionFactory var1, javax.jms.Connection var2, int var3, String var4, String var5, String var6, String var7) throws JMSException;

    SyncConnection getSendConnection() {
        return this.m_sendConnection;
    }

    AsyncConnection getReceiveConnection() {
        return this.m_receiveConnection;
    }

    protected abstract AsyncConnection createAsyncConnection(ConnectionFactory var1, javax.jms.Connection var2, String var3, String var4, String var5, String var6) throws JMSException;

    private abstract class ConnectorSession {
        Session m_session;

        ConnectorSession(Session session) throws JMSException {
            this.m_session = session;
        }
    }

    protected abstract class AsyncConnection
    extends Connection {
        HashMap m_subscriptions = new HashMap();
        Object m_subscriptionLock = new Object();

        protected AsyncConnection(ConnectionFactory connectionFactory, javax.jms.Connection connection, String threadName, String clientID, String username, String password) throws JMSException {
            super(connectionFactory, connection, threadName, clientID, username, password);
        }

        protected abstract ListenerSession createListenerSession(javax.jms.Connection var1, Subscription var2) throws Exception;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void onShutdown() {
            Object object = this.m_subscriptionLock;
            synchronized (object) {
                Iterator subscriptions = this.m_subscriptions.keySet().iterator();
                while (subscriptions.hasNext()) {
                    Subscription subscription = (Subscription)subscriptions.next();
                    ListenerSession session = (ListenerSession)this.m_subscriptions.get(subscription);
                    if (session == null) continue;
                    session.cleanup();
                }
                this.m_subscriptions.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void subscribe(Subscription subscription) throws Exception {
            long timeoutTime = System.currentTimeMillis() + JMSConnector.this.m_timeoutTime;
            Object object = this.m_subscriptionLock;
            synchronized (object) {
                if (this.m_subscriptions.containsKey(subscription)) {
                    return;
                }
                while (true) {
                    if (System.currentTimeMillis() > timeoutTime) {
                        throw new InvokeTimeoutException("Cannot subscribe listener");
                    }
                    try {
                        ListenerSession session = this.createListenerSession(this.m_connection, subscription);
                        this.m_subscriptions.put(subscription, session);
                    }
                    catch (JMSException jmse) {
                        if (!JMSConnector.this.m_adapter.isRecoverable(jmse, 2)) {
                            throw jmse;
                        }
                        try {
                            this.m_subscriptionLock.wait(JMSConnector.this.m_interactRetryInterval);
                        }
                        catch (InterruptedException ignore) {
                            // empty catch block
                        }
                        Thread.yield();
                        continue;
                    }
                    catch (NullPointerException jmse) {
                        try {
                            this.m_subscriptionLock.wait(JMSConnector.this.m_interactRetryInterval);
                        }
                        catch (InterruptedException ignore) {
                            // empty catch block
                        }
                        Thread.yield();
                        continue;
                    }
                    break;
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void unsubscribe(Subscription subscription) {
            long timeoutTime = System.currentTimeMillis() + JMSConnector.this.m_timeoutTime;
            Object object = this.m_subscriptionLock;
            synchronized (object) {
                if (!this.m_subscriptions.containsKey(subscription)) {
                    return;
                }
                while (true) {
                    if (System.currentTimeMillis() > timeoutTime) {
                        throw new InvokeTimeoutException("Cannot unsubscribe listener");
                    }
                    Thread.yield();
                    try {
                        ListenerSession session = (ListenerSession)this.m_subscriptions.get(subscription);
                        session.cleanup();
                        this.m_subscriptions.remove(subscription);
                    }
                    catch (NullPointerException jmse) {
                        try {
                            this.m_subscriptionLock.wait(JMSConnector.this.m_interactRetryInterval);
                        }
                        catch (InterruptedException ignore) {}
                        continue;
                    }
                    break;
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void onConnect() throws Exception {
            Object object = this.m_subscriptionLock;
            synchronized (object) {
                Iterator subscriptions = this.m_subscriptions.keySet().iterator();
                while (subscriptions.hasNext()) {
                    Subscription subscription = (Subscription)subscriptions.next();
                    if (this.m_subscriptions.get(subscription) != null) continue;
                    this.m_subscriptions.put(subscription, this.createListenerSession(this.m_connection, subscription));
                }
                this.m_subscriptionLock.notifyAll();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void onException() {
            Object object = this.m_subscriptionLock;
            synchronized (object) {
                Iterator subscriptions = this.m_subscriptions.keySet().iterator();
                while (subscriptions.hasNext()) {
                    Subscription subscription = (Subscription)subscriptions.next();
                    this.m_subscriptions.put(subscription, null);
                }
            }
        }

        protected class ListenerSession
        extends ConnectorSession {
            protected MessageConsumer m_consumer;
            protected Subscription m_subscription;

            ListenerSession(Session session, MessageConsumer consumer, Subscription subscription) throws Exception {
                super(session);
                this.m_subscription = subscription;
                this.m_consumer = consumer;
                Destination destination = subscription.m_endpoint.getDestination(this.m_session);
                this.m_consumer.setMessageListener(subscription.m_listener);
            }

            void cleanup() {
                try {
                    this.m_consumer.close();
                }
                catch (Exception ignore) {
                    // empty catch block
                }
                try {
                    this.m_session.close();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
    }

    protected abstract class SyncConnection
    extends Connection {
        LinkedList m_senders = new LinkedList();
        int m_numSessions;
        Object m_senderLock;

        SyncConnection(ConnectionFactory connectionFactory, javax.jms.Connection connection, int numSessions, String threadName, String clientID, String username, String password) throws JMSException {
            super(connectionFactory, connection, threadName, clientID, username, password);
            this.m_numSessions = numSessions;
            this.m_senderLock = new Object();
        }

        protected abstract SendSession createSendSession(javax.jms.Connection var1) throws JMSException;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void onConnect() throws JMSException {
            Object object = this.m_senderLock;
            synchronized (object) {
                for (int i = 0; i < this.m_numSessions; ++i) {
                    this.m_senders.add(this.createSendSession(this.m_connection));
                }
                this.m_senderLock.notifyAll();
            }
        }

        byte[] call(JMSEndpoint endpoint, byte[] message, long timeout, HashMap properties) throws Exception {
            long timeoutTime = System.currentTimeMillis() + timeout;
            while (true) {
                if (System.currentTimeMillis() > timeoutTime) {
                    throw new InvokeTimeoutException("Unable to complete call in time allotted");
                }
                SendSession sendSession = null;
                try {
                    sendSession = this.getSessionFromPool(JMSConnector.this.m_poolTimeout);
                    byte[] response = sendSession.call(endpoint, message, timeoutTime - System.currentTimeMillis(), properties);
                    this.returnSessionToPool(sendSession);
                    if (response == null) {
                        throw new InvokeTimeoutException("Unable to complete call in time allotted");
                    }
                    return response;
                }
                catch (JMSException jmse) {
                    if (!JMSConnector.this.m_adapter.isRecoverable(jmse, 0)) {
                        this.returnSessionToPool(sendSession);
                        throw jmse;
                    }
                    Thread.yield();
                    continue;
                }
                catch (NullPointerException npe) {
                    Thread.yield();
                    continue;
                }
                break;
            }
        }

        void send(JMSEndpoint endpoint, byte[] message, HashMap properties) throws Exception {
            long timeoutTime = System.currentTimeMillis() + JMSConnector.this.m_timeoutTime;
            while (true) {
                if (System.currentTimeMillis() > timeoutTime) {
                    throw new InvokeTimeoutException("Cannot complete send in time allotted");
                }
                SendSession sendSession = null;
                try {
                    sendSession = this.getSessionFromPool(JMSConnector.this.m_poolTimeout);
                    sendSession.send(endpoint, message, properties);
                    this.returnSessionToPool(sendSession);
                }
                catch (JMSException jmse) {
                    if (!JMSConnector.this.m_adapter.isRecoverable(jmse, 0)) {
                        this.returnSessionToPool(sendSession);
                        throw jmse;
                    }
                    Thread.yield();
                    continue;
                }
                catch (NullPointerException npe) {
                    Thread.yield();
                    continue;
                }
                break;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void onException() {
            Object object = this.m_senderLock;
            synchronized (object) {
                this.m_senders.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void onShutdown() {
            Object object = this.m_senderLock;
            synchronized (object) {
                Iterator senders = this.m_senders.iterator();
                while (senders.hasNext()) {
                    SendSession session = (SendSession)senders.next();
                    session.cleanup();
                }
                this.m_senders.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private SendSession getSessionFromPool(long timeout) {
            Object object = this.m_senderLock;
            synchronized (object) {
                while (this.m_senders.size() == 0) {
                    try {
                        this.m_senderLock.wait(timeout);
                        if (this.m_senders.size() != 0) continue;
                        return null;
                    }
                    catch (InterruptedException ignore) {
                        return null;
                    }
                }
                return (SendSession)this.m_senders.removeFirst();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void returnSessionToPool(SendSession sendSession) {
            Object object = this.m_senderLock;
            synchronized (object) {
                this.m_senders.addLast(sendSession);
                this.m_senderLock.notifyAll();
            }
        }

        protected abstract class SendSession
        extends ConnectorSession {
            MessageProducer m_producer;

            SendSession(Session session, MessageProducer producer) throws JMSException {
                super(session);
                this.m_producer = producer;
            }

            protected abstract Destination createTemporaryDestination() throws JMSException;

            protected abstract void deleteTemporaryDestination(Destination var1) throws JMSException;

            protected abstract MessageConsumer createConsumer(Destination var1) throws JMSException;

            protected abstract void send(Destination var1, Message var2, int var3, int var4, long var5) throws JMSException;

            void send(JMSEndpoint endpoint, byte[] message, HashMap properties) throws Exception {
                BytesMessage jmsMessage = this.m_session.createBytesMessage();
                jmsMessage.writeBytes(message);
                int deliveryMode = this.extractDeliveryMode(properties);
                int priority = this.extractPriority(properties);
                long timeToLive = this.extractTimeToLive(properties);
                if (properties != null && !properties.isEmpty()) {
                    this.setProperties(properties, (Message)jmsMessage);
                }
                this.send(endpoint.getDestination(this.m_session), (Message)jmsMessage, deliveryMode, priority, timeToLive);
            }

            void cleanup() {
                try {
                    this.m_producer.close();
                }
                catch (Throwable t) {
                    // empty catch block
                }
                try {
                    this.m_session.close();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }

            byte[] call(JMSEndpoint endpoint, byte[] message, long timeout, HashMap properties) throws Exception {
                Destination reply = this.createTemporaryDestination();
                MessageConsumer subscriber = this.createConsumer(reply);
                BytesMessage jmsMessage = this.m_session.createBytesMessage();
                jmsMessage.writeBytes(message);
                jmsMessage.setJMSReplyTo(reply);
                int deliveryMode = this.extractDeliveryMode(properties);
                int priority = this.extractPriority(properties);
                long timeToLive = this.extractTimeToLive(properties);
                if (properties != null && !properties.isEmpty()) {
                    this.setProperties(properties, (Message)jmsMessage);
                }
                this.send(endpoint.getDestination(this.m_session), (Message)jmsMessage, deliveryMode, priority, timeToLive);
                BytesMessage response = null;
                try {
                    response = (BytesMessage)subscriber.receive(timeout);
                }
                catch (ClassCastException cce) {
                    throw new InvokeException("Error: unexpected message type received - expected BytesMessage");
                }
                byte[] respBytes = null;
                if (response != null) {
                    byte[] buffer = new byte[8192];
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int bytesRead = response.readBytes(buffer);
                    while (bytesRead != -1) {
                        out.write(buffer, 0, bytesRead);
                        bytesRead = response.readBytes(buffer);
                    }
                    respBytes = out.toByteArray();
                }
                subscriber.close();
                this.deleteTemporaryDestination(reply);
                return respBytes;
            }

            private int extractPriority(HashMap properties) {
                return MapUtils.removeIntProperty(properties, "transport.jms.priority", 4);
            }

            private int extractDeliveryMode(HashMap properties) {
                return MapUtils.removeIntProperty(properties, "transport.jms.deliveryMode", 1);
            }

            private long extractTimeToLive(HashMap properties) {
                return MapUtils.removeLongProperty(properties, "transport.jms.ttl", 0L);
            }

            private void setProperties(HashMap properties, Message message) throws JMSException {
                Iterator propertyIter = properties.entrySet().iterator();
                while (propertyIter.hasNext()) {
                    Map.Entry property = propertyIter.next();
                    this.setProperty((String)property.getKey(), property.getValue(), message);
                }
            }

            private void setProperty(String property, Object value, Message message) throws JMSException {
                if (property == null) {
                    return;
                }
                if (property.equals("transport.jms.jmsCorrelationID")) {
                    message.setJMSCorrelationID((String)value);
                } else if (property.equals("transport.jms.jmsCorrelationIDAsBytes")) {
                    message.setJMSCorrelationIDAsBytes((byte[])value);
                } else if (property.equals("transport.jms.jmsType")) {
                    message.setJMSType((String)value);
                } else {
                    message.setObjectProperty(property, value);
                }
            }
        }
    }

    private abstract class Connection
    extends Thread
    implements ExceptionListener {
        private ConnectionFactory m_connectionFactory;
        protected javax.jms.Connection m_connection;
        protected boolean m_isActive;
        private boolean m_needsToConnect;
        private boolean m_startConnection;
        private String m_clientID;
        private String m_username;
        private String m_password;
        private Object m_jmsLock;
        private Object m_lifecycleLock;

        protected Connection(ConnectionFactory connectionFactory, javax.jms.Connection connection, String threadName, String clientID, String username, String password) throws JMSException {
            super(threadName);
            this.m_connectionFactory = connectionFactory;
            this.m_clientID = clientID;
            this.m_username = username;
            this.m_password = password;
            this.m_jmsLock = new Object();
            this.m_lifecycleLock = new Object();
            if (connection != null) {
                this.m_needsToConnect = false;
                this.m_connection = connection;
                this.m_connection.setExceptionListener((ExceptionListener)this);
                if (this.m_clientID != null) {
                    this.m_connection.setClientID(this.m_clientID);
                }
            } else {
                this.m_needsToConnect = true;
            }
            this.m_isActive = true;
        }

        public ConnectionFactory getConnectionFactory() {
            return this.m_connectionFactory;
        }

        public String getClientID() {
            return this.m_clientID;
        }

        public String getUsername() {
            return this.m_username;
        }

        public String getPassword() {
            return this.m_password;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            while (this.m_isActive) {
                block14: {
                    if (this.m_needsToConnect) {
                        this.m_connection = null;
                        try {
                            this.m_connection = JMSConnector.this.internalConnect(this.m_connectionFactory, this.m_username, this.m_password);
                            this.m_connection.setExceptionListener((ExceptionListener)this);
                            if (this.m_clientID != null) {
                                this.m_connection.setClientID(this.m_clientID);
                            }
                            break block14;
                        }
                        catch (JMSException e) {
                            try {
                                Thread.sleep(JMSConnector.this.m_connectRetryInterval);
                            }
                            catch (InterruptedException ie) {}
                            continue;
                        }
                    }
                    this.m_needsToConnect = true;
                }
                try {
                    this.internalOnConnect();
                }
                catch (Exception e) {
                    continue;
                }
                Object object = this.m_jmsLock;
                synchronized (object) {
                    try {
                        this.m_jmsLock.wait();
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
            }
            this.internalOnShutdown();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void startConnection() {
            Object object = this.m_lifecycleLock;
            synchronized (object) {
                if (this.m_startConnection) {
                    return;
                }
                this.m_startConnection = true;
                try {
                    this.m_connection.start();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void stopConnection() {
            Object object = this.m_lifecycleLock;
            synchronized (object) {
                if (!this.m_startConnection) {
                    return;
                }
                this.m_startConnection = false;
                try {
                    this.m_connection.stop();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void shutdown() {
            this.m_isActive = false;
            Object object = this.m_jmsLock;
            synchronized (object) {
                this.m_jmsLock.notifyAll();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void onException(JMSException exception) {
            if (JMSConnector.this.m_adapter.isRecoverable(exception, 4)) {
                return;
            }
            this.onException();
            Object object = this.m_jmsLock;
            synchronized (object) {
                this.m_jmsLock.notifyAll();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private final void internalOnConnect() throws Exception {
            this.onConnect();
            Object object = this.m_lifecycleLock;
            synchronized (object) {
                if (this.m_startConnection) {
                    try {
                        this.m_connection.start();
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
            }
        }

        private final void internalOnShutdown() {
            this.stopConnection();
            this.onShutdown();
            try {
                this.m_connection.close();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }

        protected abstract void onConnect() throws Exception;

        protected abstract void onShutdown();

        protected abstract void onException();
    }
}

