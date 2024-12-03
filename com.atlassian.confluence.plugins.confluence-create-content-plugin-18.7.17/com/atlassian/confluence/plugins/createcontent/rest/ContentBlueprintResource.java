/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.xwork.FlashScope
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.api.services.ContentBlueprintService;
import com.atlassian.confluence.plugins.createcontent.extensions.UserBlueprintConfigManager;
import com.atlassian.confluence.plugins.createcontent.rest.AbstractRestResource;
import com.atlassian.confluence.plugins.createcontent.rest.entities.BlueprintDraftEntity;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateBlueprintPageRestEntity;
import com.atlassian.confluence.plugins.createcontent.rest.entities.PageEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.BlueprintPage;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.xwork.FlashScope;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path(value="/content-blueprint")
public class ContentBlueprintResource
extends AbstractRestResource {
    private final ContentBlueprintService contentBlueprintService;
    private final SettingsManager settingsManager;
    private final UserBlueprintConfigManager userBlueprintConfigManager;

    public ContentBlueprintResource(@ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, ContentBlueprintService legacyContentBlueprintService, @ComponentImport SettingsManager settingsManager, UserBlueprintConfigManager userBlueprintConfigManager, @ComponentImport AccessModeService accessModeService) {
        super(permissionManager, spaceManager, accessModeService);
        this.contentBlueprintService = legacyContentBlueprintService;
        this.settingsManager = settingsManager;
        this.userBlueprintConfigManager = userBlueprintConfigManager;
    }

    @POST
    @Path(value="create-content")
    @AnonymousSiteAccess
    @Consumes(value={"application/json", "application/xml"})
    public PageEntity createPage(CreateBlueprintPageRestEntity entity) throws BlueprintIllegalArgumentException {
        this.checkNullEntity(entity);
        ConfluenceUser user = this.getUser();
        boolean firstBlueprintCreation = false;
        if (user != null) {
            firstBlueprintCreation = entity.getContentBlueprintId() != null && this.userBlueprintConfigManager.isFirstBlueprintOfTypeForUser(UUID.fromString(entity.getContentBlueprintId()), user);
        }
        BlueprintPage page = this.contentBlueprintService.createPage(entity, user);
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        PageEntity result = new PageEntity(page, baseUrl);
        if (firstBlueprintCreation) {
            this.enableFirstBlueprintCreationFlashScope(result);
        }
        return result;
    }

    @POST
    @Path(value="create-draft")
    @AnonymousSiteAccess
    @Consumes(value={"application/json", "application/xml"})
    public BlueprintDraftEntity createDraft(CreateBlueprintPageRestEntity entity) throws BlueprintIllegalArgumentException {
        this.checkNullEntity(entity);
        ContentEntityObject contentDraft = this.contentBlueprintService.createContentDraft(entity, this.getUser());
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        return new BlueprintDraftEntity(contentDraft, baseUrl);
    }

    private void enableFirstBlueprintCreationFlashScope(PageEntity result) {
        String flashId = FlashScope.persist();
        result.setCreateSuccessRedirectUrl(FlashScope.getFlashScopeUrl((String)result.getUrl(), (String)flashId));
        if (result.getIndexPage() != null) {
            result.getIndexPage().setCreateSuccessRedirectUrl(FlashScope.getFlashScopeUrl((String)result.getIndexPage().getUrl(), (String)flashId));
        }
    }
}

