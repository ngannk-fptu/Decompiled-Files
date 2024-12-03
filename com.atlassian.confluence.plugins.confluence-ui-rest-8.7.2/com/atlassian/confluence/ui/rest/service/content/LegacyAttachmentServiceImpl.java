/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.AbstractLabelableEntityObject
 *  com.atlassian.confluence.core.service.ServiceCommand
 *  com.atlassian.confluence.labels.service.AddLabelsCommand
 *  com.atlassian.confluence.labels.service.LabelsService
 *  com.atlassian.confluence.labels.service.RemoveLabelCommand
 *  com.atlassian.confluence.legacyapi.NotFoundException
 *  com.atlassian.confluence.legacyapi.model.content.Label
 *  com.atlassian.confluence.legacyapi.model.content.Label$Prefix
 *  com.atlassian.confluence.legacyapi.service.content.AttachmentService
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ui.rest.service.content;

import com.atlassian.confluence.core.AbstractLabelableEntityObject;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.labels.service.AddLabelsCommand;
import com.atlassian.confluence.labels.service.LabelsService;
import com.atlassian.confluence.labels.service.RemoveLabelCommand;
import com.atlassian.confluence.legacyapi.NotFoundException;
import com.atlassian.confluence.legacyapi.model.content.Label;
import com.atlassian.confluence.legacyapi.service.content.AttachmentService;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.ui.rest.service.content.LegacyLabelHelper;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Deprecated
@Component(value="localAttachmentService")
public class LegacyAttachmentServiceImpl
implements AttachmentService {
    private static final String ATTACHMENT_ENTITY_TYPE = "attachment";
    private final AttachmentManager attachmentManager;
    private final PermissionManager permissionManager;
    private final LabelsService labelsService;

    @Autowired
    public LegacyAttachmentServiceImpl(@ComponentImport AttachmentManager attachmentManager, @ComponentImport PermissionManager permissionManager, @ComponentImport LabelsService labelsService) {
        this.attachmentManager = attachmentManager;
        this.permissionManager = permissionManager;
        this.labelsService = labelsService;
    }

    public Iterable<Label> getLabels(long attachmentId, Collection<Label.Prefix> prefixes) throws NotFoundException {
        return LegacyLabelHelper.extractViewableLabels(this.getAttachmentIfViewable(attachmentId), prefixes, (User)AuthenticatedUserThreadLocal.get());
    }

    private AbstractLabelableEntityObject getAttachmentIfViewable(long attachmentId) {
        Attachment attachment = this.attachmentManager.getAttachment(attachmentId);
        if (attachment == null || !this.canView(attachment)) {
            throw new NotFoundException("Attachment with id " + attachmentId + " is either missing or not visible to this user");
        }
        return attachment;
    }

    private boolean canView(Attachment attachment) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)attachment);
    }

    public Iterable<Label> addLabels(long attachmentId, Iterable<Label> labels) throws IllegalArgumentException {
        String labelsString = LegacyLabelHelper.concatentateLabels(labels);
        AddLabelsCommand command = this.labelsService.newAddLabelCommand(labelsString, (User)AuthenticatedUserThreadLocal.get(), attachmentId, ATTACHMENT_ENTITY_TYPE);
        LegacyLabelHelper.validateLabelsCommand((ServiceCommand)command);
        command.execute();
        return this.getLabels(attachmentId, Arrays.asList(Label.Prefix.values()));
    }

    public void removeLabel(long attachmentId, long labelId) throws IllegalArgumentException {
        RemoveLabelCommand command = this.labelsService.newRemoveLabelCommand(labelId, (User)AuthenticatedUserThreadLocal.get(), attachmentId);
        LegacyLabelHelper.validateLabelsCommand((ServiceCommand)command);
        command.execute();
    }
}

