/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public final class IndexablePermissionSetFilter {
    public static Collection<ContentPermissionSet> filterPermissionSets(ContentEntityObject contentEntity, Collection<ContentPermissionSet> directPermissions, Collection<ContentPermissionSet> inheritedPermissions) {
        return IndexablePermissionSetFilter.filterPermissionSets(contentEntity, IndexablePermissionSetFilter.permissionsByType(directPermissions, "View").findFirst().orElse(null), IndexablePermissionSetFilter.permissionsByType(directPermissions, "Share").findFirst().orElse(null), IndexablePermissionSetFilter.permissionsByType(inheritedPermissions, "View").collect(Collectors.toList()));
    }

    public static Collection<ContentPermissionSet> filterPermissionSets(ContentEntityObject contentEntity, @Nullable ContentPermissionSet directViewPermissions, @Nullable ContentPermissionSet directSharedPermissions, Collection<ContentPermissionSet> inheritedViewPermissions) {
        ArrayList<ContentPermissionSet> results = new ArrayList<ContentPermissionSet>();
        if (directViewPermissions != null) {
            results.add(directViewPermissions);
        }
        results.addAll(inheritedViewPermissions);
        if (contentEntity.isDraft() && contentEntity.getCreator() != null) {
            results.add(IndexablePermissionSetFilter.buildSharedPermissionSet(contentEntity, directSharedPermissions));
        }
        return ImmutableList.copyOf(results);
    }

    private static ContentPermissionSet buildSharedPermissionSet(ContentEntityObject contentEntity, @Nullable ContentPermissionSet directSharedPermissions) {
        ContentPermissionSet permissionSet = Optional.ofNullable(directSharedPermissions).orElseGet(() -> new ContentPermissionSet("Share", contentEntity));
        permissionSet.addContentPermission(ContentPermission.createUserPermission("Share", contentEntity.getCreator()));
        return permissionSet;
    }

    private static Stream<ContentPermissionSet> permissionsByType(Collection<ContentPermissionSet> permissionSets, String type) {
        return permissionSets.stream().filter(cps -> type.equals(cps.getType()));
    }
}

