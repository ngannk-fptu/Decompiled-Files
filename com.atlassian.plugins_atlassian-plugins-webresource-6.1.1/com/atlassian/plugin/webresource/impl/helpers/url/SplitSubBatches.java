/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.helpers.url;

import com.atlassian.plugin.webresource.impl.helpers.url.SubBatch;
import com.atlassian.plugin.webresource.impl.helpers.url.WebResourceBatch;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import java.util.List;

class SplitSubBatches {
    private List<SubBatch> contextSubBatches;
    private List<Resource> contextStandaloneResources;
    private List<WebResourceBatch> legacyWebResources;
    private boolean isAdditionalSortingRequired;

    SplitSubBatches() {
    }

    public List<SubBatch> getContextSubBatches() {
        return this.contextSubBatches;
    }

    public void setContextSubBatches(List<SubBatch> contextSubBatches) {
        this.contextSubBatches = contextSubBatches;
    }

    public List<Resource> getContextStandaloneResources() {
        return this.contextStandaloneResources;
    }

    public void setContextStandaloneResources(List<Resource> contextStandaloneResources) {
        this.contextStandaloneResources = contextStandaloneResources;
    }

    public List<WebResourceBatch> getLegacyWebResources() {
        return this.legacyWebResources;
    }

    public void setLegacyWebResources(List<WebResourceBatch> legacyWebResources) {
        this.legacyWebResources = legacyWebResources;
    }

    public boolean isAdditionalSortingRequired() {
        return this.isAdditionalSortingRequired;
    }

    public void setAdditionalSortingRequired(boolean additionalSortingRequired) {
        this.isAdditionalSortingRequired = additionalSortingRequired;
    }
}

