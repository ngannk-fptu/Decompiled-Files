/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.webresource.Flags;
import com.atlassian.plugin.webresource.ResourceBatchingConfiguration;
import java.util.Collections;
import java.util.List;

public class DefaultResourceBatchingConfiguration
implements ResourceBatchingConfiguration {
    public static final String PLUGIN_WEBRESOURCE_BATCHING_OFF = "plugin.webresource.batching.off";
    public static final String PLUGIN_WEB_RESOURCE_BATCH_CONTENT_TRACKING = "plugin.webresource.batch.content.tracking";
    public static final String PLUGIN_WEB_RESOURCE_JAVASCRIPT_TRY_CATCH_WRAPPING = "plugin.webresource.javascript.try.catch.wrapping";
    public static final String PLUGIN_WEB_RESOURCE_SOURCE_MAP_ENABLED = "plugin.webresource.source.map.enabled";
    public static final String PLUGIN_WEB_RESOURCE_SOURCE_MAP_OPTIMISED_FOR_DEVELOPMENT = "plugin.webresource.source.map.optimised.for.development";

    @Override
    public boolean isSuperBatchingEnabled() {
        return false;
    }

    @Override
    public List<String> getSuperBatchModuleCompleteKeys() {
        return Collections.emptyList();
    }

    @Override
    public boolean isContextBatchingEnabled() {
        return false;
    }

    @Override
    public boolean isPluginWebResourceBatchingEnabled() {
        String explicitSetting = System.getProperty(PLUGIN_WEBRESOURCE_BATCHING_OFF);
        if (explicitSetting != null) {
            return !Boolean.parseBoolean(explicitSetting);
        }
        return !Flags.isDevMode();
    }

    @Override
    public boolean isJavaScriptTryCatchWrappingEnabled() {
        String javaScriptTryCatchWrapping = System.getProperty(PLUGIN_WEB_RESOURCE_JAVASCRIPT_TRY_CATCH_WRAPPING);
        return javaScriptTryCatchWrapping != null && Boolean.parseBoolean(javaScriptTryCatchWrapping);
    }

    @Override
    public boolean isBatchContentTrackingEnabled() {
        String trackingSetting = System.getProperty(PLUGIN_WEB_RESOURCE_BATCH_CONTENT_TRACKING);
        return trackingSetting != null && Boolean.parseBoolean(trackingSetting);
    }

    @Override
    public boolean resplitMergedContextBatchesForThisRequest() {
        return false;
    }

    @Override
    public boolean isSourceMapEnabled() {
        return Boolean.parseBoolean(System.getProperty(PLUGIN_WEB_RESOURCE_SOURCE_MAP_ENABLED));
    }

    @Override
    public boolean optimiseSourceMapsForDevelopment() {
        return Boolean.parseBoolean(System.getProperty(PLUGIN_WEB_RESOURCE_SOURCE_MAP_OPTIMISED_FOR_DEVELOPMENT));
    }
}

