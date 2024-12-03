/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 *  org.joda.time.DateTime
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.osgi.framework.Bundle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.atlassian.upm.api.util.Option;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PluginMetadata {
    private static final Logger log = LoggerFactory.getLogger(PluginMetadata.class);
    public static final String BUILD_DATE_ATTRIBUTE = "Atlassian-Build-Date";
    private static final DateTimeFormatter BUILD_DATE_FORMAT = DateTimeFormat.forPattern((String)"yyyy-MM-dd'T'HH:mm:ssZ").withOffsetParsed();

    public static Option<DateTime> getPluginBuildDate(Option<Plugin> maybePlugin) {
        for (Plugin plugin : maybePlugin) {
            for (Bundle bundle : PluginMetadata.getPluginBundle(plugin)) {
                if (bundle == null) {
                    return Option.none();
                }
                Object value = bundle.getHeaders().get(BUILD_DATE_ATTRIBUTE);
                if (value == null) continue;
                try {
                    return Option.some(BUILD_DATE_FORMAT.parseDateTime(value.toString()));
                }
                catch (IllegalArgumentException e) {
                    log.warn("Plugin with key \"" + plugin.getKey() + "\" has invalid Atlassian-Build-Date of \"" + value + "\"");
                    return Option.none();
                }
            }
        }
        return Option.none();
    }

    public static Option<Bundle> getPluginBundle(Plugin plugin) {
        if (plugin instanceof OsgiPlugin) {
            return Option.option(((OsgiPlugin)plugin).getBundle());
        }
        return Option.none();
    }
}

