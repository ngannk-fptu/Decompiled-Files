/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.PersonalInformationManager
 *  com.atlassian.confluence.util.i18n.DocumentationBean
 *  com.atlassian.confluence.util.i18n.DocumentationBeanFactory
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.exceptions.ResourceException;
import com.atlassian.confluence.plugins.createcontent.rest.AbstractRestResource;
import com.atlassian.confluence.plugins.createcontent.rest.BlueprintWebItemService;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateDialogWebItemEntity;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.user.User;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(value="/space-blueprint/dialog")
public class SpaceBlueprintDialogResource
extends AbstractRestResource {
    private final PermissionManager permissionManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final DocumentationBeanFactory documentationBeanFactory;
    private final BlueprintWebItemService webItemService;
    private final PersonalInformationManager personalInformationManager;
    private final AccessModeService accessModeService;

    public SpaceBlueprintDialogResource(@ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, @ComponentImport DocumentationBeanFactory documentationBeanFactory, BlueprintWebItemService webItemService, @ComponentImport PersonalInformationManager personalInformationManager, @ComponentImport AccessModeService accessModeService) {
        super(permissionManager, spaceManager, accessModeService);
        this.permissionManager = permissionManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.documentationBeanFactory = documentationBeanFactory;
        this.webItemService = webItemService;
        this.personalInformationManager = personalInformationManager;
        this.accessModeService = accessModeService;
    }

    @GET
    @Path(value="web-items")
    @AnonymousSiteAccess
    public List<CreateDialogWebItemEntity> getWebItems() {
        if (AccessMode.READ_ONLY == this.accessModeService.getAccessMode()) {
            throw new ReadOnlyException("Read only mode is enabled.");
        }
        ConfluenceUser remoteUser = this.getUser();
        boolean canCreateSpaces = this.userCanCreateSpace(remoteUser);
        boolean canCreatePersonalSpaces = this.userCanCreatePersonalSpace(remoteUser);
        if (!canCreateSpaces && !canCreatePersonalSpaces) {
            throw new ResourceException("You are not permitted to create spaces or personal spaces.", Response.Status.FORBIDDEN, remoteUser == null ? ResourceErrorType.PERMISSION_ANONYMOUS_CREATE_SPACE : ResourceErrorType.PERMISSION_USER_CREATE_SPACE, (Object)(remoteUser == null ? null : remoteUser.getName()));
        }
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)remoteUser));
        DocumentationBean documentationBean = this.documentationBeanFactory.getDocumentationBean();
        return !canCreateSpaces ? this.webItemService.getCreatePersonalSpaceWebItems(i18NBean, documentationBean, remoteUser) : this.webItemService.getCreateSpaceWebItems(i18NBean, documentationBean, remoteUser);
    }

    private boolean userCanCreateSpace(ConfluenceUser user) {
        return this.permissionManager.hasCreatePermission((User)user, PermissionManager.TARGET_APPLICATION, Space.class);
    }

    private boolean userCanCreatePersonalSpace(ConfluenceUser user) {
        return this.permissionManager.hasCreatePermission((User)user, (Object)this.personalInformationManager.getOrCreatePersonalInformation((User)user), Space.class);
    }
}

