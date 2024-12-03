/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 */
package com.atlassian.confluence.security.persistence.dao;

import bucket.core.persistence.ObjectDao;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.ContentPermissionSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ContentPermissionSetDao
extends ObjectDao {
    public ContentPermissionSet getById(long var1);

    public Map<Long, List<ContentPermissionSet>> getExplicitPermissionSetsFor(Collection<Long> var1);

    public List getInheritedContentPermissionSets(Page var1, String var2);

    public Map<Long, Set<ContentPermissionSet>> getPermissionSets(String var1, List<Long> var2);

    public List<Long> getContentIdsWithPermissionSet(String var1);
}

