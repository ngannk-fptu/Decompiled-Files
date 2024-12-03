/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.PageRequest
 *  com.atlassian.diagnostics.PageSummary
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.PageRequest;
import com.atlassian.diagnostics.PageSummary;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public class SimplePageSummary
implements PageSummary {
    private final PageRequest nextRequest;
    private final PageRequest prevRequest;
    private final int size;

    public SimplePageSummary(PageRequest prevRequest, PageRequest nextRequest, int size) {
        this.prevRequest = prevRequest;
        this.nextRequest = nextRequest;
        this.size = size;
    }

    public SimplePageSummary(@Nonnull PageRequest pageRequest, int size) {
        Objects.requireNonNull(pageRequest, "pageRequest");
        this.size = Math.min(size, pageRequest.getLimit());
        int start = pageRequest.getStart();
        int limit = pageRequest.getLimit();
        this.prevRequest = start > 0 ? PageRequest.of((int)Math.max(0, start - limit), (int)limit) : null;
        this.nextRequest = size > limit ? PageRequest.of((int)(start + limit), (int)limit) : null;
    }

    @Nonnull
    public Optional<PageRequest> getNextRequest() {
        return Optional.ofNullable(this.nextRequest);
    }

    @Nonnull
    public Optional<PageRequest> getPrevRequest() {
        return Optional.ofNullable(this.prevRequest);
    }

    public int size() {
        return this.size;
    }
}

