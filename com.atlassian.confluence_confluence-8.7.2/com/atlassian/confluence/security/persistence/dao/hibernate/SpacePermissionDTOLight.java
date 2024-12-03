/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.security.persistence.dao.hibernate;

import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.SpacePermissionType;
import com.atlassian.sal.api.user.UserKey;

public class SpacePermissionDTOLight {
    private Long id;
    private UserKey userKey;
    private String groupName;
    private SpacePermissionType type;
    private String permAllUserSubject;
    private Long spaceId;

    public SpacePermissionDTOLight(Long id, UserKey userKey, String groupName, String type, String permAllUserSubject, Long spaceId) {
        this.id = id;
        this.userKey = userKey;
        this.groupName = groupName;
        this.type = SpacePermissionType.valueOf(type);
        this.permAllUserSubject = permAllUserSubject;
        this.spaceId = spaceId;
    }

    public Long getId() {
        return this.id;
    }

    public UserKey getUserKey() {
        return this.userKey;
    }

    public SpacePermissionType getType() {
        return this.type;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public String getPermAllUserSubject() {
        return this.permAllUserSubject;
    }

    public Long getSpaceId() {
        return this.spaceId;
    }

    public boolean isAvailableForAnonymous() {
        return this.userKey == null && this.groupName == null && this.permAllUserSubject == null;
    }

    public boolean isAvailableForAuthenticatedUsers() {
        return "authenticated-users".equals(this.permAllUserSubject);
    }
}

