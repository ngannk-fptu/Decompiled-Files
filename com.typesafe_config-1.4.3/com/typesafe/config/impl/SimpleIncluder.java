/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigIncludeContext;
import com.typesafe.config.ConfigIncluder;
import com.typesafe.config.ConfigIncluderClasspath;
import com.typesafe.config.ConfigIncluderFile;
import com.typesafe.config.ConfigIncluderURL;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigParseable;
import com.typesafe.config.ConfigSyntax;
import com.typesafe.config.impl.ConfigImpl;
import com.typesafe.config.impl.FullIncluder;
import com.typesafe.config.impl.Parseable;
import com.typesafe.config.impl.SimpleConfigObject;
import com.typesafe.config.impl.SimpleConfigOrigin;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

class SimpleIncluder
implements FullIncluder {
    private ConfigIncluder fallback;

    SimpleIncluder(ConfigIncluder fallback) {
        this.fallback = fallback;
    }

    static ConfigParseOptions clearForInclude(ConfigParseOptions options) {
        return options.setSyntax(null).setOriginDescription(null).setAllowMissing(true);
    }

    @Override
    public ConfigObject include(ConfigIncludeContext context, String name) {
        ConfigObject obj = SimpleIncluder.includeWithoutFallback(context, name);
        if (this.fallback != null) {
            return obj.withFallback(this.fallback.include(context, name));
        }
        return obj;
    }

    static ConfigObject includeWithoutFallback(ConfigIncludeContext context, String name) {
        URL url;
        try {
            url = new URL(name);
        }
        catch (MalformedURLException e) {
            url = null;
        }
        if (url != null) {
            return SimpleIncluder.includeURLWithoutFallback(context, url);
        }
        RelativeNameSource source = new RelativeNameSource(context);
        return SimpleIncluder.fromBasename(source, name, context.parseOptions());
    }

    @Override
    public ConfigObject includeURL(ConfigIncludeContext context, URL url) {
        ConfigObject obj = SimpleIncluder.includeURLWithoutFallback(context, url);
        if (this.fallback != null && this.fallback instanceof ConfigIncluderURL) {
            return obj.withFallback(((ConfigIncluderURL)((Object)this.fallback)).includeURL(context, url));
        }
        return obj;
    }

    static ConfigObject includeURLWithoutFallback(ConfigIncludeContext context, URL url) {
        return ConfigFactory.parseURL(url, context.parseOptions()).root();
    }

    @Override
    public ConfigObject includeFile(ConfigIncludeContext context, File file) {
        ConfigObject obj = SimpleIncluder.includeFileWithoutFallback(context, file);
        if (this.fallback != null && this.fallback instanceof ConfigIncluderFile) {
            return obj.withFallback(((ConfigIncluderFile)((Object)this.fallback)).includeFile(context, file));
        }
        return obj;
    }

    static ConfigObject includeFileWithoutFallback(ConfigIncludeContext context, File file) {
        return ConfigFactory.parseFileAnySyntax(file, context.parseOptions()).root();
    }

    @Override
    public ConfigObject includeResources(ConfigIncludeContext context, String resource) {
        ConfigObject obj = SimpleIncluder.includeResourceWithoutFallback(context, resource);
        if (this.fallback != null && this.fallback instanceof ConfigIncluderClasspath) {
            return obj.withFallback(((ConfigIncluderClasspath)((Object)this.fallback)).includeResources(context, resource));
        }
        return obj;
    }

    static ConfigObject includeResourceWithoutFallback(ConfigIncludeContext context, String resource) {
        return ConfigFactory.parseResourcesAnySyntax(resource, context.parseOptions()).root();
    }

    @Override
    public ConfigIncluder withFallback(ConfigIncluder fallback) {
        if (this == fallback) {
            throw new ConfigException.BugOrBroken("trying to create includer cycle");
        }
        if (this.fallback == fallback) {
            return this;
        }
        if (this.fallback != null) {
            return new SimpleIncluder(this.fallback.withFallback(fallback));
        }
        return new SimpleIncluder(fallback);
    }

    static ConfigObject fromBasename(NameSource source, String name, ConfigParseOptions options) {
        ConfigObject obj;
        if (name.endsWith(".conf") || name.endsWith(".json") || name.endsWith(".properties")) {
            ConfigParseable p = source.nameToParseable(name, options);
            obj = p.parse(p.options().setAllowMissing(options.getAllowMissing()));
        } else {
            ConfigObject parsed;
            ConfigParseable confHandle = source.nameToParseable(name + ".conf", options);
            ConfigParseable jsonHandle = source.nameToParseable(name + ".json", options);
            ConfigParseable propsHandle = source.nameToParseable(name + ".properties", options);
            boolean gotSomething = false;
            ArrayList<ConfigException.IO> fails = new ArrayList<ConfigException.IO>();
            ConfigSyntax syntax = options.getSyntax();
            obj = SimpleConfigObject.empty(SimpleConfigOrigin.newSimple(name));
            if (syntax == null || syntax == ConfigSyntax.CONF) {
                try {
                    obj = confHandle.parse(confHandle.options().setAllowMissing(false).setSyntax(ConfigSyntax.CONF));
                    gotSomething = true;
                }
                catch (ConfigException.IO e) {
                    fails.add(e);
                }
            }
            if (syntax == null || syntax == ConfigSyntax.JSON) {
                try {
                    parsed = jsonHandle.parse(jsonHandle.options().setAllowMissing(false).setSyntax(ConfigSyntax.JSON));
                    obj = obj.withFallback(parsed);
                    gotSomething = true;
                }
                catch (ConfigException.IO e) {
                    fails.add(e);
                }
            }
            if (syntax == null || syntax == ConfigSyntax.PROPERTIES) {
                try {
                    parsed = propsHandle.parse(propsHandle.options().setAllowMissing(false).setSyntax(ConfigSyntax.PROPERTIES));
                    obj = obj.withFallback(parsed);
                    gotSomething = true;
                }
                catch (ConfigException.IO e) {
                    fails.add(e);
                }
            }
            if (!options.getAllowMissing() && !gotSomething) {
                if (ConfigImpl.traceLoadsEnabled()) {
                    ConfigImpl.trace("Did not find '" + name + "' with any extension (.conf, .json, .properties); exceptions should have been logged above.");
                }
                if (fails.isEmpty()) {
                    throw new ConfigException.BugOrBroken("should not be reached: nothing found but no exceptions thrown");
                }
                StringBuilder sb = new StringBuilder();
                for (Throwable throwable : fails) {
                    sb.append(throwable.getMessage());
                    sb.append(", ");
                }
                sb.setLength(sb.length() - 2);
                throw new ConfigException.IO(SimpleConfigOrigin.newSimple(name), sb.toString(), (Throwable)fails.get(0));
            }
            if (!gotSomething && ConfigImpl.traceLoadsEnabled()) {
                ConfigImpl.trace("Did not find '" + name + "' with any extension (.conf, .json, .properties); but '" + name + "' is allowed to be missing. Exceptions from load attempts should have been logged above.");
            }
        }
        return obj;
    }

    static FullIncluder makeFull(ConfigIncluder includer) {
        if (includer instanceof FullIncluder) {
            return (FullIncluder)includer;
        }
        return new Proxy(includer);
    }

    private static class Proxy
    implements FullIncluder {
        final ConfigIncluder delegate;

        Proxy(ConfigIncluder delegate) {
            this.delegate = delegate;
        }

        @Override
        public ConfigIncluder withFallback(ConfigIncluder fallback) {
            return this;
        }

        @Override
        public ConfigObject include(ConfigIncludeContext context, String what) {
            return this.delegate.include(context, what);
        }

        @Override
        public ConfigObject includeResources(ConfigIncludeContext context, String what) {
            if (this.delegate instanceof ConfigIncluderClasspath) {
                return ((ConfigIncluderClasspath)((Object)this.delegate)).includeResources(context, what);
            }
            return SimpleIncluder.includeResourceWithoutFallback(context, what);
        }

        @Override
        public ConfigObject includeURL(ConfigIncludeContext context, URL what) {
            if (this.delegate instanceof ConfigIncluderURL) {
                return ((ConfigIncluderURL)((Object)this.delegate)).includeURL(context, what);
            }
            return SimpleIncluder.includeURLWithoutFallback(context, what);
        }

        @Override
        public ConfigObject includeFile(ConfigIncludeContext context, File what) {
            if (this.delegate instanceof ConfigIncluderFile) {
                return ((ConfigIncluderFile)((Object)this.delegate)).includeFile(context, what);
            }
            return SimpleIncluder.includeFileWithoutFallback(context, what);
        }
    }

    private static class RelativeNameSource
    implements NameSource {
        private final ConfigIncludeContext context;

        RelativeNameSource(ConfigIncludeContext context) {
            this.context = context;
        }

        @Override
        public ConfigParseable nameToParseable(String name, ConfigParseOptions options) {
            ConfigParseable p = this.context.relativeTo(name);
            if (p == null) {
                return Parseable.newNotFound(name, "include was not found: '" + name + "'", options);
            }
            return p;
        }
    }

    static interface NameSource {
        public ConfigParseable nameToParseable(String var1, ConfigParseOptions var2);
    }
}

