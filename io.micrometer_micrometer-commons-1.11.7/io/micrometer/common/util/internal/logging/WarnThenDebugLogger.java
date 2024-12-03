/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.common.util.internal.logging;

import io.micrometer.common.util.internal.logging.InternalLogLevel;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class WarnThenDebugLogger {
    private final InternalLogger logger;
    private final AtomicBoolean warnLogged = new AtomicBoolean();

    public WarnThenDebugLogger(Class<?> clazz) {
        this.logger = InternalLoggerFactory.getInstance(clazz);
    }

    public void log(String message, Throwable ex) {
        if (this.warnLogged.compareAndSet(false, true)) {
            this.log(InternalLogLevel.WARN, this.getWarnMessage(message), ex);
        } else {
            this.log(InternalLogLevel.DEBUG, message, ex);
        }
    }

    private String getWarnMessage(String message) {
        return message + " Note that subsequent logs will be logged at debug level.";
    }

    private void log(InternalLogLevel level, String finalMessage, Throwable ex) {
        if (ex != null) {
            this.logger.log(level, finalMessage, ex);
        } else {
            this.logger.log(level, finalMessage);
        }
    }

    public void log(String message) {
        this.log(message, null);
    }

    public void log(Supplier<String> messageSupplier, Throwable ex) {
        if (this.warnLogged.compareAndSet(false, true)) {
            this.log(InternalLogLevel.WARN, this.getWarnMessage(messageSupplier.get()), ex);
        } else if (this.logger.isDebugEnabled()) {
            this.log(InternalLogLevel.DEBUG, messageSupplier.get(), ex);
        }
    }

    public void log(Supplier<String> messageSupplier) {
        this.log(messageSupplier, null);
    }
}

