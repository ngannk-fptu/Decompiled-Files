/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.service.content.template.ContentTemplateService
 *  com.atlassian.confluence.api.service.content.template.ContentTemplateService$TemplateFinder
 */
package com.atlassian.confluence.plugins.createcontent.factory;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.service.content.template.ContentTemplateService;

public interface FinderFactory {
    public ContentTemplateService.TemplateFinder createFinder(ContentTemplateService var1, Expansion ... var2);
}

