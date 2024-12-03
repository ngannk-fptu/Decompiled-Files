/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.PageReader;
import com.atlassian.marketplace.client.api.PageReference;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.util.Convert;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.util.Optional;

final class PageImpl<T>
extends Page<T> {
    public static final String NEXT_REL = "next";
    public static final String PREVIOUS_REL = "prev";
    private final PageReference<T> reference;
    private final Optional<URI> previousUri;
    private final Optional<URI> nextUri;

    PageImpl(PageReference<T> reference, Links links, Iterable<T> items, int count, PageReader<T> reader) {
        super(items, count, reader);
        this.reference = (PageReference)Preconditions.checkNotNull(reference, (Object)"reference");
        Preconditions.checkNotNull((Object)links, (Object)"links");
        this.previousUri = Convert.toOptional(links.getUri(PREVIOUS_REL));
        this.nextUri = Convert.toOptional(links.getUri(NEXT_REL));
    }

    @Override
    public Optional<PageReference<T>> safeGetReference() {
        return Optional.of(this.reference);
    }

    @Override
    public Optional<PageReference<T>> safeGetPrevious() {
        return this.previousUri.map(p -> new PageReference((URI)p, QueryBounds.offset(this.getBounds().getOffset() - this.getBounds().safeGetLimit().orElseGet(this::size)).withLimit(Optional.of(this.getBounds().safeGetLimit().orElseGet(this::size))), this.reader));
    }

    @Override
    public Optional<PageReference<T>> safeGetNext() {
        return this.nextUri.map(n -> new PageReference((URI)n, QueryBounds.offset(this.getBounds().getOffset() + this.getBounds().safeGetLimit().orElseGet(this::size)).withLimit(Optional.of(this.getBounds().safeGetLimit().orElseGet(this::size))), this.reader));
    }

    private QueryBounds getBounds() {
        return this.reference.getBounds();
    }
}

