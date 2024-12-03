/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.spi.LocationAwareLogger
 */
package org.jboss.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.MessageFormat;
import org.jboss.logging.Logger;
import org.slf4j.spi.LocationAwareLogger;

final class Slf4jLocationAwareLogger
extends Logger {
    private static final long serialVersionUID = 8685757928087758380L;
    private static final Object[] EMPTY = new Object[0];
    private static final boolean POST_1_6;
    private static final Method LOG_METHOD;
    private final LocationAwareLogger logger;

    Slf4jLocationAwareLogger(String name, LocationAwareLogger logger2) {
        super(name);
        this.logger = logger2;
    }

    @Override
    public boolean isEnabled(Logger.Level level) {
        if (level != null) {
            switch (level) {
                case FATAL: {
                    return this.logger.isErrorEnabled();
                }
                case ERROR: {
                    return this.logger.isErrorEnabled();
                }
                case WARN: {
                    return this.logger.isWarnEnabled();
                }
                case INFO: {
                    return this.logger.isInfoEnabled();
                }
                case DEBUG: {
                    return this.logger.isDebugEnabled();
                }
                case TRACE: {
                    return this.logger.isTraceEnabled();
                }
            }
        }
        return true;
    }

    @Override
    protected void doLog(Logger.Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
        if (this.isEnabled(level)) {
            String text = parameters == null || parameters.length == 0 ? String.valueOf(message) : MessageFormat.format(String.valueOf(message), parameters);
            Slf4jLocationAwareLogger.doLog(this.logger, loggerClassName, Slf4jLocationAwareLogger.translate(level), text, thrown);
        }
    }

    @Override
    protected void doLogf(Logger.Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
        if (this.isEnabled(level)) {
            String text = parameters == null ? String.format(format, new Object[0]) : String.format(format, parameters);
            Slf4jLocationAwareLogger.doLog(this.logger, loggerClassName, Slf4jLocationAwareLogger.translate(level), text, thrown);
        }
    }

    private static void doLog(LocationAwareLogger logger2, String className, int level, String text, Throwable thrown) {
        try {
            if (POST_1_6) {
                LOG_METHOD.invoke((Object)logger2, null, className, level, text, EMPTY, thrown);
            } else {
                LOG_METHOD.invoke((Object)logger2, null, className, level, text, thrown);
            }
        }
        catch (InvocationTargetException e) {
            try {
                throw e.getCause();
            }
            catch (RuntimeException ex) {
                throw ex;
            }
            catch (Error er) {
                throw er;
            }
            catch (Throwable throwable) {
                throw new UndeclaredThrowableException(throwable);
            }
        }
        catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    private static int translate(Logger.Level level) {
        if (level != null) {
            switch (level) {
                case FATAL: 
                case ERROR: {
                    return 40;
                }
                case WARN: {
                    return 30;
                }
                case INFO: {
                    return 20;
                }
                case DEBUG: {
                    return 10;
                }
                case TRACE: {
                    return 0;
                }
            }
        }
        return 0;
    }

    static {
        Method[] methods = LocationAwareLogger.class.getDeclaredMethods();
        Method logMethod = null;
        boolean post16 = false;
        for (Method method : methods) {
            if (!method.getName().equals("log")) continue;
            logMethod = method;
            Class<?>[] parameterTypes = method.getParameterTypes();
            post16 = parameterTypes.length == 6;
        }
        if (logMethod == null) {
            throw new NoSuchMethodError("Cannot find LocationAwareLogger.log() method");
        }
        POST_1_6 = post16;
        LOG_METHOD = logMethod;
    }
}

