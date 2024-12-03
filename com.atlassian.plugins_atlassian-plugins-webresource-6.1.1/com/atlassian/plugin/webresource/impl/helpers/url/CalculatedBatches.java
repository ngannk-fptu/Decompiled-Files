/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.helpers.url;

import com.atlassian.plugin.webresource.impl.helpers.url.ContextBatch;
import com.atlassian.plugin.webresource.impl.helpers.url.WebResourceBatch;
import java.util.List;
import java.util.Set;

public class CalculatedBatches {
    private final List<ContextBatch> contextBatches;
    private final List<WebResourceBatch> webResourceBatches;
    private final Set<String> excludedResolved;

    public CalculatedBatches(List<ContextBatch> contextBatches, List<WebResourceBatch> webResourceBatches, Set<String> excludedResolved) {
        this.contextBatches = contextBatches;
        this.webResourceBatches = webResourceBatches;
        this.excludedResolved = excludedResolved;
    }

    public List<ContextBatch> getContextBatches() {
        return this.contextBatches;
    }

    public List<WebResourceBatch> getWebResourceBatches() {
        return this.webResourceBatches;
    }

    public Set<String> getExcludedResolved() {
        return this.excludedResolved;
    }
}

