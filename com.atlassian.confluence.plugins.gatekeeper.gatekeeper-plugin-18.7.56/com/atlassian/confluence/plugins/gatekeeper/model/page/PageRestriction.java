/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.page;

import com.atlassian.confluence.plugins.gatekeeper.model.page.RestrictionType;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import java.util.List;

public class PageRestriction {
    private TinyPage page;
    private RestrictionType restrictionType;
    private List<String> users;
    private List<String> groups;

    public PageRestriction(RestrictionType restrictionType, TinyPage page, List<String> users, List<String> groups) {
        this.restrictionType = restrictionType;
        this.page = page;
        this.users = users;
        this.groups = groups;
    }

    public TinyPage getPage() {
        return this.page;
    }

    public RestrictionType getType() {
        return this.restrictionType;
    }

    public List<String> getUsers() {
        return this.users;
    }

    public List<String> getGroups() {
        return this.groups;
    }

    public String toString() {
        return "ConfluencePageRestriction{page=" + this.page + ", users=" + this.users + ", groups=" + this.groups + "}";
    }
}

