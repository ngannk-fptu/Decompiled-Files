/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.search.query.Query
 *  com.atlassian.user.security.password.Credential
 *  com.opensymphony.module.propertyset.PropertySet
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.GroupMembershipAccessor;
import com.atlassian.confluence.user.GroupResolver;
import com.atlassian.confluence.user.UserExistenceChecker;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.confluence.user.UserProfilePictureAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.security.password.Credential;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface UserAccessor
extends bucket.user.UserAccessor,
UserPreferencesAccessor,
ConfluenceUserResolver,
UserProfilePictureAccessor,
GroupResolver,
GroupMembershipAccessor,
UserExistenceChecker {
    @Deprecated
    public static final String GROUP_CONFLUENCE_USERS = "confluence-users";
    public static final String GROUP_CONFLUENCE_ADMINS = "confluence-administrators";
    @Deprecated
    public static final List<String> DEFAULT_GROUP_NAMES = Collections.unmodifiableList(Arrays.asList("confluence-users", "confluence-administrators"));
    public static final String PROFILE_PICTURE_BUILTIN_PATH = "/images/icons/profilepics/";

    @Deprecated
    public User getUser(String var1);

    @Override
    public @Nullable ConfluenceUser getUserByName(String var1);

    @Override
    public @Nullable ConfluenceUser getUserByKey(UserKey var1);

    @Override
    public @Nullable ConfluenceUser getExistingUserByKey(UserKey var1);

    @Override
    public boolean exists(String var1);

    public ConfluenceUser createUser(User var1, Credential var2);

    public boolean isReadOnly(User var1);

    public boolean isReadOnly(Group var1);

    @Override
    public ProfilePictureInfo getUserProfilePicture(@Nullable User var1);

    @Override
    public ConfluenceUserPreferences getConfluenceUserPreferences(@Nullable User var1);

    @Deprecated
    public boolean isSuperUser(User var1);

    public List<Group> getGroupsAsList();

    @Override
    public List<String> getMemberNamesAsList(Group var1);

    public Iterable<ConfluenceUser> getMembers(Group var1);

    public List<String> getGroupNames(User var1);

    public List<String> getGroupNamesForUserName(String var1);

    public List<Group> getWriteableGroups();

    @Deprecated
    public PropertySet getPropertySet(User var1);

    @Deprecated
    public PropertySet getPropertySet(ConfluenceUser var1);

    public List<String> getUserNamesWithConfluenceAccess();

    public int countLicenseConsumingUsers();

    public List<User> findUsersAsList(Query<User> var1) throws EntityException;

    public void setUserProfilePicture(User var1, Attachment var2);

    public void setUserProfilePicture(User var1, String var2);

    @Deprecated
    public List<String> getAllDefaultGroupNames();

    @Deprecated
    public String getNewUserDefaultGroupName();

    public ConfluenceUser renameUser(ConfluenceUser var1, String var2) throws EntityException;

    @Override
    public List<ConfluenceUser> getUsersByUserKeys(List<UserKey> var1);

    public List<Group> getGroupsByGroupNames(List<String> var1);

    public int countUnsyncedUsers();

    @Deprecated
    public boolean isLicensedToAddMoreUsers();

    @Deprecated
    public boolean isDeactivated(User var1);

    @Deprecated
    public boolean isDeactivated(String var1);

    public void alterPassword(User var1, String var2, String var3) throws EntityException;
}

