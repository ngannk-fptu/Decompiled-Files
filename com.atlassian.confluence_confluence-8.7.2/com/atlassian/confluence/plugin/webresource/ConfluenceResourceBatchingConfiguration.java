/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.ResourceBatchingConfiguration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.util.UserAgentUtil;
import com.atlassian.plugin.webresource.ResourceBatchingConfiguration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceResourceBatchingConfiguration
implements ResourceBatchingConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceResourceBatchingConfiguration.class);
    private final List<String> superBatchModuleCompleteKeys;
    private ResourceBatchingConfiguration delegate;

    public ConfluenceResourceBatchingConfiguration(List<String> superBatchModuleCompleteKeys, ResourceBatchingConfiguration delegate) {
        this.superBatchModuleCompleteKeys = Collections.unmodifiableList(new ArrayList<String>(superBatchModuleCompleteKeys));
        this.delegate = delegate;
    }

    public boolean isSuperBatchingEnabled() {
        if (UserAgentUtil.isBrowserFamily(UserAgentUtil.BrowserFamily.MSIE)) {
            log.debug("IE detected. Forcing batching to prevent rendering errors");
            return true;
        }
        return !this.superBatchModuleCompleteKeys.isEmpty();
    }

    public List<String> getSuperBatchModuleCompleteKeys() {
        return this.superBatchModuleCompleteKeys;
    }

    public boolean isContextBatchingEnabled() {
        if (UserAgentUtil.isBrowserFamily(UserAgentUtil.BrowserFamily.MSIE)) {
            log.debug("IE detected. Forcing batching to prevent rendering errors");
            return true;
        }
        return !ConfluenceSystemProperties.isContextBatchingDisabled();
    }

    public boolean isPluginWebResourceBatchingEnabled() {
        if (UserAgentUtil.isBrowserFamily(UserAgentUtil.BrowserFamily.MSIE)) {
            log.debug("IE detected. Forcing batching to prevent rendering errors");
            return true;
        }
        return this.delegate.isPluginWebResourceBatchingEnabled();
    }

    public boolean isBatchContentTrackingEnabled() {
        return this.delegate.isBatchContentTrackingEnabled();
    }

    public boolean isJavaScriptTryCatchWrappingEnabled() {
        return this.delegate.isJavaScriptTryCatchWrappingEnabled();
    }

    public boolean resplitMergedContextBatchesForThisRequest() {
        return this.delegate.resplitMergedContextBatchesForThisRequest();
    }

    public boolean isSourceMapEnabled() {
        return this.delegate.isSourceMapEnabled();
    }

    public boolean optimiseSourceMapsForDevelopment() {
        return this.delegate.optimiseSourceMapsForDevelopment();
    }

    static {
        if (!ConfluenceSystemProperties.isDevMode()) {
            log.info("Enabling javascript try catch wrapping");
            System.setProperty("plugin.webresource.javascript.try.catch.wrapping", "true");
        }
    }
}

