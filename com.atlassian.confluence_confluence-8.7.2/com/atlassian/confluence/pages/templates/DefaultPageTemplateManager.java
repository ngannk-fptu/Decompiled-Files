/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventManager
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.templates;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.WikiToXhtmlMigrator;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.event.events.template.TemplateRemoveEvent;
import com.atlassian.confluence.event.events.template.TemplateUpdateEvent;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.pages.templates.PluginTemplateReference;
import com.atlassian.confluence.pages.templates.TemplateHandler;
import com.atlassian.confluence.pages.templates.persistence.dao.PageTemplateDao;
import com.atlassian.confluence.renderer.PageTemplateContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.Event;
import com.atlassian.event.EventManager;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class DefaultPageTemplateManager
implements PageTemplateManager {
    private EventManager eventManager;
    private PageTemplateDao pageTemplateDao;
    private WikiToXhtmlMigrator wikiToXhtmlMigrator;
    private Map<BodyType, TemplateHandler> templateHandlers;

    public void setPageTemplateDao(PageTemplateDao pageTemplateDao) {
        this.pageTemplateDao = pageTemplateDao;
    }

    public void setWikiToXhtmlMigrator(WikiToXhtmlMigrator wikiToXhtmlMigrator) {
        this.wikiToXhtmlMigrator = wikiToXhtmlMigrator;
    }

    public void setTemplateHandlers(Map<BodyType, TemplateHandler> templateHandlers) {
        this.templateHandlers = templateHandlers;
    }

    @Override
    public void savePageTemplate(PageTemplate pageTemplate, PageTemplate originalTemplate) {
        if (pageTemplate.getBodyType() == BodyType.WIKI) {
            pageTemplate.setContent(this.wikiToXhtmlMigrator.migrate(pageTemplate.getContent(), new PageTemplateContext(), new ArrayList<RuntimeException>()));
            pageTemplate.setBodyType(BodyType.XHTML);
        }
        PageTemplate originalTemplateForEvent = originalTemplate != null ? new PageTemplate(originalTemplate) : null;
        this.pageTemplateDao.save(pageTemplate, originalTemplate);
        this.eventManager.publishEvent((Event)new TemplateUpdateEvent(this, originalTemplateForEvent, pageTemplate));
    }

    @Override
    public void refreshPageTemplate(PageTemplate pageTemplate) {
        this.pageTemplateDao.refresh(pageTemplate);
    }

    @Override
    public void removePageTemplate(PageTemplate page) {
        this.removePreviousVersions(page);
        if (page.getSpace() != null) {
            page.getSpace().getPageTemplates().remove(page);
        }
        this.pageTemplateDao.remove(page);
        this.eventManager.publishEvent((Event)new TemplateRemoveEvent(this, page));
    }

    private void removePreviousVersions(PageTemplate template) {
        ArrayList<PageTemplate> templates = new ArrayList<PageTemplate>();
        List list = this.findPreviousVersions(template);
        for (PageTemplate t : list) {
            templates.add(t);
            this.pageTemplateDao.remove(t);
        }
        if (template.getSpace() != null) {
            template.getSpace().getPageTemplates().removeAll(templates);
        }
    }

    private List findPreviousVersions(PageTemplate template) {
        return this.pageTemplateDao.findPreviousVersions(template.getId());
    }

    @Override
    public PageTemplate getPageTemplate(long id) {
        return this.pageTemplateDao.getById(id);
    }

    @Override
    public PageTemplate getPageTemplate(PluginTemplateReference pluginTemplateReference) {
        return this.pageTemplateDao.findCustomisedPluginTemplate(pluginTemplateReference);
    }

    @Override
    public PageTemplate getPageTemplate(String name, Space space) {
        return space == null ? this.pageTemplateDao.findPageTemplateByName(name) : this.pageTemplateDao.findPageTemplateByNameAndSpace(name, space);
    }

    @Override
    public List getPageTemplates(Space space) {
        ArrayList result = new ArrayList();
        if (space != null) {
            result.addAll(space.getPageTemplates());
        }
        result.addAll(this.getGlobalPageTemplates());
        return result;
    }

    @Override
    public List getGlobalPageTemplates() {
        return this.pageTemplateDao.findAllGlobalPageTemplates();
    }

    @Override
    public PageTemplate getGlobalPageTemplate(String name) {
        return this.pageTemplateDao.findPageTemplateByName(name);
    }

    @Override
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void removeAllPageTemplates(Space space) {
        Iterator i = space.getPageTemplates().iterator();
        while (i.hasNext()) {
            PageTemplate t = (PageTemplate)i.next();
            i.remove();
            this.removePageTemplate(t);
        }
    }

    @Override
    public List getTemplateVariables(PageTemplate template) throws XhtmlException {
        TemplateHandler templateHandler = this.getTemplateHandler(template);
        return templateHandler == null ? null : templateHandler.getTemplateVariables(template);
    }

    @Override
    public String mergeVariables(PageTemplate template, List variables, String spaceKey) throws XhtmlException {
        TemplateHandler templateHandler = this.getTemplateHandler(template);
        return templateHandler == null ? null : templateHandler.generateEditorFormat(template, variables, spaceKey);
    }

    @Override
    public String insertVariables(PageTemplate template, List variables) {
        TemplateHandler templateHandler = this.getTemplateHandler(template);
        return templateHandler == null ? null : templateHandler.insertVariables(new StringReader(template.getContent()), variables);
    }

    @Override
    public boolean canCreate(PageTemplate template, PageTemplate foundTemplate) {
        if (foundTemplate == null || StringUtils.isNotBlank((CharSequence)foundTemplate.getPluginKey())) {
            return false;
        }
        if (template == null) {
            return true;
        }
        if (StringUtils.isNotBlank((CharSequence)template.getPluginKey())) {
            return false;
        }
        return foundTemplate.getId() != template.getId();
    }

    private TemplateHandler getTemplateHandler(PageTemplate pageTemplate) {
        return pageTemplate == null ? null : this.templateHandlers.get(pageTemplate.getBodyType());
    }
}

