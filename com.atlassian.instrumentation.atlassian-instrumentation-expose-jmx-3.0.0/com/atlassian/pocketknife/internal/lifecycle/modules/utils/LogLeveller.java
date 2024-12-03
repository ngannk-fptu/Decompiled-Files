/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pocketknife.internal.lifecycle.modules.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogLeveller {
    private static final Logger log = LoggerFactory.getLogger(LogLeveller.class);

    public static Logger setInfo(Logger slf4jLogger) {
        if (!slf4jLogger.isInfoEnabled()) {
            LogLeveller.setLevelImpl(slf4jLogger, "INFO");
        }
        return slf4jLogger;
    }

    public static Logger setWarn(Logger slf4jLogger) {
        if (!slf4jLogger.isWarnEnabled()) {
            LogLeveller.setLevelImpl(slf4jLogger, "WARN");
        }
        return slf4jLogger;
    }

    public static Logger setWarnIfDevMode(Logger slf4jLogger) {
        if (LogLeveller.isDevMode()) {
            LogLeveller.setWarn(slf4jLogger);
        }
        return slf4jLogger;
    }

    public static Logger setInfoIfDevMode(Logger slf4jLogger) {
        if (LogLeveller.isDevMode()) {
            LogLeveller.setInfo(slf4jLogger);
        }
        return slf4jLogger;
    }

    private static boolean isDevMode() {
        return Boolean.getBoolean("jira.dev.mode") || Boolean.getBoolean("atlassian.dev.mode");
    }

    private static void setLevelImpl(Logger slf4jLogger, String levelName) {
        Class log4JClass = LogLeveller.findClass("org.apache.log4j.Logger");
        if (log4JClass != null) {
            try {
                Method getLogger = log4JClass.getMethod("getLogger", String.class);
                Object logger = getLogger.invoke(null, slf4jLogger.getName());
                Class levelClass = LogLeveller.findClass("org.apache.log4j.Level");
                Field logLevelField = levelClass.getField(levelName);
                Object levelInstance = logLevelField.get(null);
                Method setLevel = log4JClass.getMethod("setLevel", levelClass);
                setLevel.invoke(logger, levelInstance);
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
            catch (InvocationTargetException invocationTargetException) {
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (NoSuchFieldException noSuchFieldException) {
                // empty catch block
            }
        }
    }

    private static Class findClass(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            log.warn(String.format("Unable to find class '%s'.  Have you OSGI imported it??", className));
            return null;
        }
    }
}

