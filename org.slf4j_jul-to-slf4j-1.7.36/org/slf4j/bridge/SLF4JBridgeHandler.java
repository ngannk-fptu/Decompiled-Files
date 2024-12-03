/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.spi.LocationAwareLogger
 */
package org.slf4j.bridge;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class SLF4JBridgeHandler
extends Handler {
    private static final String FQCN = java.util.logging.Logger.class.getName();
    private static final String UNKNOWN_LOGGER_NAME = "unknown.jul.logger";
    private static final int TRACE_LEVEL_THRESHOLD = Level.FINEST.intValue();
    private static final int DEBUG_LEVEL_THRESHOLD = Level.FINE.intValue();
    private static final int INFO_LEVEL_THRESHOLD = Level.INFO.intValue();
    private static final int WARN_LEVEL_THRESHOLD = Level.WARNING.intValue();

    public static void install() {
        LogManager.getLogManager().getLogger("").addHandler(new SLF4JBridgeHandler());
    }

    private static java.util.logging.Logger getRootLogger() {
        return LogManager.getLogManager().getLogger("");
    }

    public static void uninstall() throws SecurityException {
        java.util.logging.Logger rootLogger = SLF4JBridgeHandler.getRootLogger();
        Handler[] handlers = rootLogger.getHandlers();
        for (int i = 0; i < handlers.length; ++i) {
            if (!(handlers[i] instanceof SLF4JBridgeHandler)) continue;
            rootLogger.removeHandler(handlers[i]);
        }
    }

    public static boolean isInstalled() throws SecurityException {
        java.util.logging.Logger rootLogger = SLF4JBridgeHandler.getRootLogger();
        Handler[] handlers = rootLogger.getHandlers();
        for (int i = 0; i < handlers.length; ++i) {
            if (!(handlers[i] instanceof SLF4JBridgeHandler)) continue;
            return true;
        }
        return false;
    }

    public static void removeHandlersForRootLogger() {
        java.util.logging.Logger rootLogger = SLF4JBridgeHandler.getRootLogger();
        Handler[] handlers = rootLogger.getHandlers();
        for (int i = 0; i < handlers.length; ++i) {
            rootLogger.removeHandler(handlers[i]);
        }
    }

    public void close() {
    }

    public void flush() {
    }

    protected Logger getSLF4JLogger(LogRecord record) {
        String name = record.getLoggerName();
        if (name == null) {
            name = UNKNOWN_LOGGER_NAME;
        }
        return LoggerFactory.getLogger((String)name);
    }

    protected void callLocationAwareLogger(LocationAwareLogger lal, LogRecord record) {
        int julLevelValue = record.getLevel().intValue();
        int slf4jLevel = julLevelValue <= TRACE_LEVEL_THRESHOLD ? 0 : (julLevelValue <= DEBUG_LEVEL_THRESHOLD ? 10 : (julLevelValue <= INFO_LEVEL_THRESHOLD ? 20 : (julLevelValue <= WARN_LEVEL_THRESHOLD ? 30 : 40)));
        String i18nMessage = this.getMessageI18N(record);
        lal.log(null, FQCN, slf4jLevel, i18nMessage, null, record.getThrown());
    }

    protected void callPlainSLF4JLogger(Logger slf4jLogger, LogRecord record) {
        String i18nMessage = this.getMessageI18N(record);
        int julLevelValue = record.getLevel().intValue();
        if (julLevelValue <= TRACE_LEVEL_THRESHOLD) {
            slf4jLogger.trace(i18nMessage, record.getThrown());
        } else if (julLevelValue <= DEBUG_LEVEL_THRESHOLD) {
            slf4jLogger.debug(i18nMessage, record.getThrown());
        } else if (julLevelValue <= INFO_LEVEL_THRESHOLD) {
            slf4jLogger.info(i18nMessage, record.getThrown());
        } else if (julLevelValue <= WARN_LEVEL_THRESHOLD) {
            slf4jLogger.warn(i18nMessage, record.getThrown());
        } else {
            slf4jLogger.error(i18nMessage, record.getThrown());
        }
    }

    private String getMessageI18N(LogRecord record) {
        Object[] params;
        String message = record.getMessage();
        if (message == null) {
            return null;
        }
        ResourceBundle bundle = record.getResourceBundle();
        if (bundle != null) {
            try {
                message = bundle.getString(message);
            }
            catch (MissingResourceException missingResourceException) {
                // empty catch block
            }
        }
        if ((params = record.getParameters()) != null && params.length > 0) {
            try {
                message = MessageFormat.format(message, params);
            }
            catch (IllegalArgumentException e) {
                return message;
            }
        }
        return message;
    }

    public void publish(LogRecord record) {
        if (record == null) {
            return;
        }
        Logger slf4jLogger = this.getSLF4JLogger(record);
        String message = record.getMessage();
        if (message == null) {
            message = "";
        }
        if (slf4jLogger instanceof LocationAwareLogger) {
            this.callLocationAwareLogger((LocationAwareLogger)slf4jLogger, record);
        } else {
            this.callPlainSLF4JLogger(slf4jLogger, record);
        }
    }
}

