/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Maybe
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.fugue.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExperimentalApi
public class ProductionAwareLoggerSwitch {
    private final Logger logger;

    private ProductionAwareLoggerSwitch(Logger logger) {
        this.logger = logger;
    }

    public static ProductionAwareLoggerSwitch forCaller() {
        return new ProductionAwareLoggerSwitch(LoggerFactory.getLogger((String)Thread.currentThread().getStackTrace()[2].getClassName()));
    }

    public static ProductionAwareLoggerSwitch forClass(Class clazz) {
        return new ProductionAwareLoggerSwitch(LoggerFactory.getLogger((Class)clazz));
    }

    private static boolean isDevMode() {
        return Boolean.getBoolean("confluence.devmode") || Boolean.getBoolean("atlassian.dev.mode");
    }

    public <T> T errorOrDebug(Throwable exception) {
        return this.errorOrDebug(exception, exception.getMessage(), new Object[0]);
    }

    public <T> T errorOrDebug(Throwable exception, String message, Object ... args) {
        String errorMessage;
        String string = errorMessage = message != null ? String.format(message, args) : "Error";
        if (ProductionAwareLoggerSwitch.isDevMode()) {
            this.logger.error(errorMessage, exception);
        } else if (this.logger.isDebugEnabled()) {
            this.logger.debug(errorMessage, exception);
        }
        return null;
    }

    public <T> T errorOrDebug(Maybe maybeNot, String message, Object ... args) {
        if (maybeNot.isEmpty()) {
            if (ProductionAwareLoggerSwitch.isDevMode()) {
                try {
                    maybeNot.get();
                }
                catch (RuntimeException exception) {
                    this.errorOrDebug(exception);
                }
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug(String.format(message, args));
            }
        }
        return null;
    }

    public <T> T warnOrDebug(String message, Object ... args) {
        if (ProductionAwareLoggerSwitch.isDevMode()) {
            this.logger.warn(String.format(message, args));
        } else if (this.logger.isDebugEnabled()) {
            this.logger.debug(String.format(message, args));
        }
        return null;
    }

    public <T> T onlyDebug(String message, Object ... args) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(String.format(message, args));
        }
        return null;
    }

    public <T> T onlyTrace(String message, Object ... args) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(String.format(message, args));
        }
        return null;
    }
}

