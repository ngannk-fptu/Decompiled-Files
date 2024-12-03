/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.dispatcher.task;

interface SendResult {
    public boolean isSuccessfulSendForRecipient();

    public boolean isAnyEnabledServers();
}

