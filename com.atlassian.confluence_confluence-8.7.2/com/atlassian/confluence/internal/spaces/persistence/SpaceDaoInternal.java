/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.spaces.persistence;

import com.atlassian.confluence.internal.persistence.ObjectDaoInternal;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.persistence.dao.SpaceDao;
import java.util.List;

public interface SpaceDaoInternal
extends SpaceDao,
ObjectDaoInternal<Space> {
    public List<Long> findSpaceIdListWithIdGreaterOrEqual(Long var1, int var2);

    public List<String> findAllSpaceKeys();

    public void removeSpaceFromCache(String var1);
}

