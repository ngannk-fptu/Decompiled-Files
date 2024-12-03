/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.mail;

public class SendTestMailResult {
    private final String debugLogs;
    private final boolean success;

    public SendTestMailResult(String debugLogs, boolean messageSent) {
        this.debugLogs = debugLogs;
        this.success = messageSent;
    }

    public String getDebugLogs() {
        return this.debugLogs;
    }

    public boolean isSuccess() {
        return this.success;
    }
}

