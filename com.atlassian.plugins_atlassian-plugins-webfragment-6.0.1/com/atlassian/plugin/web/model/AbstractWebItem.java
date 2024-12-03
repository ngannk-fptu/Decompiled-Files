/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.WebFragmentHelper
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 */
package com.atlassian.plugin.web.model;

import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import java.util.Collections;
import java.util.Map;

public abstract class AbstractWebItem {
    private WebFragmentHelper webFragmentHelper;
    private ContextProvider contextProvider;
    private final WebFragmentModuleDescriptor descriptor;

    protected AbstractWebItem(WebFragmentHelper webFragmentHelper, ContextProvider contextProvider, WebFragmentModuleDescriptor descriptor) {
        this.webFragmentHelper = webFragmentHelper;
        this.contextProvider = contextProvider;
        this.descriptor = descriptor;
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        if (this.contextProvider != null) {
            return this.contextProvider.getContextMap(context);
        }
        return Collections.EMPTY_MAP;
    }

    public WebFragmentHelper getWebFragmentHelper() {
        return this.webFragmentHelper;
    }

    public WebFragmentModuleDescriptor getDescriptor() {
        return this.descriptor;
    }
}

