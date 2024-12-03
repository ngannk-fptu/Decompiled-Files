/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.efi.services;

import com.atlassian.confluence.user.ConfluenceUser;

public class SpaceImportConfig {
    private String spaceKey;
    private String spaceTitle;
    private String homepageTitle;
    private ConfluenceUser actor;
    private boolean temporary;

    public String getHomepageTitle() {
        return this.homepageTitle;
    }

    public void setHomepageTitle(String homepageTitle) {
        this.homepageTitle = homepageTitle;
    }

    public String getSpaceTitle() {
        return this.spaceTitle;
    }

    public void setSpaceTitle(String spaceTitle) {
        this.spaceTitle = spaceTitle;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public ConfluenceUser getActor() {
        return this.actor;
    }

    public void setActor(ConfluenceUser actor) {
        this.actor = actor;
    }

    public boolean isTemporary() {
        return this.temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }
}

