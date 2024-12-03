/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server.soap.synch;

import java.util.Date;

public class SynchConnection {
    private String connectorId;
    private String subscribeUrl;
    private String synchToken;
    private long lastPing;

    public SynchConnection(String connectorId, String subscribeUrl, String synchToken) {
        this.connectorId = connectorId;
        this.subscribeUrl = subscribeUrl;
        this.synchToken = synchToken;
    }

    public void setConnectorId(String val) {
        this.connectorId = val;
    }

    public String getConnectorId() {
        return this.connectorId;
    }

    public void setSubscribeUrl(String val) {
        this.subscribeUrl = val;
    }

    public String getSubscribeUrl() {
        return this.subscribeUrl;
    }

    public void setSynchToken(String val) {
        this.synchToken = val;
    }

    public String getSynchToken() {
        return this.synchToken;
    }

    public void setLastPing(long val) {
        this.lastPing = val;
    }

    public long getLastPing() {
        return this.lastPing;
    }

    public String shortToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: \"");
        sb.append(this.getConnectorId());
        sb.append("\", url: \"");
        sb.append(this.getSubscribeUrl());
        sb.append("\", token: \"");
        sb.append(this.getSynchToken());
        if (this.getLastPing() != 0L) {
            sb.append("\", ping: ");
            sb.append(new Date(this.getLastPing()).toString());
        }
        sb.append("}");
        return sb.toString();
    }
}

