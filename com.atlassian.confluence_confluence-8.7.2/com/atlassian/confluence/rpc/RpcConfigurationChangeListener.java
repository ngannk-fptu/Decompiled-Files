/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rpc;

import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.rpc.RpcServer;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcConfigurationChangeListener
implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(RpcConfigurationChangeListener.class);
    private RpcServer soapServer;
    private RpcServer xmlRpcServer;

    public void handleEvent(Event event) {
        if (event instanceof ClusterEventWrapper) {
            event = ((ClusterEventWrapper)event).getEvent();
        }
        if (event instanceof GlobalSettingsChangedEvent) {
            GlobalSettingsChangedEvent settingsEvent = (GlobalSettingsChangedEvent)event;
            Settings oldSettings = settingsEvent.getOldSettings();
            Settings newSettings = settingsEvent.getNewSettings();
            if (oldSettings.isAllowRemoteApi() != newSettings.isAllowRemoteApi() || oldSettings.isAllowRemoteApiAnonymous() != newSettings.isAllowRemoteApiAnonymous() || !settingsEvent.getOldDomainName().equals(settingsEvent.getNewDomainName())) {
                log.info("RPC configuration has changed. Triggering an RPC subsystem reload");
                this.soapServer.reloadConfiguration();
                this.xmlRpcServer.reloadConfiguration();
            }
        }
    }

    public Class[] getHandledEventClasses() {
        return new Class[]{GlobalSettingsChangedEvent.class, ClusterEventWrapper.class};
    }

    public void setAxisSoapServer(RpcServer soapServer) {
        this.soapServer = soapServer;
    }

    public void setGlueSoapServer(RpcServer soapServer) {
        this.soapServer = soapServer;
    }

    public void setXmlRpcServer(RpcServer xmlRpcServer) {
        this.xmlRpcServer = xmlRpcServer;
    }
}

