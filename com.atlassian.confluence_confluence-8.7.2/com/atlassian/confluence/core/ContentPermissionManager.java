/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ContentPermissionManager {
    public void addContentPermission(ContentPermission var1, ContentEntityObject var2);

    public void setContentPermissions(Collection<ContentPermission> var1, ContentEntityObject var2, String var3);

    public void setContentPermissions(@NonNull Map<String, Collection<ContentPermission>> var1, ContentEntityObject var2);

    public void removeContentPermission(ContentPermission var1);

    public void removeAllGroupPermissions(String var1);

    public void removeAllUserPermissions(ConfluenceUser var1);

    @Transactional(readOnly=true)
    public List<ContentPermission> getInheritedContentUserPermissions(ContentEntityObject var1);

    @Transactional(readOnly=true)
    public List<ContentPermissionSet> getInheritedContentPermissionSets(ContentEntityObject var1);

    public boolean hasContentLevelPermission(User var1, String var2, ContentEntityObject var3);

    @Transactional(readOnly=true)
    public List<Page> getPermittedChildren(Page var1, User var2);

    public boolean hasPermittedChildrenIgnoreInheritedPermissions(Page var1, User var2);

    @Transactional(readOnly=true)
    public List<Page> getPermittedChildrenIgnoreInheritedPermissions(Page var1, User var2);

    @Deprecated
    @Transactional(readOnly=true)
    public Set<ContentPermission> getViewContentPermissions(Page var1);

    @Transactional(readOnly=true)
    public List<ContentPermissionSet> getInheritedContentPermissionSets(ContentEntityObject var1, boolean var2);

    @Transactional(readOnly=true)
    public List<ContentPermissionSet> getContentPermissionSets(ContentEntityObject var1, String var2);

    public boolean isPermissionInherited(Page var1);

    @Deprecated
    public void copyContentPermissions(AbstractPage var1, AbstractPage var2);

    public void copyContentPermissions(ContentEntityObject var1, ContentEntityObject var2);

    @Transactional(readOnly=true)
    public Map<Long, Boolean> getPermissionSets(User var1, Space var2);
}

