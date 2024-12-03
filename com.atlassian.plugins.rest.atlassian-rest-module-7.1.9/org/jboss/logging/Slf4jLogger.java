/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package org.jboss.logging;

import java.text.MessageFormat;
import org.jboss.logging.Logger;

final class Slf4jLogger
extends Logger {
    private static final long serialVersionUID = 8685757928087758380L;
    private final org.slf4j.Logger logger;

    Slf4jLogger(String name, org.slf4j.Logger logger2) {
        super(name);
        this.logger = logger2;
    }

    @Override
    public boolean isEnabled(Logger.Level level) {
        if (level == Logger.Level.TRACE) {
            return this.logger.isTraceEnabled();
        }
        if (level == Logger.Level.DEBUG) {
            return this.logger.isDebugEnabled();
        }
        return this.infoOrHigherEnabled(level);
    }

    private boolean infoOrHigherEnabled(Logger.Level level) {
        if (level == Logger.Level.INFO) {
            return this.logger.isInfoEnabled();
        }
        if (level == Logger.Level.WARN) {
            return this.logger.isWarnEnabled();
        }
        if (level == Logger.Level.ERROR || level == Logger.Level.FATAL) {
            return this.logger.isErrorEnabled();
        }
        return true;
    }

    @Override
    protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
        if (this.isEnabled(level)) {
            try {
                String text;
                String string = text = parameters == null || parameters.length == 0 ? String.valueOf(message) : MessageFormat.format(String.valueOf(message), parameters);
                if (level == Logger.Level.INFO) {
                    this.logger.info(text, thrown);
                } else if (level == Logger.Level.WARN) {
                    this.logger.warn(text, thrown);
                } else if (level == Logger.Level.ERROR || level == Logger.Level.FATAL) {
                    this.logger.error(text, thrown);
                } else if (level == Logger.Level.DEBUG) {
                    this.logger.debug(text, thrown);
                } else if (level == Logger.Level.TRACE) {
                    this.logger.debug(text, thrown);
                }
            }
            catch (Throwable ignored) {
                // empty catch block
            }
        }
    }

    @Override
    protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
        if (this.isEnabled(level)) {
            try {
                String text;
                String string = text = parameters == null ? String.format(format, new Object[0]) : String.format(format, parameters);
                if (level == Logger.Level.INFO) {
                    this.logger.info(text, thrown);
                } else if (level == Logger.Level.WARN) {
                    this.logger.warn(text, thrown);
                } else if (level == Logger.Level.ERROR || level == Logger.Level.FATAL) {
                    this.logger.error(text, thrown);
                } else if (level == Logger.Level.DEBUG) {
                    this.logger.debug(text, thrown);
                } else if (level == Logger.Level.TRACE) {
                    this.logger.debug(text, thrown);
                }
            }
            catch (Throwable ignored) {
                // empty catch block
            }
        }
    }
}

