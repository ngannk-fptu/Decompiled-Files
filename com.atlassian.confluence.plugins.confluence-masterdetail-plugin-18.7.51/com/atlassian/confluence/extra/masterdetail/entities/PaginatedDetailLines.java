/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.masterdetail.entities;

import com.atlassian.confluence.extra.masterdetail.RenderError;
import com.atlassian.confluence.extra.masterdetail.entities.DetailLine;
import java.util.Collections;
import java.util.List;

public class PaginatedDetailLines {
    private final List<String> renderedHeadings;
    private List<DetailLine> detailLines;
    private final boolean asyncRenderSafe;
    private RenderError error = null;

    public PaginatedDetailLines(List<String> renderedHeadings, List<DetailLine> detailLines, boolean asyncRenderSafe) {
        this.renderedHeadings = renderedHeadings;
        this.detailLines = detailLines;
        this.asyncRenderSafe = asyncRenderSafe;
    }

    public PaginatedDetailLines(List<String> renderedHeadings, List<DetailLine> detailLines, boolean asyncRenderSafe, RenderError error) {
        this.renderedHeadings = renderedHeadings;
        this.detailLines = detailLines;
        this.asyncRenderSafe = asyncRenderSafe;
        this.error = error;
    }

    public List<DetailLine> getDetailLines() {
        return this.detailLines;
    }

    public List<String> getRenderedHeadings() {
        return this.renderedHeadings;
    }

    public boolean isAsyncRenderSafe() {
        return this.asyncRenderSafe;
    }

    public boolean hasError() {
        return this.error != null;
    }

    public RenderError getError() {
        return this.error;
    }

    public static PaginatedDetailLines empty() {
        return new PaginatedDetailLines(Collections.emptyList(), Collections.emptyList(), true);
    }
}

