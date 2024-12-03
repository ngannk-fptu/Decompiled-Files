/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.status.StatusLogger
 */
package org.apache.log4j;

import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.logging.log4j.status.StatusLogger;

public class ConsoleAppender
extends WriterAppender {
    public static final String SYSTEM_OUT = "System.out";
    public static final String SYSTEM_ERR = "System.err";
    protected String target = "System.out";
    private boolean follow;

    public ConsoleAppender() {
    }

    public ConsoleAppender(Layout layout) {
        this(layout, SYSTEM_OUT);
    }

    public ConsoleAppender(Layout layout, String target) {
        this.setLayout(layout);
        this.setTarget(target);
        this.activateOptions();
    }

    @Override
    public void append(LoggingEvent theEvent) {
    }

    @Override
    public void close() {
    }

    public boolean getFollow() {
        return this.follow;
    }

    public String getTarget() {
        return this.target;
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public void setTarget(String value) {
        String v = value.trim();
        if (SYSTEM_OUT.equalsIgnoreCase(v)) {
            this.target = SYSTEM_OUT;
        } else if (SYSTEM_ERR.equalsIgnoreCase(v)) {
            this.target = SYSTEM_ERR;
        } else {
            this.targetWarn(value);
        }
    }

    void targetWarn(String val) {
        StatusLogger.getLogger().warn("[" + val + "] should be System.out or System.err.");
        StatusLogger.getLogger().warn("Using previously set target, System.out by default.");
    }
}

