/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.event.Level
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.util.function.Supplier;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.StringUtils;

@SdkProtectedApi
public final class Logger {
    private final org.slf4j.Logger log;

    Logger(org.slf4j.Logger log) {
        this.log = log;
    }

    public org.slf4j.Logger logger() {
        return this.log;
    }

    public void info(Supplier<String> msg) {
        if (this.log.isInfoEnabled()) {
            this.log.info(msg.get());
        }
    }

    public void info(Supplier<String> msg, Throwable throwable) {
        if (this.log.isInfoEnabled()) {
            this.log.info(msg.get(), throwable);
        }
    }

    public void error(Supplier<String> msg) {
        if (this.log.isErrorEnabled()) {
            this.log.error(msg.get());
        }
    }

    public void error(Supplier<String> msg, Throwable throwable) {
        if (this.log.isErrorEnabled()) {
            this.log.error(msg.get(), throwable);
        }
    }

    public void debug(Supplier<String> msg) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(msg.get());
        }
    }

    public void debug(Supplier<String> msg, Throwable throwable) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(msg.get(), throwable);
        }
    }

    public void warn(Supplier<String> msg) {
        if (this.log.isWarnEnabled()) {
            this.log.warn(msg.get());
        }
    }

    public void warn(Supplier<String> msg, Throwable throwable) {
        if (this.log.isWarnEnabled()) {
            this.log.warn(msg.get(), throwable);
        }
    }

    public void trace(Supplier<String> msg) {
        if (this.log.isTraceEnabled()) {
            this.log.trace(msg.get());
        }
    }

    public void trace(Supplier<String> msg, Throwable throwable) {
        if (this.log.isTraceEnabled()) {
            this.log.trace(msg.get(), throwable);
        }
    }

    public boolean isLoggingLevelEnabled(Level logLevel) {
        switch (logLevel) {
            case TRACE: {
                return this.log.isTraceEnabled();
            }
            case DEBUG: {
                return this.log.isDebugEnabled();
            }
            case INFO: {
                return this.log.isInfoEnabled();
            }
            case WARN: {
                return this.log.isWarnEnabled();
            }
            case ERROR: {
                return this.log.isErrorEnabled();
            }
        }
        throw new IllegalStateException("Unsupported log level: " + logLevel);
    }

    public boolean isLoggingLevelEnabled(String logLevel) {
        String lowerLogLevel;
        switch (lowerLogLevel = StringUtils.lowerCase(logLevel)) {
            case "debug": {
                return this.log.isDebugEnabled();
            }
            case "trace": {
                return this.log.isTraceEnabled();
            }
            case "error": {
                return this.log.isErrorEnabled();
            }
            case "info": {
                return this.log.isInfoEnabled();
            }
            case "warn": {
                return this.log.isWarnEnabled();
            }
        }
        throw new IllegalArgumentException("Unknown log level: " + lowerLogLevel);
    }

    public void log(Level logLevel, Supplier<String> msg) {
        switch (logLevel) {
            case TRACE: {
                this.trace(msg);
                break;
            }
            case DEBUG: {
                this.debug(msg);
                break;
            }
            case INFO: {
                this.info(msg);
                break;
            }
            case WARN: {
                this.warn(msg);
                break;
            }
            case ERROR: {
                this.error(msg);
                break;
            }
            default: {
                throw new IllegalStateException("Unsupported log level: " + logLevel);
            }
        }
    }

    public static Logger loggerFor(Class<?> clz) {
        return new Logger(LoggerFactory.getLogger(clz));
    }

    public static Logger loggerFor(String name) {
        return new Logger(LoggerFactory.getLogger((String)name));
    }
}

