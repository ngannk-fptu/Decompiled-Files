/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.space.dao;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.DenormalisedSpaceChangeLog;
import java.util.List;

public interface DenormalisedSpaceChangeLogDao {
    public void removeAllSpaceChangeLogRecords();

    public void removeSpaceChangeLogRecords(List<DenormalisedSpaceChangeLog> var1);

    public List<DenormalisedSpaceChangeLog> findSpaceChangeLogRecords(int var1);

    @VisibleForTesting
    public void saveRecord(DenormalisedSpaceChangeLog var1);

    public List<Long> getAllChangedSpaceIds();
}

