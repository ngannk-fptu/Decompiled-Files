/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.lang.Coerce
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 */
package com.mchange.v2.c3p0.cfg;

import com.mchange.v2.c3p0.cfg.C3P0Config;
import com.mchange.v2.c3p0.cfg.NamedScope;
import com.mchange.v2.c3p0.impl.C3P0Defaults;
import com.mchange.v2.lang.Coerce;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class C3P0ConfigUtils {
    public static final String PROPS_FILE_RSRC_PATH = "/c3p0.properties";
    public static final String PROPS_FILE_PROP_PFX = "c3p0.";
    public static final int PROPS_FILE_PROP_PFX_LEN = 5;
    private static final String[] MISSPELL_PFXS = new String[]{"/c3pO", "/c3po", "/C3P0", "/C3PO"};
    static final MLogger logger = MLog.getLogger(C3P0ConfigUtils.class);

    public static HashMap extractHardcodedC3P0Defaults(boolean stringify_coercibles) {
        HashMap<String, Object> out = new HashMap<String, Object>();
        try {
            for (Method m : C3P0Defaults.class.getMethods()) {
                Object val;
                int mods = m.getModifiers();
                if ((mods & 1) == 0 || (mods & 8) == 0 || m.getParameterTypes().length != 0 || (val = m.invoke(null, (Object[])null)) == null) continue;
                out.put(m.getName(), stringify_coercibles && Coerce.canCoerce((Object)val) ? String.valueOf(val) : val);
            }
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, "Failed to extract hardcoded default config!?", (Throwable)e);
        }
        return out;
    }

    public static HashMap extractHardcodedC3P0Defaults() {
        return C3P0ConfigUtils.extractHardcodedC3P0Defaults(true);
    }

    public static HashMap extractC3P0PropertiesResources() {
        HashMap<String, String> out = new HashMap<String, String>();
        Properties props = C3P0ConfigUtils.findAllOneLevelC3P0Properties();
        for (String string : props.keySet()) {
            String val = (String)props.get(string);
            if (!string.startsWith(PROPS_FILE_PROP_PFX)) continue;
            out.put(string.substring(5).trim(), val.trim());
        }
        return out;
    }

    public static C3P0Config configFromFlatDefaults(HashMap flatDefaults) {
        NamedScope defaults = new NamedScope();
        defaults.props.putAll(flatDefaults);
        HashMap configNamesToNamedScopes = new HashMap();
        return new C3P0Config(defaults, configNamesToNamedScopes);
    }

    public static String getPropsFileConfigProperty(String prop) {
        return C3P0Config.getPropsFileConfigProperty(prop);
    }

    public static Properties findResourceProperties() {
        return C3P0Config.findResourceProperties();
    }

    private static Properties findAllOneLevelC3P0Properties() {
        return C3P0Config.findAllOneLevelC3P0Properties();
    }

    static Properties findAllC3P0SystemProperties() {
        Properties out;
        block3: {
            out = new Properties();
            try {
                for (String key : C3P0Defaults.getKnownProperties(null)) {
                    String prefixedKey = PROPS_FILE_PROP_PFX + key;
                    String value = System.getProperty(prefixedKey);
                    if (value == null || value.trim().length() <= 0) continue;
                    out.put(key, value);
                }
            }
            catch (SecurityException e) {
                if (!logger.isLoggable(MLevel.WARNING)) break block3;
                logger.log(MLevel.WARNING, "A SecurityException occurred while trying to read c3p0 System properties. c3p0 configuration set via System properties may be ignored!", (Throwable)e);
            }
        }
        return out;
    }

    public static Object extractUserOverride(String propName, String userName, Map userOverrides) {
        Map specificUserOverrides = (Map)userOverrides.get(userName);
        if (specificUserOverrides != null) {
            return specificUserOverrides.get(propName);
        }
        return null;
    }

    public static Boolean extractBooleanOverride(String propName, String userName, Map userOverrides) {
        Object check = C3P0ConfigUtils.extractUserOverride(propName, userName, userOverrides);
        if (check == null || check instanceof Boolean) {
            return (Boolean)check;
        }
        if (check instanceof String) {
            return Boolean.valueOf((String)check);
        }
        throw new ClassCastException("Parameter '" + propName + "' as overridden for user '" + userName + "' is " + check + ", which cannot be converted to Boolean.");
    }

    private C3P0ConfigUtils() {
    }

    static {
        if (logger.isLoggable(MLevel.WARNING) && C3P0ConfigUtils.class.getResource(PROPS_FILE_RSRC_PATH) == null) {
            for (int i = 0; i < MISSPELL_PFXS.length; ++i) {
                String test = MISSPELL_PFXS[i] + ".properties";
                if (C3P0ConfigUtils.class.getResource(MISSPELL_PFXS[i] + ".properties") == null) continue;
                logger.warning("POSSIBLY MISSPELLED c3p0.properties CONFIG RESOURCE FOUND. Please ensure the file name is c3p0.properties, all lower case, with the digit 0 (NOT the letter O) in c3p0. It should be placed  in the top level of c3p0's effective classpath.");
                break;
            }
        }
    }
}

