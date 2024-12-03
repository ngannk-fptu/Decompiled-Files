/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.plugin.webresource;

import com.atlassian.annotations.ExperimentalApi;
import java.util.List;

public interface ResourceBatchingConfiguration {
    public boolean isSuperBatchingEnabled();

    public List<String> getSuperBatchModuleCompleteKeys();

    public boolean isContextBatchingEnabled();

    public boolean isPluginWebResourceBatchingEnabled();

    public boolean isJavaScriptTryCatchWrappingEnabled();

    public boolean isBatchContentTrackingEnabled();

    @ExperimentalApi
    public boolean resplitMergedContextBatchesForThisRequest();

    @ExperimentalApi
    public boolean isSourceMapEnabled();

    @ExperimentalApi
    public boolean optimiseSourceMapsForDevelopment();
}

