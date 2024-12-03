/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.persistence;

import com.atlassian.confluence.core.VersionHistory;
import com.atlassian.confluence.core.persistence.VersionHistoryDao;
import com.atlassian.confluence.internal.persistence.ObjectDaoInternal;
import java.util.List;
import java.util.stream.Collectors;

public interface VersionHistoryDaoInternal
extends VersionHistoryDao,
ObjectDaoInternal<VersionHistory> {
    @Override
    default public List<VersionHistory> getUpgradeHistory(int start, int maxResults) {
        return this.getFullUpgradeHistory().stream().skip(start).limit(maxResults).collect(Collectors.toList());
    }

    public List<VersionHistory> getFullUpgradeHistory();
}

