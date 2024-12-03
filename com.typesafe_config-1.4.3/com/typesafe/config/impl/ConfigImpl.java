/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigIncluder;
import com.typesafe.config.ConfigMemorySize;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigParseable;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.impl.AbstractConfigObject;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigBoolean;
import com.typesafe.config.impl.ConfigDouble;
import com.typesafe.config.impl.ConfigImplUtil;
import com.typesafe.config.impl.ConfigInt;
import com.typesafe.config.impl.ConfigLong;
import com.typesafe.config.impl.ConfigNull;
import com.typesafe.config.impl.ConfigNumber;
import com.typesafe.config.impl.ConfigString;
import com.typesafe.config.impl.FromMapMode;
import com.typesafe.config.impl.Parseable;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.PropertiesParser;
import com.typesafe.config.impl.SimpleConfigList;
import com.typesafe.config.impl.SimpleConfigObject;
import com.typesafe.config.impl.SimpleConfigOrigin;
import com.typesafe.config.impl.SimpleIncluder;
import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

public class ConfigImpl {
    private static final String ENV_VAR_OVERRIDE_PREFIX = "CONFIG_FORCE_";
    private static final ConfigOrigin defaultValueOrigin = SimpleConfigOrigin.newSimple("hardcoded value");
    private static final ConfigBoolean defaultTrueValue = new ConfigBoolean(defaultValueOrigin, true);
    private static final ConfigBoolean defaultFalseValue = new ConfigBoolean(defaultValueOrigin, false);
    private static final ConfigNull defaultNullValue = new ConfigNull(defaultValueOrigin);
    private static final SimpleConfigList defaultEmptyList = new SimpleConfigList(defaultValueOrigin, Collections.emptyList());
    private static final SimpleConfigObject defaultEmptyObject = SimpleConfigObject.empty(defaultValueOrigin);

    public static Config computeCachedConfig(ClassLoader loader, String key, Callable<Config> updater) {
        LoaderCache cache;
        try {
            cache = LoaderCacheHolder.cache;
        }
        catch (ExceptionInInitializerError e) {
            throw ConfigImplUtil.extractInitializerError(e);
        }
        return cache.getOrElseUpdate(loader, key, updater);
    }

    public static ConfigObject parseResourcesAnySyntax(Class<?> klass, String resourceBasename, ConfigParseOptions baseOptions) {
        ClasspathNameSourceWithClass source = new ClasspathNameSourceWithClass(klass);
        return SimpleIncluder.fromBasename(source, resourceBasename, baseOptions);
    }

    public static ConfigObject parseResourcesAnySyntax(String resourceBasename, ConfigParseOptions baseOptions) {
        ClasspathNameSource source = new ClasspathNameSource();
        return SimpleIncluder.fromBasename(source, resourceBasename, baseOptions);
    }

    public static ConfigObject parseFileAnySyntax(File basename, ConfigParseOptions baseOptions) {
        FileNameSource source = new FileNameSource();
        return SimpleIncluder.fromBasename(source, basename.getPath(), baseOptions);
    }

    static AbstractConfigObject emptyObject(String originDescription) {
        SimpleConfigOrigin origin = originDescription != null ? SimpleConfigOrigin.newSimple(originDescription) : null;
        return ConfigImpl.emptyObject(origin);
    }

    public static Config emptyConfig(String originDescription) {
        return ConfigImpl.emptyObject(originDescription).toConfig();
    }

    static AbstractConfigObject empty(ConfigOrigin origin) {
        return ConfigImpl.emptyObject(origin);
    }

    private static SimpleConfigList emptyList(ConfigOrigin origin) {
        if (origin == null || origin == defaultValueOrigin) {
            return defaultEmptyList;
        }
        return new SimpleConfigList(origin, Collections.emptyList());
    }

    private static AbstractConfigObject emptyObject(ConfigOrigin origin) {
        if (origin == defaultValueOrigin) {
            return defaultEmptyObject;
        }
        return SimpleConfigObject.empty(origin);
    }

    private static ConfigOrigin valueOrigin(String originDescription) {
        if (originDescription == null) {
            return defaultValueOrigin;
        }
        return SimpleConfigOrigin.newSimple(originDescription);
    }

    public static ConfigValue fromAnyRef(Object object, String originDescription) {
        ConfigOrigin origin = ConfigImpl.valueOrigin(originDescription);
        return ConfigImpl.fromAnyRef(object, origin, FromMapMode.KEYS_ARE_KEYS);
    }

    public static ConfigObject fromPathMap(Map<String, ? extends Object> pathMap, String originDescription) {
        ConfigOrigin origin = ConfigImpl.valueOrigin(originDescription);
        return (ConfigObject)((Object)ConfigImpl.fromAnyRef(pathMap, origin, FromMapMode.KEYS_ARE_PATHS));
    }

    static AbstractConfigValue fromAnyRef(Object object, ConfigOrigin origin, FromMapMode mapMode) {
        if (origin == null) {
            throw new ConfigException.BugOrBroken("origin not supposed to be null");
        }
        if (object == null) {
            if (origin != defaultValueOrigin) {
                return new ConfigNull(origin);
            }
            return defaultNullValue;
        }
        if (object instanceof AbstractConfigValue) {
            return (AbstractConfigValue)object;
        }
        if (object instanceof Boolean) {
            if (origin != defaultValueOrigin) {
                return new ConfigBoolean(origin, (Boolean)object);
            }
            if (((Boolean)object).booleanValue()) {
                return defaultTrueValue;
            }
            return defaultFalseValue;
        }
        if (object instanceof String) {
            return new ConfigString.Quoted(origin, (String)object);
        }
        if (object instanceof Number) {
            if (object instanceof Double) {
                return new ConfigDouble(origin, (Double)object, null);
            }
            if (object instanceof Integer) {
                return new ConfigInt(origin, (Integer)object, null);
            }
            if (object instanceof Long) {
                return new ConfigLong(origin, (Long)object, null);
            }
            return ConfigNumber.newNumber(origin, ((Number)object).doubleValue(), null);
        }
        if (object instanceof Duration) {
            return new ConfigLong(origin, ((Duration)object).toMillis(), null);
        }
        if (object instanceof Map) {
            if (((Map)object).isEmpty()) {
                return ConfigImpl.emptyObject(origin);
            }
            if (mapMode == FromMapMode.KEYS_ARE_KEYS) {
                HashMap<String, AbstractConfigValue> values = new HashMap<String, AbstractConfigValue>();
                for (Map.Entry entry : ((Map)object).entrySet()) {
                    Object key = entry.getKey();
                    if (!(key instanceof String)) {
                        throw new ConfigException.BugOrBroken("bug in method caller: not valid to create ConfigObject from map with non-String key: " + key);
                    }
                    AbstractConfigValue value = ConfigImpl.fromAnyRef(entry.getValue(), origin, mapMode);
                    values.put((String)key, value);
                }
                return new SimpleConfigObject(origin, values);
            }
            return PropertiesParser.fromPathMap(origin, (Map)object);
        }
        if (object instanceof Iterable) {
            Iterator i = ((Iterable)object).iterator();
            if (!i.hasNext()) {
                return ConfigImpl.emptyList(origin);
            }
            ArrayList<AbstractConfigValue> values = new ArrayList<AbstractConfigValue>();
            while (i.hasNext()) {
                AbstractConfigValue v = ConfigImpl.fromAnyRef(i.next(), origin, mapMode);
                values.add(v);
            }
            return new SimpleConfigList(origin, values);
        }
        if (object instanceof ConfigMemorySize) {
            return new ConfigLong(origin, ((ConfigMemorySize)object).toBytes(), null);
        }
        throw new ConfigException.BugOrBroken("bug in method caller: not valid to create ConfigValue from: " + object);
    }

    static ConfigIncluder defaultIncluder() {
        try {
            return DefaultIncluderHolder.defaultIncluder;
        }
        catch (ExceptionInInitializerError e) {
            throw ConfigImplUtil.extractInitializerError(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Properties getSystemProperties() {
        Properties systemProperties = System.getProperties();
        Properties systemPropertiesCopy = new Properties();
        Properties properties = systemProperties;
        synchronized (properties) {
            for (Map.Entry<Object, Object> entry : systemProperties.entrySet()) {
                if (entry.getKey().toString().startsWith("java.version.")) continue;
                systemPropertiesCopy.put(entry.getKey(), entry.getValue());
            }
        }
        return systemPropertiesCopy;
    }

    private static AbstractConfigObject loadSystemProperties() {
        return (AbstractConfigObject)Parseable.newProperties(ConfigImpl.getSystemProperties(), ConfigParseOptions.defaults().setOriginDescription("system properties")).parse();
    }

    static AbstractConfigObject systemPropertiesAsConfigObject() {
        try {
            return SystemPropertiesHolder.systemProperties;
        }
        catch (ExceptionInInitializerError e) {
            throw ConfigImplUtil.extractInitializerError(e);
        }
    }

    public static Config systemPropertiesAsConfig() {
        return ConfigImpl.systemPropertiesAsConfigObject().toConfig();
    }

    public static void reloadSystemPropertiesConfig() {
        SystemPropertiesHolder.systemProperties = ConfigImpl.loadSystemProperties();
    }

    private static AbstractConfigObject loadEnvVariables() {
        return PropertiesParser.fromStringMap(ConfigImpl.newEnvVariable("env variables"), System.getenv());
    }

    static AbstractConfigObject envVariablesAsConfigObject() {
        try {
            return EnvVariablesHolder.envVariables;
        }
        catch (ExceptionInInitializerError e) {
            throw ConfigImplUtil.extractInitializerError(e);
        }
    }

    public static Config envVariablesAsConfig() {
        return ConfigImpl.envVariablesAsConfigObject().toConfig();
    }

    public static void reloadEnvVariablesConfig() {
        EnvVariablesHolder.envVariables = ConfigImpl.loadEnvVariables();
    }

    private static AbstractConfigObject loadEnvVariablesOverrides() {
        HashMap<String, String> env = new HashMap<String, String>(System.getenv());
        HashMap<String, String> result = new HashMap<String, String>();
        for (String key : env.keySet()) {
            if (!key.startsWith(ENV_VAR_OVERRIDE_PREFIX)) continue;
            result.put(ConfigImplUtil.envVariableAsProperty(key, ENV_VAR_OVERRIDE_PREFIX), (String)env.get(key));
        }
        return PropertiesParser.fromStringMap(ConfigImpl.newSimpleOrigin("env variables overrides"), result);
    }

    static AbstractConfigObject envVariablesOverridesAsConfigObject() {
        try {
            return EnvVariablesOverridesHolder.envVariables;
        }
        catch (ExceptionInInitializerError e) {
            throw ConfigImplUtil.extractInitializerError(e);
        }
    }

    public static Config envVariablesOverridesAsConfig() {
        return ConfigImpl.envVariablesOverridesAsConfigObject().toConfig();
    }

    public static void reloadEnvVariablesOverridesConfig() {
        EnvVariablesOverridesHolder.envVariables = ConfigImpl.loadEnvVariablesOverrides();
    }

    public static Config defaultReference(final ClassLoader loader) {
        return ConfigImpl.computeCachedConfig(loader, "defaultReference", new Callable<Config>(){

            @Override
            public Config call() {
                Config unresolvedResources = ConfigImpl.unresolvedReference(loader);
                return ConfigImpl.systemPropertiesAsConfig().withFallback(unresolvedResources).resolve();
            }
        });
    }

    private static Config unresolvedReference(final ClassLoader loader) {
        return ConfigImpl.computeCachedConfig(loader, "unresolvedReference", new Callable<Config>(){

            @Override
            public Config call() {
                return Parseable.newResources("reference.conf", ConfigParseOptions.defaults().setClassLoader(loader)).parse().toConfig();
            }
        });
    }

    public static Config defaultReferenceUnresolved(ClassLoader loader) {
        try {
            ConfigImpl.defaultReference(loader);
        }
        catch (ConfigException.UnresolvedSubstitution e) {
            throw e.addExtraDetail("Could not resolve substitution in reference.conf to a value: %s. All reference.conf files are required to be fully, independently resolvable, and should not require the presence of values for substitutions from further up the hierarchy.");
        }
        return ConfigImpl.unresolvedReference(loader);
    }

    public static boolean traceLoadsEnabled() {
        try {
            return DebugHolder.traceLoadsEnabled();
        }
        catch (ExceptionInInitializerError e) {
            throw ConfigImplUtil.extractInitializerError(e);
        }
    }

    public static boolean traceSubstitutionsEnabled() {
        try {
            return DebugHolder.traceSubstitutionsEnabled();
        }
        catch (ExceptionInInitializerError e) {
            throw ConfigImplUtil.extractInitializerError(e);
        }
    }

    public static void trace(String message) {
        System.err.println(message);
    }

    public static void trace(int indentLevel, String message) {
        while (indentLevel > 0) {
            System.err.print("  ");
            --indentLevel;
        }
        System.err.println(message);
    }

    static ConfigException.NotResolved improveNotResolved(Path what, ConfigException.NotResolved original) {
        String newMessage = what.render() + " has not been resolved, you need to call Config#resolve(), see API docs for Config#resolve()";
        if (newMessage.equals(original.getMessage())) {
            return original;
        }
        return new ConfigException.NotResolved(newMessage, original);
    }

    public static ConfigOrigin newSimpleOrigin(String description) {
        if (description == null) {
            return defaultValueOrigin;
        }
        return SimpleConfigOrigin.newSimple(description);
    }

    public static ConfigOrigin newFileOrigin(String filename) {
        return SimpleConfigOrigin.newFile(filename);
    }

    public static ConfigOrigin newURLOrigin(URL url) {
        return SimpleConfigOrigin.newURL(url);
    }

    public static ConfigOrigin newEnvVariable(String description) {
        return SimpleConfigOrigin.newEnvVariable(description);
    }

    static /* synthetic */ AbstractConfigObject access$000() {
        return ConfigImpl.loadSystemProperties();
    }

    static /* synthetic */ AbstractConfigObject access$100() {
        return ConfigImpl.loadEnvVariables();
    }

    static /* synthetic */ AbstractConfigObject access$200() {
        return ConfigImpl.loadEnvVariablesOverrides();
    }

    private static class DebugHolder {
        private static String LOADS = "loads";
        private static String SUBSTITUTIONS = "substitutions";
        private static final Map<String, Boolean> diagnostics = DebugHolder.loadDiagnostics();
        private static final boolean traceLoadsEnabled = diagnostics.get(LOADS);
        private static final boolean traceSubstitutionsEnabled = diagnostics.get(SUBSTITUTIONS);

        private DebugHolder() {
        }

        private static Map<String, Boolean> loadDiagnostics() {
            String[] keys;
            HashMap<String, Boolean> result = new HashMap<String, Boolean>();
            result.put(LOADS, false);
            result.put(SUBSTITUTIONS, false);
            String s = System.getProperty("config.trace");
            if (s == null) {
                return result;
            }
            for (String k : keys = s.split(",")) {
                if (k.equals(LOADS)) {
                    result.put(LOADS, true);
                    continue;
                }
                if (k.equals(SUBSTITUTIONS)) {
                    result.put(SUBSTITUTIONS, true);
                    continue;
                }
                System.err.println("config.trace property contains unknown trace topic '" + k + "'");
            }
            return result;
        }

        static boolean traceLoadsEnabled() {
            return traceLoadsEnabled;
        }

        static boolean traceSubstitutionsEnabled() {
            return traceSubstitutionsEnabled;
        }
    }

    private static class EnvVariablesOverridesHolder {
        static volatile AbstractConfigObject envVariables = ConfigImpl.access$200();

        private EnvVariablesOverridesHolder() {
        }
    }

    private static class EnvVariablesHolder {
        static volatile AbstractConfigObject envVariables = ConfigImpl.access$100();

        private EnvVariablesHolder() {
        }
    }

    private static class SystemPropertiesHolder {
        static volatile AbstractConfigObject systemProperties = ConfigImpl.access$000();

        private SystemPropertiesHolder() {
        }
    }

    private static class DefaultIncluderHolder {
        static final ConfigIncluder defaultIncluder = new SimpleIncluder(null);

        private DefaultIncluderHolder() {
        }
    }

    static class ClasspathNameSourceWithClass
    implements SimpleIncluder.NameSource {
        private final Class<?> klass;

        public ClasspathNameSourceWithClass(Class<?> klass) {
            this.klass = klass;
        }

        @Override
        public ConfigParseable nameToParseable(String name, ConfigParseOptions parseOptions) {
            return Parseable.newResources(this.klass, name, parseOptions);
        }
    }

    static class ClasspathNameSource
    implements SimpleIncluder.NameSource {
        ClasspathNameSource() {
        }

        @Override
        public ConfigParseable nameToParseable(String name, ConfigParseOptions parseOptions) {
            return Parseable.newResources(name, parseOptions);
        }
    }

    static class FileNameSource
    implements SimpleIncluder.NameSource {
        FileNameSource() {
        }

        @Override
        public ConfigParseable nameToParseable(String name, ConfigParseOptions parseOptions) {
            return Parseable.newFile(new File(name), parseOptions);
        }
    }

    private static class LoaderCacheHolder {
        static final LoaderCache cache = new LoaderCache();

        private LoaderCacheHolder() {
        }
    }

    private static class LoaderCache {
        private Config currentSystemProperties = null;
        private WeakReference<ClassLoader> currentLoader = new WeakReference<Object>(null);
        private Map<String, Config> cache = new HashMap<String, Config>();

        LoaderCache() {
        }

        synchronized Config getOrElseUpdate(ClassLoader loader, String key, Callable<Config> updater) {
            Config config;
            Config systemProperties;
            if (loader != this.currentLoader.get()) {
                this.cache.clear();
                this.currentLoader = new WeakReference<ClassLoader>(loader);
            }
            if ((systemProperties = ConfigImpl.systemPropertiesAsConfig()) != this.currentSystemProperties) {
                this.cache.clear();
                this.currentSystemProperties = systemProperties;
            }
            if ((config = this.cache.get(key)) == null) {
                try {
                    config = updater.call();
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new ConfigException.Generic(e.getMessage(), e);
                }
                if (config == null) {
                    throw new ConfigException.BugOrBroken("null config from cache updater");
                }
                this.cache.put(key, config);
            }
            return config;
        }
    }
}

