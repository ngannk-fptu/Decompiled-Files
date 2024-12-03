/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.logging.log4j.appender.fluentd;

import com.atlassian.logging.log4j.appender.fluentd.FluentdSender;

public class FluentdStdOutSender
implements FluentdSender {
    @Override
    public void send(String payload) {
        System.out.println(payload);
    }
}

