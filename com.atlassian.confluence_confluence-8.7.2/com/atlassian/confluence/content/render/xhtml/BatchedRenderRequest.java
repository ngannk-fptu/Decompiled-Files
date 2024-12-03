/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class BatchedRenderRequest {
    private final ConversionContext context;
    private final List<String> storageFragments;

    public BatchedRenderRequest(ConversionContext context, List<String> storageFragments) {
        this.context = context;
        this.storageFragments = ImmutableList.copyOf(storageFragments);
    }

    public ConversionContext getContext() {
        return this.context;
    }

    public List<String> getStorageFragments() {
        return this.storageFragments;
    }
}

