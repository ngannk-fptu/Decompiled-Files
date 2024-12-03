/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.plugins.createcontent.BlueprintConstants;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.SpaceBandanaContext;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.services.BlueprintResolver;
import com.atlassian.confluence.plugins.createcontent.services.PromotedBlueprintService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultPromotedBlueprintService
implements PromotedBlueprintService {
    private final ContentBlueprintManager contentBlueprintManager;
    private final BandanaManager bandanaManager;
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final BlueprintResolver resolver;
    public static final String KEY_PROMOTED_BPS = "promotedBps";
    private static final Logger log = LoggerFactory.getLogger(DefaultPromotedBlueprintService.class);

    @Autowired
    public DefaultPromotedBlueprintService(ContentBlueprintManager contentBlueprintManager, @ComponentImport BandanaManager bandanaManager, @ComponentImport SpaceManager spaceManager, @ComponentImport PermissionManager permissionManager, BlueprintResolver resolver) {
        this.contentBlueprintManager = contentBlueprintManager;
        this.bandanaManager = bandanaManager;
        this.spaceManager = spaceManager;
        this.permissionManager = permissionManager;
        this.resolver = resolver;
    }

    @Override
    @Nonnull
    public Collection<ContentBlueprint> getPromotedBlueprints(@Nonnull Collection<ContentBlueprint> contentBlueprints, @Nullable Space space) {
        ArrayList promotedBlueprints = Lists.newArrayList();
        ArrayList blueprintsInSpace = Lists.newArrayList(contentBlueprints);
        if (space == null) {
            return promotedBlueprints;
        }
        List<String> promotedBpsStringUuids = this.retrievePromotedIds(space);
        if (promotedBpsStringUuids.isEmpty()) {
            return promotedBlueprints;
        }
        blueprintsInSpace.add(BlueprintConstants.BLOG_POST_BLUEPRINT);
        blueprintsInSpace.add(BlueprintConstants.BLANK_PAGE_BLUEPRINT);
        for (UUID id : this.convertStringToUuid(promotedBpsStringUuids)) {
            ContentBlueprint blueprint = this.matchIdWithBlueprint(id, blueprintsInSpace);
            if (blueprint == null) continue;
            promotedBlueprints.add(blueprint);
        }
        return promotedBlueprints;
    }

    @Override
    public boolean promoteBlueprint(@Nonnull String blueprintId, @Nonnull String spaceKey) throws BlueprintIllegalArgumentException {
        return this.toggleBlueprintPromotion(blueprintId, spaceKey, true);
    }

    @Override
    public boolean demoteBlueprint(@Nonnull String blueprintId, @Nonnull String spaceKey) throws BlueprintIllegalArgumentException {
        return this.toggleBlueprintPromotion(blueprintId, spaceKey, false);
    }

    private boolean toggleBlueprintPromotion(@Nonnull String blueprintId, @Nonnull String spaceKey, boolean promote) throws BlueprintIllegalArgumentException {
        boolean didSomething;
        if (StringUtils.isBlank((CharSequence)blueprintId)) {
            throw new BlueprintIllegalArgumentException("Blueprint UUID is required to promote/demote blueprint", ResourceErrorType.PARAMETER_MISSING, (Object)"blueprintId");
        }
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            throw new BlueprintIllegalArgumentException("Space key is required to promote/demote blueprint with id: " + blueprintId, ResourceErrorType.PARAMETER_MISSING, (Object)"spaceKey");
        }
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            throw new BlueprintIllegalArgumentException("Space with key '" + spaceKey + "' could not be found.", ResourceErrorType.NOT_FOUND_SPACE, (Object)spaceKey);
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, (Object)space)) {
            throw new BlueprintIllegalArgumentException("Only space administrators can enable / disable plugin modules per space", ResourceErrorType.PERMISSION_USER_ADMIN_SPACE, (Object)spaceKey);
        }
        if (this.contentBlueprintManager.getById(UUID.fromString(blueprintId)) == null) {
            throw new BlueprintIllegalArgumentException("Valid blueprint UUID is required to promote/demote blueprint", ResourceErrorType.INVALID_ID_BLUEPRINT, (Object)blueprintId);
        }
        HashSet promotedBpsIds = Sets.newHashSet(this.retrievePromotedIds(space));
        if (promote) {
            didSomething = promotedBpsIds.add(blueprintId);
        } else {
            boolean bl = didSomething = promotedBpsIds.remove(blueprintId) || this.removeStaleId(blueprintId, promotedBpsIds, spaceKey);
        }
        if (didSomething) {
            this.storePromotedIds(space, promotedBpsIds);
        }
        return didSomething;
    }

    private boolean removeStaleId(String staleId, Set<String> promotedBpsIds, String spaceKey) {
        for (String promotedBpsId : promotedBpsIds) {
            ContentBlueprint newerBlueprint = this.resolver.resolveContentBlueprint(promotedBpsId, spaceKey);
            if (!newerBlueprint.getId().toString().equals(staleId)) continue;
            return promotedBpsIds.remove(promotedBpsId);
        }
        return false;
    }

    @Override
    public void promoteBlueprints(@Nonnull List<String> blueprintIds, @Nonnull Space space) {
        List<String> ids = this.retrievePromotedIds(space);
        HashSet promotedBpsIds = Sets.newHashSet(ids);
        promotedBpsIds.addAll(blueprintIds);
        this.storePromotedIds(space, promotedBpsIds);
    }

    private void storePromotedIds(Space space, Set<String> promotedBpsIds) {
        SpaceBandanaContext context = new SpaceBandanaContext(space);
        this.bandanaManager.setValue((BandanaContext)context, KEY_PROMOTED_BPS, (Object)Lists.newArrayList(promotedBpsIds));
    }

    @Nonnull
    private List<String> retrievePromotedIds(Space space) {
        SpaceBandanaContext context = new SpaceBandanaContext(space);
        List ids = (List)this.bandanaManager.getValue((BandanaContext)context, KEY_PROMOTED_BPS);
        if (ids == null) {
            ids = Lists.newArrayList();
        }
        return ids;
    }

    private ContentBlueprint matchIdWithBlueprint(@Nonnull UUID id, @Nonnull Collection<ContentBlueprint> contentBlueprints) {
        ContentBlueprint blueprint = this.getBlueprintFromListById(id, contentBlueprints);
        if (blueprint != null) {
            return blueprint;
        }
        blueprint = (ContentBlueprint)this.contentBlueprintManager.getById(id);
        if (blueprint == null) {
            log.warn("blueprint not found with id: " + id);
            return null;
        }
        if ((blueprint = this.getBlueprintFromListByKey(blueprint, contentBlueprints)) == null) {
            log.warn("blueprint not found with id: " + id);
            return null;
        }
        return blueprint;
    }

    private ContentBlueprint getBlueprintFromListById(@Nonnull UUID uuid, @Nonnull Collection<ContentBlueprint> contentBlueprints) {
        for (ContentBlueprint contentBlueprint : contentBlueprints) {
            if (!uuid.equals(contentBlueprint.getId())) continue;
            return contentBlueprint;
        }
        return null;
    }

    private ContentBlueprint getBlueprintFromListByKey(@Nonnull ContentBlueprint blueprint, @Nonnull Collection<ContentBlueprint> contentBlueprints) {
        String blueprintModuleCompleteKey = blueprint.getModuleCompleteKey();
        for (ContentBlueprint contentBlueprint : contentBlueprints) {
            if (!blueprintModuleCompleteKey.equals(contentBlueprint.getModuleCompleteKey())) continue;
            return contentBlueprint;
        }
        return null;
    }

    private List<UUID> convertStringToUuid(@Nonnull List<String> stringIds) {
        ArrayList uuids = Lists.newArrayList();
        for (String id : stringIds) {
            uuids.add(UUID.fromString(id));
        }
        return uuids;
    }
}

