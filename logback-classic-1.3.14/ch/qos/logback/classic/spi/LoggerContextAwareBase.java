/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.Context
 *  ch.qos.logback.core.spi.ContextAwareBase
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextAware;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;

public class LoggerContextAwareBase
extends ContextAwareBase
implements LoggerContextAware {
    @Override
    public void setLoggerContext(LoggerContext context) {
        super.setContext((Context)context);
    }

    public void setContext(Context context) {
        if (!(context instanceof LoggerContext) && context != null) {
            throw new IllegalArgumentException("LoggerContextAwareBase only accepts contexts of type c.l.classic.LoggerContext");
        }
        super.setContext(context);
    }

    public LoggerContext getLoggerContext() {
        return (LoggerContext)this.context;
    }
}

