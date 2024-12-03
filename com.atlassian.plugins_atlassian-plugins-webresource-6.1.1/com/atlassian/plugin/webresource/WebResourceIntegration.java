/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.webresource.api.assembler.resource.CompleteWebResourceKey
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.webresource.BigPipeConfiguration;
import com.atlassian.plugin.webresource.DefaultBigPipeConfiguration;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.cdn.CDNStrategy;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.webresource.api.assembler.resource.CompleteWebResourceKey;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public interface WebResourceIntegration {
    public PluginAccessor getPluginAccessor();

    @Deprecated
    public PluginEventManager getPluginEventManager();

    public Map<String, Object> getRequestCache();

    @Deprecated
    default public String getSystemCounter() {
        return this.getResourceUrlPrefix();
    }

    default public String getResourceUrlPrefix() {
        return this.getSystemCounter();
    }

    default public void rebuildResourceUrlPrefix() {
    }

    public String getSystemBuildNumber();

    public String getHostApplicationVersion();

    public String getBaseUrl();

    public String getBaseUrl(UrlMode var1);

    public String getSuperBatchVersion();

    @Deprecated
    public String getStaticResourceLocale();

    public String getI18nStateHash();

    public File getTemporaryDirectory();

    public CDNStrategy getCDNStrategy();

    public Locale getLocale();

    public Iterable<Locale> getSupportedLocales();

    public String getI18nRawText(Locale var1, String var2);

    public String getI18nText(Locale var1, String var2);

    public Set<String> allowedCondition1Keys();

    public Set<String> allowedTransform1Keys();

    default public boolean forbidCondition1AndTransformer1() {
        return false;
    }

    default public boolean isIncrementalCacheEnabled() {
        return false;
    }

    @Deprecated
    default public boolean isDeferJsAttributeEnabled() {
        return false;
    }

    default public BigPipeConfiguration getBigPipeConfiguration() {
        return new DefaultBigPipeConfiguration();
    }

    @ExperimentalApi
    default public List<CompleteWebResourceKey> getSyncWebResourceKeys() {
        return new ArrayList<CompleteWebResourceKey>();
    }

    default public boolean usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins() {
        return false;
    }

    default public boolean isCtCdnMappingEnabled() {
        return false;
    }

    default public boolean isCompiledResourceEnabled() {
        return false;
    }

    @Nonnull
    public EventPublisher getEventPublisher();

    @Nonnull
    public DarkFeatureManager getDarkFeatureManager();
}

