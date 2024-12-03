/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DefaultDeleteContext
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.DefaultSaveContext$Builder
 *  com.atlassian.confluence.core.OperationTrigger
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 *  com.atlassian.confluence.pages.TrashManager
 *  com.atlassian.confluence.search.IndexManager
 *  com.atlassian.confluence.search.IndexManager$IndexQueueFlushMode
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.core.DefaultDeleteContext;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.OperationTrigger;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.pages.TrashManager;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.ContentTemplateRefManager;
import com.atlassian.confluence.plugins.createcontent.actions.BlueprintManager;
import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintCreateEvent;
import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintHomePageCreateEvent;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.api.services.SpaceBlueprintService;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreatePersonalSpaceRestEntity;
import com.atlassian.confluence.plugins.createcontent.services.PromotedBlueprintService;
import com.atlassian.confluence.plugins.createcontent.services.RequestResolver;
import com.atlassian.confluence.plugins.createcontent.services.model.BlueprintSpace;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintSpaceEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintSpaceRequest;
import com.atlassian.confluence.plugins.createcontent.services.model.CreatePersonalSpaceRequest;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService(value={SpaceBlueprintService.class})
@Component
public class DefaultSpaceBlueprintService
implements SpaceBlueprintService {
    private static final String PRIVATE = "private";
    private final SpaceManager spaceManager;
    private final PageManager pageManager;
    private final TrashManager trashManager;
    private final ContentTemplateRefManager contentTemplateRefManager;
    private final BlueprintManager blueprintManager;
    private final EventPublisher eventPublisher;
    private final ContentBlueprintManager contentBlueprintManager;
    private final RequestResolver requestResolver;
    private final NotificationManager notificationManager;
    private final IndexManager indexManager;
    private final PromotedBlueprintService promotedBlueprintService;
    private Logger log = LoggerFactory.getLogger(DefaultSpaceBlueprintService.class);

    @Autowired
    public DefaultSpaceBlueprintService(@ComponentImport SpaceManager spaceManager, @ComponentImport PageManager pageManager, @ComponentImport TrashManager trashManager, ContentTemplateRefManager contentTemplateRefManager, BlueprintManager blueprintManager, @ComponentImport EventPublisher eventPublisher, ContentBlueprintManager contentBlueprintManager, RequestResolver requestResolver, @ComponentImport NotificationManager notificationManager, @ComponentImport IndexManager indexManager, PromotedBlueprintService promotedBlueprintService) {
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
        this.trashManager = trashManager;
        this.contentTemplateRefManager = contentTemplateRefManager;
        this.blueprintManager = blueprintManager;
        this.eventPublisher = eventPublisher;
        this.contentBlueprintManager = contentBlueprintManager;
        this.requestResolver = requestResolver;
        this.notificationManager = notificationManager;
        this.indexManager = indexManager;
        this.promotedBlueprintService = promotedBlueprintService;
    }

    @Override
    public BlueprintSpace createSpace(@Nonnull CreateBlueprintSpaceEntity entity, @Nullable ConfluenceUser creator) throws BlueprintIllegalArgumentException {
        CreateBlueprintSpaceRequest createRequest = this.requestResolver.resolve(entity, creator);
        String key = createRequest.getSpaceKey();
        String name = createRequest.getName();
        String description = createRequest.getDescription();
        SpaceBlueprint spaceBlueprint = createRequest.getBlueprint();
        Map<String, Object> context = createRequest.getContext();
        Space space = PRIVATE.equalsIgnoreCase(entity.getPermissions()) ? this.spaceManager.createPrivateSpace(key, name, description, (User)creator) : this.spaceManager.createSpace(key, name, description, (User)creator);
        this.eventPublisher.publish((Object)new SpaceBlueprintCreateEvent(this, space, spaceBlueprint, creator, context));
        if (this.setHomePage(createRequest, creator, space)) {
            this.eventPublisher.publish((Object)new SpaceBlueprintHomePageCreateEvent(this, space, spaceBlueprint, creator, context));
        }
        this.addPromotedBps(spaceBlueprint, space, creator);
        return new BlueprintSpace(space);
    }

    @Override
    public BlueprintSpace createPersonalSpace(CreatePersonalSpaceRestEntity entity, ConfluenceUser creator) {
        CreatePersonalSpaceRequest request = this.requestResolver.resolve(entity, creator);
        ConfluenceUser spaceUser = request.getSpaceUser();
        String spaceUserFullName = spaceUser.getFullName();
        Space space = PRIVATE.equals(request.getSpacePermission()) ? this.spaceManager.createPrivatePersonalSpace(spaceUserFullName, null, (User)creator) : this.spaceManager.createPersonalSpace(spaceUserFullName, null, (User)creator);
        this.notificationManager.addSpaceNotification((User)spaceUser, space);
        this.indexManager.flushQueue(IndexManager.IndexQueueFlushMode.ONLY_FIRST_BATCH);
        return new BlueprintSpace(space);
    }

    private boolean setHomePage(CreateBlueprintSpaceRequest createRequest, ConfluenceUser creator, Space space) {
        UUID homePageId = createRequest.getBlueprint().getHomePageId();
        if (homePageId == null) {
            return false;
        }
        Page oldHomePage = space.getHomePage();
        ContentTemplateRef contentTemplateRef = (ContentTemplateRef)this.contentTemplateRefManager.getById(homePageId);
        DefaultSaveContext saveContext = ((DefaultSaveContext.Builder)DefaultSaveContext.builder().updateLastModifier(true).updateTrigger((OperationTrigger)PageUpdateTrigger.SPACE_CREATE)).build();
        Page newHomePage = this.blueprintManager.createPageFromTemplate(contentTemplateRef, creator, space, null, createRequest.getContext(), (SaveContext)saveContext);
        space.setHomePage(newHomePage);
        this.spaceManager.saveSpace(space);
        this.pageManager.trashPage((AbstractPage)oldHomePage, DefaultDeleteContext.DEFAULT);
        this.trashManager.purge(space.getKey(), oldHomePage.getId());
        return true;
    }

    private void addPromotedBps(SpaceBlueprint spaceBlueprint, Space space, ConfluenceUser creator) {
        List<ModuleCompleteKey> promotedBps = spaceBlueprint.getPromotedBps();
        if (promotedBps == null || promotedBps.isEmpty()) {
            return;
        }
        ArrayList uuids = Lists.newArrayList();
        for (ModuleCompleteKey promotedBp : promotedBps) {
            ContentBlueprint pluginBlueprint = this.contentBlueprintManager.getPluginBlueprint(promotedBp);
            if (pluginBlueprint != null) {
                uuids.add(pluginBlueprint.getId().toString());
                continue;
            }
            this.log.warn("Could not find plugin blueprint for blueprint with moduleCompleteKey " + promotedBp + " when creating space for user " + creator.getFullName());
        }
        this.promotedBlueprintService.promoteBlueprints(uuids, space);
    }
}

