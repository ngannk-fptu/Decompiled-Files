/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.spi.LoggerContext;

public interface LoggerContextShutdownAware {
    public void contextShutdown(LoggerContext var1);
}

