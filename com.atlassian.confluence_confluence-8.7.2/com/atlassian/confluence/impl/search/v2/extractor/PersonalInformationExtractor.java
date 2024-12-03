/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.HasBackingUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.UnknownUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonalInformationExtractor
implements Extractor2 {
    private static final Logger log = LoggerFactory.getLogger(PersonalInformationExtractor.class);
    private final UserAccessor userAccessor;
    private final PermissionManager permissionManager;

    public PersonalInformationExtractor(UserAccessor userAccessor, PermissionManager permissionManager) {
        this.userAccessor = Objects.requireNonNull(userAccessor);
        this.permissionManager = Objects.requireNonNull(permissionManager);
    }

    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        if (!(searchable instanceof PersonalInformation)) {
            return resultBuilder.build();
        }
        PersonalInformation pi = (PersonalInformation)searchable;
        String username = pi.getUsername();
        if (username == null) {
            log.warn("PersonalInformation {} is missing username", (Object)pi.getId());
            return resultBuilder.build();
        }
        if (this.neverModified(pi)) {
            resultBuilder.add((Object)SearchFieldMappings.LAST_MODIFICATION_DATE.createField(new Date(0L)));
        }
        boolean shadowed = false;
        ConfluenceUser confluenceUser = pi.getUser();
        if (confluenceUser instanceof HasBackingUser) {
            User backingUser = ((HasBackingUser)((Object)pi.getUser())).getBackingUser();
            shadowed = backingUser instanceof UnknownUser;
        }
        resultBuilder.add((Object)SearchFieldMappings.IS_SHADOWED_USER.createField(shadowed));
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        boolean deactivated = user == null ? this.userAccessor.isDeactivated(username) : this.userAccessor.isDeactivated(user);
        resultBuilder.add((Object)SearchFieldMappings.IS_DEACTIVATED_USER.createField(deactivated));
        if (user != null) {
            ProfilePictureInfo profilePic = this.userAccessor.getUserProfilePicture(user);
            resultBuilder.add((Object)SearchFieldMappings.PROFILE_PICTURE_URL.createField(profilePic.getUriReference()));
            UserKey userKey = user.getKey();
            resultBuilder.add((Object)SearchFieldMappings.USER_KEY.createField(userKey.getStringValue()));
            resultBuilder.add((Object)SearchFieldMappings.FULL_NAME.createField(user.getFullName()));
            resultBuilder.add((Object)SearchFieldMappings.FULL_NAME_UNTOKENIZED.createField(user.getFullName()));
            resultBuilder.add((Object)SearchFieldMappings.USER_NAME.createField(user.getName()));
            resultBuilder.add((Object)SearchFieldMappings.EMAIL.createField(user.getEmail()));
            resultBuilder.add((Object)SearchFieldMappings.PERSONAL_INFORMATION_HAS_PERSONAL_SPACE.createField(pi.getHasPersonalSpace()));
            resultBuilder.add((Object)SearchFieldMappings.CONTENT_NAME_UNSTEMMED.createField(user.getFullName()));
        }
        boolean deleted = user == null;
        resultBuilder.add((Object)SearchFieldMappings.IS_EXTERNALLY_DELETED_USER.createField(deleted));
        boolean licensed = this.permissionManager.hasPermission((User)user, Permission.VIEW, PermissionManager.TARGET_APPLICATION);
        resultBuilder.add((Object)SearchFieldMappings.IS_LICENSED_USER.createField(licensed));
        return resultBuilder.build();
    }

    private boolean neverModified(PersonalInformation pi) {
        return pi.getCreationDate() != null && pi.getCreationDate().equals(pi.getLastModificationDate());
    }
}

