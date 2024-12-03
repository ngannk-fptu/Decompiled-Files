/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource
 */
package com.atlassian.confluence.extra.calendar3.webresource;

import com.atlassian.confluence.extra.calendar3.SubCalendarColorRegistry;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource;
import java.util.HashMap;

public class SubCalendarColorClassesTransformedDownloadableResource
extends CharSequenceDownloadableResource {
    private final SubCalendarColorRegistry subCalendarColorRegistry;
    private final VelocityHelperService velocityHelperService;

    public SubCalendarColorClassesTransformedDownloadableResource(DownloadableResource originalResource, SubCalendarColorRegistry subCalendarColorRegistry, VelocityHelperService velocityHelperService) {
        super(originalResource);
        this.subCalendarColorRegistry = subCalendarColorRegistry;
        this.velocityHelperService = velocityHelperService;
    }

    protected CharSequence transform(CharSequence original) {
        HashMap<String, SubCalendarColorRegistry> velocityContext = new HashMap<String, SubCalendarColorRegistry>();
        velocityContext.put("subCalendarColorRegistry", this.subCalendarColorRegistry);
        return this.velocityHelperService.getRenderedContent(original.toString(), velocityContext);
    }
}

