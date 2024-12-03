/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.EventManager
 */
package com.atlassian.confluence.pages.templates;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PluginTemplateReference;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.EventManager;
import java.util.List;

public interface PageTemplateManager {
    public void savePageTemplate(PageTemplate var1, PageTemplate var2);

    public void refreshPageTemplate(PageTemplate var1);

    public PageTemplate getPageTemplate(long var1);

    public PageTemplate getPageTemplate(PluginTemplateReference var1);

    public PageTemplate getPageTemplate(String var1, Space var2);

    public List getPageTemplates(Space var1);

    public void removePageTemplate(PageTemplate var1);

    public List getTemplateVariables(PageTemplate var1) throws XhtmlException;

    public String mergeVariables(PageTemplate var1, List var2, String var3) throws XhtmlException;

    public String insertVariables(PageTemplate var1, List var2);

    public boolean canCreate(PageTemplate var1, PageTemplate var2);

    public List getGlobalPageTemplates();

    public PageTemplate getGlobalPageTemplate(String var1);

    public void setEventManager(EventManager var1);

    public void removeAllPageTemplates(Space var1);
}

