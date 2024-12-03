/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy
 *  org.apache.logging.log4j.core.config.plugins.util.PluginManager
 *  org.apache.logging.log4j.core.config.plugins.util.PluginType
 *  org.apache.logging.log4j.status.StatusLogger
 *  org.apache.logging.log4j.util.LoaderUtil
 */
package org.apache.log4j.builders;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.bridge.AppenderWrapper;
import org.apache.log4j.bridge.FilterWrapper;
import org.apache.log4j.bridge.LayoutWrapper;
import org.apache.log4j.bridge.RewritePolicyWrapper;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.Builder;
import org.apache.log4j.builders.Parser;
import org.apache.log4j.builders.appender.AppenderBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.rewrite.RewritePolicy;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.w3c.dom.Element;

public class BuilderManager {
    public static final String CATEGORY = "Log4j Builder";
    public static final Appender INVALID_APPENDER = new AppenderWrapper(null);
    public static final Filter INVALID_FILTER = new FilterWrapper(null);
    public static final Layout INVALID_LAYOUT = new LayoutWrapper(null);
    public static final RewritePolicy INVALID_REWRITE_POLICY = new RewritePolicyWrapper(null);
    private static final Logger LOGGER = StatusLogger.getLogger();
    private static Class<?>[] CONSTRUCTOR_PARAMS = new Class[]{String.class, Properties.class};
    private final Map<String, PluginType<?>> plugins;

    public BuilderManager() {
        PluginManager manager = new PluginManager(CATEGORY);
        manager.collectPlugins();
        this.plugins = manager.getPlugins();
    }

    private <T extends Builder<U>, U> T createBuilder(PluginType<T> plugin, String prefix, Properties props) {
        if (plugin == null) {
            return null;
        }
        try {
            Class clazz = plugin.getPluginClass();
            if (AbstractBuilder.class.isAssignableFrom(clazz)) {
                return (T)((Builder)clazz.getConstructor(CONSTRUCTOR_PARAMS).newInstance(prefix, props));
            }
            Builder builder = (Builder)LoaderUtil.newInstanceOf((Class)clazz);
            if (!Builder.class.isAssignableFrom(clazz)) {
                LOGGER.warn("Unable to load plugin: builder {} does not implement {}", (Object)clazz, Builder.class);
                return null;
            }
            return (T)builder;
        }
        catch (ReflectiveOperationException ex) {
            LOGGER.warn("Unable to load plugin: {} due to: {}", (Object)plugin.getKey(), (Object)ex.getMessage());
            return null;
        }
    }

    private <T> PluginType<T> getPlugin(String className) {
        Objects.requireNonNull(this.plugins, "plugins");
        Objects.requireNonNull(className, "className");
        String key = className.toLowerCase(Locale.ROOT).trim();
        PluginType<?> pluginType = this.plugins.get(key);
        if (pluginType == null) {
            LOGGER.warn("Unable to load plugin class name {} with key {}", (Object)className, (Object)key);
        }
        return pluginType;
    }

    private <T extends Builder<U>, U> U newInstance(PluginType<T> plugin, Function<T, U> consumer, U invalidValue) {
        if (plugin != null) {
            try {
                Builder builder = (Builder)LoaderUtil.newInstanceOf((Class)plugin.getPluginClass());
                if (builder != null) {
                    U result = consumer.apply(builder);
                    return result != null ? result : invalidValue;
                }
            }
            catch (ReflectiveOperationException ex) {
                LOGGER.warn("Unable to load plugin: {} due to: {}", (Object)plugin.getKey(), (Object)ex.getMessage());
            }
        }
        return null;
    }

    public <P extends Parser<T>, T> T parse(String className, String prefix, Properties props, PropertiesConfiguration config, T invalidValue) {
        Parser parser = (Parser)this.createBuilder(this.getPlugin(className), prefix, props);
        if (parser != null) {
            Object value = parser.parse(config);
            return value != null ? value : invalidValue;
        }
        return null;
    }

    public Appender parseAppender(String className, Element appenderElement, XmlConfiguration config) {
        return this.newInstance(this.getPlugin(className), b -> b.parseAppender(appenderElement, config), INVALID_APPENDER);
    }

    public Appender parseAppender(String name, String className, String prefix, String layoutPrefix, String filterPrefix, Properties props, PropertiesConfiguration config) {
        AppenderBuilder builder = (AppenderBuilder)this.createBuilder(this.getPlugin(className), prefix, props);
        if (builder != null) {
            Appender appender = builder.parseAppender(name, prefix, layoutPrefix, filterPrefix, props, config);
            return appender != null ? appender : INVALID_APPENDER;
        }
        return null;
    }

    public Filter parseFilter(String className, Element filterElement, XmlConfiguration config) {
        return this.newInstance(this.getPlugin(className), b -> (Filter)b.parse(filterElement, config), INVALID_FILTER);
    }

    public Layout parseLayout(String className, Element layoutElement, XmlConfiguration config) {
        return this.newInstance(this.getPlugin(className), b -> (Layout)b.parse(layoutElement, config), INVALID_LAYOUT);
    }

    public RewritePolicy parseRewritePolicy(String className, Element rewriteElement, XmlConfiguration config) {
        return this.newInstance(this.getPlugin(className), b -> (RewritePolicy)b.parse(rewriteElement, config), INVALID_REWRITE_POLICY);
    }

    public TriggeringPolicy parseTriggeringPolicy(String className, Element policyElement, XmlConfiguration config) {
        return this.newInstance(this.getPlugin(className), b -> (TriggeringPolicy)b.parse(policyElement, config), null);
    }
}

