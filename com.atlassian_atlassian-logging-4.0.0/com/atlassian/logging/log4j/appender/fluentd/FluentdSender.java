/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.logging.log4j.appender.fluentd;

import com.atlassian.logging.log4j.appender.fluentd.FluentdRetryableException;

public interface FluentdSender {
    public void send(String var1) throws FluentdRetryableException;
}

