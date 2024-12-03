/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.plugins.createcontent.api.services.SpaceBlueprintService
 *  com.atlassian.confluence.plugins.createcontent.rest.BlueprintWebItemService
 *  com.atlassian.confluence.plugins.createcontent.rest.entities.CreateBlueprintSpaceRestEntity
 *  com.atlassian.confluence.plugins.createcontent.rest.entities.CreateDialogWebItemEntity
 *  com.atlassian.confluence.plugins.createcontent.services.model.BlueprintSpace
 *  com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintSpaceEntity
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.DocumentationBeanFactory
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.projectcreate.crud.service;

import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.createcontent.api.services.SpaceBlueprintService;
import com.atlassian.confluence.plugins.createcontent.rest.BlueprintWebItemService;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateBlueprintSpaceRestEntity;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateDialogWebItemEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.BlueprintSpace;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintSpaceEntity;
import com.atlassian.confluence.plugins.projectcreate.crud.exception.CreateSpaceFailureException;
import com.atlassian.confluence.plugins.projectcreate.crud.service.AbstractSpaceCreator;
import com.atlassian.confluence.plugins.projectcreate.crud.service.SpaceCreator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SoftwareSpaceCreator
extends AbstractSpaceCreator
implements SpaceCreator {
    private final SpaceBlueprintService spaceBlueprintService;
    private I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final BlueprintWebItemService blueprintWebItemService;
    private final DocumentationBeanFactory documentationBeanFactory;
    private static final String SOFTWARE_SPACE_BLUEPRINT_KEY = "com.atlassian.confluence.plugins.confluence-software-project:sp-space-blueprint";

    @Autowired
    public SoftwareSpaceCreator(@ComponentImport SpaceService spaceService, @ComponentImport SpaceBlueprintService spaceBlueprintService, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @ComponentImport BlueprintWebItemService blueprintWebItemService, @ComponentImport DocumentationBeanFactory documentationBeanFactory) {
        super(spaceService);
        this.spaceBlueprintService = spaceBlueprintService;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.blueprintWebItemService = blueprintWebItemService;
        this.documentationBeanFactory = documentationBeanFactory;
    }

    @Override
    public Space createSpace(ConfluenceUser user, String spaceKey, String spaceName, Map<String, String> context) throws CreateSpaceFailureException {
        String bluePrintId = this.getBluePrintId(user);
        CreateBlueprintSpaceRestEntity entity = new CreateBlueprintSpaceRestEntity(spaceKey, spaceName, "", null, bluePrintId, new HashMap());
        try {
            BlueprintSpace spaceBP = this.spaceBlueprintService.createSpace((CreateBlueprintSpaceEntity)entity, user);
            if (spaceBP == null || spaceBP.getSpace() == null) {
                throw new CreateSpaceFailureException("confluence.projectcreate.space.create.failed");
            }
            return Space.builder().key(spaceBP.getSpace().getKey()).name(spaceBP.getSpace().getName()).build();
        }
        catch (Exception e) {
            throw new CreateSpaceFailureException("confluence.projectcreate.space.create.failed");
        }
    }

    @Override
    public boolean canHandle(ConfluenceUser user, String spaceKey, String spaceName, Map<String, String> context) {
        String projectTemplateModuleKey;
        return context != null && context.get("projectTemplateModuleKey") != null && (projectTemplateModuleKey = context.get("projectTemplateModuleKey")).startsWith("com.pyxis.greenhopper.jira");
    }

    private String getBluePrintId(ConfluenceUser user) throws CreateSpaceFailureException {
        String softwareSpaceBlueprintId = null;
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)user));
        List spaceBlueprints = this.blueprintWebItemService.getCreateSpaceWebItems(i18NBean, this.documentationBeanFactory.getDocumentationBean(), user);
        for (CreateDialogWebItemEntity blueprint : spaceBlueprints) {
            if (!blueprint.getBlueprintModuleCompleteKey().equals(SOFTWARE_SPACE_BLUEPRINT_KEY)) continue;
            softwareSpaceBlueprintId = blueprint.getContentBlueprintId().toString();
        }
        if (softwareSpaceBlueprintId == null) {
            throw new CreateSpaceFailureException("confluence.projectcreate.space.create.failed");
        }
        return softwareSpaceBlueprintId;
    }
}

