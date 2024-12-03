/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 */
package com.atlassian.upm.analytics.event;

import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.analytics.SenFinder;
import com.atlassian.upm.core.analytics.event.PluginAnalyticsEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class PluginUpdateRequestEvent
extends PluginAnalyticsEvent {
    private final Map<String, String> metadata;

    private PluginUpdateRequestEvent(PluginUpdateRequestEventBuilder builder, Plugin plugin, DefaultHostApplicationInformation hostApplicationInformation, Option<String> sen) {
        super(plugin, hostApplicationInformation, sen);
        this.metadata = Collections.unmodifiableMap(builder.metadata);
    }

    private PluginUpdateRequestEvent(PluginUpdateRequestEventBuilder builder, Addon addon, AddonVersion version, DefaultHostApplicationInformation hostApplicationInformation, Option<String> sen) {
        super(addon.getKey(), (String)version.getName().getOrElse((Object)""), Plugins.getAddonHostingType(addon, hostApplicationInformation), sen);
        this.metadata = Collections.unmodifiableMap(builder.metadata);
    }

    @Override
    public String getEventType() {
        return "plugin-update-request";
    }

    @Override
    public Iterable<Pair<String, String>> getMetadata() {
        return this.metadata.entrySet().stream().map(e -> Pair.pair(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    public static class PluginUpdateRequestEventBuilder {
        private Option<Plugin> installedPlugin = Option.none();
        private Option<Addon> availablePlugin = Option.none();
        private Map<String, String> metadata = new HashMap<String, String>();
        private DefaultHostApplicationInformation hostApplicationInformation;
        private final SenFinder senFinder;

        public PluginUpdateRequestEventBuilder(SenFinder senFinder) {
            this.senFinder = senFinder;
        }

        public static PluginUpdateRequestEventBuilder builderForInstalledPlugin(Plugin installedPlugin, PluginControlHandlerRegistry pluginControlHandlerRegistry, DefaultHostApplicationInformation hostApplicationInformation, SenFinder senFinder) {
            PluginUpdateRequestEventBuilder builder = new PluginUpdateRequestEventBuilder(senFinder);
            builder.installedPlugin = Option.some(installedPlugin);
            builder.hostApplicationInformation = hostApplicationInformation;
            return builder;
        }

        public static PluginUpdateRequestEventBuilder builderForAvailablePlugin(Addon availablePlugin, DefaultHostApplicationInformation hostApplicationInformation, SenFinder senFinder) {
            PluginUpdateRequestEventBuilder builder = new PluginUpdateRequestEventBuilder(senFinder);
            builder.availablePlugin = Option.some(availablePlugin);
            builder.hostApplicationInformation = hostApplicationInformation;
            return builder;
        }

        public PluginUpdateRequestEvent build() {
            if (this.availablePlugin.isDefined() && this.availablePlugin.get().getVersion().isDefined()) {
                return new PluginUpdateRequestEvent(this, this.availablePlugin.get(), (AddonVersion)this.availablePlugin.get().getVersion().get(), this.hostApplicationInformation, this.senFinder.findSen(this.availablePlugin.get().getKey()));
            }
            Iterator<Plugin> iterator = this.installedPlugin.iterator();
            if (iterator.hasNext()) {
                Plugin p = iterator.next();
                return new PluginUpdateRequestEvent(this, p, this.hostApplicationInformation, this.senFinder.findSen(p));
            }
            throw new IllegalArgumentException("Missing required parameters");
        }

        public PluginUpdateRequestEventBuilder applicationProperties(ApplicationProperties applicationProperties) {
            this.metadata.put("app", applicationProperties.getDisplayName());
            this.metadata.put("appVersion", applicationProperties.getVersion());
            return this;
        }

        public PluginUpdateRequestEventBuilder message(Option<String> message) {
            for (String m : message) {
                this.metadata.put("message", m);
            }
            return this;
        }

        public PluginUpdateRequestEventBuilder userInitiated(boolean userInitiated) {
            this.metadata.put("automated", Boolean.toString(!userInitiated));
            return this;
        }

        public PluginUpdateRequestEventBuilder dataCenterIncompatible(boolean dataCenterIncompatible) {
            this.metadata.put("dataCenterIncompatible", Boolean.toString(dataCenterIncompatible));
            return this;
        }

        public PluginUpdateRequestEventBuilder email(Option<String> email) {
            for (String e : email) {
                this.metadata.put("email", e);
            }
            return this;
        }

        public PluginUpdateRequestEventBuilder fullName(Option<String> fullName) {
            for (String name : fullName) {
                this.metadata.put("fullName", name);
            }
            return this;
        }
    }
}

