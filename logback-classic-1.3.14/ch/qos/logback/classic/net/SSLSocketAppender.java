/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.net.AbstractSSLSocketAppender
 *  ch.qos.logback.core.spi.PreSerializationTransformer
 */
package ch.qos.logback.classic.net;

import ch.qos.logback.classic.net.LoggingEventPreSerializationTransformer;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.net.AbstractSSLSocketAppender;
import ch.qos.logback.core.spi.PreSerializationTransformer;

public class SSLSocketAppender
extends AbstractSSLSocketAppender<ILoggingEvent> {
    private final PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();
    private boolean includeCallerData;

    protected void postProcessEvent(ILoggingEvent event) {
        if (this.includeCallerData) {
            event.getCallerData();
        }
    }

    public void setIncludeCallerData(boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }

    public PreSerializationTransformer<ILoggingEvent> getPST() {
        return this.pst;
    }
}

