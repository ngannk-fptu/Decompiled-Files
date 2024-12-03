/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logmanager.ExtLogRecord$FormatStyle
 *  org.jboss.logmanager.Level
 *  org.jboss.logmanager.Logger
 */
package org.jboss.logging;

import org.jboss.logging.Logger;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.Level;

final class JBossLogManagerLogger
extends Logger {
    private static final long serialVersionUID = 7429618317727584742L;
    private final org.jboss.logmanager.Logger logger;

    JBossLogManagerLogger(String name, org.jboss.logmanager.Logger logger) {
        super(name);
        this.logger = logger;
    }

    @Override
    public boolean isEnabled(Logger.Level level) {
        return this.logger.isLoggable(JBossLogManagerLogger.translate(level));
    }

    @Override
    protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
        java.util.logging.Level translatedLevel = JBossLogManagerLogger.translate(level);
        if (this.logger.isLoggable(translatedLevel)) {
            if (parameters == null) {
                this.logger.log(loggerClassName, translatedLevel, String.valueOf(message), thrown);
            } else {
                this.logger.log(loggerClassName, translatedLevel, String.valueOf(message), ExtLogRecord.FormatStyle.MESSAGE_FORMAT, parameters, thrown);
            }
        }
    }

    @Override
    protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
        if (parameters == null) {
            this.logger.log(loggerClassName, JBossLogManagerLogger.translate(level), format, thrown);
        } else {
            this.logger.log(loggerClassName, JBossLogManagerLogger.translate(level), format, ExtLogRecord.FormatStyle.PRINTF, parameters, thrown);
        }
    }

    private static java.util.logging.Level translate(Logger.Level level) {
        if (level == Logger.Level.TRACE) {
            return Level.TRACE;
        }
        if (level == Logger.Level.DEBUG) {
            return Level.DEBUG;
        }
        return JBossLogManagerLogger.infoOrHigher(level);
    }

    private static java.util.logging.Level infoOrHigher(Logger.Level level) {
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

