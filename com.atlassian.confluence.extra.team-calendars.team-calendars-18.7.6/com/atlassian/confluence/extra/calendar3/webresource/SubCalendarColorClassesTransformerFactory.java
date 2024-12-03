/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder
 *  com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer
 *  com.atlassian.plugin.webresource.transformer.WebResourceTransformerFactory
 */
package com.atlassian.confluence.extra.calendar3.webresource;

import com.atlassian.confluence.extra.calendar3.SubCalendarColorRegistry;
import com.atlassian.confluence.extra.calendar3.webresource.SubCalendarColorClassesUrlReadingWebResourceTransformer;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformerFactory;

public class SubCalendarColorClassesTransformerFactory
implements WebResourceTransformerFactory {
    private final SubCalendarColorRegistry subCalendarColorRegistry;
    private final VelocityHelperService velocityHelperService;

    public SubCalendarColorClassesTransformerFactory(SubCalendarColorRegistry subCalendarColorRegistry, VelocityHelperService velocityHelperService) {
        this.subCalendarColorRegistry = subCalendarColorRegistry;
        this.velocityHelperService = velocityHelperService;
    }

    public TransformerUrlBuilder makeUrlBuilder(TransformerParameters parameters) {
        return urlBuilder -> urlBuilder.addToHash("sub-calendar-color-registry", (Object)this.subCalendarColorRegistry.hashCode());
    }

    public UrlReadingWebResourceTransformer makeResourceTransformer(TransformerParameters parameters) {
        return new SubCalendarColorClassesUrlReadingWebResourceTransformer(this.subCalendarColorRegistry, this.velocityHelperService);
    }
}

