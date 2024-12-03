/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.http.Router;
import com.atlassian.plugin.webresource.impl.support.http.BaseRouter;
import java.util.HashMap;

public class WebResourceUrlProviderImpl
implements WebResourceUrlProvider {
    static final String STATIC_RESOURCE_PREFIX = "s";
    static final String STATIC_RESOURCE_SUFFIX = "_";
    private final WebResourceIntegration webResourceIntegration;
    private final boolean usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins;

    public WebResourceUrlProviderImpl(WebResourceIntegration webResourceIntegration) {
        this.webResourceIntegration = webResourceIntegration;
        this.usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins = webResourceIntegration.usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins();
    }

    @Override
    public String getStaticResourcePrefix(UrlMode urlMode) {
        return BaseRouter.joinWithSlashWithoutEmpty(this.webResourceIntegration.getBaseUrl(urlMode), STATIC_RESOURCE_PREFIX, this.webResourceIntegration.getI18nStateHash(), this.webResourceIntegration.getSystemBuildNumber(), this.webResourceIntegration.getSystemCounter(), STATIC_RESOURCE_SUFFIX);
    }

    @Override
    public String getStaticResourcePrefix(String bundleHash, UrlMode urlMode) {
        return BaseRouter.joinWithSlashWithoutEmpty(this.webResourceIntegration.getBaseUrl(urlMode), STATIC_RESOURCE_PREFIX, this.webResourceIntegration.getI18nStateHash(), this.webResourceIntegration.getSystemBuildNumber(), this.webResourceIntegration.getSystemCounter(), bundleHash, STATIC_RESOURCE_SUFFIX);
    }

    @Override
    public String getStaticResourcePrefix(String contributedHash, String bundleHash, UrlMode urlMode) {
        return BaseRouter.joinWithSlashWithoutEmpty(this.webResourceIntegration.getBaseUrl(urlMode), STATIC_RESOURCE_PREFIX, contributedHash, this.webResourceIntegration.getI18nStateHash(), this.webResourceIntegration.getSystemBuildNumber(), this.webResourceIntegration.getSystemCounter(), bundleHash, STATIC_RESOURCE_SUFFIX);
    }

    @Override
    public String getStaticPluginResourceUrl(String moduleCompleteKey, String resourceName, UrlMode urlMode) {
        ModuleDescriptor moduleDescriptor = this.webResourceIntegration.getPluginAccessor().getEnabledPluginModule(moduleCompleteKey);
        if (moduleDescriptor == null) {
            return null;
        }
        return this.getStaticPluginResourceUrl(moduleDescriptor, resourceName, urlMode);
    }

    public String getStaticPluginResourceUrl(ModuleDescriptor moduleDescriptor, String resourceName, UrlMode urlMode) {
        String pluginVersion = Config.getPluginVersionOrInstallTime(moduleDescriptor.getPlugin(), this.usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins);
        String staticUrlPrefix = this.getStaticResourcePrefix(pluginVersion, urlMode);
        return staticUrlPrefix + this.getResourceUrl(moduleDescriptor.getCompleteKey(), resourceName);
    }

    @Override
    public String getResourceUrl(String moduleCompleteKey, String resourceName) {
        return "/download" + Router.resourceUrlAsStaticMethod(moduleCompleteKey, resourceName, new HashMap<String, String>());
    }

    @Override
    public String getBaseUrl() {
        return this.webResourceIntegration.getBaseUrl();
    }

    @Override
    public String getBaseUrl(UrlMode urlMode) {
        return this.webResourceIntegration.getBaseUrl(urlMode);
    }
}

