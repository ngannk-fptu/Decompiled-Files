/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.ISearch;

@FunctionalInterface
public interface SearchDecorator {
    public ISearch decorate(ISearch var1);
}

