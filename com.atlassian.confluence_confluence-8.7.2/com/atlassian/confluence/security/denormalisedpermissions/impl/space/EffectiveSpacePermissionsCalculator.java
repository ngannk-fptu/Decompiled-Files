/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.space;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.security.persistence.dao.hibernate.SpacePermissionDTOLight;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

@Internal
public class EffectiveSpacePermissionsCalculator {
    static EffectivePermissions calculateEffectivePermissions(List<SpacePermissionDTOLight> realSpacePermissions) {
        if (realSpacePermissions == null) {
            return new EffectivePermissions(AccessType.RESTRICTED_TO_LIST_OF_USERS_AND_GROUPS, Collections.emptySet(), Collections.emptySet());
        }
        boolean availableForAnonymous = realSpacePermissions.stream().anyMatch(SpacePermissionDTOLight::isAvailableForAnonymous);
        if (availableForAnonymous) {
            return new EffectivePermissions(AccessType.ANONYMOUS);
        }
        boolean availableForAuthenticatedUsers = realSpacePermissions.stream().anyMatch(SpacePermissionDTOLight::isAvailableForAuthenticatedUsers);
        if (availableForAuthenticatedUsers) {
            return new EffectivePermissions(AccessType.AUTHENTICATED_USER);
        }
        Set<String> userList = realSpacePermissions.stream().map(permission -> permission.getUserKey() != null ? permission.getUserKey().getStringValue() : null).filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
        Set<String> groupList = realSpacePermissions.stream().map(SpacePermissionDTOLight::getGroupName).filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
        return new EffectivePermissions(AccessType.RESTRICTED_TO_LIST_OF_USERS_AND_GROUPS, userList, groupList);
    }

    static class EffectivePermissions {
        private final AccessType accessType;
        private final Set<String> usersWithAccess;
        private final Set<String> groupsWithAccess;

        public EffectivePermissions(AccessType accessType) {
            if (accessType == AccessType.RESTRICTED_TO_LIST_OF_USERS_AND_GROUPS) {
                throw new IllegalArgumentException("Access type " + accessType + " requires a list of allowed users and groups");
            }
            this.accessType = accessType;
            this.usersWithAccess = new HashSet<String>();
            this.groupsWithAccess = new HashSet<String>();
        }

        public EffectivePermissions(AccessType accessType, Set<String> usersWithAccess, Set<String> groupsWithAccess) {
            if (accessType != AccessType.RESTRICTED_TO_LIST_OF_USERS_AND_GROUPS) {
                throw new IllegalArgumentException("Access type " + accessType + " does not need a list of allowed users or groups");
            }
            this.accessType = accessType;
            this.usersWithAccess = usersWithAccess;
            this.groupsWithAccess = groupsWithAccess;
        }

        public Set<String> getUsersWithAccess() {
            return this.usersWithAccess;
        }

        public Set<String> getGroupsWithAccess() {
            return this.groupsWithAccess;
        }

        public AccessType getAccessType() {
            return this.accessType;
        }
    }

    static enum AccessType {
        ANONYMOUS,
        AUTHENTICATED_USER,
        RESTRICTED_TO_LIST_OF_USERS_AND_GROUPS;

    }
}

