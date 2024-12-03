/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 *  org.apache.log4j.Priority
 */
package org.jboss.logging;

import java.text.MessageFormat;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.jboss.logging.Logger;

final class Log4jLogger
extends Logger {
    private static final long serialVersionUID = -5446154366955151335L;
    private final org.apache.log4j.Logger logger;

    Log4jLogger(String name) {
        super(name);
        this.logger = org.apache.log4j.Logger.getLogger((String)name);
    }

    @Override
    public boolean isEnabled(Logger.Level level) {
        Level l = Log4jLogger.translate(level);
        return this.logger.isEnabledFor((Priority)l) && l.isGreaterOrEqual((Priority)this.logger.getEffectiveLevel());
    }

    @Override
    protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
        Level translatedLevel = Log4jLogger.translate(level);
        if (this.logger.isEnabledFor((Priority)translatedLevel)) {
            try {
                this.logger.log(loggerClassName, (Priority)translatedLevel, parameters == null || parameters.length == 0 ? message : MessageFormat.format(String.valueOf(message), parameters), thrown);
            }
            catch (Throwable ignored) {
                // empty catch block
            }
        }
    }

    @Override
    protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
        Level translatedLevel = Log4jLogger.translate(level);
        if (this.logger.isEnabledFor((Priority)translatedLevel)) {
            try {
                this.logger.log(loggerClassName, (Priority)translatedLevel, (Object)(parameters == null ? String.format(format, new Object[0]) : String.format(format, parameters)), thrown);
            }
            catch (Throwable ignored) {
                // empty catch block
            }
        }
    }

    private static Level translate(Logger.Level level) {
        if (level == Logger.Level.TRACE) {
            return Level.TRACE;
        }
        if (level == Logger.Level.DEBUG) {
            return Level.DEBUG;
        }
        return Log4jLogger.infoOrHigher(level);
    }

    private static Level infoOrHigher(Logger.Level level) {
        if (level == Logger.Level.INFO) {
            return Level.INFO;
        }
        if (level == Logger.Level.WARN) {
            return Level.WARN;
        }
        if (level == Logger.Level.ERROR) {
            return Level.ERROR;
        }
        if (level == Logger.Level.FATAL) {
            return Level.FATAL;
        }
        return Level.ALL;
    }
}

