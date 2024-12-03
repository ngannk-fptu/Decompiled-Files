/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.io;

import org.apache.jackrabbit.server.io.IOContext;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.IOListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIOListener
implements IOListener {
    private static Logger log = LoggerFactory.getLogger(DefaultIOListener.class);
    private Logger ioLog;

    public DefaultIOListener(Logger ioLog) {
        this.ioLog = ioLog != null ? ioLog : log;
    }

    @Override
    public void onBegin(IOHandler handler, IOContext ioContext) {
        this.ioLog.debug("Starting IOHandler (" + handler.getName() + ")");
    }

    @Override
    public void onEnd(IOHandler handler, IOContext ioContext, boolean success) {
        this.ioLog.debug("Result for IOHandler (" + handler.getName() + "): " + (success ? "OK" : "Failed"));
    }

    @Override
    public void onError(IOHandler ioHandler, IOContext ioContext, Exception e) {
        this.ioLog.debug("Error: " + e.getMessage());
    }
}

