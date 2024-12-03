/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.analytics.client.sender;

import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.event.ProcessedEvent;
import com.atlassian.analytics.event.transport.UDPSender;
import java.io.IOException;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

class EventSender
implements DisposableBean {
    private static final String ANALYTICS_SERVER_DEFAULT = System.getProperty("analytics.server", "analytics");
    private static final int ANALYTICS_PORT_DEFAULT = 19876;
    private static final Logger log = LoggerFactory.getLogger(EventSender.class);
    private UDPSender udpSender;
    private InetAddress address;
    private int port;
    private final String hostname;

    public static EventSender newInstance(AnalyticsConfig analyticsConfig) {
        String destination = analyticsConfig.getDestinationOrDefault(ANALYTICS_SERVER_DEFAULT);
        return new EventSender(EventSender.getHostname(destination), EventSender.getPort(destination, 19876));
    }

    private static String getHostname(String serverName) {
        int colonIndex = serverName.indexOf(58);
        return colonIndex > 0 ? serverName.substring(0, colonIndex) : serverName;
    }

    private static int getPort(String serverName, int defaultPort) {
        int colonIndex = serverName.indexOf(58);
        return colonIndex > 0 ? Integer.parseInt(serverName.substring(colonIndex + 1)) : defaultPort;
    }

    public EventSender(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        try {
            this.udpSender = new UDPSender();
            this.address = InetAddress.getByName(hostname);
        }
        catch (IOException e) {
            log.debug("Unable to open connection to analytics server. Event transmission disabled. ", (Throwable)e);
            this.address = null;
            this.udpSender = null;
        }
    }

    public void destroy() {
        if (this.udpSender != null) {
            try {
                this.udpSender.shutdown();
            }
            catch (Exception e) {
                log.error("Failed to shut down UDP sender on port " + this.port + ": ", (Throwable)e);
            }
            this.udpSender = null;
        }
        this.address = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void send(ProcessedEvent event) {
        if (this.udpSender == null || this.address == null) {
            return;
        }
        long start = System.nanoTime();
        try {
            this.udpSender.send(event.toEventMessage(), this.address, this.port);
        }
        catch (IOException e) {
            log.error("Failed to send event [{}]: ", (Object)event, (Object)e);
        }
        finally {
            log.debug("Sending event message {} took {} \u00b5s", (Object)event.getName(), (Object)((System.nanoTime() - start) / 1000L));
        }
    }

    public String toString() {
        return String.format("EventSender[%s:%d%s]", this.hostname, this.port, this.udpSender == null ? " (disabled)" : "");
    }
}

