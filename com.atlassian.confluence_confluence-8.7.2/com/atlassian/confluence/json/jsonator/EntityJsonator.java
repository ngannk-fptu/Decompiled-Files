/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Entity
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.json.jsonator;

import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.json.jsonator.Jsonator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserProfilePictureAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.user.Entity;
import com.atlassian.user.Group;
import com.atlassian.user.User;

public class EntityJsonator
implements Jsonator<Entity> {
    private final UserProfilePictureAccessor userProfilePictureAccessor;

    public EntityJsonator(UserProfilePictureAccessor userProfilePictureAccessor) {
        this.userProfilePictureAccessor = userProfilePictureAccessor;
    }

    @Override
    public Json convert(Entity entity) {
        JsonObject json = new JsonObject();
        if (entity instanceof User) {
            ConfluenceUser user = FindUserHelper.getUser((User)entity);
            ProfilePictureInfo profilePicture = this.userProfilePictureAccessor.getUserProfilePicture(user);
            json.setProperty("type", "user");
            json.setProperty("userKey", user.getKey().getStringValue());
            json.setProperty("name", user.getName());
            json.setProperty("fullName", user.getFullName());
            json.setProperty("profilePictureDownloadPath", profilePicture.getDownloadPath());
            json.setProperty("avatarUrl", profilePicture.getUriReference());
        } else if (entity instanceof Group) {
            Group group = (Group)entity;
            json.setProperty("type", "group");
            json.setProperty("name", group.getName());
        }
        return json;
    }
}

