/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.spaces.listeners;

import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.event.events.space.SpaceCreateEvent;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.templates.variables.Variable;
import com.atlassian.confluence.spaces.SystemTemplateManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.PluginAccessor;
import java.util.List;

public abstract class AbstractSpaceContentListener {
    protected final FormatConverter formatConverter;
    protected final I18NBeanFactory i18NBeanFactory;
    protected final LocaleManager localeManager;
    protected final PageManager pageManager;
    protected final SystemTemplateManager systemTemplateManager;
    protected final PluginAccessor pluginAccessor;
    protected final XhtmlContent xhtmlContent;

    protected AbstractSpaceContentListener(FormatConverter formatConverter, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, PageManager pageManager, SystemTemplateManager systemTemplateManager, PluginAccessor pluginAccessor, XhtmlContent xhtmlContent) {
        this.formatConverter = formatConverter;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.pageManager = pageManager;
        this.systemTemplateManager = systemTemplateManager;
        this.pluginAccessor = pluginAccessor;
        this.xhtmlContent = xhtmlContent;
    }

    @EventListener
    public void handleEvent(SpaceCreateEvent event) {
        this.handleSpaceCreate(event);
    }

    protected abstract void handleSpaceCreate(SpaceCreateEvent var1);

    protected BodyContent getDefaultHomePageContent(Page homePage, List<Variable> variables, String templateKey) {
        BodyContent bodyContent = new BodyContent();
        bodyContent.setBodyType(BodyType.XHTML);
        String content = this.systemTemplateManager.getTemplate(templateKey, variables, homePage);
        bodyContent.setBody(content);
        return bodyContent;
    }
}

