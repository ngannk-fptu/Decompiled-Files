/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.event.Evented
 *  com.atlassian.confluence.event.events.template.TemplateListViewEvent
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.templates.actions;

import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.template.TemplateListViewEvent;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugins.templates.actions.AbstractPageTemplateAction;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ListPageTemplatesAction
extends AbstractPageTemplateAction
implements Evented<TemplateListViewEvent> {
    private List<PageTemplate> pageTemplates;
    private List<PageTemplate> xhtmlPageTemplates;
    private static final String PLUGIN_KEY = "space-templates";

    public List<PageTemplate> getPageTemplates() {
        return this.pageTemplates;
    }

    public List<PageTemplate> getXHtmlPageTemplates() {
        return this.xhtmlPageTemplates;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        GeneralUtil.setCookie((String)"confluence.browse.space.cookie", (String)PLUGIN_KEY);
        this.pageTemplates = new ArrayList<PageTemplate>();
        this.xhtmlPageTemplates = new ArrayList<PageTemplate>();
        for (PageTemplate pageTemplate : this.getTemplatesToUse()) {
            if (StringUtils.isNotBlank((CharSequence)pageTemplate.getPluginKey())) continue;
            this.pageTemplates.add(pageTemplate);
            if (pageTemplate.getBodyType() != BodyType.XHTML) continue;
            this.xhtmlPageTemplates.add(pageTemplate);
        }
        return "success";
    }

    private List<PageTemplate> getTemplatesToUse() {
        if (this.getSpace() != null) {
            return this.getSpace().getPageTemplates();
        }
        return this.pageTemplateManager.getGlobalPageTemplates();
    }

    public Map<String, Object> getContext() {
        Map context = super.getContext();
        if (context == null) {
            context = Maps.newHashMap();
        }
        context.put("space", this.getSpace());
        return context;
    }

    public TemplateListViewEvent getEventToPublish(String result) {
        return new TemplateListViewEvent((Object)this, this.getSpace());
    }
}

