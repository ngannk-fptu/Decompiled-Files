/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 */
package com.atlassian.confluence.core.persistence;

import bucket.core.persistence.ObjectDao;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.List;

public interface ContentPermissionDao
extends ObjectDao {
    public ContentPermission getById(long var1);

    public List<ContentPermission> getGroupPermissions(String var1);

    public List<ContentPermission> getUserPermissions(ConfluenceUser var1);
}

