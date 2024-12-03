/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package com.atlassian.analytics.event;

import com.atlassian.analytics.EventMessage;
import com.atlassian.analytics.event.AnalyticsEvent;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RawEvent
extends AnalyticsEvent {
    protected RawEvent(String name, String server, String product, String subproduct, String version, String user, String session, long clientTime, long receivedTime, String sen, String sourceIP, String atlPath, String appAccess, String requestCorrelationId, Map<String, Object> properties) {
        super(name, server, product, subproduct, version, user, session, clientTime, receivedTime, sen, sourceIP, atlPath, appAccess, requestCorrelationId, properties);
    }

    public RawEvent(EventMessage message) {
        this(RawEvent.safeToString(message.getName()), RawEvent.safeToString(message.getServer()), RawEvent.safeToString(message.getProduct()), RawEvent.safeToString(message.getSubProduct()), RawEvent.safeToString(message.getVersion()), RawEvent.safeToString(message.getUser()), RawEvent.safeToString(message.getSession()), message.getClientTime(), message.getReceivedTime(), RawEvent.safeToString(message.getSen()), RawEvent.safeToString(message.getSourceIP()), RawEvent.safeToString(message.getAtlPath()), RawEvent.safeToString(message.getAppAccess()), RawEvent.safeToString(message.getRequestCorrelationId()), RawEvent.getProperties(message));
    }

    private static Map<String, Object> getProperties(EventMessage message) {
        if (message.getProperties() == null) {
            return null;
        }
        HashMap properties = Maps.newHashMap();
        for (Map.Entry<CharSequence, Object> entry : message.getProperties().entrySet()) {
            properties.put(RawEvent.safeToString(entry.getKey()), RawEvent.safeToString(entry.getValue()));
        }
        return properties;
    }

    private static String safeToString(Object o) {
        return o == null ? null : o.toString();
    }

    public static class Builder {
        private String name = "";
        private String server = "";
        private String product = "";
        private String subproduct = "";
        private String version = "";
        private String user = "";
        private String session = "";
        private long clientTime = 0L;
        private long receivedTime = 0L;
        private String sen = "";
        private String sourceIP = "";
        private String atlPath = "";
        private String appAccess = "";
        private String requestCorrelationId = "";
        private Map<String, Object> properties = Collections.emptyMap();

        public Builder() {
        }

        public Builder(AnalyticsEvent template) {
            this.name = template.getName();
            this.server = template.getServer();
            this.product = template.getProduct();
            this.subproduct = template.getSubProduct();
            this.version = template.getVersion();
            this.user = template.getUser();
            this.session = template.getSession();
            this.clientTime = template.getClientTime();
            this.receivedTime = template.getReceivedTime();
            this.sen = template.getSen();
            this.sourceIP = template.getSourceIP();
            this.atlPath = template.getAtlPath();
            this.appAccess = template.getAppAccess();
            this.requestCorrelationId = template.getRequestCorrelationId();
            this.properties = ImmutableMap.copyOf(template.getProperties());
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder server(String server) {
            this.server = server;
            return this;
        }

        public Builder product(String product) {
            this.product = product;
            return this;
        }

        public Builder subproduct(String subproduct) {
            this.subproduct = subproduct;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder session(String session) {
            this.session = session;
            return this;
        }

        public Builder clientTime(long clientTime) {
            this.clientTime = clientTime;
            return this;
        }

        public Builder receivedTime(long receivedTime) {
            this.receivedTime = receivedTime;
            return this;
        }

        public Builder sen(String sen) {
            this.sen = sen;
            return this;
        }

        public Builder sourceIP(String sourceIP) {
            this.sourceIP = sourceIP;
            return this;
        }

        public Builder atlPath(String atlPath) {
            this.atlPath = atlPath;
            return this;
        }

        public Builder appAccess(String appAccess) {
            this.appAccess = appAccess;
            return this;
        }

        public Builder requestCorrelationId(String requestCorrelationId) {
            this.requestCorrelationId = requestCorrelationId;
            return this;
        }

        public Builder properties(Map<String, Object> properties) {
            this.properties = ImmutableMap.copyOf(properties);
            return this;
        }

        public RawEvent build() {
            return new RawEvent(this.name, this.server, this.product, this.subproduct, this.version, this.user, this.session, this.clientTime, this.receivedTime, this.sen, this.sourceIP, this.atlPath, this.appAccess, this.requestCorrelationId, this.properties);
        }
    }
}

