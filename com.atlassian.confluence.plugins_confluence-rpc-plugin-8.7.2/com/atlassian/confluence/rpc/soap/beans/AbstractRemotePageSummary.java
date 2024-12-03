/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.util.GeneralUtil
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.util.GeneralUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class AbstractRemotePageSummary {
    long id;
    String space;
    String title;
    String url;
    int permissions;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.pages.AbstractPage page \nsetId long id \nsetPermissions int permissions \nsetSpace java.lang.String space \nsetTitle java.lang.String title \nsetUrl java.lang.String url \n";

    protected AbstractRemotePageSummary() {
    }

    protected AbstractRemotePageSummary(AbstractPage page) {
        this.id = page.getId();
        this.space = page.getLatestVersion().getSpace().getKey();
        this.title = page.getTitle();
        this.url = GeneralUtil.getGlobalSettings().getBaseUrl() + page.getUrlPath();
        this.permissions = page.getPermissions().size();
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSpace() {
        return this.space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPermissions() {
        return this.permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

