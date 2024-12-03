/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.template.TemplateRemoveEvent
 *  com.atlassian.confluence.event.events.template.TemplateUpdateEvent
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.listeners;

import com.atlassian.confluence.event.events.template.TemplateRemoveEvent;
import com.atlassian.confluence.event.events.template.TemplateUpdateEvent;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.services.PromotedTemplateService;
import com.atlassian.confluence.plugins.createcontent.services.TemplateUpdater;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TemplateUpdatedListener {
    private final TemplateUpdater templateUpdater;
    private final PromotedTemplateService promotedTemplateService;
    private final EventPublisher eventPublisher;

    @Autowired
    public TemplateUpdatedListener(TemplateUpdater templateUpdater, PromotedTemplateService promotedTemplateService, @ComponentImport EventPublisher eventPublisher) {
        this.templateUpdater = templateUpdater;
        this.promotedTemplateService = promotedTemplateService;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onTempateUpdateEvent(TemplateUpdateEvent event) {
        PageTemplate newTemplate = event.getNewTemplate();
        PageTemplate oldTemplate = event.getOldTemplate();
        if (oldTemplate != null && oldTemplate.getId() == newTemplate.getId()) {
            return;
        }
        this.templateUpdater.updateContentTemplateRef(newTemplate);
    }

    @EventListener
    public void onTemplateRemoveEvent(TemplateRemoveEvent event) throws BlueprintIllegalArgumentException {
        PageTemplate template = event.getTemplate();
        Space space = template.getSpace();
        if (StringUtils.isBlank((CharSequence)template.getPluginKey()) && space != null) {
            this.promotedTemplateService.demoteTemplate(template.getId(), space.getKey());
        }
        this.templateUpdater.revertContentTemplateRef(template);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }
}

