/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.CreatePageAction;
import com.atlassian.user.User;

public class CreatePageEntryAction
extends CreatePageAction {
    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasCreatePermission((User)this.getAuthenticatedUser(), (Object)this.getSpace(), Page.class) && this.hasDraftPermission();
    }
}

