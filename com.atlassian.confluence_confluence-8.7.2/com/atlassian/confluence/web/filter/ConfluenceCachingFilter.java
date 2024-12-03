/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.cache.AbstractCachingFilter
 *  com.atlassian.core.filters.cache.CachingStrategy
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.confluence.web.filter.AttachmentCachingStrategies;
import com.atlassian.confluence.web.filter.PreventCachingStrategy;
import com.atlassian.confluence.web.filter.RssCachingStrategy;
import com.atlassian.core.filters.cache.AbstractCachingFilter;
import com.atlassian.core.filters.cache.CachingStrategy;

public class ConfluenceCachingFilter
extends AbstractCachingFilter {
    private static final CachingStrategy[] STRATEGIES = new CachingStrategy[]{new RssCachingStrategy(), new AttachmentCachingStrategies.SpecificVersionCachingStrategy(), new AttachmentCachingStrategies.InternetExplorerSslCachingStrategy(), new AttachmentCachingStrategies.DefaultCachingStrategy(), new PreventCachingStrategy()};

    protected CachingStrategy[] getCachingStrategies() {
        return STRATEGIES;
    }
}

