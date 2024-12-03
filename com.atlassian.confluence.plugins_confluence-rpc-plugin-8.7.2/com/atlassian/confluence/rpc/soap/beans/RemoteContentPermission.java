/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.ContentPermission
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.user.ConfluenceUser;

public class RemoteContentPermission {
    private String type;
    private String userName;
    private String groupName;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.security.SpacePermission spacePermission \n<init> com.atlassian.confluence.security.ContentPermission contentPermission \nsetGroupName java.lang.String groupName \nsetType java.lang.String type \nsetUserName java.lang.String userName \n";

    public RemoteContentPermission() {
    }

    public RemoteContentPermission(SpacePermission spacePermission) {
        this.type = spacePermission.getType();
        ConfluenceUser userSubject = spacePermission.getUserSubject();
        this.userName = userSubject != null ? userSubject.getName() : null;
        this.groupName = spacePermission.getGroup();
    }

    public RemoteContentPermission(ContentPermission contentPermission) {
        this.type = contentPermission.getType();
        this.userName = contentPermission.getUserName();
        this.groupName = contentPermission.getGroupName();
    }

    public String getType() {
        return this.type;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}

