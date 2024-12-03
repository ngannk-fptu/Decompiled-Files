/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.dispatcher.task;

import com.atlassian.plugin.notifications.dispatcher.task.SendResult;

interface Sender {
    public SendResult send();
}

