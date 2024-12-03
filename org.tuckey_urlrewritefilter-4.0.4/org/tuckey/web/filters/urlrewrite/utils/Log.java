/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.tuckey.web.filters.urlrewrite.utils;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

public class Log {
    private static Log localLog = Log.getLog(Log.class);
    private static ServletContext context = null;
    private static final String DEFAULT_LOG_LEVEL = "INFO";
    private static boolean usingSystemOut = false;
    private static boolean usingSystemErr = false;
    private static boolean usingSlf4j = false;
    private static boolean traceLevelEnabled = false;
    private static boolean debugLevelEnabled = false;
    private static boolean infoLevelEnabled = false;
    private static boolean warnLevelEnabled = false;
    private static boolean errorLevelEnabled = false;
    private static boolean fatalLevelEnabled = false;
    private Class clazz = null;
    private Logger slf4jLogger = null;

    private Log(Class clazz) {
        this.clazz = clazz;
        this.isUsingSlf4j();
    }

    public boolean isUsingSlf4j() {
        if (usingSlf4j && this.slf4jLogger == null) {
            this.slf4jLogger = LoggerFactory.getLogger((Class)this.clazz);
        }
        return usingSlf4j;
    }

    public boolean isUsingSystemOut() {
        return usingSystemOut;
    }

    public boolean isUsingSystemErr() {
        return usingSystemErr;
    }

    public boolean isTraceEnabled() {
        if (this.isUsingSlf4j()) {
            return this.slf4jLogger.isTraceEnabled();
        }
        return traceLevelEnabled;
    }

    public boolean isDebugEnabled() {
        if (this.isUsingSlf4j()) {
            return this.slf4jLogger.isDebugEnabled();
        }
        return traceLevelEnabled || debugLevelEnabled;
    }

    public boolean isInfoEnabled() {
        if (this.isUsingSlf4j()) {
            return this.slf4jLogger.isInfoEnabled();
        }
        return traceLevelEnabled || debugLevelEnabled || infoLevelEnabled;
    }

    public boolean isWarnEnabled() {
        if (this.isUsingSlf4j()) {
            return this.slf4jLogger.isWarnEnabled();
        }
        return traceLevelEnabled || debugLevelEnabled || infoLevelEnabled || warnLevelEnabled;
    }

    public boolean isErrorEnabled() {
        if (this.isUsingSlf4j()) {
            return this.slf4jLogger.isErrorEnabled();
        }
        return traceLevelEnabled || debugLevelEnabled || infoLevelEnabled || warnLevelEnabled || errorLevelEnabled;
    }

    public boolean isFatalEnabled() {
        if (this.isUsingSlf4j()) {
            return this.slf4jLogger.isErrorEnabled();
        }
        return traceLevelEnabled || debugLevelEnabled || infoLevelEnabled || warnLevelEnabled || errorLevelEnabled || fatalLevelEnabled;
    }

    public void trace(Object o) {
        if (!this.isTraceEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.trace(String.valueOf(o));
            return;
        }
        this.write("TRACE", o);
    }

    public void trace(Object o, Throwable throwable) {
        if (!this.isTraceEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.trace(String.valueOf(o), throwable);
            return;
        }
        this.write("TRACE", o, throwable);
    }

    public void trace(Throwable throwable) {
        if (!this.isTraceEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.trace("", throwable);
            return;
        }
        this.write("TRACE", throwable, throwable);
    }

    public void debug(Object o) {
        if (!this.isDebugEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.debug(String.valueOf(o));
            return;
        }
        this.write("DEBUG", o);
    }

    public void debug(Object o, Throwable throwable) {
        if (!this.isDebugEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.debug(String.valueOf(o), throwable);
            return;
        }
        this.write("DEBUG", o, throwable);
    }

    public void debug(Throwable throwable) {
        if (!this.isDebugEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.debug("", throwable);
            return;
        }
        this.write("DEBUG", throwable, throwable);
    }

    public void info(Object o) {
        if (!this.isInfoEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.info(String.valueOf(o));
            return;
        }
        this.write(DEFAULT_LOG_LEVEL, o);
    }

    public void info(Object o, Throwable throwable) {
        if (!this.isInfoEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.info(String.valueOf(o), throwable);
            return;
        }
        this.write(DEFAULT_LOG_LEVEL, o, throwable);
    }

    public void info(Throwable throwable) {
        if (!this.isInfoEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.info("", throwable);
            return;
        }
        this.write(DEFAULT_LOG_LEVEL, throwable, throwable);
    }

    public void warn(Object o) {
        if (!this.isWarnEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.warn(String.valueOf(o));
            return;
        }
        this.write("WARN", o);
    }

    public void warn(Object o, Throwable throwable) {
        if (!this.isWarnEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.warn(String.valueOf(o), throwable);
            return;
        }
        this.write("WARN", o, throwable);
    }

    public void warn(Throwable throwable) {
        if (!this.isWarnEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.warn("", throwable);
            return;
        }
        this.write("WARN", throwable, throwable);
    }

    public void error(Object o) {
        if (!this.isErrorEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.error(String.valueOf(o));
            return;
        }
        this.write("ERROR", o);
    }

    public void error(Object o, Throwable throwable) {
        if (!this.isErrorEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.error(String.valueOf(o), throwable);
            return;
        }
        this.write("ERROR", o, throwable);
    }

    public void error(Throwable throwable) {
        if (!this.isErrorEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.error("", throwable);
            return;
        }
        this.write("ERROR", throwable, throwable);
    }

    public void fatal(Object o) {
        if (!this.isFatalEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.error(String.valueOf(o));
            return;
        }
        this.write("FATAL", o);
    }

    public void fatal(Object o, Throwable throwable) {
        if (!this.isFatalEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.error(String.valueOf(o), throwable);
            return;
        }
        this.write("FATAL", o, throwable);
    }

    public void fatal(Throwable throwable) {
        if (!this.isFatalEnabled()) {
            return;
        }
        if (this.isUsingSlf4j()) {
            this.slf4jLogger.error("", throwable);
            return;
        }
        this.write("FATAL", throwable, throwable);
    }

    public static Log getLog(Class aClass) {
        return new Log(aClass);
    }

    public static void setLevel(String level) {
        level = level == null ? null : level.toUpperCase();
        usingSystemOut = false;
        usingSystemErr = false;
        if ("SLF4J".equalsIgnoreCase(level)) {
            usingSlf4j = true;
        } else {
            if (level != null) {
                if (level.startsWith("SYSOUT:")) {
                    usingSystemOut = true;
                    level = level.substring("SYSOUT:".length());
                }
                if (level.startsWith("STDOUT:")) {
                    usingSystemOut = true;
                    level = level.substring("STDOUT:".length());
                }
                if (level.startsWith("STDERR:")) {
                    usingSystemErr = true;
                    level = level.substring("STDERR:".length());
                }
                if (level.startsWith("SYSERR:")) {
                    usingSystemErr = true;
                    level = level.substring("SYSERR:".length());
                }
            }
            Log.setLevelInternal(level);
        }
    }

    private static void setLevelInternal(String level) {
        traceLevelEnabled = false;
        debugLevelEnabled = false;
        infoLevelEnabled = false;
        warnLevelEnabled = false;
        errorLevelEnabled = false;
        fatalLevelEnabled = false;
        boolean levelSelected = false;
        if ("TRACE".equalsIgnoreCase(level)) {
            traceLevelEnabled = true;
            levelSelected = true;
        }
        if ("DEBUG".equalsIgnoreCase(level)) {
            debugLevelEnabled = true;
            levelSelected = true;
        }
        if (DEFAULT_LOG_LEVEL.equalsIgnoreCase(level)) {
            infoLevelEnabled = true;
            levelSelected = true;
        }
        if ("WARN".equalsIgnoreCase(level)) {
            warnLevelEnabled = true;
            levelSelected = true;
        }
        if ("ERROR".equalsIgnoreCase(level)) {
            errorLevelEnabled = true;
            levelSelected = true;
        }
        if ("FATAL".equalsIgnoreCase(level)) {
            fatalLevelEnabled = true;
            levelSelected = true;
        }
        if (!levelSelected) {
            infoLevelEnabled = true;
        }
    }

    private void write(String level, Object o, Throwable throwable) {
        String msg = this.getMsg(level, o).toString();
        if (usingSystemOut || context == null) {
            System.out.println(msg);
            throwable.printStackTrace(System.out);
        } else if (usingSystemErr) {
            System.err.println(msg);
            throwable.printStackTrace(System.err);
        } else {
            context.log(msg, throwable);
        }
    }

    private void write(String level, Object o) {
        String msg = this.getMsg(level, o).toString();
        if (usingSystemOut || context == null) {
            System.out.println(msg);
        } else if (usingSystemErr) {
            System.err.println(msg);
        } else {
            context.log(msg);
        }
    }

    private StringBuffer getMsg(String level, Object o) {
        StringBuffer msg = new StringBuffer();
        if (this.clazz == null) {
            msg.append("null");
        } else {
            msg.append(this.clazz.getName());
        }
        msg.append(" ");
        msg.append(level);
        msg.append(": ");
        msg.append(o.toString());
        return msg;
    }

    public static void resetAll() {
        context = null;
        Log.setLevel(DEFAULT_LOG_LEVEL);
        usingSystemOut = false;
        usingSystemErr = false;
        usingSlf4j = false;
    }

    public static void setConfiguration(FilterConfig filterConfig) {
        Log.resetAll();
        if (filterConfig == null) {
            localLog.error("no filter config passed");
            return;
        }
        context = filterConfig.getServletContext();
        String logLevelConf = filterConfig.getInitParameter("logLevel");
        if (logLevelConf != null) {
            logLevelConf = StringUtils.trim(logLevelConf);
        }
        Log.setLevel(logLevelConf);
        localLog.debug("logLevel set to " + logLevelConf);
    }
}

