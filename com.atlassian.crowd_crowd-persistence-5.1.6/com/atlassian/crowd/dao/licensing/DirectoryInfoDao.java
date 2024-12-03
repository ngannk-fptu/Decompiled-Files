/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 */
package com.atlassian.crowd.dao.licensing;

import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.licensing.DirectoryInfo;
import com.atlassian.crowd.model.licensing.LicensingSummary;
import java.util.List;

public interface DirectoryInfoDao {
    public DirectoryInfo findById(long var1) throws ObjectNotFoundException;

    public void saveOrUpdate(DirectoryInfo var1);

    public List<DirectoryInfo> findDirectories(LicensingSummary var1);

    public void removeAll(LicensingSummary var1);

    public void removeByDirectoryId(Long var1);
}

