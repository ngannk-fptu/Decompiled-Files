/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.internal.pages.persistence.PageDaoInternal;
import com.atlassian.confluence.internal.spaces.persistence.SpaceDaoInternal;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.List;
import java.util.Set;

@Internal
public class RegularEntitiesAndPermissionsHelper {
    private final SpaceDaoInternal spaceDaoInternal;
    private final SpacePermissionManager spacePermissionManager;
    private final PageDaoInternal pageDao;

    public RegularEntitiesAndPermissionsHelper(SpaceDaoInternal spaceDaoInternal, SpacePermissionManager spacePermissionManager, PageDaoInternal pageDao) {
        this.spaceDaoInternal = spaceDaoInternal;
        this.spacePermissionManager = spacePermissionManager;
        this.pageDao = pageDao;
    }

    public Space getSpaceById(long spaceId) {
        return this.spaceDaoInternal.getById(spaceId);
    }

    public Page getPageById(long pageId) {
        return (Page)this.pageDao.getById(pageId);
    }

    public boolean isSpacePermitted(ConfluenceUser confluenceUser, long spaceId, String spacePermissionType) {
        Space space = this.getSpaceById(spaceId);
        if (space == null) {
            return false;
        }
        return this.spacePermissionManager.hasPermission(spacePermissionType, space, confluenceUser);
    }

    public List<String> findAllSpaceKeys() {
        return this.spaceDaoInternal.findAllSpaceKeys();
    }

    public List<Page> getPagesByIds(Set<Long> pageIds) {
        return this.pageDao.getPagesByIds(pageIds);
    }
}

