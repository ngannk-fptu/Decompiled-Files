/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.logging;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LoggerFactory;
import com.hazelcast.logging.NoLogFactory;
import com.hazelcast.logging.StandardLoggerFactory;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.util.StringUtil;

public final class Logger {
    private static volatile LoggerFactory loggerFactory;
    private static String loggerFactoryClassOrType;
    private static final Object FACTORY_LOCK;

    private Logger() {
    }

    public static ILogger getLogger(Class clazz) {
        return Logger.getLogger(clazz.getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ILogger getLogger(String name) {
        LoggerFactory existingFactory = loggerFactory;
        if (existingFactory != null) {
            return existingFactory.getLogger(name);
        }
        Object object = FACTORY_LOCK;
        synchronized (object) {
            existingFactory = loggerFactory;
            if (existingFactory != null) {
                return existingFactory.getLogger(name);
            }
            LoggerFactory createdFactory = null;
            String loggingClass = System.getProperty("hazelcast.logging.class");
            if (!StringUtil.isNullOrEmpty(loggingClass)) {
                createdFactory = Logger.tryToCreateLoggerFactory(loggingClass);
            }
            if (createdFactory != null) {
                loggerFactory = createdFactory;
                loggerFactoryClassOrType = loggingClass;
            } else {
                String loggingType = System.getProperty("hazelcast.logging.type");
                createdFactory = Logger.createLoggerFactory(loggingType);
                if (!StringUtil.isNullOrEmpty(loggingType)) {
                    loggerFactory = createdFactory;
                    loggerFactoryClassOrType = loggingType;
                }
            }
            return createdFactory.getLogger(name);
        }
    }

    public static ILogger noLogger() {
        return new NoLogFactory.NoLogger();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static LoggerFactory newLoggerFactory(String preferredType) {
        Object object = FACTORY_LOCK;
        synchronized (object) {
            LoggerFactory obtainedFactory = Logger.tryToObtainFactoryByConfiguredClass();
            if (obtainedFactory != null) {
                return obtainedFactory;
            }
            obtainedFactory = Logger.tryToObtainFactoryByPreferredType(preferredType);
            if (obtainedFactory != null) {
                return obtainedFactory;
            }
            assert (StringUtil.isNullOrEmpty(preferredType));
            obtainedFactory = Logger.obtainFactoryByRecoveringFromNullOrEmptyPreferredType();
            return obtainedFactory;
        }
    }

    private static LoggerFactory tryToObtainFactoryByConfiguredClass() {
        String loggingClass = System.getProperty("hazelcast.logging.class");
        if (!StringUtil.isNullOrEmpty(loggingClass)) {
            if (Logger.sharedFactoryIsCompatibleWith(loggingClass)) {
                return loggerFactory;
            }
            LoggerFactory createdFactory = Logger.tryToCreateLoggerFactory(loggingClass);
            if (createdFactory != null) {
                if (loggerFactory == null) {
                    loggerFactory = createdFactory;
                    loggerFactoryClassOrType = loggingClass;
                }
                return createdFactory;
            }
        }
        return null;
    }

    private static LoggerFactory tryToObtainFactoryByPreferredType(String preferredType) {
        if (!StringUtil.isNullOrEmpty(preferredType)) {
            if (Logger.sharedFactoryIsCompatibleWith(preferredType)) {
                return loggerFactory;
            }
            LoggerFactory createdFactory = Logger.createLoggerFactory(preferredType);
            if (loggerFactory == null) {
                loggerFactory = createdFactory;
                loggerFactoryClassOrType = preferredType;
            }
            return createdFactory;
        }
        return null;
    }

    private static LoggerFactory obtainFactoryByRecoveringFromNullOrEmptyPreferredType() {
        if (loggerFactory != null) {
            return loggerFactory;
        }
        String loggingType = System.getProperty("hazelcast.logging.type");
        if (!StringUtil.isNullOrEmpty(loggingType)) {
            LoggerFactory createdFactory;
            loggerFactory = createdFactory = Logger.createLoggerFactory(loggingType);
            loggerFactoryClassOrType = loggingType;
            return createdFactory;
        }
        StandardLoggerFactory createdFactory = new StandardLoggerFactory();
        loggerFactory = createdFactory;
        loggerFactoryClassOrType = "jdk";
        return createdFactory;
    }

    private static boolean sharedFactoryIsCompatibleWith(String requiredClassOrType) {
        return loggerFactory != null && !StringUtil.isNullOrEmpty(loggerFactoryClassOrType) && loggerFactoryClassOrType.equals(requiredClassOrType);
    }

    private static LoggerFactory createLoggerFactory(String preferredType) {
        LoggerFactory createdFactory;
        if ("log4j".equals(preferredType)) {
            createdFactory = Logger.tryToCreateLoggerFactory("com.hazelcast.logging.Log4jFactory");
        } else if ("log4j2".equals(preferredType)) {
            createdFactory = Logger.tryToCreateLoggerFactory("com.hazelcast.logging.Log4j2Factory");
        } else if ("slf4j".equals(preferredType)) {
            createdFactory = Logger.tryToCreateLoggerFactory("com.hazelcast.logging.Slf4jFactory");
        } else if ("jdk".equals(preferredType)) {
            createdFactory = new StandardLoggerFactory();
        } else if ("none".equals(preferredType)) {
            createdFactory = new NoLogFactory();
        } else {
            if (!StringUtil.isNullOrEmpty(preferredType)) {
                Logger.logError("Unexpected logging type '" + preferredType + "', falling back to JDK logging.", null);
            }
            createdFactory = new StandardLoggerFactory();
        }
        if (createdFactory == null) {
            Logger.logError("Falling back to JDK logging.", null);
            createdFactory = new StandardLoggerFactory();
        }
        return createdFactory;
    }

    private static LoggerFactory tryToCreateLoggerFactory(String className) {
        try {
            return (LoggerFactory)ClassLoaderUtil.newInstance(null, className);
        }
        catch (Exception e) {
            Logger.logError("Failed to create '" + className + "' logger factory:", e);
            return null;
        }
    }

    private static void logError(String message, Throwable cause) {
        System.err.println(message);
        if (cause != null) {
            cause.printStackTrace();
        }
    }

    static {
        FACTORY_LOCK = new Object();
    }
}

