/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.DownloadableResource
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.transformer.TransformableResource;

public interface UrlReadingWebResourceTransformer {
    public DownloadableResource transform(TransformableResource var1, QueryParams var2);
}

