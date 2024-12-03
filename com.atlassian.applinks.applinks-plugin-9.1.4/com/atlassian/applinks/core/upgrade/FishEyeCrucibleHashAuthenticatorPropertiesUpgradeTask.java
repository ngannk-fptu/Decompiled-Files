/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.upgrade;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.core.property.ApplicationLinkProperties;
import com.atlassian.applinks.core.property.PropertyService;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.FecruOnly;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(value={FecruOnly.class})
public class FishEyeCrucibleHashAuthenticatorPropertiesUpgradeTask
implements PluginUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(FishEyeCrucibleHashAuthenticatorPropertiesUpgradeTask.class);
    private final PropertyService propertyService;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ApplicationLinkService applicationLinkService;
    private final I18nResolver i18nResolver;

    @Autowired
    public FishEyeCrucibleHashAuthenticatorPropertiesUpgradeTask(PropertyService propertyService, PluginSettingsFactory pluginSettingsFactory, ApplicationLinkService applicationLinkService, I18nResolver i18nResolver) {
        this.propertyService = propertyService;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.applicationLinkService = applicationLinkService;
        this.i18nResolver = i18nResolver;
    }

    public int getBuildNumber() {
        return 10;
    }

    public String getShortDescription() {
        return "Hash Authentication Provider config properties";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Collection<Message> doUpgrade() throws Exception {
        PluginSettings pluginSettings = this.pluginSettingsFactory.createGlobalSettings();
        for (ApplicationLink applicationLink : this.applicationLinkService.getApplicationLinks()) {
            ApplicationLinkProperties props = this.propertyService.getApplicationLinkProperties(applicationLink.getId());
            for (String originalProviderKey : props.getProviderKeys()) {
                String oldPropertyKey;
                block13: {
                    oldPropertyKey = this.formatDeprecatedKey(applicationLink.getId(), originalProviderKey);
                    Map providerConfig = (Map)pluginSettings.get(oldPropertyKey);
                    if (providerConfig != null) {
                        props.setProviderConfig(originalProviderKey, providerConfig);
                        break block13;
                    }
                    log.warn(this.i18nResolver.getText("applinks.upgrade.warn.no.auth.provider.config.found", new Serializable[]{originalProviderKey, applicationLink.toString()}));
                }
                try {
                    pluginSettings.remove(oldPropertyKey);
                }
                catch (Exception e) {
                    log.error(this.i18nResolver.getText("applinks.upgrade.warn.upgrade.auth.provider.delete.failed", new Serializable[]{originalProviderKey, applicationLink.toString()}), (Throwable)e);
                }
                continue;
                catch (Exception e) {
                    try {
                        log.error(this.i18nResolver.getText("applinks.upgrade.warn.upgrade.auth.provider.hash.failed", new Serializable[]{originalProviderKey, applicationLink.toString()}), (Throwable)e);
                    }
                    catch (Throwable throwable) {
                        try {
                            pluginSettings.remove(oldPropertyKey);
                        }
                        catch (Exception e2) {
                            log.error(this.i18nResolver.getText("applinks.upgrade.warn.upgrade.auth.provider.delete.failed", new Serializable[]{originalProviderKey, applicationLink.toString()}), (Throwable)e2);
                        }
                        throw throwable;
                    }
                    try {
                        pluginSettings.remove(oldPropertyKey);
                    }
                    catch (Exception e3) {
                        log.error(this.i18nResolver.getText("applinks.upgrade.warn.upgrade.auth.provider.delete.failed", new Serializable[]{originalProviderKey, applicationLink.toString()}), (Throwable)e3);
                    }
                }
            }
        }
        return Lists.newArrayList();
    }

    private String formatDeprecatedKey(ApplicationId id, String originalProviderKey) {
        return String.format("applinks.admin.%s.auth.%s", id.toString(), originalProviderKey);
    }

    public String getPluginKey() {
        return "com.atlassian.applinks.applinks-plugin";
    }
}

