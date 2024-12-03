/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.core.persistence;

import bucket.core.persistence.ObjectDao;
import com.atlassian.confluence.core.VersionHistory;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface VersionHistoryDao
extends ObjectDao {
    public static final int UNKNOWN_BUILD_NUMBER = 0;

    @Transactional(readOnly=true)
    public int getLatestBuildNumber();

    public void addBuildToHistory(int var1);

    @Transactional(readOnly=true)
    public VersionHistory getVersionHistory(int var1);

    public boolean tagBuild(int var1, String var2);

    @Transactional(readOnly=true)
    public List<VersionHistory> getUpgradeHistory(int var1, int var2);

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public int getFinalizedBuildNumber();

    public void finalizeBuild(int var1);
}

