/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.DocumentationBean
 *  com.atlassian.confluence.util.i18n.DocumentationBeanFactory
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AdminOnly
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.user.User
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.exceptions.ResourceException;
import com.atlassian.confluence.plugins.createcontent.extensions.UserBlueprintConfigManager;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.rest.AbstractRestResource;
import com.atlassian.confluence.plugins.createcontent.rest.BlueprintWebItemService;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateDialogWebItemEntity;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AdminOnly;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="/blueprints")
@AnonymousSiteAccess
public class BlueprintResource
extends AbstractRestResource {
    public static final String PARAM_SPACE_KEY = "spaceKey";
    public static final String PARAM_PAGE_TITLE = "pageTitle";
    public static final String PARAM_CONTENT_BLUEPRINT_ID = "contentBlueprintId";
    public static final String PARAM_SKIP = "skip";
    public static final String PARAM_ID = "id";
    public static final String PARAM_BP_MODULE_COMPLETE_KEY = "blueprintModuleCompleteKey";
    public static final String PARAM_KEY = "key";
    private final PermissionManager permissionManager;
    private final PageManager pageManager;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final DocumentationBeanFactory documentationBeanFactory;
    private final UserBlueprintConfigManager userBlueprintConfigManager;
    private final ContentBlueprintManager contentBlueprintManager;
    private final BlueprintWebItemService webItemService;
    private final AccessModeService accessModeService;
    private final UserManager userManager;

    public BlueprintResource(@ComponentImport SpaceManager spaceManager, @ComponentImport PermissionManager permissionManager, @ComponentImport PageManager pageManager, @ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport DocumentationBeanFactory documentationBeanFactory, UserBlueprintConfigManager userBlueprintConfigManager, ContentBlueprintManager contentBlueprintManager, BlueprintWebItemService webItemService, @ComponentImport AccessModeService accessModeService, @ComponentImport UserManager userManager) {
        super(permissionManager, spaceManager, accessModeService);
        this.permissionManager = permissionManager;
        this.pageManager = pageManager;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.documentationBeanFactory = documentationBeanFactory;
        this.userBlueprintConfigManager = userBlueprintConfigManager;
        this.contentBlueprintManager = contentBlueprintManager;
        this.webItemService = webItemService;
        this.accessModeService = accessModeService;
        this.userManager = userManager;
    }

    @GET
    @Path(value="web-items")
    public List<CreateDialogWebItemEntity> getWebItems(@QueryParam(value="spaceKey") String spaceKey) {
        this.unlicensedLimitedUserShouldNotBeAllowed();
        if (AccessMode.READ_ONLY.equals((Object)this.accessModeService.getAccessMode())) {
            throw new ReadOnlyException("Read only mode is enabled.");
        }
        this.checkEmptyParameter(spaceKey, PARAM_SPACE_KEY);
        Space space = this.getAndCheckSpace(spaceKey);
        ConfluenceUser remoteUser = this.getUser();
        if (!this.userCanCreateInSpace(remoteUser, space)) {
            throw new ResourceException("You are not permitted to see Create dialog items for space: " + spaceKey, Response.Status.FORBIDDEN, remoteUser == null ? ResourceErrorType.PERMISSION_ANONYMOUS_CREATE : ResourceErrorType.PERMISSION_USER_CREATE, (Object)spaceKey);
        }
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)remoteUser));
        DocumentationBean documentationBean = this.documentationBeanFactory.getDocumentationBean();
        return this.webItemService.getCreateContentWebItems(space, i18NBean, documentationBean, remoteUser);
    }

    private boolean userCanCreateInSpace(ConfluenceUser remoteUser, Space space) {
        return this.permissionManager.hasCreatePermission((User)remoteUser, (Object)space, Page.class) || this.permissionManager.hasCreatePermission((User)remoteUser, (Object)space, BlogPost.class);
    }

    @GET
    @Path(value="can-create-page")
    public Boolean canCreatePage(@QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="pageTitle") String pageTitle) {
        this.unlicensedLimitedUserShouldNotBeAllowed();
        this.checkEmptyParameter(spaceKey, PARAM_SPACE_KEY);
        this.checkEmptyParameter(pageTitle, PARAM_PAGE_TITLE);
        Space space = this.getAndCheckSpace(spaceKey);
        return this.canCreatePage(space, pageTitle);
    }

    private boolean canCreatePage(Space space, String pageTitle) {
        if (!this.permissionManager.hasCreatePermission((User)this.getUser(), (Object)space, Page.class)) {
            return false;
        }
        return this.pageManager.getPage(space.getKey(), pageTitle) == null;
    }

    @POST
    @Path(value="skip-how-to-use")
    public void skipHowToUse(@FormParam(value="contentBlueprintId") UUID contentBlueprintId, @FormParam(value="skip") Boolean skip) {
        this.unlicensedLimitedUserShouldNotBeAllowed();
        this.checkNullParameter(contentBlueprintId, PARAM_CONTENT_BLUEPRINT_ID);
        this.checkNullParameter(skip, PARAM_SKIP);
        this.userBlueprintConfigManager.setSkipHowToUse(this.getUser(), contentBlueprintId, skip);
    }

    @GET
    @Path(value="get/{id}")
    public ContentBlueprint get(@PathParam(value="id") UUID id) {
        this.unlicensedLimitedUserShouldNotBeAllowed();
        this.checkNullParameter(id, PARAM_ID);
        return (ContentBlueprint)this.contentBlueprintManager.getById(id);
    }

    @GET
    @Path(value="get")
    public ContentBlueprint get(@QueryParam(value="blueprintModuleCompleteKey") String blueprintModuleCompleteKey) {
        this.unlicensedLimitedUserShouldNotBeAllowed();
        this.checkEmptyParameter(blueprintModuleCompleteKey, PARAM_BP_MODULE_COMPLETE_KEY);
        return this.contentBlueprintManager.getPluginBackedContentBlueprint(new ModuleCompleteKey(blueprintModuleCompleteKey), null);
    }

    @GET
    @Path(value="list")
    public List<ContentBlueprint> getAllContentBlueprints() {
        this.unlicensedLimitedUserShouldNotBeAllowed();
        return this.contentBlueprintManager.getAll();
    }

    @PUT
    @ReadOnlyAccessAllowed
    @Path(value="create")
    public ContentBlueprint create(ContentBlueprint blueprint) {
        this.unlicensedLimitedUserShouldNotBeAllowed();
        this.checkNullEntity(blueprint);
        String spaceKey = blueprint.getSpaceKey();
        if (StringUtils.isNotBlank((CharSequence)spaceKey)) {
            this.checkSpaceAdminPermission(spaceKey);
        } else {
            this.checkAdminPermission();
        }
        return this.contentBlueprintManager.create(blueprint);
    }

    @GET
    @Path(value="byKey/{key}")
    public ContentBlueprint getByModuleCompleteKey(@PathParam(value="key") String moduleCompleteKey) {
        this.unlicensedLimitedUserShouldNotBeAllowed();
        this.checkEmptyParameter(moduleCompleteKey, PARAM_KEY);
        ModuleCompleteKey key = new ModuleCompleteKey(moduleCompleteKey);
        return this.contentBlueprintManager.getPluginBackedContentBlueprint(key, null);
    }

    @DELETE
    @ReadOnlyAccessAllowed
    @Path(value="deleteAll")
    @AdminOnly
    public Integer deleteAll() {
        return this.contentBlueprintManager.deleteAll();
    }

    private void unlicensedLimitedUserShouldNotBeAllowed() {
        UserKey userKey = this.userManager.getRemoteUserKey();
        if (userKey != null && this.userManager.isLimitedUnlicensedUser(userKey)) {
            throw new ResourceException("Only licensed user can make this request.", Response.Status.BAD_REQUEST, ResourceErrorType.PERMISSION_USER_CREATE);
        }
    }
}

