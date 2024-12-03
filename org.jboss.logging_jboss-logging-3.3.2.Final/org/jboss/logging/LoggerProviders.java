/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.logging.LogManager;
import org.jboss.logging.JBossLogManagerProvider;
import org.jboss.logging.JDKLoggerProvider;
import org.jboss.logging.Log4j2LoggerProvider;
import org.jboss.logging.Log4jLoggerProvider;
import org.jboss.logging.Logger;
import org.jboss.logging.LoggerProvider;
import org.jboss.logging.Slf4jLoggerProvider;

final class LoggerProviders {
    static final String LOGGING_PROVIDER_KEY = "org.jboss.logging.provider";
    static final LoggerProvider PROVIDER = LoggerProviders.find();

    private static LoggerProvider find() {
        return LoggerProviders.findProvider();
    }

    private static LoggerProvider findProvider() {
        ClassLoader cl = LoggerProviders.class.getClassLoader();
        try {
            String loggerProvider = AccessController.doPrivileged(new PrivilegedAction<String>(){

                @Override
                public String run() {
                    return System.getProperty(LoggerProviders.LOGGING_PROVIDER_KEY);
                }
            });
            if (loggerProvider != null) {
                if ("jboss".equalsIgnoreCase(loggerProvider)) {
                    return LoggerProviders.tryJBossLogManager(cl, "system property");
                }
                if ("jdk".equalsIgnoreCase(loggerProvider)) {
                    return LoggerProviders.tryJDK("system property");
                }
                if ("log4j2".equalsIgnoreCase(loggerProvider)) {
                    return LoggerProviders.tryLog4j2(cl, "system property");
                }
                if ("log4j".equalsIgnoreCase(loggerProvider)) {
                    return LoggerProviders.tryLog4j(cl, "system property");
                }
                if ("slf4j".equalsIgnoreCase(loggerProvider)) {
                    return LoggerProviders.trySlf4j("system property");
                }
            }
        }
        catch (Throwable t) {
            // empty catch block
        }
        try {
            ServiceLoader<LoggerProvider> loader = ServiceLoader.load(LoggerProvider.class, cl);
            Iterator<LoggerProvider> iter = loader.iterator();
            while (true) {
                try {
                    if (iter.hasNext()) {
                        LoggerProvider provider = iter.next();
                        LoggerProviders.logProvider(provider, "service loader");
                        return provider;
                    }
                }
                catch (ServiceConfigurationError serviceConfigurationError) {
                    continue;
                }
                break;
            }
        }
        catch (Throwable ignore) {
            // empty catch block
        }
        try {
            return LoggerProviders.tryJBossLogManager(cl, null);
        }
        catch (Throwable t) {
            try {
                return LoggerProviders.tryLog4j2(cl, null);
            }
            catch (Throwable t2) {
                try {
                    return LoggerProviders.tryLog4j(cl, null);
                }
                catch (Throwable t3) {
                    try {
                        Class.forName("ch.qos.logback.classic.Logger", false, cl);
                        return LoggerProviders.trySlf4j(null);
                    }
                    catch (Throwable throwable) {
                        return LoggerProviders.tryJDK(null);
                    }
                }
            }
        }
    }

    private static JDKLoggerProvider tryJDK(String via) {
        JDKLoggerProvider provider = new JDKLoggerProvider();
        LoggerProviders.logProvider(provider, via);
        return provider;
    }

    private static LoggerProvider trySlf4j(String via) {
        Slf4jLoggerProvider provider = new Slf4jLoggerProvider();
        LoggerProviders.logProvider(provider, via);
        return provider;
    }

    private static LoggerProvider tryLog4j2(ClassLoader cl, String via) throws ClassNotFoundException {
        Class.forName("org.apache.logging.log4j.Logger", true, cl);
        Class.forName("org.apache.logging.log4j.LogManager", true, cl);
        Class.forName("org.apache.logging.log4j.spi.AbstractLogger", true, cl);
        Log4j2LoggerProvider provider = new Log4j2LoggerProvider();
        LoggerProviders.logProvider(provider, via);
        return provider;
    }

    private static LoggerProvider tryLog4j(ClassLoader cl, String via) throws ClassNotFoundException {
        Class.forName("org.apache.log4j.LogManager", true, cl);
        Class.forName("org.apache.log4j.config.PropertySetter", true, cl);
        Log4jLoggerProvider provider = new Log4jLoggerProvider();
        LoggerProviders.logProvider(provider, via);
        return provider;
    }

    private static LoggerProvider tryJBossLogManager(ClassLoader cl, String via) throws ClassNotFoundException {
        Class<?> logManagerClass = LogManager.getLogManager().getClass();
        if (logManagerClass == Class.forName("org.jboss.logmanager.LogManager", false, cl) && Class.forName("org.jboss.logmanager.Logger$AttachmentKey", true, cl).getClassLoader() == logManagerClass.getClassLoader()) {
            JBossLogManagerProvider provider = new JBossLogManagerProvider();
            LoggerProviders.logProvider(provider, via);
            return provider;
        }
        throw new IllegalStateException();
    }

    private static void logProvider(LoggerProvider provider, String via) {
        Logger logger = provider.getLogger(LoggerProviders.class.getPackage().getName());
        if (via == null) {
            logger.debugf("Logging Provider: %s", (Object)provider.getClass().getName());
        } else {
            logger.debugf("Logging Provider: %s found via %s", (Object)provider.getClass().getName(), (Object)via);
        }
    }

    private LoggerProviders() {
    }
}

