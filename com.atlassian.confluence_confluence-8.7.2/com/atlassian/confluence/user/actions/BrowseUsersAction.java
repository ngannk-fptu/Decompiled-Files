/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.user.actions.SearchUsersAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class BrowseUsersAction
extends SearchUsersAction {
    @Override
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        this.setSearchTerm("*");
        this.setUsernameTerm("*");
        return this.doUserSearch();
    }
}

