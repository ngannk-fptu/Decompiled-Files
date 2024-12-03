/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gzipfilter.GzipFilter
 *  com.atlassian.gzipfilter.integration.GzipFilterIntegration
 */
package com.atlassian.confluence.util.zip;

import com.atlassian.confluence.util.zip.ConfluenceGzipFilterIntegration;
import com.atlassian.gzipfilter.GzipFilter;
import com.atlassian.gzipfilter.integration.GzipFilterIntegration;

public class ConfluenceGzipFilter
extends GzipFilter {
    public ConfluenceGzipFilter() {
        super((GzipFilterIntegration)new ConfluenceGzipFilterIntegration());
    }
}

