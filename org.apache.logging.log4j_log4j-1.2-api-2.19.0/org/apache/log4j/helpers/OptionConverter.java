/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.spi.StandardLevel
 *  org.apache.logging.log4j.status.StatusLogger
 *  org.apache.logging.log4j.util.LoaderUtil
 *  org.apache.logging.log4j.util.PropertiesUtil
 *  org.apache.logging.log4j.util.Strings
 */
package org.apache.log4j.helpers;

import java.io.InputStream;
import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.StandardLevel;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

public class OptionConverter {
    static String DELIM_START = "${";
    static char DELIM_STOP = (char)125;
    static int DELIM_START_LEN = 2;
    static int DELIM_STOP_LEN = 1;
    private static final Logger LOGGER = StatusLogger.getLogger();
    static final int MAX_CUTOFF_LEVEL = 50000 + 100 * (StandardLevel.FATAL.intLevel() - StandardLevel.OFF.intLevel() - 1) + 1;
    static final int MIN_CUTOFF_LEVEL = -2147478648 - (Integer.MIN_VALUE + StandardLevel.ALL.intLevel()) + StandardLevel.TRACE.intLevel();
    static final ConcurrentMap<String, Level> LEVELS = new ConcurrentHashMap<String, Level>();
    private static final String LOG4J2_LEVEL_CLASS = org.apache.logging.log4j.Level.class.getName();
    private static final CharMap[] charMap = new CharMap[]{new CharMap('n', '\n'), new CharMap('r', '\r'), new CharMap('t', '\t'), new CharMap('f', '\f'), new CharMap('\b', '\b'), new CharMap('\"', '\"'), new CharMap('\'', '\''), new CharMap('\\', '\\')};

    public static String[] concatanateArrays(String[] l, String[] r) {
        int len = l.length + r.length;
        String[] a = new String[len];
        System.arraycopy(l, 0, a, 0, l.length);
        System.arraycopy(r, 0, a, l.length, r.length);
        return a;
    }

    static int toLog4j2Level(int v1Level) {
        if (v1Level >= MAX_CUTOFF_LEVEL) {
            return StandardLevel.OFF.intLevel();
        }
        if (v1Level > 10000) {
            int offset = Math.round((float)(v1Level - 10000) / 100.0f);
            return StandardLevel.DEBUG.intLevel() - offset;
        }
        if (v1Level > 5000) {
            int offset = Math.round((float)(v1Level - 5000) / 50.0f);
            return StandardLevel.TRACE.intLevel() - offset;
        }
        if (v1Level > MIN_CUTOFF_LEVEL) {
            int offset = 5000 - v1Level;
            return StandardLevel.TRACE.intLevel() + offset;
        }
        return StandardLevel.ALL.intLevel();
    }

    static int toLog4j1Level(int v2Level) {
        if (v2Level == StandardLevel.ALL.intLevel()) {
            return Integer.MIN_VALUE;
        }
        if (v2Level > StandardLevel.TRACE.intLevel()) {
            return MIN_CUTOFF_LEVEL + (StandardLevel.ALL.intLevel() - v2Level);
        }
        if (v2Level > StandardLevel.DEBUG.intLevel()) {
            return 5000 + 50 * (StandardLevel.TRACE.intLevel() - v2Level);
        }
        if (v2Level > StandardLevel.OFF.intLevel()) {
            return 10000 + 100 * (StandardLevel.DEBUG.intLevel() - v2Level);
        }
        return Integer.MAX_VALUE;
    }

    static int toSyslogLevel(int v2Level) {
        if (v2Level <= StandardLevel.FATAL.intLevel()) {
            return 0;
        }
        if (v2Level <= StandardLevel.ERROR.intLevel()) {
            return 3 - 3 * (StandardLevel.ERROR.intLevel() - v2Level) / (StandardLevel.ERROR.intLevel() - StandardLevel.FATAL.intLevel());
        }
        if (v2Level <= StandardLevel.WARN.intLevel()) {
            return 4;
        }
        if (v2Level <= StandardLevel.INFO.intLevel()) {
            return 6 - 2 * (StandardLevel.INFO.intLevel() - v2Level) / (StandardLevel.INFO.intLevel() - StandardLevel.WARN.intLevel());
        }
        return 7;
    }

    public static org.apache.logging.log4j.Level createLevel(Priority level) {
        String name = level.toString().toUpperCase() + "#" + level.getClass().getName();
        return org.apache.logging.log4j.Level.forName((String)name, (int)OptionConverter.toLog4j2Level(level.toInt()));
    }

    public static org.apache.logging.log4j.Level convertLevel(Priority level) {
        return level != null ? level.getVersion2Level() : org.apache.logging.log4j.Level.ERROR;
    }

    public static Level convertLevel(org.apache.logging.log4j.Level level) {
        Level actualLevel = OptionConverter.toLevel(level.name(), null);
        if (actualLevel == null) {
            actualLevel = OptionConverter.toLevel(LOG4J2_LEVEL_CLASS, level.name(), null);
        }
        return actualLevel != null ? actualLevel : Level.ERROR;
    }

    public static org.apache.logging.log4j.Level convertLevel(String level, org.apache.logging.log4j.Level defaultLevel) {
        Level actualLevel = OptionConverter.toLevel(level, null);
        return actualLevel != null ? actualLevel.getVersion2Level() : defaultLevel;
    }

    public static String convertSpecialChars(String s) {
        int len = s.length();
        StringBuilder sbuf = new StringBuilder(len);
        int i = 0;
        while (i < len) {
            char c;
            if ((c = s.charAt(i++)) == '\\') {
                c = s.charAt(i++);
                for (CharMap entry : charMap) {
                    if (entry.key != c) continue;
                    c = entry.replacement;
                }
            }
            sbuf.append(c);
        }
        return sbuf.toString();
    }

    public static String findAndSubst(String key, Properties props) {
        String value = props.getProperty(key);
        if (value == null) {
            return null;
        }
        try {
            return OptionConverter.substVars(value, props);
        }
        catch (IllegalArgumentException e) {
            LOGGER.error("Bad option value [{}].", (Object)value, (Object)e);
            return value;
        }
    }

    public static String getSystemProperty(String key, String def) {
        try {
            return System.getProperty(key, def);
        }
        catch (Throwable e) {
            LOGGER.debug("Was not allowed to read system property \"{}\".", (Object)key);
            return def;
        }
    }

    public static Object instantiateByClassName(String className, Class<?> superClass, Object defaultValue) {
        if (className != null) {
            try {
                Object obj = LoaderUtil.newInstanceOf((String)className);
                if (!superClass.isAssignableFrom(obj.getClass())) {
                    LOGGER.error("A \"{}\" object is not assignable to a \"{}\" variable", (Object)className, (Object)superClass.getName());
                    return defaultValue;
                }
                return obj;
            }
            catch (ReflectiveOperationException e) {
                LOGGER.error("Could not instantiate class [" + className + "].", (Throwable)e);
            }
        }
        return defaultValue;
    }

    public static Object instantiateByKey(Properties props, String key, Class superClass, Object defaultValue) {
        String className = OptionConverter.findAndSubst(key, props);
        if (className == null) {
            LogLog.error("Could not find value for key " + key);
            return defaultValue;
        }
        return OptionConverter.instantiateByClassName(className.trim(), superClass, defaultValue);
    }

    public static void selectAndConfigure(InputStream inputStream, String clazz, LoggerRepository hierarchy) {
        Configurator configurator = null;
        if (clazz != null) {
            LOGGER.debug("Preferred configurator class: " + clazz);
            configurator = (Configurator)OptionConverter.instantiateByClassName(clazz, Configurator.class, null);
            if (configurator == null) {
                LOGGER.error("Could not instantiate configurator [" + clazz + "].");
                return;
            }
        } else {
            configurator = new PropertyConfigurator();
        }
        configurator.doConfigure(inputStream, hierarchy);
    }

    public static void selectAndConfigure(URL url, String clazz, LoggerRepository hierarchy) {
        Configurator configurator = null;
        String filename = url.getFile();
        if (clazz == null && filename != null && filename.endsWith(".xml")) {
            clazz = "org.apache.log4j.xml.DOMConfigurator";
        }
        if (clazz != null) {
            LOGGER.debug("Preferred configurator class: " + clazz);
            configurator = (Configurator)OptionConverter.instantiateByClassName(clazz, Configurator.class, null);
            if (configurator == null) {
                LOGGER.error("Could not instantiate configurator [" + clazz + "].");
                return;
            }
        } else {
            configurator = new PropertyConfigurator();
        }
        configurator.doConfigure(url, hierarchy);
    }

    public static String substVars(String val, Properties props) throws IllegalArgumentException {
        return OptionConverter.substVars(val, props, new ArrayList<String>());
    }

    private static String substVars(String val, Properties props, List<String> keys) throws IllegalArgumentException {
        if (val == null) {
            return null;
        }
        StringBuilder sbuf = new StringBuilder();
        int i = 0;
        while (true) {
            int j;
            if ((j = val.indexOf(DELIM_START, i)) == -1) {
                if (i == 0) {
                    return val;
                }
                sbuf.append(val.substring(i));
                return sbuf.toString();
            }
            sbuf.append(val.substring(i, j));
            int k = val.indexOf(DELIM_STOP, j);
            if (k == -1) {
                throw new IllegalArgumentException(Strings.dquote((String)val) + " has no closing brace. Opening brace at position " + j + '.');
            }
            String key = val.substring(j += DELIM_START_LEN, k);
            String replacement = PropertiesUtil.getProperties().getStringProperty(key, null);
            if (replacement == null && props != null) {
                replacement = props.getProperty(key);
            }
            if (replacement != null) {
                if (!keys.contains(key)) {
                    ArrayList<String> usedKeys = new ArrayList<String>(keys);
                    usedKeys.add(key);
                    String recursiveReplacement = OptionConverter.substVars(replacement, props, usedKeys);
                    sbuf.append(recursiveReplacement);
                } else {
                    sbuf.append(replacement);
                }
            }
            i = k + DELIM_STOP_LEN;
        }
    }

    public static boolean toBoolean(String value, boolean dEfault) {
        if (value == null) {
            return dEfault;
        }
        String trimmedVal = value.trim();
        if ("true".equalsIgnoreCase(trimmedVal)) {
            return true;
        }
        if ("false".equalsIgnoreCase(trimmedVal)) {
            return false;
        }
        return dEfault;
    }

    public static long toFileSize(String value, long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        String s = value.trim().toUpperCase();
        long multiplier = 1L;
        int index = s.indexOf("KB");
        if (index != -1) {
            multiplier = 1024L;
            s = s.substring(0, index);
        } else {
            index = s.indexOf("MB");
            if (index != -1) {
                multiplier = 0x100000L;
                s = s.substring(0, index);
            } else {
                index = s.indexOf("GB");
                if (index != -1) {
                    multiplier = 0x40000000L;
                    s = s.substring(0, index);
                }
            }
        }
        if (s != null) {
            try {
                return Long.valueOf(s) * multiplier;
            }
            catch (NumberFormatException e) {
                LogLog.error("[" + s + "] is not in proper int form.");
                LogLog.error("[" + value + "] not in expected format.", e);
            }
        }
        return defaultValue;
    }

    public static int toInt(String value, int dEfault) {
        if (value != null) {
            String s = value.trim();
            try {
                return Integer.valueOf(s);
            }
            catch (NumberFormatException e) {
                LogLog.error("[" + s + "] is not in proper int form.");
                e.printStackTrace();
            }
        }
        return dEfault;
    }

    public static Level toLevel(String value, Level defaultValue) {
        String levelName;
        if (value == null) {
            return defaultValue;
        }
        Level cached = (Level)LEVELS.get(value = value.trim());
        if (cached != null) {
            return cached;
        }
        int hashIndex = value.indexOf(35);
        if (hashIndex == -1) {
            if ("NULL".equalsIgnoreCase(value)) {
                return null;
            }
            Level standardLevel = Level.toLevel(value, defaultValue);
            if (standardLevel != null && value.equals(standardLevel.toString())) {
                LEVELS.putIfAbsent(value, standardLevel);
            }
            return standardLevel;
        }
        String clazz = value.substring(hashIndex + 1);
        Level customLevel = OptionConverter.toLevel(clazz, levelName = value.substring(0, hashIndex), defaultValue);
        if (customLevel != null && levelName.equals(customLevel.toString()) && clazz.equals(customLevel.getClass().getName())) {
            LEVELS.putIfAbsent(value, customLevel);
        }
        return customLevel;
    }

    public static Level toLevel(String clazz, String levelName, Level defaultValue) {
        if ("NULL".equalsIgnoreCase(levelName)) {
            return null;
        }
        LOGGER.debug("toLevel:class=[" + clazz + "]:pri=[" + levelName + "]");
        if (LOG4J2_LEVEL_CLASS.equals(clazz)) {
            org.apache.logging.log4j.Level v2Level = org.apache.logging.log4j.Level.getLevel((String)levelName.toUpperCase());
            if (v2Level != null) {
                return new LevelWrapper(v2Level);
            }
            return defaultValue;
        }
        try {
            Class customLevel = LoaderUtil.loadClass((String)clazz);
            Class[] paramTypes = new Class[]{String.class, Level.class};
            Method toLevelMethod = customLevel.getMethod("toLevel", paramTypes);
            Object[] params = new Object[]{levelName, defaultValue};
            Object o = toLevelMethod.invoke(null, params);
            return (Level)o;
        }
        catch (ClassNotFoundException e) {
            LOGGER.warn("custom level class [" + clazz + "] not found.");
        }
        catch (NoSuchMethodException e) {
            LOGGER.warn("custom level class [" + clazz + "] does not have a class function toLevel(String, Level)", (Throwable)e);
        }
        catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof InterruptedException || e.getTargetException() instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            LOGGER.warn("custom level class [" + clazz + "] could not be instantiated", (Throwable)e);
        }
        catch (ClassCastException e) {
            LOGGER.warn("class [" + clazz + "] is not a subclass of org.apache.log4j.Level", (Throwable)e);
        }
        catch (IllegalAccessException e) {
            LOGGER.warn("class [" + clazz + "] cannot be instantiated due to access restrictions", (Throwable)e);
        }
        catch (RuntimeException e) {
            LOGGER.warn("class [" + clazz + "], level [" + levelName + "] conversion failed.", (Throwable)e);
        }
        return defaultValue;
    }

    private OptionConverter() {
    }

    private static class LevelWrapper
    extends Level {
        private static final long serialVersionUID = -7693936267612508528L;

        protected LevelWrapper(org.apache.logging.log4j.Level v2Level) {
            super(OptionConverter.toLog4j1Level(v2Level.intLevel()), v2Level.name(), OptionConverter.toSyslogLevel(v2Level.intLevel()), v2Level);
        }
    }

    private static class CharMap {
        final char key;
        final char replacement;

        public CharMap(char key, char replacement) {
            this.key = key;
            this.replacement = replacement;
        }
    }
}

