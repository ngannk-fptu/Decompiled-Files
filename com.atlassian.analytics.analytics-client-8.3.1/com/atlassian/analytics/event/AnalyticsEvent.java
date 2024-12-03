/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.analytics.event;

import com.atlassian.analytics.EventMessage;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public abstract class AnalyticsEvent {
    private final String name;
    private final String server;
    private final String product;
    private final String subproduct;
    private final String version;
    private final String user;
    private final String session;
    private final long clientTime;
    private final long receivedTime;
    private final String sen;
    private final String sourceIP;
    private final String atlPath;
    private final String appAccess;
    private final String requestCorrelationId;
    private final Map<String, Object> properties;

    public AnalyticsEvent(String name, String server, String product, String subproduct, String version, String user, String session, long clientTime, long receivedTime, String sen, String sourceIP, String atlPath, String appAccess, String requestCorrelationId, Map<String, Object> properties) {
        this.name = name;
        this.server = server;
        this.product = product;
        this.subproduct = subproduct;
        this.version = version;
        this.user = user;
        this.session = session;
        this.clientTime = clientTime;
        this.receivedTime = receivedTime;
        this.sen = sen;
        this.sourceIP = sourceIP;
        this.atlPath = atlPath;
        this.appAccess = appAccess;
        this.requestCorrelationId = requestCorrelationId;
        this.properties = properties == null ? Collections.emptyMap() : ImmutableMap.copyOf(properties);
    }

    public String getName() {
        return this.name;
    }

    public String getServer() {
        return this.server;
    }

    public String getProduct() {
        return this.product;
    }

    public String getSubProduct() {
        return this.subproduct;
    }

    public String getVersion() {
        return this.version;
    }

    public String getUser() {
        return this.user;
    }

    public String getSession() {
        return this.session;
    }

    public long getClientTime() {
        return this.clientTime;
    }

    public long getReceivedTime() {
        return this.receivedTime;
    }

    public String getSen() {
        return this.sen;
    }

    public String getSourceIP() {
        return this.sourceIP;
    }

    public String getAtlPath() {
        return this.atlPath;
    }

    public String getAppAccess() {
        return this.appAccess;
    }

    public String getRequestCorrelationId() {
        return this.requestCorrelationId;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("name", (Object)this.name).add("server", (Object)this.server).add("product", (Object)this.product).add("subproduct", (Object)this.subproduct).add("version", (Object)this.version).add("user", (Object)this.user).add("session", (Object)this.session).add("clientTime", this.clientTime).add("receivedTime", this.receivedTime).add("sen", (Object)this.sen).add("sourceIP", (Object)this.sourceIP).add("atlPath", (Object)this.atlPath).add("appAccess", (Object)this.appAccess).add("requestCorrelationId", (Object)this.requestCorrelationId).add("properties", this.properties).toString();
    }

    public int hashCode() {
        return Objects.hash(this.name, this.server, this.product, this.subproduct, this.version, this.user, this.session, this.clientTime, this.receivedTime, this.sen, this.sourceIP, this.atlPath, this.appAccess, this.requestCorrelationId, this.properties);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AnalyticsEvent)) {
            return false;
        }
        AnalyticsEvent other = (AnalyticsEvent)obj;
        return Objects.equals(this.name, other.name) && Objects.equals(this.server, other.server) && Objects.equals(this.product, other.product) && Objects.equals(this.subproduct, other.subproduct) && Objects.equals(this.version, other.version) && Objects.equals(this.user, other.user) && Objects.equals(this.session, other.session) && Objects.equals(this.clientTime, other.clientTime) && Objects.equals(this.receivedTime, other.receivedTime) && Objects.equals(this.sen, other.sen) && Objects.equals(this.sourceIP, other.sourceIP) && Objects.equals(this.atlPath, other.atlPath) && Objects.equals(this.appAccess, other.appAccess) && Objects.equals(this.requestCorrelationId, other.requestCorrelationId) && Objects.equals(this.properties, other.properties);
    }

    public EventMessage toEventMessage() {
        EventMessage msg = new EventMessage();
        msg.setName(this.getName());
        msg.setServer(this.getServer());
        msg.setProduct(this.getProduct());
        msg.setSubProduct(this.getSubProduct());
        msg.setVersion(this.getVersion());
        msg.setUser(this.getUser());
        msg.setSession(this.getSession());
        msg.setClientTime(this.getClientTime());
        msg.setReceivedTime(this.getReceivedTime());
        msg.setSen(this.getSen());
        msg.setSourceIP(this.getSourceIP());
        msg.setAtlPath(this.getAtlPath());
        msg.setAppAccess(this.getAppAccess());
        msg.setRequestCorrelationId(this.getRequestCorrelationId());
        msg.setProperties((Map<CharSequence, Object>)ImmutableMap.copyOf(this.getProperties()));
        return msg;
    }
}

