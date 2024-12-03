/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.jms;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Transport;
import org.apache.axis.components.jms.JMSVendorAdapter;
import org.apache.axis.components.jms.JMSVendorAdapterFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.transport.jms.JMSConnector;
import org.apache.axis.transport.jms.JMSConnectorManager;
import org.apache.axis.transport.jms.JMSURLHelper;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class JMSTransport
extends Transport {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$jms$JMSTransport == null ? (class$org$apache$axis$transport$jms$JMSTransport = JMSTransport.class$("org.apache.axis.transport.jms.JMSTransport")) : class$org$apache$axis$transport$jms$JMSTransport).getName());
    private static HashMap vendorConnectorPools = new HashMap();
    private HashMap defaultConnectorProps;
    private HashMap defaultConnectionFactoryProps;
    static /* synthetic */ Class class$org$apache$axis$transport$jms$JMSTransport;

    public JMSTransport() {
        this.transportName = "JMSTransport";
    }

    public JMSTransport(HashMap connectorProps, HashMap connectionFactoryProps) {
        this();
        this.defaultConnectorProps = connectorProps;
        this.defaultConnectionFactoryProps = connectionFactoryProps;
    }

    public void setupMessageContextImpl(MessageContext context, Call message, AxisEngine engine) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: JMSTransport::setupMessageContextImpl");
        }
        JMSConnector connector = null;
        HashMap connectorProperties = null;
        HashMap connectionFactoryProperties = null;
        JMSVendorAdapter vendorAdapter = null;
        JMSURLHelper jmsurl = null;
        String username = message.getUsername();
        String password = message.getPassword();
        String endpointAddr = message.getTargetEndpointAddress();
        if (endpointAddr != null) {
            try {
                jmsurl = new JMSURLHelper(new URL(endpointAddr));
                String vendorId = jmsurl.getVendor();
                if (vendorId == null) {
                    vendorId = "JNDI";
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)("JMSTransport.setupMessageContextImpl(): endpt=" + endpointAddr + ", vendor=" + vendorId));
                }
                if ((vendorAdapter = JMSVendorAdapterFactory.getJMSVendorAdapter(vendorId)) == null) {
                    throw new AxisFault("cannotLoadAdapterClass:" + vendorId);
                }
                connectorProperties = vendorAdapter.getJMSConnectorProperties(jmsurl);
                connectionFactoryProperties = vendorAdapter.getJMSConnectionFactoryProperties(jmsurl);
            }
            catch (MalformedURLException e) {
                log.error((Object)Messages.getMessage("malformedURLException00"), (Throwable)e);
                throw new AxisFault(Messages.getMessage("malformedURLException00"), e);
            }
        } else {
            vendorAdapter = JMSVendorAdapterFactory.getJMSVendorAdapter();
            if (vendorAdapter == null) {
                throw new AxisFault("cannotLoadAdapterClass");
            }
            connectorProperties = this.defaultConnectorProps;
            connectionFactoryProperties = this.defaultConnectionFactoryProps;
        }
        try {
            connector = JMSConnectorManager.getInstance().getConnector(connectorProperties, connectionFactoryProperties, username, password, vendorAdapter);
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("cannotConnectError"), (Throwable)e);
            if (e instanceof AxisFault) {
                throw (AxisFault)e;
            }
            throw new AxisFault("cannotConnect", e);
        }
        context.setProperty("transport.jms.Connector", connector);
        context.setProperty("transport.jms.VendorAdapter", vendorAdapter);
        vendorAdapter.setupMessageContext(context, message, jmsurl);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: JMSTransport::setupMessageContextImpl");
        }
    }

    public void shutdown() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: JMSTransport::shutdown");
        }
        JMSTransport.closeAllConnectors();
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: JMSTransport::shutdown");
        }
    }

    public static void closeAllConnectors() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: JMSTransport::closeAllConnectors");
        }
        JMSConnectorManager.getInstance().closeAllConnectors();
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: JMSTransport::closeAllConnectors");
        }
    }

    public static void closeMatchingJMSConnectors(String endpointAddr, String username, String password) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: JMSTransport::closeMatchingJMSConnectors");
        }
        try {
            JMSURLHelper jmsurl = new JMSURLHelper(new URL(endpointAddr));
            String vendorId = jmsurl.getVendor();
            JMSVendorAdapter vendorAdapter = null;
            if (vendorId == null) {
                vendorId = "JNDI";
            }
            if ((vendorAdapter = JMSVendorAdapterFactory.getJMSVendorAdapter(vendorId)) == null) {
                return;
            }
            HashMap connectorProps = vendorAdapter.getJMSConnectorProperties(jmsurl);
            HashMap cfProps = vendorAdapter.getJMSConnectionFactoryProperties(jmsurl);
            JMSConnectorManager.getInstance().closeMatchingJMSConnectors(connectorProps, cfProps, username, password, vendorAdapter);
        }
        catch (MalformedURLException e) {
            log.warn((Object)Messages.getMessage("malformedURLException00"), (Throwable)e);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: JMSTransport::closeMatchingJMSConnectors");
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(){

            public void run() {
                JMSTransport.closeAllConnectors();
            }
        });
    }
}

