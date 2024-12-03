/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.logging.log4j.appender.fluentd;

public class FluentdRetryableException
extends Exception {
    public FluentdRetryableException(String message) {
        super(message);
    }

    public FluentdRetryableException(Throwable cause) {
        super(cause);
    }
}

