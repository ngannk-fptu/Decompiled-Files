/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.jmx.access;

import java.io.IOException;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.lang.Nullable;

class ConnectorDelegate {
    private static final Log logger = LogFactory.getLog(ConnectorDelegate.class);
    @Nullable
    private JMXConnector connector;

    ConnectorDelegate() {
    }

    public MBeanServerConnection connect(@Nullable JMXServiceURL serviceUrl, @Nullable Map<String, ?> environment2, @Nullable String agentId) throws MBeanServerNotFoundException {
        if (serviceUrl != null) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Connecting to remote MBeanServer at URL [" + serviceUrl + "]"));
            }
            try {
                this.connector = JMXConnectorFactory.connect(serviceUrl, environment2);
                return this.connector.getMBeanServerConnection();
            }
            catch (IOException ex) {
                throw new MBeanServerNotFoundException("Could not connect to remote MBeanServer [" + serviceUrl + "]", ex);
            }
        }
        logger.debug((Object)"Attempting to locate local MBeanServer");
        return JmxUtils.locateMBeanServer(agentId);
    }

    public void close() {
        if (this.connector != null) {
            try {
                this.connector.close();
            }
            catch (IOException ex) {
                logger.debug((Object)"Could not close JMX connector", (Throwable)ex);
            }
        }
    }
}

