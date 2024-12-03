/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.listener;

import org.apache.tools.ant.DefaultLogger;

public class TimestampedLogger
extends DefaultLogger {
    public static final String SPACER = " - at ";

    @Override
    protected String getBuildFailedMessage() {
        return super.getBuildFailedMessage() + SPACER + this.getTimestamp();
    }

    @Override
    protected String getBuildSuccessfulMessage() {
        return super.getBuildSuccessfulMessage() + SPACER + this.getTimestamp();
    }
}

