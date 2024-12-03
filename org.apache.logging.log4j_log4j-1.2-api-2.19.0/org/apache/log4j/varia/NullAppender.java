/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j.varia;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class NullAppender
extends AppenderSkeleton {
    private static final NullAppender INSTANCE = new NullAppender();

    public static NullAppender getNullAppender() {
        return INSTANCE;
    }

    @Override
    public void activateOptions() {
    }

    @Override
    protected void append(LoggingEvent event) {
    }

    @Override
    public void close() {
    }

    @Override
    public void doAppend(LoggingEvent event) {
    }

    @Deprecated
    public NullAppender getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}

