/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Maps
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.listeners;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.TemplatePageCreateEvent;
import com.atlassian.confluence.plugins.createcontent.actions.BlueprintManager;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContextKeys;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TemplateChildCreatorListener {
    private final BlueprintManager blueprintManager;
    private final EventPublisher eventPublisher;

    @Autowired
    public TemplateChildCreatorListener(BlueprintManager blueprintManager, @ComponentImport EventPublisher eventPublisher) {
        this.blueprintManager = blueprintManager;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onCreateEvent(TemplatePageCreateEvent event) {
        ContentTemplateRef templateRef = event.getTemplateRef();
        List<ContentTemplateRef> children = templateRef.getChildren();
        if (children.isEmpty()) {
            return;
        }
        Page parentPage = event.getPage();
        String parentTitle = parentPage.getTitle();
        Map<String, Object> originalContext = event.getContext();
        boolean addPageTitlePrefix = !"true".equals(originalContext.get(BlueprintContextKeys.NO_PAGE_TITLE_PREFIX.key()));
        for (ContentTemplateRef child : children) {
            HashMap childContext = Maps.newHashMap(originalContext);
            childContext.remove("ContentPageTitle");
            childContext.put("UsePageTemplateNameForTitle", true);
            if (addPageTitlePrefix) {
                childContext.put("ParentPageTitle", parentTitle);
            }
            this.blueprintManager.createPageFromTemplate(child, event.getUser(), parentPage.getSpace(), parentPage, childContext, event.getSaveContext());
        }
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

