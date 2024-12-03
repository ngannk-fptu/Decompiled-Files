/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  com.atlassian.plugin.event.events.PluginUpgradedEvent
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.apache.commons.lang3.ArrayUtils
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.upm;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import com.atlassian.plugin.event.events.PluginUpgradedEvent;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.impl.NamespacedPluginSettings;
import com.atlassian.upm.osgi.Version;
import com.atlassian.upm.osgi.impl.Versions;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.Objects;
import org.apache.commons.lang3.ArrayUtils;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class UpmInformation
implements InitializingBean,
DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(UpmInformation.class);
    private static final String KEY_PREFIX = Sys.class.getName() + ":upm-settings:";
    private static final String MOST_RECENT_UPM_VERSION_KEY = "updated-upm-version";
    private final PluginAccessor pluginAccessor;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final EventPublisher eventPublisher;
    private final ApplicationProperties applicationProperties;
    private final String pluginKey;
    private final Version version;
    private final long bundleId;
    private boolean upmUpdatedSinceLastRestart = true;

    public UpmInformation(PluginAccessor pluginAccessor, PluginSettingsFactory pluginSettingsFactory, EventPublisher eventPublisher, ApplicationProperties applicationProperties, BundleContext bundleContext) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        Dictionary headers = bundleContext.getBundle().getHeaders();
        this.pluginKey = headers.get("Atlassian-Plugin-Key").toString();
        this.version = Versions.fromString(headers.get("Bundle-Version").toString());
        this.bundleId = bundleContext.getBundle().getBundleId();
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public Version getVersion() {
        return this.version;
    }

    public String getVersionString() {
        return this.pluginAccessor.getPlugin(this.pluginKey).getPluginInformation().getVersion();
    }

    public long getBundleId() {
        return this.bundleId;
    }

    public void setCurrentUpmVersionAsMostRecentlyUpdated() {
        this.getPluginSettings().put(MOST_RECENT_UPM_VERSION_KEY, (Object)this.version.toString());
    }

    public boolean isLegacyLicensingCompatibilitySpiUpm20Aware() {
        if (ArrayUtils.contains((Object[])new String[]{"fisheye", "refimpl"}, (Object)this.applicationProperties.getDisplayName().toLowerCase())) {
            return true;
        }
        Iterator<String> iterator = this.getMostRecentlyUpdatedUpmVersion().iterator();
        if (iterator.hasNext()) {
            String upmVersion = iterator.next();
            return this.isMostRecentlyUpdatedUpmVersionLicensingAware(upmVersion);
        }
        return !this.upmUpdatedSinceLastRestart;
    }

    private Option<String> getMostRecentlyUpdatedUpmVersion() {
        Object upmVersion = this.getPluginSettings().get(MOST_RECENT_UPM_VERSION_KEY);
        if (upmVersion == null || !(upmVersion instanceof String)) {
            return Option.none(String.class);
        }
        return Option.some((String)upmVersion);
    }

    private boolean isMostRecentlyUpdatedUpmVersionLicensingAware(String upmVersion) {
        try {
            int majorVersion = Integer.parseInt(upmVersion.split("\\.")[0]);
            return majorVersion >= 2;
        }
        catch (NumberFormatException e) {
            logger.warn("Number format exception while parsing UPM version: " + upmVersion);
            return false;
        }
    }

    private PluginSettings getPluginSettings() {
        return new NamespacedPluginSettings(this.pluginSettingsFactory.createGlobalSettings(), KEY_PREFIX);
    }

    @EventListener
    public void handleUpmPluginUpgrade(PluginUpgradedEvent event) {
        if (this.pluginKey.equals(event.getPlugin().getKey())) {
            this.upmUpdatedSinceLastRestart = true;
        }
    }

    @EventListener
    public void handleApplicationStartedEvent(Object event) {
        String eventName = event.getClass().getName();
        if (eventName.equals("com.atlassian.config.lifecycle.events.ApplicationStartedEvent") || eventName.equals("com.atlassian.bamboo.event.ServerStartedEvent")) {
            this.applicationStartup();
        }
    }

    @EventListener
    public void handlePluginFrameworkStartedEvent(PluginFrameworkStartedEvent event) {
        this.applicationStartup();
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    private void applicationStartup() {
        this.upmUpdatedSinceLastRestart = false;
    }
}

