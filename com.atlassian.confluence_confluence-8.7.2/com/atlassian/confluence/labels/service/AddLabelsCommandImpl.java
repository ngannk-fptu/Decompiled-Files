/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.labels.service;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelPermissionEnforcer;
import com.atlassian.confluence.labels.service.AbstractLabelsCommand;
import com.atlassian.confluence.labels.service.AddLabelsCommand;
import com.atlassian.confluence.labels.service.LabelValidationHelper;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class AddLabelsCommandImpl
extends AbstractLabelsCommand
implements AddLabelsCommand {
    private final PermissionManager permissionManager;
    private final LabelManager labelManager;
    private Collection<Label> addedLabels;
    private final String labelsString;

    public AddLabelsCommandImpl(String labelsString, User user, long entityId, String entityType, LabelManager labelManager, PermissionManager permissionManager, SpaceManager spaceManager, PageTemplateManager pageTemplateManager, ContentEntityManager contentEntityManager, LabelPermissionEnforcer labelPermissionEnforcer) {
        super(user, entityId, entityType, spaceManager, pageTemplateManager, contentEntityManager, labelPermissionEnforcer);
        this.labelsString = labelsString;
        this.labelManager = labelManager;
        this.permissionManager = permissionManager;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        if (!this.checkParameters(validator, this.getUser())) {
            return;
        }
        List<String> labelNames = LabelUtil.split(this.labelsString.toLowerCase());
        LabelValidationHelper validationHelper = new LabelValidationHelper(validator, this.getUser(), this.labelPermissionEnforcer, this.getEntity());
        validationHelper.validateLabels(labelNames);
    }

    @Override
    protected boolean isAuthorizedInternal() {
        if (this.getEntity() == null) {
            return true;
        }
        return this.labelPermissionEnforcer.isLabelableByUser(this.getEntity());
    }

    @Override
    protected void executeInternal() {
        List<String> labelNames = LabelUtil.split(this.labelsString.toLowerCase());
        this.addedLabels = new ArrayList<Label>();
        for (String labelName : labelNames) {
            Label label = LabelUtil.addLabel(labelName, this.labelManager, this.getEntity());
            if (label == null) continue;
            this.addedLabels.add(label);
        }
    }

    private boolean checkParameters(ServiceCommandValidator validator, User user) {
        if (this.getEntity() == null) {
            validator.addValidationError("no.page.found.for.id", this.getEntityId());
            return false;
        }
        if (StringUtils.isBlank((CharSequence)this.labelsString)) {
            validator.addValidationError("please.enter.a.label", new Object[0]);
            return false;
        }
        if (!this.permissionManager.hasPermission(user, Permission.VIEW, this.getEntity())) {
            validator.addValidationError("no.page.found.for.id", this.getEntityId());
            return false;
        }
        return true;
    }

    @Override
    public Collection<Label> getAddedLabels() {
        if (this.addedLabels == null) {
            this.addedLabels = new ArrayList<Label>();
        }
        return this.addedLabels;
    }
}

