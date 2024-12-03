/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.EnsuresNonNullIf
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.core.bean.EntityObject;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SpacePermission
extends ConfluenceEntityObject
implements Serializable {
    public static final String USE_CONFLUENCE_PERMISSION = "USECONFLUENCE";
    public static final String LIMITED_USE_CONFLUENCE_PERMISSION = "LIMITEDUSECONFLUENCE";
    public static final String UPDATE_USER_STATUS_PERMISSION = "UPDATEUSERSTATUS";
    @Deprecated
    public static final String VIEW_USER_PROFILES_PERMISSION = "VIEWUSERPROFILES";
    public static final String BROWSE_USERS_PERMISSION = "VIEWUSERPROFILES";
    public static final String SYSTEM_ADMINISTRATOR_PERMISSION = "SYSTEMADMINISTRATOR";
    public static final String CONFLUENCE_ADMINISTRATOR_PERMISSION = "ADMINISTRATECONFLUENCE";
    public static final String PERSONAL_SPACE_PERMISSION = "PERSONALSPACE";
    public static final String CREATE_SPACE_PERMISSION = "CREATESPACE";
    public static final String PROFILE_ATTACHMENT_PERMISSION = "PROFILEATTACHMENTS";
    public static final String VIEWSPACE_PERMISSION = "VIEWSPACE";
    public static final String REMOVE_OWN_CONTENT_PERMISSION = "REMOVEOWNCONTENT";
    public static final String COMMENT_PERMISSION = "COMMENT";
    public static final String CREATEEDIT_PAGE_PERMISSION = "EDITSPACE";
    public static final String ADMINISTER_SPACE_PERMISSION = "SETSPACEPERMISSIONS";
    public static final String REMOVE_PAGE_PERMISSION = "REMOVEPAGE";
    public static final String REMOVE_COMMENT_PERMISSION = "REMOVECOMMENT";
    public static final String REMOVE_BLOG_PERMISSION = "REMOVEBLOG";
    public static final String CREATE_ATTACHMENT_PERMISSION = "CREATEATTACHMENT";
    public static final String REMOVE_ATTACHMENT_PERMISSION = "REMOVEATTACHMENT";
    public static final String EDITBLOG_PERMISSION = "EDITBLOG";
    public static final String EXPORT_SPACE_PERMISSION = "EXPORTSPACE";
    public static final String REMOVE_MAIL_PERMISSION = "REMOVEMAIL";
    public static final String SET_PAGE_PERMISSIONS_PERMISSION = "SETPAGEPERMISSIONS";
    public static final Collection<String> PERMISSION_TYPES;
    public static final Collection<String> GENERIC_SPACE_PERMISSIONS;
    public static final Collection<String> READ_ONLY_SPACE_PERMISSIONS;
    public static final Collection<String> GLOBAL_PERMISSIONS;
    private static final Collection<String> INVALID_LIMITED_AUTHENTICATED_ACCESS_PERMISSIONS;
    public static final Collection<String> INVALID_ANONYMOUS_PERMISSIONS;
    private String type;
    private Space space;
    private String group;
    private ConfluenceUser userSubject;
    private String allUsersSubject;

    @Deprecated
    public SpacePermission() {
    }

    @Deprecated
    public SpacePermission(String type) {
        this(type, null, null, (ConfluenceUser)null);
    }

    @Deprecated
    public SpacePermission(String type, Space space) {
        this(type, space, null, (ConfluenceUser)null);
    }

    @Deprecated
    public SpacePermission(String type, @Nullable Space space, String group) {
        this(type, space, group, (ConfluenceUser)null);
    }

    @Deprecated
    public SpacePermission(String type, @Nullable Space space, @Nullable String group, @Nullable String userName) {
        this.type = type;
        this.space = space;
        this.group = group;
        if (StringUtils.isNotBlank((CharSequence)userName)) {
            this.userSubject = FindUserHelper.getUserByUsername(userName);
            Preconditions.checkArgument((this.userSubject != null ? 1 : 0) != 0, (Object)("No user could be found for the username '" + userName + "'"));
        } else {
            this.userSubject = null;
        }
    }

    @Deprecated
    public SpacePermission(String type, @Nullable Space space, @Nullable String group, @Nullable ConfluenceUser subject) {
        this.type = type;
        this.space = space;
        this.group = group;
        this.userSubject = subject;
    }

    @Deprecated
    public SpacePermission(SpacePermission spacePermission) {
        this.type = spacePermission.getType();
        this.space = spacePermission.getSpace();
        this.group = spacePermission.getGroup();
        this.userSubject = spacePermission.userSubject;
        this.allUsersSubject = spacePermission.allUsersSubject;
    }

    public @Nullable Space getSpace() {
        return this.space;
    }

    public void setSpace(@Nullable Space space) {
        this.space = space;
    }

    public long getSpaceId() {
        return Optional.ofNullable(this.getSpace()).map(EntityObject::getId).orElse(0L);
    }

    public String getType() {
        return this.type;
    }

    @Deprecated
    public void setType(String type) {
        this.type = type;
    }

    public @Nullable String getGroup() {
        return this.group;
    }

    @Deprecated
    public void setGroup(String group) {
        this.group = group;
        if (group != null) {
            this.allUsersSubject = null;
        }
    }

    public String getAllUsersSubject() {
        return this.allUsersSubject;
    }

    @VisibleForTesting
    protected void setAllUsersSubject(String storedValue) {
        this.allUsersSubject = storedValue;
        if (Objects.equals(storedValue, AccessSubject.ALL_AUTHENTICATED_USERS.value)) {
            this.userSubject = null;
            this.group = null;
        }
    }

    @Deprecated
    public @Nullable String getUserName() {
        if (this.userSubject != null) {
            return this.userSubject.getName();
        }
        return null;
    }

    @Deprecated
    public void setUserName(String userName) {
        this.setUserSubject(FindUserHelper.getUserByUsername(userName));
    }

    public @Nullable ConfluenceUser getUserSubject() {
        return this.userSubject;
    }

    public void setUserSubject(@Nullable ConfluenceUser user) {
        this.userSubject = user;
        if (user != null) {
            this.allUsersSubject = null;
        }
    }

    @EnsuresNonNullIf(expression={"getUserSubject()", "getUserName()"}, result=true)
    public boolean isUserPermission() {
        return this.userSubject != null;
    }

    @EnsuresNonNullIf(expression={"getGroup()"}, result=true)
    public boolean isGroupPermission() {
        return StringUtils.isNotBlank((CharSequence)this.group);
    }

    public boolean isAnonymousPermission() {
        return !this.isPermissionForUserOrGroup() && Objects.equals(this.allUsersSubject, AccessSubject.ANONYMOUS_USERS.value);
    }

    public boolean isAuthenticatedUsersPermission() {
        return !this.isPermissionForUserOrGroup() && Objects.equals(this.allUsersSubject, AccessSubject.ALL_AUTHENTICATED_USERS.value);
    }

    private boolean isPermissionForUserOrGroup() {
        return this.isUserPermission() || this.isGroupPermission();
    }

    @EnsuresNonNullIf(expression={"getSpace()"}, result=false)
    public boolean isGlobalPermission() {
        return !this.isSpacePermission();
    }

    @EnsuresNonNullIf(expression={"getSpace()"}, result=true)
    public boolean isSpacePermission() {
        return this.getSpace() != null;
    }

    public String toString() {
        return "[" + this.getType() + "," + this.getSpaceId() + "," + this.getGroup() + "," + this.getUserName() + "," + this.allUsersSubjectAsString() + "]";
    }

    private String allUsersSubjectAsString() {
        if (this.isAuthenticatedUsersPermission()) {
            return AccessSubject.ALL_AUTHENTICATED_USERS.value;
        }
        return null;
    }

    public boolean equals(Object obj) {
        SpacePermission perm;
        if (obj instanceof SpacePermission && this.type.equals((perm = (SpacePermission)obj).getType())) {
            return this.spacesEqual(perm) && this.permissionSubjectsEqual(perm);
        }
        return false;
    }

    private boolean permissionSubjectsEqual(@NonNull SpacePermission other) {
        if (!Objects.equals(this.group, other.group)) {
            return false;
        }
        if (!Objects.equals(this.getUserName(), other.getUserName())) {
            return false;
        }
        if (this.isAnonymousPermission() != other.isAnonymousPermission()) {
            return false;
        }
        return this.isAuthenticatedUsersPermission() == other.isAuthenticatedUsersPermission();
    }

    private boolean spacesEqual(@NonNull SpacePermission other) {
        return this.space == null && other.getSpace() == null || this.space != null && other.getSpace() != null && this.space.getId() == other.getSpace().getId();
    }

    public int hashCode() {
        int result = 29;
        result = 29 * result + (this.type != null ? this.type.hashCode() : 0);
        result = 29 * result + (this.space != null ? (int)(this.space.getId() ^ this.space.getId() >>> 32) : 0);
        result = 29 * result + (this.group != null ? this.group.hashCode() : 0);
        result = 29 * result + (this.userSubject != null ? this.userSubject.getName().hashCode() : 0);
        result = 29 * result + (this.allUsersSubject != null ? this.allUsersSubject.hashCode() : 0);
        return result;
    }

    @Deprecated
    public String toFormParameterName(String parameterType) {
        StringBuilder result = new StringBuilder("confluence_").append(parameterType).append("_");
        result.append(this.getType().toLowerCase(Locale.ENGLISH)).append("_");
        ConfluenceUser userSubject = this.getUserSubject();
        if (userSubject != null) {
            result.append("user_").append(userSubject.getName());
        } else if (this.isGroupPermission()) {
            result.append("group_").append(this.getGroup());
        } else if (this.isAnonymousPermission()) {
            result.append("anonymous");
        } else if (this.isAuthenticatedUsersPermission()) {
            result.append("authenticated");
        }
        return result.toString();
    }

    public boolean isGuardPermission() {
        String permissionType = this.getType();
        return USE_CONFLUENCE_PERMISSION.equals(permissionType) || LIMITED_USE_CONFLUENCE_PERMISSION.equals(permissionType) || VIEWSPACE_PERMISSION.equals(permissionType);
    }

    public boolean isDependentOn(SpacePermission otherPermission) {
        boolean isSameSpacePermission;
        if (!otherPermission.isGuardPermission()) {
            return false;
        }
        ConfluenceUser userSubject = this.getUserSubject();
        if (userSubject != null && !userSubject.equals(otherPermission.getUserSubject())) {
            return false;
        }
        String group = this.getGroup();
        if (group != null && !group.equals(otherPermission.getGroup())) {
            return false;
        }
        if (this.isGlobalPermission() && this.isAnonymousPermission()) {
            return otherPermission.isGlobalPermission() && otherPermission.isAnonymousPermission();
        }
        if (this.isGlobalPermission() && this.isAuthenticatedUsersPermission()) {
            return otherPermission.isGlobalPermission() && otherPermission.isAuthenticatedUsersPermission();
        }
        if (this.isGlobalPermission()) {
            return otherPermission.isGlobalPermission();
        }
        if (otherPermission.isGlobalPermission()) {
            return false;
        }
        boolean bl = isSameSpacePermission = this.getSpaceId() == otherPermission.getSpaceId();
        if (isSameSpacePermission && this.isAnonymousPermission()) {
            return otherPermission.isAnonymousPermission();
        }
        if (isSameSpacePermission && this.isAuthenticatedUsersPermission()) {
            return otherPermission.isAuthenticatedUsersPermission();
        }
        return isSameSpacePermission;
    }

    public boolean isInvalidAnonymousPermission() {
        return this.isAnonymousPermission() && !SpacePermission.isValidAnonymousPermission(this.getType());
    }

    public static boolean isValidAnonymousPermission(String permissionType) {
        return !INVALID_ANONYMOUS_PERMISSIONS.contains(permissionType);
    }

    public boolean isInvalidAuthenticatedUsersPermission() {
        return this.isAuthenticatedUsersPermission() && !SpacePermission.isValidAuthenticatedUsersPermission(this.getType());
    }

    public static boolean isValidAuthenticatedUsersPermission(String permissionType) {
        return !INVALID_LIMITED_AUTHENTICATED_ACCESS_PERMISSIONS.contains(permissionType);
    }

    public static SpacePermission createAnonymousSpacePermission(String type, @Nullable Space space) {
        return SpacePermission.createUserSpacePermission(type, space, (ConfluenceUser)null);
    }

    @Deprecated
    public static SpacePermission createUserSpacePermission(String type, @Nullable Space space, String userName) {
        return new SpacePermission(type, space, null, userName);
    }

    public static SpacePermission createUserSpacePermission(String type, @Nullable Space space, ConfluenceUser subject) {
        return new SpacePermission(type, space, null, subject);
    }

    public static SpacePermission createGroupSpacePermission(String type, @Nullable Space space, String group) {
        return new SpacePermission(type, space, group, (ConfluenceUser)null);
    }

    public static SpacePermission createAuthenticatedUsersSpacePermission(String type, @Nullable Space space) {
        SpacePermission newSpacePermission = SpacePermission.createUserSpacePermission(type, space, (ConfluenceUser)null);
        newSpacePermission.setAllUsersSubject(AccessSubject.ALL_AUTHENTICATED_USERS.value);
        return newSpacePermission;
    }

    static {
        LinkedHashSet<String> permissionTypes = new LinkedHashSet<String>();
        permissionTypes.add(USE_CONFLUENCE_PERMISSION);
        permissionTypes.add(LIMITED_USE_CONFLUENCE_PERMISSION);
        permissionTypes.add(UPDATE_USER_STATUS_PERMISSION);
        permissionTypes.add("VIEWUSERPROFILES");
        permissionTypes.add(SYSTEM_ADMINISTRATOR_PERMISSION);
        permissionTypes.add(CONFLUENCE_ADMINISTRATOR_PERMISSION);
        permissionTypes.add(PERSONAL_SPACE_PERMISSION);
        permissionTypes.add(CREATE_SPACE_PERMISSION);
        permissionTypes.add(VIEWSPACE_PERMISSION);
        permissionTypes.add(REMOVE_OWN_CONTENT_PERMISSION);
        permissionTypes.add(COMMENT_PERMISSION);
        permissionTypes.add(CREATEEDIT_PAGE_PERMISSION);
        permissionTypes.add(ADMINISTER_SPACE_PERMISSION);
        permissionTypes.add(REMOVE_PAGE_PERMISSION);
        permissionTypes.add(REMOVE_COMMENT_PERMISSION);
        permissionTypes.add(REMOVE_BLOG_PERMISSION);
        permissionTypes.add(CREATE_ATTACHMENT_PERMISSION);
        permissionTypes.add(REMOVE_ATTACHMENT_PERMISSION);
        permissionTypes.add(EDITBLOG_PERMISSION);
        permissionTypes.add(EXPORT_SPACE_PERMISSION);
        permissionTypes.add(REMOVE_MAIL_PERMISSION);
        permissionTypes.add(SET_PAGE_PERMISSIONS_PERMISSION);
        permissionTypes.add(PROFILE_ATTACHMENT_PERMISSION);
        PERMISSION_TYPES = Collections.unmodifiableSet(permissionTypes);
        LinkedHashSet<String> spacePermissions = new LinkedHashSet<String>();
        spacePermissions.add(VIEWSPACE_PERMISSION);
        spacePermissions.add(REMOVE_OWN_CONTENT_PERMISSION);
        spacePermissions.add(COMMENT_PERMISSION);
        spacePermissions.add(CREATEEDIT_PAGE_PERMISSION);
        spacePermissions.add(ADMINISTER_SPACE_PERMISSION);
        spacePermissions.add(REMOVE_PAGE_PERMISSION);
        spacePermissions.add(REMOVE_COMMENT_PERMISSION);
        spacePermissions.add(REMOVE_BLOG_PERMISSION);
        spacePermissions.add(CREATE_ATTACHMENT_PERMISSION);
        spacePermissions.add(REMOVE_ATTACHMENT_PERMISSION);
        spacePermissions.add(EDITBLOG_PERMISSION);
        spacePermissions.add(EXPORT_SPACE_PERMISSION);
        spacePermissions.add(REMOVE_MAIL_PERMISSION);
        spacePermissions.add(SET_PAGE_PERMISSIONS_PERMISSION);
        GENERIC_SPACE_PERMISSIONS = Collections.unmodifiableSet(spacePermissions);
        LinkedHashSet<String> readOnlySpacePermissions = new LinkedHashSet<String>();
        readOnlySpacePermissions.add(USE_CONFLUENCE_PERMISSION);
        readOnlySpacePermissions.add(LIMITED_USE_CONFLUENCE_PERMISSION);
        readOnlySpacePermissions.add("VIEWUSERPROFILES");
        readOnlySpacePermissions.add(SYSTEM_ADMINISTRATOR_PERMISSION);
        readOnlySpacePermissions.add(CONFLUENCE_ADMINISTRATOR_PERMISSION);
        readOnlySpacePermissions.add(VIEWSPACE_PERMISSION);
        readOnlySpacePermissions.add(EXPORT_SPACE_PERMISSION);
        READ_ONLY_SPACE_PERMISSIONS = Collections.unmodifiableSet(readOnlySpacePermissions);
        LinkedHashSet<String> globalPermissions = new LinkedHashSet<String>();
        globalPermissions.add(USE_CONFLUENCE_PERMISSION);
        globalPermissions.add(LIMITED_USE_CONFLUENCE_PERMISSION);
        globalPermissions.add(SYSTEM_ADMINISTRATOR_PERMISSION);
        globalPermissions.add(CONFLUENCE_ADMINISTRATOR_PERMISSION);
        globalPermissions.add(PERSONAL_SPACE_PERMISSION);
        globalPermissions.add(CREATE_SPACE_PERMISSION);
        globalPermissions.add(PROFILE_ATTACHMENT_PERMISSION);
        globalPermissions.add(UPDATE_USER_STATUS_PERMISSION);
        GLOBAL_PERMISSIONS = Collections.unmodifiableSet(globalPermissions);
        ImmutableSet validLimitedAuthenticatedAccessPermissions = ImmutableSet.builder().add((Object)LIMITED_USE_CONFLUENCE_PERMISSION).add((Object)"VIEWUSERPROFILES").add((Object)VIEWSPACE_PERMISSION).build();
        INVALID_LIMITED_AUTHENTICATED_ACCESS_PERMISSIONS = Sets.difference((Set)Sets.newHashSet(PERMISSION_TYPES), (Set)validLimitedAuthenticatedAccessPermissions);
        INVALID_ANONYMOUS_PERMISSIONS = ImmutableSet.builder().add((Object)SYSTEM_ADMINISTRATOR_PERMISSION).add((Object)CONFLUENCE_ADMINISTRATOR_PERMISSION).add((Object)PERSONAL_SPACE_PERMISSION).add((Object)CREATE_SPACE_PERMISSION).add((Object)SET_PAGE_PERMISSIONS_PERMISSION).add((Object)ADMINISTER_SPACE_PERMISSION).add((Object)UPDATE_USER_STATUS_PERMISSION).add((Object)LIMITED_USE_CONFLUENCE_PERMISSION).build();
    }

    private static enum AccessSubject {
        ALL_AUTHENTICATED_USERS("authenticated-users"),
        ANONYMOUS_USERS(null);

        final String value;

        private AccessSubject(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }
}

