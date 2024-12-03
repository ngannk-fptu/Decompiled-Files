/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.content.render.xhtml.view;

import com.atlassian.confluence.content.render.xhtml.view.RenderResult;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class BatchedRenderResult {
    private final List<RenderResult> results;

    public BatchedRenderResult(List<RenderResult> results) {
        this.results = ImmutableList.copyOf(results);
    }

    public BatchedRenderResult() {
        this.results = ImmutableList.of();
    }

    public boolean isSuccessful() {
        return this.results.stream().allMatch(RenderResult::isSuccessful);
    }

    public List<RenderResult> getResults() {
        return this.results;
    }
}

