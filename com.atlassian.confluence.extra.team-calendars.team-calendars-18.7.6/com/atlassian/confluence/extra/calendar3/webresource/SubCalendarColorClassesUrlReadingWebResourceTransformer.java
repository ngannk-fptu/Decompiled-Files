/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.transformer.TransformableResource
 *  com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer
 */
package com.atlassian.confluence.extra.calendar3.webresource;

import com.atlassian.confluence.extra.calendar3.SubCalendarColorRegistry;
import com.atlassian.confluence.extra.calendar3.webresource.SubCalendarColorClassesTransformedDownloadableResource;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.transformer.TransformableResource;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer;

public class SubCalendarColorClassesUrlReadingWebResourceTransformer
implements UrlReadingWebResourceTransformer {
    private final SubCalendarColorRegistry subCalendarColorRegistry;
    private final VelocityHelperService velocityHelperService;

    public SubCalendarColorClassesUrlReadingWebResourceTransformer(SubCalendarColorRegistry subCalendarColorRegistry, VelocityHelperService velocityHelperService) {
        this.subCalendarColorRegistry = subCalendarColorRegistry;
        this.velocityHelperService = velocityHelperService;
    }

    public DownloadableResource transform(TransformableResource transformableResource, QueryParams params) {
        return new SubCalendarColorClassesTransformedDownloadableResource(transformableResource.nextResource(), this.subCalendarColorRegistry, this.velocityHelperService);
    }
}

