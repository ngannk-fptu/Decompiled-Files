/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.google.common.base.Enums
 */
package com.atlassian.confluence.labels.service;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.labels.LabelPermissionEnforcer;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.user.User;
import com.google.common.base.Enums;

public abstract class AbstractLabelsCommand
extends AbstractServiceCommand {
    private Labelable entity;
    private final User user;
    private final long entityId;
    private final LabelableType entityType;
    private final SpaceManager spaceManager;
    private final PageTemplateManager pageTemplateManager;
    private final ContentEntityManager contentEntityManager;
    protected final LabelPermissionEnforcer labelPermissionEnforcer;

    public AbstractLabelsCommand(User user, long entityId, String entityType, SpaceManager spaceManager, PageTemplateManager pageTemplateManager, ContentEntityManager contentEntityManager, LabelPermissionEnforcer labelPermissionEnforcer) {
        this.user = user;
        this.entityId = entityId;
        this.entityType = this.getLabelableType(entityType);
        this.spaceManager = spaceManager;
        this.pageTemplateManager = pageTemplateManager;
        this.contentEntityManager = contentEntityManager;
        this.labelPermissionEnforcer = labelPermissionEnforcer;
    }

    public User getUser() {
        return this.user;
    }

    public long getEntityId() {
        return this.entityId;
    }

    public Labelable getEntity() {
        if (this.entity == null) {
            if (this.entityType == null) {
                this.entity = this.contentEntityManager.getById(this.getEntityId());
                if (this.entity == null) {
                    this.entity = this.getSpaceDescription();
                }
                if (this.entity == null) {
                    this.entity = this.pageTemplateManager.getPageTemplate(this.getEntityId());
                }
            } else if (this.isPageBasedEntity(this.entityType)) {
                this.entity = this.contentEntityManager.getById(this.getEntityId());
            } else if (this.entityType.equals((Object)LabelableType.space)) {
                this.entity = this.getSpaceDescription();
            } else if (this.entityType.equals((Object)LabelableType.template)) {
                this.entity = this.pageTemplateManager.getPageTemplate(this.getEntityId());
            } else {
                throw new IllegalArgumentException("Unknown entity type :" + this.entityType);
            }
        }
        return this.entity;
    }

    private LabelableType getLabelableType(String entityTypeName) {
        LabelableType entityType = null;
        try {
            if (entityTypeName != null) {
                entityType = (LabelableType)((Object)Enums.stringConverter(LabelableType.class).convert((Object)entityTypeName.toLowerCase()));
            }
        }
        catch (IllegalArgumentException ex) {
            entityType = null;
        }
        return entityType;
    }

    private boolean isPageBasedEntity(LabelableType entityType) {
        switch (entityType) {
            case page: 
            case blogpost: 
            case attachment: 
            case draft: {
                return true;
            }
        }
        return false;
    }

    private SpaceDescription getSpaceDescription() {
        Space space = this.spaceManager.getSpace(this.getEntityId());
        if (space != null) {
            if (space.getDescription() == null) {
                SpaceDescription spaceDescription = new SpaceDescription();
                spaceDescription.setSpace(space);
                spaceDescription.setBodyAsString("");
                space.setDescription(spaceDescription);
                this.spaceManager.saveSpace(space);
            }
            return space.getDescription();
        }
        return null;
    }

    private static enum LabelableType {
        page,
        blogpost,
        space,
        attachment,
        template,
        draft;

    }
}

