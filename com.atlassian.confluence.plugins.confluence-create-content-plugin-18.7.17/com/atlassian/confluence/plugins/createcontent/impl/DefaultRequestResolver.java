/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.user.PersonalInformationManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.SpaceBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.exceptions.ResourceException;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.impl.ModuleCompleteKeyUtils;
import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreatePersonalSpaceRestEntity;
import com.atlassian.confluence.plugins.createcontent.services.BlueprintResolver;
import com.atlassian.confluence.plugins.createcontent.services.RequestResolver;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageRequest;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintSpaceEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintSpaceRequest;
import com.atlassian.confluence.plugins.createcontent.services.model.CreatePersonalSpaceRequest;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultRequestResolver
implements RequestResolver {
    private final BlueprintResolver blueprintResolver;
    private final SpaceManager spaceManager;
    private final PageManager pageManager;
    private final ContentBlueprintManager contentBlueprintManager;
    private final PermissionManager permissionManager;
    private final SpaceBlueprintManager spaceBlueprintManager;
    private final UserAccessor userAccessor;
    private final PersonalInformationManager personalInformationManager;

    @Autowired
    public DefaultRequestResolver(BlueprintResolver blueprintResolver, @ComponentImport SpaceManager spaceManager, @ComponentImport PageManager pageManager, ContentBlueprintManager contentBlueprintManager, @ComponentImport PermissionManager permissionManager, SpaceBlueprintManager spaceBlueprintManager, @ComponentImport UserAccessor userAccessor, @ComponentImport PersonalInformationManager personalInformationManager) {
        this.blueprintResolver = blueprintResolver;
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
        this.contentBlueprintManager = contentBlueprintManager;
        this.permissionManager = permissionManager;
        this.spaceBlueprintManager = spaceBlueprintManager;
        this.userAccessor = userAccessor;
        this.personalInformationManager = personalInformationManager;
    }

    @Override
    public CreateBlueprintPageRequest resolve(CreateBlueprintPageEntity entity, ConfluenceUser creator) throws BlueprintIllegalArgumentException {
        ContentBlueprint blueprint;
        String spaceKey = entity.getSpaceKey();
        Long parentPageId = entity.getParentPageId();
        Space space = StringUtils.isNotBlank((CharSequence)spaceKey) ? this.spaceManager.getSpace(spaceKey) : this.spaceManager.getSpace(entity.getSpaceId());
        Page parentPage = this.pageManager.getPage(parentPageId.longValue());
        if (StringUtils.isNotBlank((CharSequence)entity.getContentBlueprintId())) {
            blueprint = (ContentBlueprint)this.contentBlueprintManager.getById(UUID.fromString(entity.getContentBlueprintId()));
        } else {
            ModuleCompleteKey moduleCompleteKey = new ModuleCompleteKey(entity.getModuleCompleteKey());
            blueprint = this.contentBlueprintManager.getPluginBackedContentBlueprint(moduleCompleteKey, spaceKey);
        }
        ContentTemplateRef contentTemplateRef = this.getContentTemplateRef(blueprint, entity);
        this.validateBlueprintPageEntity(blueprint, creator, space, parentPage);
        return new CreateBlueprintPageRequest(space, entity.getTitle(), entity.getViewPermissionsUsers(), parentPage, entity.getContext(), contentTemplateRef, creator, blueprint);
    }

    @Override
    public CreateBlueprintSpaceRequest resolve(@Nonnull CreateBlueprintSpaceEntity entity, @Nullable ConfluenceUser creator) {
        this.validateBlueprintSpaceEntity(entity, creator);
        UUID spaceBlueprintId = UUID.fromString(entity.getSpaceBlueprintId());
        SpaceBlueprint blueprint = (SpaceBlueprint)this.spaceBlueprintManager.getById(spaceBlueprintId);
        return new CreateBlueprintSpaceRequest(blueprint, entity);
    }

    @Override
    public CreatePersonalSpaceRequest resolve(CreatePersonalSpaceRestEntity entity, ConfluenceUser creator) {
        String spaceUserKey = entity.getSpaceUserKey();
        ConfluenceUser spaceUser = StringUtils.isNotBlank((CharSequence)spaceUserKey) ? this.userAccessor.getUserByKey(new UserKey(spaceUserKey)) : creator;
        this.validatePersonalSpaceEntity(spaceUser, creator);
        return new CreatePersonalSpaceRequest(spaceUser, entity.isSpacePermission());
    }

    private void validatePersonalSpaceEntity(ConfluenceUser spaceUser, ConfluenceUser creator) {
        if (creator == null) {
            throw new ResourceException("Anonymous users cannot create personal spaces", Response.Status.BAD_REQUEST, ResourceErrorType.PERMISSION_ANONYMOUS_CREATE_PERSONAL_SPACE);
        }
        if (spaceUser == null) {
            throw new ResourceException("Cannot create personal space for unknown users", Response.Status.BAD_REQUEST, ResourceErrorType.PERMISSION_UNKNOWN_USER_CREATE_PERSONAL_SPACE);
        }
        String spaceUserName = spaceUser.getName();
        if (!creator.getKey().equals((Object)spaceUser.getKey()) && !this.permissionManager.hasPermission((User)creator, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION)) {
            throw new ResourceException("No permission to create a personal space for user " + spaceUserName, Response.Status.BAD_REQUEST, ResourceErrorType.PERMISSION_USER_CREATE_PERSONAL_SPACE, (Object)spaceUserName);
        }
        PersonalInformation pi = this.personalInformationManager.getOrCreatePersonalInformation((User)spaceUser);
        if (!this.permissionManager.hasCreatePermission((User)creator, (Object)pi, Space.class)) {
            throw new ResourceException("No permission to create personal spaces.", Response.Status.BAD_REQUEST, ResourceErrorType.PERMISSION_USER_CREATE_PERSONAL_SPACE, (Object)spaceUserName);
        }
        if (this.spaceManager.getSpace("~" + spaceUserName) != null) {
            String cause = "A space already exists with key ~" + spaceUserName;
            throw new ResourceException(cause, Response.Status.BAD_REQUEST, ResourceErrorType.DUPLICATED_PERSONAL_SPACE, (Object)spaceUserName);
        }
    }

    private void validateBlueprintSpaceEntity(@Nullable CreateBlueprintSpaceEntity entity, @Nullable ConfluenceUser creator) {
        if (entity == null) {
            throw new ResourceException("Invalid space entity", Response.Status.BAD_REQUEST, ResourceErrorType.INVALID_ENTITY);
        }
        String spaceKey = entity.getSpaceKey();
        if (!Space.isValidGlobalSpaceKey((String)spaceKey)) {
            throw new ResourceException("Invalid space key: " + spaceKey, Response.Status.BAD_REQUEST, ResourceErrorType.INVALID_SPACE_KEY, (Object)spaceKey);
        }
        if (!this.permissionManager.hasCreatePermission((User)creator, PermissionManager.TARGET_APPLICATION, Space.class)) {
            throw new ResourceException("No permission to create spaces.", Response.Status.BAD_REQUEST, creator == null ? ResourceErrorType.PERMISSION_ANONYMOUS_CREATE_SPACE : ResourceErrorType.PERMISSION_USER_CREATE_SPACE, (Object)(creator == null ? null : creator.getName()));
        }
        if (this.spaceManager.getSpace(spaceKey) != null) {
            throw new ResourceException("A space already exists with key " + spaceKey, Response.Status.BAD_REQUEST, ResourceErrorType.DUPLICATED_SPACE, (Object)spaceKey);
        }
        if (entity.getSpaceBlueprintId() == null) {
            throw new ResourceException("Cannot create space from Blueprint with no id.", Response.Status.BAD_REQUEST, ResourceErrorType.INVALID_BLUEPRINT);
        }
    }

    private void validateBlueprintPageEntity(ContentBlueprint blueprint, ConfluenceUser creator, Space space, Page parentPage) throws BlueprintIllegalArgumentException {
        if (space == null) {
            throw new BlueprintIllegalArgumentException("Invalid space.", ResourceErrorType.INVALID_SPACE);
        }
        if (blueprint == null) {
            throw new BlueprintIllegalArgumentException("Invalid blueprint module key specified.", ResourceErrorType.INVALID_BLUEPRINT);
        }
        String spaceKey = space.getKey();
        if (!this.permissionManager.hasCreatePermission((User)creator, (Object)space, Page.class)) {
            throw new BlueprintIllegalArgumentException("No permission to create pages in space " + spaceKey + ".", ResourceErrorType.PERMISSION_USER_CREATE_PAGE, (Object)spaceKey);
        }
        if (parentPage != null && !this.permissionManager.hasPermission((User)creator, Permission.VIEW, (Object)parentPage)) {
            throw new BlueprintIllegalArgumentException("No permission to create child pages of " + parentPage.getDisplayTitle() + ".", ResourceErrorType.PERMISSION_USER_VIEW_PAGE, (Object)parentPage.getId());
        }
    }

    private ContentTemplateRef getContentTemplateRef(ContentBlueprint blueprint, CreateBlueprintPageEntity entity) {
        if (blueprint == null) {
            return null;
        }
        String overrideTemplateId = entity.getContentTemplateId();
        if (StringUtils.isNotBlank((CharSequence)overrideTemplateId)) {
            UUID contentTemplateRefId = UUID.fromString(overrideTemplateId);
            return this.findContentTemplateRefInBlueprint(blueprint, contentTemplateRefId);
        }
        String overrideTemplateKey = entity.getContentTemplateKey();
        ContentTemplateRef ref = StringUtils.isNotBlank((CharSequence)overrideTemplateKey) ? this.findContentTemplateRefInBlueprint(blueprint, overrideTemplateKey) : blueprint.getFirstContentTemplateRef();
        return this.blueprintResolver.resolveTemplateRef(ref);
    }

    private ContentTemplateRef findContentTemplateRefInBlueprint(ContentBlueprint contentBlueprint, UUID refId) {
        for (ContentTemplateRef ref : contentBlueprint.getContentTemplateRefs()) {
            if (!ref.getId().equals(refId)) continue;
            return ref;
        }
        throw new IllegalStateException("Content blueprint has no ContentTemplateRef with id: " + refId);
    }

    private ContentTemplateRef findContentTemplateRefInBlueprint(ContentBlueprint contentBlueprint, String key) {
        ModuleCompleteKey blueprintKey = new ModuleCompleteKey(contentBlueprint.getModuleCompleteKey());
        ModuleCompleteKey templateKey = ModuleCompleteKeyUtils.getModuleCompleteKeyFromRelative(blueprintKey.getPluginKey(), key);
        String overrideTemplateKey = templateKey.getCompleteKey();
        for (ContentTemplateRef ref : contentBlueprint.getContentTemplateRefs()) {
            String blueprintTemplateKey = ref.getModuleCompleteKey();
            if (!blueprintTemplateKey.equals(overrideTemplateKey)) continue;
            return ref;
        }
        throw new IllegalStateException("Content blueprint has no ContentTemplateRef with key: " + key);
    }
}

