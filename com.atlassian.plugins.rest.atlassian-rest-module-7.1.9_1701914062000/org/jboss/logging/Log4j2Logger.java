/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.LoggingException
 *  org.apache.logging.log4j.message.Message
 *  org.apache.logging.log4j.message.MessageFormatMessageFactory
 *  org.apache.logging.log4j.message.StringFormattedMessage
 *  org.apache.logging.log4j.spi.AbstractLogger
 */
package org.jboss.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFormatMessageFactory;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.jboss.logging.Logger;

final class Log4j2Logger
extends Logger {
    private static final long serialVersionUID = -2507841068232627725L;
    private final AbstractLogger logger;
    private final MessageFormatMessageFactory messageFactory;

    Log4j2Logger(String name) {
        super(name);
        org.apache.logging.log4j.Logger logger2 = LogManager.getLogger((String)name);
        if (!(logger2 instanceof AbstractLogger)) {
            throw new LoggingException("The logger for [" + name + "] does not extend AbstractLogger. Actual logger: " + logger2.getClass().getName());
        }
        this.logger = (AbstractLogger)logger2;
        this.messageFactory = new MessageFormatMessageFactory();
    }

    @Override
    public boolean isEnabled(Logger.Level level) {
        return this.logger.isEnabled(Log4j2Logger.translate(level));
    }

    @Override
    protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
        Level translatedLevel = Log4j2Logger.translate(level);
        if (this.logger.isEnabled(translatedLevel)) {
            try {
                this.logger.logMessage(loggerClassName, translatedLevel, null, parameters == null || parameters.length == 0 ? this.messageFactory.newMessage(message) : this.messageFactory.newMessage(String.valueOf(message), parameters), thrown);
            }
            catch (Throwable ignored) {
                // empty catch block
            }
        }
    }

    @Override
    protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
        Level translatedLevel = Log4j2Logger.translate(level);
        if (this.logger.isEnabled(translatedLevel)) {
            try {
                this.logger.logMessage(loggerClassName, translatedLevel, null, (Message)new StringFormattedMessage(format, parameters), thrown);
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
        return Log4j2Logger.infoOrHigher(level);
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

