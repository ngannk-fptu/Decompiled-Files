/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.security.SpacePermission
 */
package com.atlassian.confluence.plugins.gatekeeper.service;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.gatekeeper.exception.PermissionModificationException;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.page.PageRestriction;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.security.SpacePermission;
import java.util.List;
import java.util.Map;

public interface ConfluenceService {
    public boolean canCurrentUserViewSpace(String var1);

    public boolean canCurrentUserViewPage(String var1, long var2);

    public boolean canCurrentUserSetPermissions(String var1);

    public List<PageRestriction> getViewRestrictions(long var1);

    public List<PageRestriction> getEditRestrictions(long var1);

    public List<PageRestriction> getPageRestrictions(Page var1, String var2);

    public TinyPage getPage(String var1, long var2);

    public TinyPage getPage(String var1, String var2);

    public TinySpace getSpace(String var1);

    public List<SpacePermission> getPermissions(String var1);

    public void setPermissions(String var1, Map<TinyOwner, PermissionSet> var2) throws PermissionModificationException;

    public String getUserAvatarUrl(String var1);

    public String getHelpLink(String var1);

    public boolean hasGroupParents(String var1);
}

