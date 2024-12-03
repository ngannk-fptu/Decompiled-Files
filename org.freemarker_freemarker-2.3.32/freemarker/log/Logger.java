/*
 * Decompiled with CFR 0.152.
 */
package freemarker.log;

import freemarker.log.LoggerFactory;
import freemarker.log._JULLoggerFactory;
import freemarker.log._NullLoggerFactory;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

public abstract class Logger {
    public static final String SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY = "org.freemarker.loggerLibrary";
    public static final int LIBRARY_AUTO = -1;
    private static final int MIN_LIBRARY_ENUM = -1;
    public static final String LIBRARY_NAME_AUTO = "auto";
    public static final int LIBRARY_NONE = 0;
    public static final String LIBRARY_NAME_NONE = "none";
    public static final int LIBRARY_JAVA = 1;
    public static final String LIBRARY_NAME_JUL = "JUL";
    @Deprecated
    public static final int LIBRARY_AVALON = 2;
    @Deprecated
    public static final String LIBRARY_NAME_AVALON = "Avalon";
    public static final int LIBRARY_LOG4J = 3;
    public static final String LIBRARY_NAME_LOG4J = "Log4j";
    public static final int LIBRARY_COMMONS = 4;
    public static final String LIBRARY_NAME_COMMONS_LOGGING = "CommonsLogging";
    public static final int LIBRARY_SLF4J = 5;
    public static final String LIBRARY_NAME_SLF4J = "SLF4J";
    private static final int MAX_LIBRARY_ENUM = 5;
    private static final String REAL_LOG4J_PRESENCE_CLASS = "org.apache.log4j.FileAppender";
    private static final String LOG4J_OVER_SLF4J_TESTER_CLASS = "freemarker.log._Log4jOverSLF4JTester";
    private static final String[] LIBRARIES_BY_PRIORITY = new String[]{null, "JUL", "org.apache.log.Logger", "Avalon", "org.apache.log4j.Logger", "Log4j", "org.apache.commons.logging.Log", "CommonsLogging", "org.slf4j.Logger", "SLF4J"};
    private static int libraryEnum;
    private static LoggerFactory loggerFactory;
    private static boolean initializedFromSystemProperty;
    private static String categoryPrefix;
    private static final Map loggersByCategory;

    private static String getAvailabilityCheckClassName(int libraryEnum) {
        if (libraryEnum == -1 || libraryEnum == 0) {
            return null;
        }
        return LIBRARIES_BY_PRIORITY[(libraryEnum - 1) * 2];
    }

    private static String getLibraryName(int libraryEnum) {
        if (libraryEnum == -1) {
            return LIBRARY_NAME_AUTO;
        }
        if (libraryEnum == 0) {
            return LIBRARY_NAME_NONE;
        }
        return LIBRARIES_BY_PRIORITY[(libraryEnum - 1) * 2 + 1];
    }

    private static boolean isAutoDetected(int libraryEnum) {
        return libraryEnum != -1 && libraryEnum != 0 && libraryEnum != 5 && libraryEnum != 4;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public static void selectLoggerLibrary(int libraryEnum) throws ClassNotFoundException {
        if (libraryEnum < -1 || libraryEnum > 5) {
            throw new IllegalArgumentException("Library enum value out of range");
        }
        Class<Logger> clazz = Logger.class;
        synchronized (Logger.class) {
            boolean loggerFactoryWasAlreadySet;
            boolean bl = loggerFactoryWasAlreadySet = loggerFactory != null;
            if (!loggerFactoryWasAlreadySet || libraryEnum != Logger.libraryEnum) {
                Logger.ensureLoggerFactorySet(true);
                if (!initializedFromSystemProperty || loggerFactory == null) {
                    int replacedLibraryEnum = Logger.libraryEnum;
                    Logger.setLibrary(libraryEnum);
                    loggersByCategory.clear();
                    if (loggerFactoryWasAlreadySet) {
                        Logger.logWarnInLogger("Logger library was already set earlier to \"" + Logger.getLibraryName(replacedLibraryEnum) + "\"; change to \"" + Logger.getLibraryName(libraryEnum) + "\" won't effect loggers created earlier.");
                    }
                } else if (libraryEnum != Logger.libraryEnum) {
                    Logger.logWarnInLogger("Ignored " + Logger.class.getName() + ".selectLoggerLibrary(\"" + Logger.getLibraryName(libraryEnum) + "\") call, because the \"" + SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY + "\" system property is set to \"" + Logger.getLibraryName(Logger.libraryEnum) + "\".");
                }
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public static void setCategoryPrefix(String prefix) {
        Class<Logger> clazz = Logger.class;
        synchronized (Logger.class) {
            if (prefix == null) {
                throw new IllegalArgumentException();
            }
            categoryPrefix = prefix;
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    public abstract void debug(String var1);

    public abstract void debug(String var1, Throwable var2);

    public abstract void info(String var1);

    public abstract void info(String var1, Throwable var2);

    public abstract void warn(String var1);

    public abstract void warn(String var1, Throwable var2);

    public abstract void error(String var1);

    public abstract void error(String var1, Throwable var2);

    public abstract boolean isDebugEnabled();

    public abstract boolean isInfoEnabled();

    public abstract boolean isWarnEnabled();

    public abstract boolean isErrorEnabled();

    public abstract boolean isFatalEnabled();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Logger getLogger(String category) {
        if (categoryPrefix.length() != 0) {
            category = categoryPrefix + category;
        }
        Map map = loggersByCategory;
        synchronized (map) {
            Logger logger = (Logger)loggersByCategory.get(category);
            if (logger == null) {
                Logger.ensureLoggerFactorySet(false);
                logger = loggerFactory.getLogger(category);
                loggersByCategory.put(category, logger);
            }
            return logger;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void ensureLoggerFactorySet(boolean onlyIfCanBeSetFromSysProp) {
        if (loggerFactory != null) {
            return;
        }
        Class<Logger> clazz = Logger.class;
        synchronized (Logger.class) {
            block17: {
                int libraryEnum;
                if (loggerFactory != null) {
                    // ** MonitorExit[var1_1] (shouldn't be in output)
                    return;
                }
                String sysPropVal = Logger.getSystemProperty(SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY);
                if (sysPropVal != null) {
                    sysPropVal = sysPropVal.trim();
                    boolean foundMatch = false;
                    int matchedEnum = -1;
                    do {
                        if (sysPropVal.equalsIgnoreCase(Logger.getLibraryName(matchedEnum))) {
                            foundMatch = true;
                            continue;
                        }
                        ++matchedEnum;
                    } while (matchedEnum <= 5 && !foundMatch);
                    if (!foundMatch) {
                        Logger.logWarnInLogger("Ignored invalid \"org.freemarker.loggerLibrary\" system property value: \"" + sysPropVal + "\"");
                        if (onlyIfCanBeSetFromSysProp) {
                            // ** MonitorExit[var1_1] (shouldn't be in output)
                            return;
                        }
                    }
                    libraryEnum = foundMatch ? matchedEnum : -1;
                } else {
                    if (onlyIfCanBeSetFromSysProp) {
                        // ** MonitorExit[var1_1] (shouldn't be in output)
                        return;
                    }
                    libraryEnum = -1;
                }
                try {
                    Logger.setLibrary(libraryEnum);
                    if (sysPropVal != null) {
                        initializedFromSystemProperty = true;
                    }
                }
                catch (Throwable e) {
                    boolean disableLogging = !onlyIfCanBeSetFromSysProp || sysPropVal == null;
                    Logger.logErrorInLogger("Couldn't set up logger for \"" + Logger.getLibraryName(libraryEnum) + "\"" + (disableLogging ? "; logging disabled" : "."), e);
                    if (!disableLogging) break block17;
                    try {
                        Logger.setLibrary(0);
                    }
                    catch (ClassNotFoundException e2) {
                        throw new RuntimeException("Bug", e2);
                    }
                }
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    private static LoggerFactory createLoggerFactory(int libraryEnum) throws ClassNotFoundException {
        if (libraryEnum == -1) {
            for (int libraryEnumToTry = 5; libraryEnumToTry >= -1; --libraryEnumToTry) {
                if (!Logger.isAutoDetected(libraryEnumToTry)) continue;
                if (libraryEnumToTry == 3 && Logger.hasLog4LibraryThatDelegatesToWorkingSLF4J()) {
                    libraryEnumToTry = 5;
                }
                try {
                    return Logger.createLoggerFactoryForNonAuto(libraryEnumToTry);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    continue;
                }
                catch (Throwable e) {
                    Logger.logErrorInLogger("Unexpected error when initializing logging for \"" + Logger.getLibraryName(libraryEnumToTry) + "\".", e);
                }
            }
            Logger.logWarnInLogger("Auto detecton couldn't set up any logger libraries; FreeMarker logging suppressed.");
            return new _NullLoggerFactory();
        }
        return Logger.createLoggerFactoryForNonAuto(libraryEnum);
    }

    private static LoggerFactory createLoggerFactoryForNonAuto(int libraryEnum) throws ClassNotFoundException {
        String availabilityCheckClassName = Logger.getAvailabilityCheckClassName(libraryEnum);
        if (availabilityCheckClassName != null) {
            Class.forName(availabilityCheckClassName);
            String libraryName = Logger.getLibraryName(libraryEnum);
            try {
                return (LoggerFactory)Class.forName("freemarker.log._" + libraryName + "LoggerFactory").newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException("Unexpected error when creating logger factory for \"" + libraryName + "\".", e);
            }
        }
        if (libraryEnum == 1) {
            return new _JULLoggerFactory();
        }
        if (libraryEnum == 0) {
            return new _NullLoggerFactory();
        }
        throw new RuntimeException("Bug");
    }

    private static boolean hasLog4LibraryThatDelegatesToWorkingSLF4J() {
        try {
            Class.forName(Logger.getAvailabilityCheckClassName(3));
            Class.forName(Logger.getAvailabilityCheckClassName(5));
        }
        catch (Throwable e) {
            return false;
        }
        try {
            Class.forName(REAL_LOG4J_PRESENCE_CLASS);
            return false;
        }
        catch (ClassNotFoundException e) {
            try {
                Object r = Class.forName(LOG4J_OVER_SLF4J_TESTER_CLASS).getMethod("test", new Class[0]).invoke(null, new Object[0]);
                return (Boolean)r;
            }
            catch (Throwable e2) {
                return false;
            }
        }
    }

    private static synchronized void setLibrary(int libraryEnum) throws ClassNotFoundException {
        loggerFactory = Logger.createLoggerFactory(libraryEnum);
        Logger.libraryEnum = libraryEnum;
    }

    private static void logWarnInLogger(String message) {
        Logger.logInLogger(false, message, null);
    }

    private static void logErrorInLogger(String message, Throwable exception) {
        Logger.logInLogger(true, message, exception);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void logInLogger(boolean error, String message, Throwable exception) {
        Class<Logger> clazz = Logger.class;
        synchronized (Logger.class) {
            boolean canUseRealLogger = loggerFactory != null && !(loggerFactory instanceof _NullLoggerFactory);
            // ** MonitorExit[var4_3] (shouldn't be in output)
            if (canUseRealLogger) {
                try {
                    Logger logger = Logger.getLogger("freemarker.logger");
                    if (error) {
                        logger.error(message);
                    } else {
                        logger.warn(message);
                    }
                }
                catch (Throwable e) {
                    canUseRealLogger = false;
                }
            }
            if (!canUseRealLogger) {
                System.err.println((error ? "ERROR" : "WARN") + " " + LoggerFactory.class.getName() + ": " + message);
                if (exception != null) {
                    System.err.println("\tException: " + Logger.tryToString(exception));
                    while (exception.getCause() != null) {
                        exception = exception.getCause();
                        System.err.println("\tCaused by: " + Logger.tryToString(exception));
                    }
                }
            }
            return;
        }
    }

    private static String getSystemProperty(final String key) {
        try {
            return (String)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return System.getProperty(key, null);
                }
            });
        }
        catch (AccessControlException e) {
            Logger.logWarnInLogger("Insufficient permissions to read system property \"" + key + "\".");
            return null;
        }
        catch (Throwable e) {
            Logger.logErrorInLogger("Failed to read system property \"" + key + "\".", e);
            return null;
        }
    }

    private static String tryToString(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return object.toString();
        }
        catch (Throwable e) {
            return object.getClass().getName();
        }
    }

    static {
        if (LIBRARIES_BY_PRIORITY.length / 2 != 5) {
            throw new AssertionError();
        }
        categoryPrefix = "";
        loggersByCategory = new HashMap();
    }
}

