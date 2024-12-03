/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.spi.LoggerContext
 *  org.apache.logging.log4j.util.StackLocatorUtil
 */
package org.apache.log4j;

import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.Collectors;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.legacy.core.ContextUtil;
import org.apache.log4j.spi.DefaultRepositorySelector;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.NOPLoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.StackLocatorUtil;

public final class LogManager {
    @Deprecated
    public static final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";
    @Deprecated
    public static final String DEFAULT_CONFIGURATION_KEY = "log4j.configuration";
    @Deprecated
    public static final String CONFIGURATOR_CLASS_KEY = "log4j.configuratorClass";
    @Deprecated
    public static final String DEFAULT_INIT_OVERRIDE_KEY = "log4j.defaultInitOverride";
    static final String DEFAULT_XML_CONFIGURATION_FILE = "log4j.xml";
    private static RepositorySelector repositorySelector;
    private static final boolean LOG4J_CORE_PRESENT;

    private static boolean checkLog4jCore() {
        try {
            return Class.forName("org.apache.logging.log4j.core.LoggerContext") != null;
        }
        catch (Throwable ex) {
            return false;
        }
    }

    public static Logger exists(String name) {
        return LogManager.exists(name, StackLocatorUtil.getCallerClassLoader((int)2));
    }

    static Logger exists(String name, ClassLoader classLoader) {
        return LogManager.getHierarchy().exists(name, classLoader);
    }

    static LoggerContext getContext(ClassLoader classLoader) {
        return org.apache.logging.log4j.LogManager.getContext((ClassLoader)classLoader, (boolean)false);
    }

    public static Enumeration getCurrentLoggers() {
        return LogManager.getCurrentLoggers(StackLocatorUtil.getCallerClassLoader((int)2));
    }

    static Enumeration getCurrentLoggers(ClassLoader classLoader) {
        return Collections.enumeration(LogManager.getContext(classLoader).getLoggerRegistry().getLoggers().stream().map(e -> LogManager.getLogger(e.getName(), classLoader)).collect(Collectors.toList()));
    }

    static Hierarchy getHierarchy() {
        LoggerRepository loggerRepository = LogManager.getLoggerRepository();
        return loggerRepository instanceof Hierarchy ? (Hierarchy)loggerRepository : null;
    }

    public static Logger getLogger(Class<?> clazz) {
        Hierarchy hierarchy = LogManager.getHierarchy();
        return hierarchy != null ? hierarchy.getLogger(clazz.getName(), StackLocatorUtil.getCallerClassLoader((int)2)) : LogManager.getLoggerRepository().getLogger(clazz.getName());
    }

    public static Logger getLogger(String name) {
        Hierarchy hierarchy = LogManager.getHierarchy();
        return hierarchy != null ? hierarchy.getLogger(name, StackLocatorUtil.getCallerClassLoader((int)2)) : LogManager.getLoggerRepository().getLogger(name);
    }

    static Logger getLogger(String name, ClassLoader classLoader) {
        Hierarchy hierarchy = LogManager.getHierarchy();
        return hierarchy != null ? hierarchy.getLogger(name, classLoader) : LogManager.getLoggerRepository().getLogger(name);
    }

    public static Logger getLogger(String name, LoggerFactory factory) {
        Hierarchy hierarchy = LogManager.getHierarchy();
        return hierarchy != null ? hierarchy.getLogger(name, factory, StackLocatorUtil.getCallerClassLoader((int)2)) : LogManager.getLoggerRepository().getLogger(name, factory);
    }

    static Logger getLogger(String name, LoggerFactory factory, ClassLoader classLoader) {
        Hierarchy hierarchy = LogManager.getHierarchy();
        return hierarchy != null ? hierarchy.getLogger(name, factory, classLoader) : LogManager.getLoggerRepository().getLogger(name, factory);
    }

    public static LoggerRepository getLoggerRepository() {
        if (repositorySelector == null) {
            repositorySelector = new DefaultRepositorySelector(new NOPLoggerRepository());
        }
        return repositorySelector.getLoggerRepository();
    }

    public static Logger getRootLogger() {
        return LogManager.getRootLogger(StackLocatorUtil.getCallerClassLoader((int)2));
    }

    static Logger getRootLogger(ClassLoader classLoader) {
        Hierarchy hierarchy = LogManager.getHierarchy();
        return hierarchy != null ? hierarchy.getRootLogger(classLoader) : LogManager.getLoggerRepository().getRootLogger();
    }

    static boolean isLog4jCorePresent() {
        return LOG4J_CORE_PRESENT;
    }

    static void reconfigure(ClassLoader classLoader) {
        if (LogManager.isLog4jCorePresent()) {
            ContextUtil.reconfigure(LogManager.getContext(classLoader));
        }
    }

    public static void resetConfiguration() {
        LogManager.resetConfiguration(StackLocatorUtil.getCallerClassLoader((int)2));
    }

    static void resetConfiguration(ClassLoader classLoader) {
        Hierarchy hierarchy = LogManager.getHierarchy();
        if (hierarchy != null) {
            hierarchy.resetConfiguration(classLoader);
        } else {
            LogManager.getLoggerRepository().resetConfiguration();
        }
    }

    public static void setRepositorySelector(RepositorySelector selector, Object guard) throws IllegalArgumentException {
        if (selector == null) {
            throw new IllegalArgumentException("RepositorySelector must be non-null.");
        }
        repositorySelector = selector;
    }

    public static void shutdown() {
        LogManager.shutdown(StackLocatorUtil.getCallerClassLoader((int)2));
    }

    static void shutdown(ClassLoader classLoader) {
        Hierarchy hierarchy = LogManager.getHierarchy();
        if (hierarchy != null) {
            hierarchy.shutdown(classLoader);
        } else {
            LogManager.getLoggerRepository().shutdown();
        }
    }

    static {
        LOG4J_CORE_PRESENT = LogManager.checkLog4jCore();
        Hierarchy hierarchy = new Hierarchy(new RootLogger(Level.DEBUG));
        repositorySelector = new DefaultRepositorySelector(hierarchy);
    }
}

