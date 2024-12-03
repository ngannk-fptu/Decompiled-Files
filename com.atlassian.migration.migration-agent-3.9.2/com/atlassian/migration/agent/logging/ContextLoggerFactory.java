/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.ArrayUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.Marker
 */
package com.atlassian.migration.agent.logging;

import com.google.common.annotations.VisibleForTesting;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

@ParametersAreNonnullByDefault
public final class ContextLoggerFactory {
    @VisibleForTesting
    static final Throwable MARKER_THROWABLE = new Throwable(){

        @Override
        public void printStackTrace() {
        }

        @Override
        public void printStackTrace(PrintStream s) {
        }

        @Override
        public void printStackTrace(PrintWriter s) {
        }
    };

    private ContextLoggerFactory() {
    }

    public static Logger getLogger(Class clazz) {
        return ContextLoggerFactory.getLogger(LoggerFactory.getLogger((Class)clazz));
    }

    @VisibleForTesting
    static Logger getLogger(Logger logger) {
        return new ContextLogger(logger);
    }

    static {
        MARKER_THROWABLE.setStackTrace(new StackTraceElement[0]);
    }

    private static class ContextLogger
    implements Logger {
        private final Logger delegate;

        private ContextLogger(Logger delegate) {
            this.delegate = Objects.requireNonNull(delegate);
        }

        public String getName() {
            return this.delegate.getName();
        }

        public boolean isTraceEnabled() {
            return this.delegate.isTraceEnabled();
        }

        public void trace(String msg) {
            this.delegate.trace(msg, MARKER_THROWABLE);
        }

        public void trace(String format, Object arg) {
            if (arg instanceof Throwable) {
                this.delegate.trace(format, arg);
            } else {
                this.delegate.trace(format, arg, (Object)MARKER_THROWABLE);
            }
        }

        public void trace(String format, Object arg1, Object arg2) {
            if (arg2 instanceof Throwable) {
                this.delegate.trace(format, arg1, arg2);
            } else {
                this.delegate.trace(format, new Object[]{arg1, arg2, MARKER_THROWABLE});
            }
        }

        public void trace(String format, Object ... arguments) {
            this.delegate.trace(format, ContextLogger.appendMarkerThrowable(arguments));
        }

        public void trace(String msg, Throwable t) {
            this.delegate.trace(msg, t);
        }

        public boolean isTraceEnabled(Marker marker) {
            return this.delegate.isTraceEnabled(marker);
        }

        public void trace(Marker marker, String msg) {
            this.delegate.trace(marker, msg, MARKER_THROWABLE);
        }

        public void trace(Marker marker, String format, Object arg) {
            if (arg instanceof Throwable) {
                this.delegate.trace(marker, format, arg);
            } else {
                this.delegate.trace(marker, format, arg, (Object)MARKER_THROWABLE);
            }
        }

        public void trace(Marker marker, String format, Object arg1, Object arg2) {
            if (arg2 instanceof Throwable) {
                this.delegate.trace(marker, format, arg1, arg2);
            } else {
                this.delegate.trace(marker, format, new Object[]{arg1, arg2, MARKER_THROWABLE});
            }
        }

        public void trace(Marker marker, String format, Object ... arguments) {
            this.delegate.trace(marker, format, ContextLogger.appendMarkerThrowable(arguments));
        }

        public void trace(Marker marker, String msg, Throwable t) {
            this.delegate.trace(marker, msg, t);
        }

        public boolean isDebugEnabled() {
            return this.delegate.isDebugEnabled();
        }

        public void debug(String msg) {
            this.delegate.debug(msg, MARKER_THROWABLE);
        }

        public void debug(String format, Object arg) {
            if (arg instanceof Throwable) {
                this.delegate.debug(format, arg);
            } else {
                this.delegate.debug(format, arg, (Object)MARKER_THROWABLE);
            }
        }

        public void debug(String format, Object arg1, Object arg2) {
            if (arg2 instanceof Throwable) {
                this.delegate.debug(format, arg1, arg2);
            } else {
                this.delegate.debug(format, new Object[]{arg1, arg2, MARKER_THROWABLE});
            }
        }

        public void debug(String format, Object ... arguments) {
            this.delegate.debug(format, ContextLogger.appendMarkerThrowable(arguments));
        }

        public void debug(String msg, Throwable t) {
            this.delegate.debug(msg, t);
        }

        public boolean isDebugEnabled(Marker marker) {
            return this.delegate.isDebugEnabled(marker);
        }

        public void debug(Marker marker, String msg) {
            this.delegate.debug(marker, msg, MARKER_THROWABLE);
        }

        public void debug(Marker marker, String format, Object arg) {
            if (arg instanceof Throwable) {
                this.delegate.debug(marker, format, arg);
            } else {
                this.delegate.debug(marker, format, arg, (Object)MARKER_THROWABLE);
            }
        }

        public void debug(Marker marker, String format, Object arg1, Object arg2) {
            if (arg2 instanceof Throwable) {
                this.delegate.debug(marker, format, arg1, arg2);
            } else {
                this.delegate.debug(marker, format, new Object[]{arg1, arg2, MARKER_THROWABLE});
            }
        }

        public void debug(Marker marker, String format, Object ... arguments) {
            this.delegate.debug(marker, format, ContextLogger.appendMarkerThrowable(arguments));
        }

        public void debug(Marker marker, String msg, Throwable t) {
            this.delegate.debug(marker, msg, t);
        }

        public boolean isInfoEnabled() {
            return this.delegate.isInfoEnabled();
        }

        public void info(String msg) {
            this.delegate.info(msg, MARKER_THROWABLE);
        }

        public void info(String format, Object arg) {
            if (arg instanceof Throwable) {
                this.delegate.info(format, arg);
            } else {
                this.delegate.info(format, arg, (Object)MARKER_THROWABLE);
            }
        }

        public void info(String format, Object arg1, Object arg2) {
            if (arg2 instanceof Throwable) {
                this.delegate.info(format, arg1, arg2);
            } else {
                this.delegate.info(format, new Object[]{arg1, arg2, MARKER_THROWABLE});
            }
        }

        public void info(String format, Object ... arguments) {
            this.delegate.info(format, ContextLogger.appendMarkerThrowable(arguments));
        }

        public void info(String msg, Throwable t) {
            this.delegate.info(msg, t);
        }

        public boolean isInfoEnabled(Marker marker) {
            return this.delegate.isInfoEnabled(marker);
        }

        public void info(Marker marker, String msg) {
            this.delegate.info(marker, msg, MARKER_THROWABLE);
        }

        public void info(Marker marker, String format, Object arg) {
            if (arg instanceof Throwable) {
                this.delegate.info(marker, format, arg);
            } else {
                this.delegate.info(marker, format, arg, (Object)MARKER_THROWABLE);
            }
        }

        public void info(Marker marker, String format, Object arg1, Object arg2) {
            if (arg2 instanceof Throwable) {
                this.delegate.info(marker, format, arg1, arg2);
            } else {
                this.delegate.info(marker, format, new Object[]{arg1, arg2, MARKER_THROWABLE});
            }
        }

        public void info(Marker marker, String format, Object ... arguments) {
            this.delegate.info(marker, format, ContextLogger.appendMarkerThrowable(arguments));
        }

        public void info(Marker marker, String msg, Throwable t) {
            this.delegate.info(marker, msg, t);
        }

        public boolean isWarnEnabled() {
            return this.delegate.isWarnEnabled();
        }

        public void warn(String msg) {
            this.delegate.warn(msg);
        }

        public void warn(String format, Object arg) {
            this.delegate.warn(format, arg);
        }

        public void warn(String format, Object ... arguments) {
            this.delegate.warn(format, arguments);
        }

        public void warn(String format, Object arg1, Object arg2) {
            this.delegate.warn(format, arg1, arg2);
        }

        public void warn(String msg, Throwable t) {
            this.delegate.warn(msg, t);
        }

        public boolean isWarnEnabled(Marker marker) {
            return this.delegate.isWarnEnabled(marker);
        }

        public void warn(Marker marker, String msg) {
            this.delegate.warn(marker, msg);
        }

        public void warn(Marker marker, String format, Object arg) {
            this.delegate.warn(marker, format, arg);
        }

        public void warn(Marker marker, String format, Object arg1, Object arg2) {
            this.delegate.warn(marker, format, arg1, arg2);
        }

        public void warn(Marker marker, String format, Object ... arguments) {
            this.delegate.warn(marker, format, arguments);
        }

        public void warn(Marker marker, String msg, Throwable t) {
            this.delegate.warn(marker, msg, t);
        }

        public boolean isErrorEnabled() {
            return this.delegate.isErrorEnabled();
        }

        public void error(String msg) {
            this.delegate.error(msg);
        }

        public void error(String format, Object arg) {
            this.delegate.error(format, arg);
        }

        public void error(String format, Object arg1, Object arg2) {
            this.delegate.error(format, arg1, arg2);
        }

        public void error(String format, Object ... arguments) {
            this.delegate.error(format, arguments);
        }

        public void error(String msg, Throwable t) {
            this.delegate.error(msg, t);
        }

        public boolean isErrorEnabled(Marker marker) {
            return this.delegate.isErrorEnabled(marker);
        }

        public void error(Marker marker, String msg) {
            this.delegate.error(marker, msg);
        }

        public void error(Marker marker, String format, Object arg) {
            this.delegate.error(marker, format, arg);
        }

        public void error(Marker marker, String format, Object arg1, Object arg2) {
            this.delegate.error(marker, format, arg1, arg2);
        }

        public void error(Marker marker, String format, Object ... arguments) {
            this.delegate.error(marker, format, arguments);
        }

        public void error(Marker marker, String msg, Throwable t) {
            this.delegate.error(marker, msg, t);
        }

        private static Object[] appendMarkerThrowable(Object[] array) {
            if (array[array.length - 1] instanceof Throwable) {
                return array;
            }
            return ArrayUtils.add((Object[])array, (Object)MARKER_THROWABLE);
        }
    }
}

