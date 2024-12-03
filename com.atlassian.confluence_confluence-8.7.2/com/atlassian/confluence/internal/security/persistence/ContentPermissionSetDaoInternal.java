/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.security.persistence;

import com.atlassian.confluence.internal.persistence.ObjectDaoInternal;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.security.persistence.dao.ContentPermissionSetDao;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ContentPermissionSetDaoInternal
extends ContentPermissionSetDao,
ObjectDaoInternal<ContentPermissionSet> {
    public Map<Long, List<ContentPermissionSet>> getInheritedContentPermissionSets(Collection<Long> var1);
}

