/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.async;

import org.apache.hc.core5.function.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LoggingExceptionCallback
implements Callback<Exception> {
    static final LoggingExceptionCallback INSTANCE = new LoggingExceptionCallback();
    private static final Logger LOG = LoggerFactory.getLogger((String)"org.apache.hc.client5.http.impl.async");

    private LoggingExceptionCallback() {
    }

    @Override
    public void execute(Exception ex) {
        LOG.error(ex.getMessage(), (Throwable)ex);
    }
}

