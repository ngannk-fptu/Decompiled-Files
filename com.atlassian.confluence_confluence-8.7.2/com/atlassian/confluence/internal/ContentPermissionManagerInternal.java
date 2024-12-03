/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal;

import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ContentPermissionManagerInternal
extends ContentPermissionManager {
    public Map<Long, ValidationResult> hasContentLevelPermission(ConfluenceUser var1, String var2, Collection<Long> var3);

    @Transactional(readOnly=true)
    public List<Page> getPermittedPagesIgnoreInheritedPermissions(List<Page> var1, ConfluenceUser var2, String var3);

    public boolean hasVisibleChildren(Page var1, ConfluenceUser var2);
}

