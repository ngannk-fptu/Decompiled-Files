/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintCreateEvent
 *  com.atlassian.confluence.plugins.ia.SidebarLinkCategory
 *  com.atlassian.confluence.plugins.ia.rest.SidebarLinkBean
 *  com.atlassian.confluence.plugins.ia.service.SidebarLinkService
 *  com.atlassian.confluence.plugins.ia.service.SidebarService
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.documentation;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintCreateEvent;
import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import com.atlassian.confluence.plugins.ia.rest.SidebarLinkBean;
import com.atlassian.confluence.plugins.ia.service.SidebarLinkService;
import com.atlassian.confluence.plugins.ia.service.SidebarService;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateSpaceEventListener {
    private static final String DOCUMENTATION_SPACE_COMPLETE_KEY = "com.atlassian.confluence.plugins.confluence-space-blueprints:documentation-space-blueprint";
    private final SidebarService sidebarService;
    private final SidebarLinkService sidebarLinkService;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final EventPublisher eventPublisher;

    @Autowired
    public CreateSpaceEventListener(@ComponentImport SidebarService sidebarService, @ComponentImport SidebarLinkService sidebarLinkService, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @ComponentImport EventPublisher eventPublisher) {
        this.sidebarService = sidebarService;
        this.sidebarLinkService = sidebarLinkService;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void initialise() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onSpaceBlueprintCreate(SpaceBlueprintCreateEvent event) throws NotPermittedException {
        if (!event.getSpaceBlueprint().getModuleCompleteKey().equals(DOCUMENTATION_SPACE_COMPLETE_KEY)) {
            return;
        }
        Map context = event.getContext();
        Space space = event.getSpace();
        context.put("spaceKey", space.getKey());
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
        String pageTitle = i18NBean.getText("confluence.blueprints.space.documentation.making-a-template.name");
        context.put("makingATemplateLink", "<ri:page ri:content-title=\"" + pageTitle + "\" />");
        String spaceKey = space.getKey();
        this.sidebarService.setOption(spaceKey, "nav-type", "page-tree");
        this.sidebarService.setOption(spaceKey, "quick-links-state", "hide");
        for (SidebarLinkBean link : this.sidebarLinkService.getLinksForSpace(SidebarLinkCategory.MAIN, spaceKey, false)) {
            this.sidebarLinkService.hide(Integer.valueOf(link.getId()));
        }
    }
}

