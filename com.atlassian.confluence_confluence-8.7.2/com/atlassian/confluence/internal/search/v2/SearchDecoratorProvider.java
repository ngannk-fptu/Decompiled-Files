/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.v2;

import com.atlassian.confluence.search.v2.SearchDecorator;

@FunctionalInterface
public interface SearchDecoratorProvider {
    public Iterable<SearchDecorator> get();
}

