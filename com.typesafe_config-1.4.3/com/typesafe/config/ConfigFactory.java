/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigLoadingStrategy;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigResolveOptions;
import com.typesafe.config.DefaultConfigLoadingStrategy;
import com.typesafe.config.impl.ConfigImpl;
import com.typesafe.config.impl.Parseable;
import java.io.File;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;

public final class ConfigFactory {
    private static final String STRATEGY_PROPERTY_NAME = "config.strategy";
    private static final String OVERRIDE_WITH_ENV_PROPERTY_NAME = "config.override_with_env_vars";

    private ConfigFactory() {
    }

    public static Config load(String resourceBasename) {
        return ConfigFactory.load(resourceBasename, ConfigParseOptions.defaults(), ConfigResolveOptions.defaults());
    }

    public static Config load(ClassLoader loader, String resourceBasename) {
        return ConfigFactory.load(resourceBasename, ConfigParseOptions.defaults().setClassLoader(loader), ConfigResolveOptions.defaults());
    }

    public static Config load(String resourceBasename, ConfigParseOptions parseOptions, ConfigResolveOptions resolveOptions) {
        ConfigParseOptions withLoader = ConfigFactory.ensureClassLoader(parseOptions, "load");
        Config appConfig = ConfigFactory.parseResourcesAnySyntax(resourceBasename, withLoader);
        return ConfigFactory.load(withLoader.getClassLoader(), appConfig, resolveOptions);
    }

    public static Config load(ClassLoader loader, String resourceBasename, ConfigParseOptions parseOptions, ConfigResolveOptions resolveOptions) {
        return ConfigFactory.load(resourceBasename, parseOptions.setClassLoader(loader), resolveOptions);
    }

    private static ClassLoader checkedContextClassLoader(String methodName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            throw new ConfigException.BugOrBroken("Context class loader is not set for the current thread; if Thread.currentThread().getContextClassLoader() returns null, you must pass a ClassLoader explicitly to ConfigFactory." + methodName);
        }
        return loader;
    }

    private static ConfigParseOptions ensureClassLoader(ConfigParseOptions options, String methodName) {
        if (options.getClassLoader() == null) {
            return options.setClassLoader(ConfigFactory.checkedContextClassLoader(methodName));
        }
        return options;
    }

    public static Config load(Config config) {
        return ConfigFactory.load(ConfigFactory.checkedContextClassLoader("load"), config);
    }

    public static Config load(ClassLoader loader, Config config) {
        return ConfigFactory.load(loader, config, ConfigResolveOptions.defaults());
    }

    public static Config load(Config config, ConfigResolveOptions resolveOptions) {
        return ConfigFactory.load(ConfigFactory.checkedContextClassLoader("load"), config, resolveOptions);
    }

    public static Config load(ClassLoader loader, Config config, ConfigResolveOptions resolveOptions) {
        return ConfigFactory.defaultOverrides(loader).withFallback(config).withFallback(ConfigImpl.defaultReferenceUnresolved(loader)).resolve(resolveOptions);
    }

    public static Config load() {
        ClassLoader loader = ConfigFactory.checkedContextClassLoader("load");
        return ConfigFactory.load(loader);
    }

    public static Config load(ConfigParseOptions parseOptions) {
        return ConfigFactory.load(parseOptions, ConfigResolveOptions.defaults());
    }

    public static Config load(final ClassLoader loader) {
        final ConfigParseOptions withLoader = ConfigParseOptions.defaults().setClassLoader(loader);
        return ConfigImpl.computeCachedConfig(loader, "load", new Callable<Config>(){

            @Override
            public Config call() {
                return ConfigFactory.load(loader, ConfigFactory.defaultApplication(withLoader));
            }
        });
    }

    public static Config load(ClassLoader loader, ConfigParseOptions parseOptions) {
        return ConfigFactory.load(parseOptions.setClassLoader(loader));
    }

    public static Config load(ClassLoader loader, ConfigResolveOptions resolveOptions) {
        return ConfigFactory.load(loader, ConfigParseOptions.defaults(), resolveOptions);
    }

    public static Config load(ClassLoader loader, ConfigParseOptions parseOptions, ConfigResolveOptions resolveOptions) {
        ConfigParseOptions withLoader = ConfigFactory.ensureClassLoader(parseOptions, "load");
        return ConfigFactory.load(loader, ConfigFactory.defaultApplication(withLoader), resolveOptions);
    }

    public static Config load(ConfigParseOptions parseOptions, ConfigResolveOptions resolveOptions) {
        ConfigParseOptions withLoader = ConfigFactory.ensureClassLoader(parseOptions, "load");
        return ConfigFactory.load(ConfigFactory.defaultApplication(withLoader), resolveOptions);
    }

    public static Config defaultReference() {
        return ConfigFactory.defaultReference(ConfigFactory.checkedContextClassLoader("defaultReference"));
    }

    public static Config defaultReference(ClassLoader loader) {
        return ConfigImpl.defaultReference(loader);
    }

    public static Config defaultReferenceUnresolved() {
        return ConfigFactory.defaultReferenceUnresolved(ConfigFactory.checkedContextClassLoader("defaultReferenceUnresolved"));
    }

    public static Config defaultReferenceUnresolved(ClassLoader loader) {
        return ConfigImpl.defaultReferenceUnresolved(loader);
    }

    public static Config defaultOverrides() {
        if (ConfigFactory.getOverrideWithEnv().booleanValue()) {
            return ConfigFactory.systemEnvironmentOverrides().withFallback(ConfigFactory.systemProperties());
        }
        return ConfigFactory.systemProperties();
    }

    public static Config defaultOverrides(ClassLoader loader) {
        return ConfigFactory.defaultOverrides();
    }

    public static Config defaultApplication() {
        return ConfigFactory.defaultApplication(ConfigParseOptions.defaults());
    }

    public static Config defaultApplication(ClassLoader loader) {
        return ConfigFactory.defaultApplication(ConfigParseOptions.defaults().setClassLoader(loader));
    }

    public static Config defaultApplication(ConfigParseOptions options) {
        return ConfigFactory.getConfigLoadingStrategy().parseApplicationConfig(ConfigFactory.ensureClassLoader(options, "defaultApplication"));
    }

    public static void invalidateCaches() {
        ConfigImpl.reloadSystemPropertiesConfig();
        ConfigImpl.reloadEnvVariablesConfig();
        ConfigImpl.reloadEnvVariablesOverridesConfig();
    }

    public static Config empty() {
        return ConfigFactory.empty(null);
    }

    public static Config empty(String originDescription) {
        return ConfigImpl.emptyConfig(originDescription);
    }

    public static Config systemProperties() {
        return ConfigImpl.systemPropertiesAsConfig();
    }

    public static Config systemEnvironmentOverrides() {
        return ConfigImpl.envVariablesOverridesAsConfig();
    }

    public static Config systemEnvironment() {
        return ConfigImpl.envVariablesAsConfig();
    }

    public static Config parseProperties(Properties properties, ConfigParseOptions options) {
        return Parseable.newProperties(properties, options).parse().toConfig();
    }

    public static Config parseProperties(Properties properties) {
        return ConfigFactory.parseProperties(properties, ConfigParseOptions.defaults());
    }

    public static Config parseReader(Reader reader, ConfigParseOptions options) {
        return Parseable.newReader(reader, options).parse().toConfig();
    }

    public static Config parseReader(Reader reader) {
        return ConfigFactory.parseReader(reader, ConfigParseOptions.defaults());
    }

    public static Config parseURL(URL url, ConfigParseOptions options) {
        return Parseable.newURL(url, options).parse().toConfig();
    }

    public static Config parseURL(URL url) {
        return ConfigFactory.parseURL(url, ConfigParseOptions.defaults());
    }

    public static Config parseFile(File file, ConfigParseOptions options) {
        return Parseable.newFile(file, options).parse().toConfig();
    }

    public static Config parseFile(File file) {
        return ConfigFactory.parseFile(file, ConfigParseOptions.defaults());
    }

    public static Config parseFileAnySyntax(File fileBasename, ConfigParseOptions options) {
        return ConfigImpl.parseFileAnySyntax(fileBasename, options).toConfig();
    }

    public static Config parseFileAnySyntax(File fileBasename) {
        return ConfigFactory.parseFileAnySyntax(fileBasename, ConfigParseOptions.defaults());
    }

    public static Config parseResources(Class<?> klass, String resource, ConfigParseOptions options) {
        return Parseable.newResources(klass, resource, options).parse().toConfig();
    }

    public static Config parseResources(Class<?> klass, String resource) {
        return ConfigFactory.parseResources(klass, resource, ConfigParseOptions.defaults());
    }

    public static Config parseResourcesAnySyntax(Class<?> klass, String resourceBasename, ConfigParseOptions options) {
        return ConfigImpl.parseResourcesAnySyntax(klass, resourceBasename, options).toConfig();
    }

    public static Config parseResourcesAnySyntax(Class<?> klass, String resourceBasename) {
        return ConfigFactory.parseResourcesAnySyntax(klass, resourceBasename, ConfigParseOptions.defaults());
    }

    public static Config parseResources(ClassLoader loader, String resource, ConfigParseOptions options) {
        return ConfigFactory.parseResources(resource, options.setClassLoader(loader));
    }

    public static Config parseResources(ClassLoader loader, String resource) {
        return ConfigFactory.parseResources(loader, resource, ConfigParseOptions.defaults());
    }

    public static Config parseResourcesAnySyntax(ClassLoader loader, String resourceBasename, ConfigParseOptions options) {
        return ConfigImpl.parseResourcesAnySyntax(resourceBasename, options.setClassLoader(loader)).toConfig();
    }

    public static Config parseResourcesAnySyntax(ClassLoader loader, String resourceBasename) {
        return ConfigFactory.parseResourcesAnySyntax(loader, resourceBasename, ConfigParseOptions.defaults());
    }

    public static Config parseResources(String resource, ConfigParseOptions options) {
        ConfigParseOptions withLoader = ConfigFactory.ensureClassLoader(options, "parseResources");
        return Parseable.newResources(resource, withLoader).parse().toConfig();
    }

    public static Config parseResources(String resource) {
        return ConfigFactory.parseResources(resource, ConfigParseOptions.defaults());
    }

    public static Config parseResourcesAnySyntax(String resourceBasename, ConfigParseOptions options) {
        return ConfigImpl.parseResourcesAnySyntax(resourceBasename, options).toConfig();
    }

    public static Config parseResourcesAnySyntax(String resourceBasename) {
        return ConfigFactory.parseResourcesAnySyntax(resourceBasename, ConfigParseOptions.defaults());
    }

    public static Optional<Config> parseApplicationReplacement() {
        return ConfigFactory.parseApplicationReplacement(ConfigParseOptions.defaults());
    }

    public static Optional<Config> parseApplicationReplacement(ClassLoader loader) {
        return ConfigFactory.parseApplicationReplacement(ConfigParseOptions.defaults().setClassLoader(loader));
    }

    public static Optional<Config> parseApplicationReplacement(ConfigParseOptions parseOptions) {
        String url;
        String file;
        ConfigParseOptions withLoader = ConfigFactory.ensureClassLoader(parseOptions, "parseApplicationReplacement");
        ClassLoader loader = withLoader.getClassLoader();
        int specified = 0;
        String resource = System.getProperty("config.resource");
        if (resource != null) {
            ++specified;
        }
        if ((file = System.getProperty("config.file")) != null) {
            ++specified;
        }
        if ((url = System.getProperty("config.url")) != null) {
            ++specified;
        }
        if (specified == 0) {
            return Optional.empty();
        }
        if (specified > 1) {
            throw new ConfigException.Generic("You set more than one of config.file='" + file + "', config.url='" + url + "', config.resource='" + resource + "'; don't know which one to use!");
        }
        ConfigParseOptions overrideOptions = parseOptions.setAllowMissing(false);
        if (resource != null) {
            if (resource.startsWith("/")) {
                resource = resource.substring(1);
            }
            return Optional.of(ConfigFactory.parseResources(loader, resource, overrideOptions));
        }
        if (file != null) {
            return Optional.of(ConfigFactory.parseFile(new File(file), overrideOptions));
        }
        try {
            return Optional.of(ConfigFactory.parseURL(new URL(url), overrideOptions));
        }
        catch (MalformedURLException e) {
            throw new ConfigException.Generic("Bad URL in config.url system property: '" + url + "': " + e.getMessage(), e);
        }
    }

    public static Config parseString(String s, ConfigParseOptions options) {
        return Parseable.newString(s, options).parse().toConfig();
    }

    public static Config parseString(String s) {
        return ConfigFactory.parseString(s, ConfigParseOptions.defaults());
    }

    public static Config parseMap(Map<String, ? extends Object> values, String originDescription) {
        return ConfigImpl.fromPathMap(values, originDescription).toConfig();
    }

    public static Config parseMap(Map<String, ? extends Object> values) {
        return ConfigFactory.parseMap(values, null);
    }

    private static ConfigLoadingStrategy getConfigLoadingStrategy() {
        String className = System.getProperties().getProperty(STRATEGY_PROPERTY_NAME);
        if (className != null) {
            try {
                return Class.forName(className).asSubclass(ConfigLoadingStrategy.class).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause == null) {
                    throw new ConfigException.BugOrBroken("Failed to load strategy: " + className, e);
                }
                throw new ConfigException.BugOrBroken("Failed to load strategy: " + className, cause);
            }
            catch (Throwable e) {
                throw new ConfigException.BugOrBroken("Failed to load strategy: " + className, e);
            }
        }
        return new DefaultConfigLoadingStrategy();
    }

    private static Boolean getOverrideWithEnv() {
        String overrideWithEnv = System.getProperties().getProperty(OVERRIDE_WITH_ENV_PROPERTY_NAME);
        return Boolean.parseBoolean(overrideWithEnv);
    }
}

