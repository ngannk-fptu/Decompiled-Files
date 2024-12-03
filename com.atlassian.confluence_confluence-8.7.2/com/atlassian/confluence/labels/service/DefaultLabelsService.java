/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.labels.service;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelPermissionEnforcer;
import com.atlassian.confluence.labels.service.AddLabelsCommand;
import com.atlassian.confluence.labels.service.AddLabelsCommandImpl;
import com.atlassian.confluence.labels.service.LabelsService;
import com.atlassian.confluence.labels.service.RemoveLabelCommand;
import com.atlassian.confluence.labels.service.RemoveLabelCommandImpl;
import com.atlassian.confluence.labels.service.ValidateLabelsCommand;
import com.atlassian.confluence.labels.service.ValidateLabelsCommandImpl;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.user.User;

public class DefaultLabelsService
implements LabelsService {
    private final PermissionManager permissionManager;
    private final LabelManager labelManager;
    private final SpaceManager spaceManager;
    private final PageTemplateManager pageTemplateManager;
    private final ContentEntityManager contentEntityManager;
    private final LabelPermissionEnforcer labelPermissionEnforcer;

    public DefaultLabelsService(LabelManager labelManager, PermissionManager permissionManager, SpaceManager spaceManager, PageTemplateManager pageTemplateManager, ContentEntityManager contentEntityManager, LabelPermissionEnforcer labelPermissionEnforcer) {
        this.permissionManager = permissionManager;
        this.labelManager = labelManager;
        this.spaceManager = spaceManager;
        this.pageTemplateManager = pageTemplateManager;
        this.contentEntityManager = contentEntityManager;
        this.labelPermissionEnforcer = labelPermissionEnforcer;
    }

    @Override
    public AddLabelsCommand newAddLabelCommand(String labelString, User user, long entityId) {
        return this.newAddLabelCommand(labelString, user, entityId, null);
    }

    @Override
    public AddLabelsCommand newAddLabelCommand(String labelString, User user, long entityId, String entityType) {
        return new AddLabelsCommandImpl(labelString, user, entityId, entityType, this.labelManager, this.permissionManager, this.spaceManager, this.pageTemplateManager, this.contentEntityManager, this.labelPermissionEnforcer);
    }

    @Override
    public ValidateLabelsCommand newValidateLabelCommand(String labelString, User user) {
        return new ValidateLabelsCommandImpl(labelString, user);
    }

    @Override
    public RemoveLabelCommand newRemoveLabelCommand(Label label, User user, long entityId) {
        return this.newRemoveLabelCommand(label, user, entityId, null);
    }

    @Override
    public RemoveLabelCommand newRemoveLabelCommand(Label label, User user, long entityId, String entityType) {
        return new RemoveLabelCommandImpl(label, user, entityId, entityType, this.labelManager, this.permissionManager, this.spaceManager, this.pageTemplateManager, this.contentEntityManager, this.labelPermissionEnforcer);
    }

    @Override
    public RemoveLabelCommand newRemoveLabelCommand(String labelIdString, User user, long entityId) {
        return new RemoveLabelCommandImpl(labelIdString, user, entityId, this.labelManager, this.permissionManager, this.spaceManager, this.pageTemplateManager, this.contentEntityManager, this.labelPermissionEnforcer);
    }

    @Override
    public RemoveLabelCommand newRemoveLabelCommand(long labelId, User user, long entityId) {
        return new RemoveLabelCommandImpl(labelId, user, entityId, this.labelManager, this.permissionManager, this.spaceManager, this.pageTemplateManager, this.contentEntityManager, this.labelPermissionEnforcer);
    }
}

