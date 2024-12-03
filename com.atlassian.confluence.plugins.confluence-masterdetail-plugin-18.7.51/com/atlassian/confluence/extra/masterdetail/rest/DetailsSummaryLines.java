/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.extra.masterdetail.rest;

import com.atlassian.confluence.extra.masterdetail.entities.DetailLine;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class DetailsSummaryLines {
    @JsonProperty
    private final int currentPage;
    @JsonProperty
    private final int totalPages;
    @JsonProperty
    private final List<String> renderedHeadings;
    @JsonProperty
    private final List<DetailLine> detailLines;
    @JsonProperty
    private final boolean asyncRenderSafe;

    @JsonCreator
    private DetailsSummaryLines() {
        this(0, 0, null, null, true);
    }

    public DetailsSummaryLines(int currentPage, int totalPages, List<String> renderedHeadings, List<DetailLine> detailLines, boolean asyncRenderSafe) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.renderedHeadings = renderedHeadings;
        this.detailLines = detailLines;
        this.asyncRenderSafe = asyncRenderSafe;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public List<String> getRenderedHeadings() {
        return this.renderedHeadings;
    }

    public List<DetailLine> getDetailLines() {
        return this.detailLines;
    }
}

