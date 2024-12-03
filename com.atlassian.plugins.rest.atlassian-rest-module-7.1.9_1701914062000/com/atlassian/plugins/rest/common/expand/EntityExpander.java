/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand;

import com.atlassian.plugins.rest.common.expand.EntityCrawler;
import com.atlassian.plugins.rest.common.expand.ExpandContext;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;

public interface EntityExpander<T> {
    public T expand(ExpandContext<T> var1, EntityExpanderResolver var2, EntityCrawler var3);
}

