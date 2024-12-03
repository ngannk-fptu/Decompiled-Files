/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.transformer.WebResourceTransformer
 *  org.dom4j.Element
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.SubCalendarColorRegistry;
import com.atlassian.confluence.extra.calendar3.webresource.SubCalendarColorClassesTransformedDownloadableResource;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformer;
import org.dom4j.Element;

public class SubCalendarColorClassesWebResourceTransformer
implements WebResourceTransformer {
    private final SubCalendarColorRegistry subCalendarColorRegistry;
    private final VelocityHelperService velocityHelperService;

    public SubCalendarColorClassesWebResourceTransformer(SubCalendarColorRegistry subCalendarColorRegistry, VelocityHelperService velocityHelperService) {
        this.subCalendarColorRegistry = subCalendarColorRegistry;
        this.velocityHelperService = velocityHelperService;
    }

    public DownloadableResource transform(Element configElement, ResourceLocation resourceLocation, String filePath, DownloadableResource nextResource) {
        return new SubCalendarColorClassesTransformedDownloadableResource(nextResource, this.subCalendarColorRegistry, this.velocityHelperService);
    }
}

