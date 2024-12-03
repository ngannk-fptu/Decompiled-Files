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
 *  com.atlassian.confluence.legacyapi.service.content.TemplateService
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
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
import com.atlassian.confluence.legacyapi.service.content.TemplateService;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
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
@Component
public class LegacyTemplateServiceImpl
implements TemplateService {
    private static final String TEMPLATE_ENTITY_TYPE = "template";
    private final PageTemplateManager pageTemplateManager;
    private final PermissionManager permissionManager;
    private final LabelsService labelsService;

    @Autowired
    public LegacyTemplateServiceImpl(@ComponentImport PageTemplateManager pageTemplateManager, @ComponentImport PermissionManager permissionManager, @ComponentImport LabelsService labelsService) {
        this.pageTemplateManager = pageTemplateManager;
        this.permissionManager = permissionManager;
        this.labelsService = labelsService;
    }

    public Iterable<Label> getLabels(long pageTemplateId, Collection<Label.Prefix> prefixes) throws NotFoundException {
        return LegacyLabelHelper.extractViewableLabels(this.getTemplateIfViewable(pageTemplateId), prefixes, (User)AuthenticatedUserThreadLocal.get());
    }

    public Iterable<Label> addLabels(long pageTemplateId, Iterable<Label> labels) throws IllegalArgumentException {
        String labelsString = LegacyLabelHelper.concatentateLabels(labels);
        AddLabelsCommand command = this.labelsService.newAddLabelCommand(labelsString, (User)AuthenticatedUserThreadLocal.get(), pageTemplateId, TEMPLATE_ENTITY_TYPE);
        LegacyLabelHelper.validateLabelsCommand((ServiceCommand)command);
        command.execute();
        return this.getLabels(pageTemplateId, Arrays.asList(Label.Prefix.values()));
    }

    public void removeLabel(long pageTemplateId, long labelId) throws IllegalArgumentException {
        RemoveLabelCommand command = this.labelsService.newRemoveLabelCommand(labelId, (User)AuthenticatedUserThreadLocal.get(), pageTemplateId);
        LegacyLabelHelper.validateLabelsCommand((ServiceCommand)command);
        command.execute();
    }

    private AbstractLabelableEntityObject getTemplateIfViewable(long pageTemplateId) {
        PageTemplate pageTemplate = this.pageTemplateManager.getPageTemplate(pageTemplateId);
        if (pageTemplate == null || !this.canView(pageTemplate)) {
            throw new NotFoundException("Attachment with id " + pageTemplateId + " is either missing or not visible to this user");
        }
        return pageTemplate;
    }

    private boolean canView(PageTemplate pageTemplate) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)pageTemplate);
    }
}

