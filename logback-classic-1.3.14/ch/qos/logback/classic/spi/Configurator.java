/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.spi.ContextAware
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.spi.ContextAware;

public interface Configurator
extends ContextAware {
    public ExecutionStatus configure(LoggerContext var1);

    public static enum ExecutionStatus {
        NEUTRAL,
        INVOKE_NEXT_IF_ANY,
        DO_NOT_INVOKE_NEXT_IF_ANY;

    }
}

