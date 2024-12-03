/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.createcontent.services.model;

import com.atlassian.confluence.user.ConfluenceUser;

public class CreatePersonalSpaceRequest {
    private final ConfluenceUser spaceUser;
    private final String spacePermission;

    public CreatePersonalSpaceRequest(ConfluenceUser spaceUser, String spacePermission) {
        this.spaceUser = spaceUser;
        this.spacePermission = spacePermission;
    }

    public ConfluenceUser getSpaceUser() {
        return this.spaceUser;
    }

    public String getSpacePermission() {
        return this.spacePermission;
    }
}

