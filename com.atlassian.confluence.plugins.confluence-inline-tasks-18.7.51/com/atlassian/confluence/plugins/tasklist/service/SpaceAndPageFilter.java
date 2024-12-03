/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.core.bean.EntityObject
 */
package com.atlassian.confluence.plugins.tasklist.service;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.core.bean.EntityObject;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpaceAndPageFilter {
    private Set<Long> directlyConfiguredSpaceIds = Collections.emptySet();
    private Set<Long> directlyConfiguredPageIds = Collections.emptySet();
    private boolean macroInstanceHasAtLeastOnePageConfigured;
    private boolean macroInstanceHasAtLeastOneSpaceConfigured;
    private Set<Long> spaceIdsForDirectlyConfiguredPages = Collections.emptySet();

    public SpaceAndPageFilter(BulkPermissionService bulkPermissionService, PageManager pageManager, List<Long> spaceIds, List<Long> pageIds, boolean permissionExempt) {
        HashSet<Long> pageIdSet;
        if (spaceIds.isEmpty() && pageIds.isEmpty()) {
            return;
        }
        HashSet<Long> spaceIdSet = new HashSet<Long>(spaceIds);
        if (!spaceIdSet.isEmpty()) {
            this.macroInstanceHasAtLeastOneSpaceConfigured = true;
            this.directlyConfiguredSpaceIds = bulkPermissionService.getPermittedSpaceIds(AuthenticatedUserThreadLocal.get(), spaceIdSet, "VIEWSPACE");
        }
        if (!(pageIdSet = new HashSet<Long>(pageIds)).isEmpty()) {
            this.macroInstanceHasAtLeastOnePageConfigured = true;
            HashSet<Long> visiblePageIds = permissionExempt ? pageIdSet : bulkPermissionService.getVisiblePageIds(AuthenticatedUserThreadLocal.get(), pageIdSet, true);
            List visiblePages = visiblePageIds.stream().map(arg_0 -> ((PageManager)pageManager).getPage(arg_0)).collect(Collectors.toList());
            this.directlyConfiguredPageIds = visiblePages.stream().filter(page -> !this.directlyConfiguredSpaceIds.contains(page.getSpace().getId())).map(EntityObject::getId).collect(Collectors.toSet());
            this.spaceIdsForDirectlyConfiguredPages = visiblePages.stream().map(page -> page.getSpace().getId()).filter(spaceId -> !this.directlyConfiguredSpaceIds.contains(spaceId)).collect(Collectors.toSet());
        }
    }

    public boolean hasAnyConfiguredSpacesOrPages() {
        return this.macroInstanceHasAtLeastOneSpaceConfigured || this.macroInstanceHasAtLeastOnePageConfigured;
    }

    public Set<Long> getDirectlyConfiguredSpaceIds() {
        return this.directlyConfiguredSpaceIds;
    }

    public Set<Long> getDirectlyConfiguredPageIds() {
        return this.directlyConfiguredPageIds;
    }

    public Set<Long> getSpaceIdsForDirectlyConfiguredPages() {
        return this.spaceIdsForDirectlyConfiguredPages;
    }
}

