/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.lang.BooleanUtils
 *  com.mchange.v2.beans.BeansUtils
 *  com.mchange.v2.cfg.MConfig
 *  com.mchange.v2.cfg.MultiPropertiesConfig
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 */
package com.mchange.v2.c3p0.cfg;

import com.mchange.v1.lang.BooleanUtils;
import com.mchange.v2.beans.BeansUtils;
import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.cfg.C3P0ConfigFinder;
import com.mchange.v2.c3p0.cfg.C3P0ConfigUtils;
import com.mchange.v2.c3p0.cfg.DefaultC3P0ConfigFinder;
import com.mchange.v2.c3p0.cfg.NamedScope;
import com.mchange.v2.c3p0.impl.C3P0Defaults;
import com.mchange.v2.c3p0.impl.C3P0ImplUtils;
import com.mchange.v2.cfg.MConfig;
import com.mchange.v2.cfg.MultiPropertiesConfig;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public final class C3P0Config {
    static final String PROP_STYLE_NAMED_CFG_PFX = "c3p0.named-configs";
    static final int PROP_STYLE_NAMED_CFG_PFX_LEN = "c3p0.named-configs".length();
    static final String PROP_STYLE_USER_OVERRIDES_PART = "user-overrides";
    static final String PROP_STYLE_USER_OVERRIDES_PFX = "c3p0.user-overrides";
    static final int PROP_STYLE_USER_OVERRIDES_PFX_LEN = "c3p0.user-overrides".length();
    static final String PROP_STYLE_EXTENSIONS_PART = "extensions";
    static final String PROP_STYLE_EXTENSIONS_PFX = "c3p0.extensions";
    static final int PROP_STYLE_EXTENSIONS_PFX_LEN = "c3p0.extensions".length();
    public static final String CFG_FINDER_CLASSNAME_KEY = "com.mchange.v2.c3p0.cfg.finder";
    public static final String DEFAULT_CONFIG_NAME = "default";
    public static final String PROPS_FILE_RSRC_PATH = "/c3p0.properties";
    static final MLogger logger = MLog.getLogger(C3P0Config.class);
    private static MultiPropertiesConfig _MPCONFIG;
    private static C3P0Config _MAIN;
    static final Class[] SUOAS_ARGS;
    static final Collection SKIP_BIND_PROPS;
    NamedScope defaultConfig;
    HashMap configNamesToNamedScopes;

    private static synchronized MultiPropertiesConfig MPCONFIG() {
        return _MPCONFIG;
    }

    private static synchronized C3P0Config MAIN() {
        return _MAIN;
    }

    private static synchronized void setLibraryMultiPropertiesConfig(MultiPropertiesConfig mpc) {
        _MPCONFIG = mpc;
    }

    public static Properties allCurrentProperties() {
        return C3P0Config.MPCONFIG().getPropertiesByPrefix("");
    }

    public static synchronized void setMainConfig(C3P0Config protoMain) {
        _MAIN = protoMain;
    }

    public static synchronized void refreshMainConfig() {
        C3P0Config.refreshMainConfig(null, null);
    }

    public static synchronized void refreshMainConfig(MultiPropertiesConfig[] overrides, String overridesDescription) {
        MultiPropertiesConfig libMpc = C3P0Config.findLibraryMultiPropertiesConfig();
        if (overrides != null) {
            int olen = overrides.length;
            MultiPropertiesConfig[] combineMe = new MultiPropertiesConfig[olen + 1];
            combineMe[0] = libMpc;
            for (int i = 0; i < olen; ++i) {
                combineMe[i + 1] = overrides[i];
            }
            MultiPropertiesConfig overriddenMpc = MConfig.combine((MultiPropertiesConfig[])combineMe);
            C3P0Config.setLibraryMultiPropertiesConfig(overriddenMpc);
            C3P0Config.setMainConfig(C3P0Config.findLibraryC3P0Config(true));
            if (logger.isLoggable(MLevel.INFO)) {
                logger.log(MLevel.INFO, "c3p0 main configuration was refreshed, with overrides specified" + (overridesDescription == null ? "." : " - " + overridesDescription));
            }
        } else {
            C3P0Config.setLibraryMultiPropertiesConfig(libMpc);
            C3P0Config.setMainConfig(C3P0Config.findLibraryC3P0Config(false));
            if (logger.isLoggable(MLevel.INFO)) {
                logger.log(MLevel.INFO, "c3p0 main configuration was refreshed, with no overrides specified (and any previous overrides removed).");
            }
        }
        C3P0Registry.markConfigRefreshed();
    }

    private static MultiPropertiesConfig findLibraryMultiPropertiesConfig() {
        String[] defaults = new String[]{"/mchange-commons.properties", "/mchange-log.properties"};
        String[] preempts = new String[]{"hocon:/reference,/application,/c3p0,/", PROPS_FILE_RSRC_PATH, "/"};
        return MConfig.readVmConfig((String[])defaults, (String[])preempts);
    }

    private static C3P0Config findLibraryC3P0Config(boolean warn_on_conflicting_overrides) {
        C3P0Config protoMain;
        C3P0ConfigFinder cfgFinder;
        block14: {
            String cname = C3P0Config.MPCONFIG().getProperty(CFG_FINDER_CLASSNAME_KEY);
            cfgFinder = null;
            try {
                if (cname != null) {
                    cfgFinder = (C3P0ConfigFinder)Class.forName(cname).newInstance();
                }
            }
            catch (Exception e) {
                if (!logger.isLoggable(MLevel.WARNING)) break block14;
                logger.log(MLevel.WARNING, "Could not load specified C3P0ConfigFinder class'" + cname + "'.", (Throwable)e);
            }
        }
        try {
            if (cfgFinder == null) {
                Class.forName("org.w3c.dom.Node");
                Class.forName("com.mchange.v2.c3p0.cfg.C3P0ConfigXmlUtils");
                cfgFinder = new DefaultC3P0ConfigFinder(warn_on_conflicting_overrides);
            }
            protoMain = cfgFinder.findConfig();
        }
        catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                if (logger.isLoggable(MLevel.WARNING)) {
                    logger.log(MLevel.WARNING, "XML configuration disabled! Verify that standard XML libs are available.", (Throwable)e);
                }
            } else if (logger.isLoggable(MLevel.SEVERE)) {
                logger.log(MLevel.SEVERE, "An error occurred while trying to parse the XML configuration!");
                logger.log(MLevel.SEVERE, "XML CONFIGURATION IGNORED!", (Throwable)e);
            }
            HashMap flatDefaults = C3P0ConfigUtils.extractHardcodedC3P0Defaults();
            flatDefaults.putAll(C3P0ConfigUtils.extractC3P0PropertiesResources());
            protoMain = C3P0ConfigUtils.configFromFlatDefaults(flatDefaults);
        }
        HashMap propStyleConfigNamesToNamedScopes = C3P0Config.findPropStyleNamedScopes();
        HashMap cfgFoundConfigNamesToNamedScopes = protoMain.configNamesToNamedScopes;
        HashMap<String, NamedScope> mergedConfigNamesToNamedScopes = new HashMap<String, NamedScope>();
        HashSet allConfigNames = new HashSet(cfgFoundConfigNamesToNamedScopes.keySet());
        allConfigNames.addAll(propStyleConfigNamesToNamedScopes.keySet());
        for (String cfgName : allConfigNames) {
            NamedScope cfgFound = (NamedScope)cfgFoundConfigNamesToNamedScopes.get(cfgName);
            NamedScope propStyle = (NamedScope)propStyleConfigNamesToNamedScopes.get(cfgName);
            if (cfgFound != null && propStyle != null) {
                mergedConfigNamesToNamedScopes.put(cfgName, cfgFound.mergedOver(propStyle));
                continue;
            }
            if (cfgFound != null && propStyle == null) {
                mergedConfigNamesToNamedScopes.put(cfgName, cfgFound);
                continue;
            }
            if (cfgFound == null && propStyle != null) {
                mergedConfigNamesToNamedScopes.put(cfgName, propStyle);
                continue;
            }
            throw new AssertionError((Object)"Huh? allConfigNames is the union, every name should be in one of the two maps.");
        }
        HashMap propStyleUserOverridesDefaultConfig = C3P0Config.findPropStyleUserOverridesDefaultConfig();
        HashMap propStyleExtensionsDefaultConfig = C3P0Config.findPropStyleExtensionsDefaultConfig();
        NamedScope mergedDefaultConfig = new NamedScope(protoMain.defaultConfig.props, NamedScope.mergeUserNamesToOverrides(protoMain.defaultConfig.userNamesToOverrides, propStyleUserOverridesDefaultConfig), NamedScope.mergeExtensions(protoMain.defaultConfig.extensions, propStyleExtensionsDefaultConfig));
        return new C3P0Config(mergedDefaultConfig, mergedConfigNamesToNamedScopes);
    }

    private static void warnOnUnknownProperties(C3P0Config cfg) {
        C3P0Config.warnOnUnknownProperties(cfg.defaultConfig);
        Iterator ii = cfg.configNamesToNamedScopes.values().iterator();
        while (ii.hasNext()) {
            C3P0Config.warnOnUnknownProperties((NamedScope)ii.next());
        }
    }

    private static void warnOnUnknownProperties(NamedScope scope) {
        C3P0Config.warnOnUnknownProperties(scope.props);
        Iterator ii = scope.userNamesToOverrides.values().iterator();
        while (ii.hasNext()) {
            C3P0Config.warnOnUnknownProperties((Map)ii.next());
        }
    }

    private static void warnOnUnknownProperties(Map propMap) {
        for (String prop : propMap.keySet()) {
            if (C3P0Defaults.isKnownProperty(prop) || !logger.isLoggable(MLevel.WARNING)) continue;
            logger.log(MLevel.WARNING, "Unknown c3p0-config property: " + prop);
        }
    }

    public static String getPropsFileConfigProperty(String prop) {
        return C3P0Config.MPCONFIG().getProperty(prop);
    }

    static Properties findResourceProperties() {
        return C3P0Config.MPCONFIG().getPropertiesByResourcePath(PROPS_FILE_RSRC_PATH);
    }

    static Properties findAllOneLevelC3P0Properties() {
        Properties out = C3P0Config.MPCONFIG().getPropertiesByPrefix("c3p0");
        Iterator<Object> ii = out.keySet().iterator();
        while (ii.hasNext()) {
            if (((String)ii.next()).lastIndexOf(46) <= 4) continue;
            ii.remove();
        }
        return out;
    }

    static HashMap findPropStyleUserOverridesDefaultConfig() {
        HashMap<String, HashMap<String, Object>> userNamesToOverrides = new HashMap<String, HashMap<String, Object>>();
        Properties props = C3P0Config.MPCONFIG().getPropertiesByPrefix(PROP_STYLE_USER_OVERRIDES_PFX);
        for (String string : props.keySet()) {
            String userProp = string.substring(PROP_STYLE_USER_OVERRIDES_PFX_LEN + 1);
            int dot_index = userProp.indexOf(46);
            if (dot_index < 0) {
                if (!logger.isLoggable(MLevel.WARNING)) continue;
                logger.log(MLevel.WARNING, "Bad specification of user-override property '" + string + "', propfile key should look like '" + PROP_STYLE_USER_OVERRIDES_PFX + ".<user>.<property>'. Ignoring.");
                continue;
            }
            String user = userProp.substring(0, dot_index);
            String propName = userProp.substring(dot_index + 1);
            HashMap<String, Object> userOverridesMap = (HashMap<String, Object>)userNamesToOverrides.get(user);
            if (userOverridesMap == null) {
                userOverridesMap = new HashMap<String, Object>();
                userNamesToOverrides.put(user, userOverridesMap);
            }
            userOverridesMap.put(propName, props.get(string));
        }
        return userNamesToOverrides;
    }

    static HashMap findPropStyleExtensionsDefaultConfig() {
        HashMap<String, Object> extensions = new HashMap<String, Object>();
        Properties props = C3P0Config.MPCONFIG().getPropertiesByPrefix(PROP_STYLE_EXTENSIONS_PFX);
        for (String string : props.keySet()) {
            String extensionsKey = string.substring(PROP_STYLE_EXTENSIONS_PFX_LEN + 1);
            extensions.put(extensionsKey, props.get(string));
        }
        return extensions;
    }

    static HashMap findPropStyleNamedScopes() {
        HashMap<String, NamedScope> namesToNamedScopes = new HashMap<String, NamedScope>();
        Properties props = C3P0Config.MPCONFIG().getPropertiesByPrefix(PROP_STYLE_NAMED_CFG_PFX);
        for (String string : props.keySet()) {
            int second_dot_index;
            String nameProp = string.substring(PROP_STYLE_NAMED_CFG_PFX_LEN + 1);
            int dot_index = nameProp.indexOf(46);
            if (dot_index < 0) {
                if (!logger.isLoggable(MLevel.WARNING)) continue;
                logger.log(MLevel.WARNING, "Bad specification of named config property '" + string + "', propfile key should look like '" + PROP_STYLE_NAMED_CFG_PFX + ".<cfgname>.<property>' or '" + PROP_STYLE_NAMED_CFG_PFX + ".<cfgname>.user-overrides.<user>.<property>'. Ignoring.");
                continue;
            }
            String configName = nameProp.substring(0, dot_index);
            String propName = nameProp.substring(dot_index + 1);
            NamedScope scope = (NamedScope)namesToNamedScopes.get(configName);
            if (scope == null) {
                scope = new NamedScope();
                namesToNamedScopes.put(configName, scope);
            }
            if ((second_dot_index = propName.indexOf(46)) >= 0) {
                if (propName.startsWith(PROP_STYLE_USER_OVERRIDES_PART)) {
                    int third_dot_index = propName.substring(second_dot_index + 1).indexOf(46);
                    if (third_dot_index < 0 && logger.isLoggable(MLevel.WARNING)) {
                        logger.log(MLevel.WARNING, "Misformatted user-override property; missing user or property name: " + propName);
                    }
                    String user = propName.substring(second_dot_index + 1, third_dot_index);
                    String userPropName = propName.substring(third_dot_index + 1);
                    HashMap<String, Object> userOverridesMap = (HashMap<String, Object>)scope.userNamesToOverrides.get(user);
                    if (userOverridesMap == null) {
                        userOverridesMap = new HashMap<String, Object>();
                        scope.userNamesToOverrides.put(user, userOverridesMap);
                    }
                    userOverridesMap.put(userPropName, props.get(string));
                    continue;
                }
                if (propName.startsWith(PROP_STYLE_EXTENSIONS_PART)) {
                    String extensionsKey = propName.substring(second_dot_index + 1);
                    scope.extensions.put(extensionsKey, props.get(string));
                    continue;
                }
                if (!logger.isLoggable(MLevel.WARNING)) continue;
                logger.log(MLevel.WARNING, "Unexpected compound property, ignored: " + propName);
                continue;
            }
            scope.props.put(propName, props.get(string));
        }
        return namesToNamedScopes;
    }

    public static String getUnspecifiedUserProperty(String propKey, String configName) {
        String out = null;
        if (configName == null) {
            out = (String)C3P0Config.MAIN().defaultConfig.props.get(propKey);
        } else {
            NamedScope named = (NamedScope)C3P0Config.MAIN().configNamesToNamedScopes.get(configName);
            if (named != null) {
                out = (String)named.props.get(propKey);
            } else {
                logger.warning("named-config with name '" + configName + "' does not exist. Using default-config for property '" + propKey + "'.");
            }
            if (out == null) {
                out = (String)C3P0Config.MAIN().defaultConfig.props.get(propKey);
            }
        }
        return out;
    }

    public static Map getExtensions(String configName) {
        HashMap raw = C3P0Config.MAIN().defaultConfig.extensions;
        if (configName != null) {
            NamedScope named = (NamedScope)C3P0Config.MAIN().configNamesToNamedScopes.get(configName);
            if (named != null) {
                raw = named.extensions;
            } else {
                logger.warning("named-config with name '" + configName + "' does not exist. Using default-config extensions.");
            }
        }
        return Collections.unmodifiableMap(raw);
    }

    public static Map getUnspecifiedUserProperties(String configName) {
        HashMap out = new HashMap();
        out.putAll(C3P0Config.MAIN().defaultConfig.props);
        if (configName != null) {
            NamedScope named = (NamedScope)C3P0Config.MAIN().configNamesToNamedScopes.get(configName);
            if (named != null) {
                out.putAll(named.props);
            } else {
                logger.warning("named-config with name '" + configName + "' does not exist. Using default-config.");
            }
        }
        return out;
    }

    public static Map getUserOverrides(String configName) {
        HashMap out = new HashMap();
        NamedScope namedConfigScope = null;
        if (configName != null) {
            namedConfigScope = (NamedScope)C3P0Config.MAIN().configNamesToNamedScopes.get(configName);
        }
        out.putAll(C3P0Config.MAIN().defaultConfig.userNamesToOverrides);
        if (namedConfigScope != null) {
            out.putAll(namedConfigScope.userNamesToOverrides);
        }
        return out.isEmpty() ? null : out;
    }

    public static String getUserOverridesAsString(String configName) throws IOException {
        Map userOverrides = C3P0Config.getUserOverrides(configName);
        if (userOverrides == null) {
            return null;
        }
        return C3P0ImplUtils.createUserOverridesAsString(userOverrides).intern();
    }

    public static void bindUserOverridesAsString(Object bean, String uoas) throws Exception {
        Method m = bean.getClass().getMethod("setUserOverridesAsString", SUOAS_ARGS);
        m.invoke(bean, uoas);
    }

    public static void bindUserOverridesToBean(Object bean, String configName) throws Exception {
        C3P0Config.bindUserOverridesAsString(bean, C3P0Config.getUserOverridesAsString(configName));
    }

    public static void bindNamedConfigToBean(Object bean, String configName, boolean shouldBindUserOverridesAsString) throws IntrospectionException {
        block4: {
            Map defaultUserProps = C3P0Config.getUnspecifiedUserProperties(configName);
            Map extensions = C3P0Config.getExtensions(configName);
            HashMap<String, Map> union = new HashMap<String, Map>();
            union.putAll(defaultUserProps);
            union.put(PROP_STYLE_EXTENSIONS_PART, extensions);
            BeansUtils.overwriteAccessiblePropertiesFromMap(union, (Object)bean, (boolean)false, (Collection)SKIP_BIND_PROPS, (boolean)true, (MLevel)MLevel.FINEST, (MLevel)MLevel.WARNING, (boolean)false);
            try {
                if (shouldBindUserOverridesAsString) {
                    C3P0Config.bindUserOverridesToBean(bean, configName);
                }
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                if (!logger.isLoggable(MLevel.WARNING)) break block4;
                logger.log(MLevel.WARNING, "An exception occurred while trying to bind user overrides for named config '" + configName + "'. Only default user configs will be used.", (Throwable)e);
            }
        }
    }

    public static String initializeUserOverridesAsString() {
        try {
            return C3P0Config.getUserOverridesAsString(null);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "Error initializing default user overrides. User overrides may be ignored.", (Throwable)e);
            }
            return null;
        }
    }

    public static Map initializeExtensions() {
        return C3P0Config.getExtensions(null);
    }

    public static String initializeStringPropertyVar(String propKey, String dflt) {
        String out = C3P0Config.getUnspecifiedUserProperty(propKey, null);
        if (out == null) {
            out = dflt;
        }
        return out;
    }

    public static int initializeIntPropertyVar(String propKey, int dflt) {
        boolean set = false;
        int out = -1;
        String outStr = C3P0Config.getUnspecifiedUserProperty(propKey, null);
        if (outStr != null) {
            try {
                out = Integer.parseInt(outStr.trim());
                set = true;
            }
            catch (NumberFormatException e) {
                logger.info("'" + outStr + "' is not a legal value for property '" + propKey + "'. Using default value: " + dflt);
            }
        }
        if (!set) {
            out = dflt;
        }
        return out;
    }

    public static boolean initializeBooleanPropertyVar(String propKey, boolean dflt) {
        boolean set = false;
        boolean out = false;
        String outStr = C3P0Config.getUnspecifiedUserProperty(propKey, null);
        if (outStr != null) {
            try {
                out = BooleanUtils.parseBoolean((String)outStr.trim());
                set = true;
            }
            catch (IllegalArgumentException e) {
                logger.info("'" + outStr + "' is not a legal value for property '" + propKey + "'. Using default value: " + dflt);
            }
        }
        if (!set) {
            out = dflt;
        }
        return out;
    }

    public static MultiPropertiesConfig getMultiPropertiesConfig() {
        return C3P0Config.MPCONFIG();
    }

    C3P0Config(NamedScope defaultConfig, HashMap configNamesToNamedScopes) {
        this.defaultConfig = defaultConfig;
        this.configNamesToNamedScopes = configNamesToNamedScopes;
    }

    static {
        C3P0Config.setLibraryMultiPropertiesConfig(C3P0Config.findLibraryMultiPropertiesConfig());
        C3P0Config.setMainConfig(C3P0Config.findLibraryC3P0Config(false));
        C3P0Config.warnOnUnknownProperties(C3P0Config.MAIN());
        SUOAS_ARGS = new Class[]{String.class};
        SKIP_BIND_PROPS = Arrays.asList("loginTimeout", "properties");
    }
}

