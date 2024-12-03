/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PluginTemplateReference
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugins.createcontent.template;

import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PluginTemplateReference;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.impl.I18nPageTemplate;
import com.atlassian.confluence.spaces.Space;
import java.util.List;

public interface PluginPageTemplateHelper {
    public PageTemplate getPageTemplate(PluginTemplateReference var1);

    public PageTemplate getPageTemplate(ContentTemplateRef var1);

    public List<PageTemplate> getPageTemplates(Space var1);

    public List<I18nPageTemplate> getSystemPageTemplates();
}

