/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.cluster;

public class InternalClusterMessage {
    private long id;
    private String channel;
    private String text;
    private long timestamp;
    private String senderNodeId;

    protected InternalClusterMessage() {
    }

    public InternalClusterMessage(String channel, String text, String senderNodeId, long timestamp) {
        this.channel = channel;
        this.text = text;
        this.senderNodeId = senderNodeId;
        this.timestamp = timestamp;
    }

    public long getId() {
        return this.id;
    }

    public String getChannel() {
        return this.channel;
    }

    public String getText() {
        return this.text;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getSenderNodeId() {
        return this.senderNodeId;
    }

    private void setText(String message) {
        this.text = message;
    }

    private void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    private void setChannel(String channel) {
        this.channel = channel;
    }

    private void setSenderNodeId(String senderNodeId) {
        this.senderNodeId = senderNodeId;
    }

    private void setId(long id) {
        this.id = id;
    }
}

