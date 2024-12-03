/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jms.ConnectionFactory
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.jms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.jms.ConnectionFactory;
import org.apache.axis.components.jms.JMSVendorAdapter;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.transport.jms.JMSConnector;
import org.apache.axis.transport.jms.JMSConnectorManager;
import org.apache.axis.transport.jms.JMSURLHelper;
import org.apache.axis.transport.jms.MapUtils;
import org.apache.axis.transport.jms.QueueConnector;
import org.apache.axis.transport.jms.TopicConnector;
import org.apache.commons.logging.Log;

public abstract class JMSConnectorFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$jms$JMSConnectorFactory == null ? (class$org$apache$axis$transport$jms$JMSConnectorFactory = JMSConnectorFactory.class$("org.apache.axis.transport.jms.JMSConnectorFactory")) : class$org$apache$axis$transport$jms$JMSConnectorFactory).getName());
    static /* synthetic */ Class class$org$apache$axis$transport$jms$JMSConnectorFactory;

    public static JMSConnector matchConnector(Set connectors, HashMap connectorProps, HashMap cfProps, String username, String password, JMSVendorAdapter adapter) {
        Iterator iter = connectors.iterator();
        while (iter.hasNext()) {
            JMSConnector conn;
            block6: {
                String connectorPassword;
                conn = (JMSConnector)iter.next();
                String connectorUsername = conn.getUsername();
                if (!((connectorUsername == null && username == null || connectorUsername != null && username != null && connectorUsername.equals(username)) && ((connectorPassword = conn.getPassword()) == null && password == null || connectorPassword != null && password != null && connectorPassword.equals(password)))) continue;
                int connectorNumRetries = conn.getNumRetries();
                String propertyNumRetries = (String)connectorProps.get("transport.jms.numRetries");
                int numRetries = 5;
                if (propertyNumRetries != null) {
                    numRetries = Integer.parseInt(propertyNumRetries);
                }
                if (connectorNumRetries != numRetries) continue;
                String connectorClientID = conn.getClientID();
                String clientID = (String)connectorProps.get("transport.jms.clientID");
                if ((connectorClientID != null || clientID != null) && (connectorClientID == null || clientID == null || !connectorClientID.equals(clientID))) continue;
                String connectorDomain = conn instanceof QueueConnector ? "QUEUE" : "TOPIC";
                String propertyDomain = (String)connectorProps.get("transport.jms.domain");
                String domain = "QUEUE";
                if (propertyDomain != null) {
                    domain = propertyDomain;
                }
                if (!(connectorDomain == null && domain == null || connectorDomain != null && domain != null && connectorDomain.equalsIgnoreCase(domain))) continue;
                JMSURLHelper jmsurl = conn.getJMSURL();
                if (!adapter.isMatchingConnectionFactory(conn.getConnectionFactory(), jmsurl, cfProps)) continue;
                try {
                    JMSConnectorManager.getInstance().reserve(conn);
                    if (!log.isDebugEnabled()) break block6;
                    log.debug((Object)"JMSConnectorFactory: Found matching connector");
                }
                catch (Exception e) {
                    continue;
                }
            }
            return conn;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"JMSConnectorFactory: No matching connectors found");
        }
        return null;
    }

    public static JMSConnector createServerConnector(HashMap connectorConfig, HashMap cfConfig, String username, String password, JMSVendorAdapter adapter) throws Exception {
        return JMSConnectorFactory.createConnector(connectorConfig, cfConfig, true, username, password, adapter);
    }

    public static JMSConnector createClientConnector(HashMap connectorConfig, HashMap cfConfig, String username, String password, JMSVendorAdapter adapter) throws Exception {
        return JMSConnectorFactory.createConnector(connectorConfig, cfConfig, false, username, password, adapter);
    }

    private static JMSConnector createConnector(HashMap connectorConfig, HashMap cfConfig, boolean allowReceive, String username, String password, JMSVendorAdapter adapter) throws Exception {
        if (connectorConfig != null) {
            connectorConfig = (HashMap)connectorConfig.clone();
        }
        int numRetries = MapUtils.removeIntProperty(connectorConfig, "transport.jms.numRetries", 5);
        int numSessions = MapUtils.removeIntProperty(connectorConfig, "transport.jms.numSessions", 5);
        long connectRetryInterval = MapUtils.removeLongProperty(connectorConfig, "transport.jms.connectRetryInterval", 2000L);
        long interactRetryInterval = MapUtils.removeLongProperty(connectorConfig, "transport.jms.interactRetryInterval", 250L);
        long timeoutTime = MapUtils.removeLongProperty(connectorConfig, "transport.jms.timeoutTime", 5000L);
        String clientID = MapUtils.removeStringProperty(connectorConfig, "transport.jms.clientID", null);
        String domain = MapUtils.removeStringProperty(connectorConfig, "transport.jms.domain", "QUEUE");
        JMSURLHelper jmsurl = (JMSURLHelper)connectorConfig.get("transport.jms.EndpointAddress");
        if (cfConfig == null) {
            throw new IllegalArgumentException("noCfConfig");
        }
        if (domain.equals("QUEUE")) {
            return new QueueConnector((ConnectionFactory)adapter.getQueueConnectionFactory(cfConfig), numRetries, numSessions, connectRetryInterval, interactRetryInterval, timeoutTime, allowReceive, clientID, username, password, adapter, jmsurl);
        }
        return new TopicConnector(adapter.getTopicConnectionFactory(cfConfig), numRetries, numSessions, connectRetryInterval, interactRetryInterval, timeoutTime, allowReceive, clientID, username, password, adapter, jmsurl);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

