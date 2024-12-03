/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.labels.service;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.LabelPermissionEnforcer;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.labels.service.AbstractLabelsCommand;
import com.atlassian.confluence.labels.service.RemoveLabelCommand;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RemoveLabelCommandImpl
extends AbstractLabelsCommand
implements RemoveLabelCommand {
    private final PermissionManager permissionManager;
    private final LabelManager labelManager;
    private final Collection<String> removedLabels = new ArrayList<String>();
    private String labelString;
    private Label label;
    private long labelId;

    public RemoveLabelCommandImpl(String labelsString, User user, long entityId, LabelManager labelManager, PermissionManager permissionManager, SpaceManager spaceManager, PageTemplateManager pageTemplateManager, ContentEntityManager contentEntityManager, LabelPermissionEnforcer labelPermissionEnforcer) {
        this(0L, user, entityId, labelManager, permissionManager, spaceManager, pageTemplateManager, contentEntityManager, labelPermissionEnforcer);
        this.labelString = labelsString;
    }

    public RemoveLabelCommandImpl(Label label, User user, long entityId, LabelManager labelManager, PermissionManager permissionManager, SpaceManager spaceManager, PageTemplateManager pageTemplateManager, ContentEntityManager contentEntityManager, LabelPermissionEnforcer labelPermissionEnforcer) {
        this(label, user, entityId, null, labelManager, permissionManager, spaceManager, pageTemplateManager, contentEntityManager, labelPermissionEnforcer);
    }

    public RemoveLabelCommandImpl(Label label, User user, long entityId, String entityType, LabelManager labelManager, PermissionManager permissionManager, SpaceManager spaceManager, PageTemplateManager pageTemplateManager, ContentEntityManager contentEntityManager, LabelPermissionEnforcer labelPermissionEnforcer) {
        this(label.getId(), user, entityId, entityType, labelManager, permissionManager, spaceManager, pageTemplateManager, contentEntityManager, labelPermissionEnforcer);
        this.label = label;
    }

    public RemoveLabelCommandImpl(long labelId, User user, long entityId, LabelManager labelManager, PermissionManager permissionManager, SpaceManager spaceManager, PageTemplateManager pageTemplateManager, ContentEntityManager contentEntityManager, LabelPermissionEnforcer labelPermissionEnforcer) {
        this(labelId, user, entityId, null, labelManager, permissionManager, spaceManager, pageTemplateManager, contentEntityManager, labelPermissionEnforcer);
    }

    public RemoveLabelCommandImpl(long labelId, User user, long entityId, String entityType, LabelManager labelManager, PermissionManager permissionManager, SpaceManager spaceManager, PageTemplateManager pageTemplateManager, ContentEntityManager contentEntityManager, LabelPermissionEnforcer labelPermissionEnforcer) {
        super(user, entityId, entityType, spaceManager, pageTemplateManager, contentEntityManager, labelPermissionEnforcer);
        this.labelId = labelId;
        this.labelManager = labelManager;
        this.permissionManager = permissionManager;
    }

    @Override
    protected boolean isAuthorizedInternal() {
        Label label = this.getLabel();
        if (this.getEntity() == null || label == null) {
            return true;
        }
        return this.hasEditPermissions(label) && this.labelPermissionEnforcer.isLabelableByUser(this.getEntity());
    }

    private boolean hasEditPermissions(Label label) {
        return this.labelPermissionEnforcer.userCanEditLabel(label, this.getEntity()) && (!label.getNamespace().equals(Namespace.PERSONAL) || Objects.equals(label.getOwner(), this.getUser().getName()));
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        if (this.getEntity() == null) {
            validator.addValidationError("no.page.found.for.id", this.getEntityId());
            return;
        }
        if (!this.permissionManager.hasPermission(this.getUser(), Permission.VIEW, this.getEntity())) {
            validator.addValidationError("no.page.found.for.id", this.getEntityId());
            return;
        }
        this.checkLabel(validator, this.getLabel());
    }

    @Override
    public void executeInternal() {
        Label label = this.getLabel();
        if (label == null) {
            return;
        }
        int result = this.labelManager.removeLabel(this.getEntity(), label);
        if (result != 0) {
            this.getRemovedLabels().add(label.getDisplayTitle());
        }
    }

    private @Nullable Label getLabel() {
        if (this.label != null) {
            return this.label;
        }
        try {
            this.labelId = Long.parseLong(this.labelString);
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        if (this.labelId != 0L) {
            return this.labelManager.getLabel(this.labelId);
        }
        ParsedLabelName parsedLabel = LabelParser.parse(this.labelString, this.getUser());
        if (parsedLabel == null) {
            return null;
        }
        return parsedLabel.toLabel();
    }

    private void checkLabel(ServiceCommandValidator validator, Label label) {
        if (label == null) {
            validator.addValidationError("label.not.found", new Object[0]);
            return;
        }
        if (!this.hasEditPermissions(label)) {
            validator.addValidationError("not.permitted.to.remove.label", "'" + label.getDisplayTitle() + "'");
        }
    }

    @Override
    public Collection<String> getRemovedLabels() {
        return this.removedLabels;
    }
}

