/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.appender.rolling;

import org.apache.logging.log4j.core.appender.rolling.action.Action;

public interface RolloverDescription {
    public String getActiveFileName();

    public boolean getAppend();

    public Action getSynchronous();

    public Action getAsynchronous();
}

