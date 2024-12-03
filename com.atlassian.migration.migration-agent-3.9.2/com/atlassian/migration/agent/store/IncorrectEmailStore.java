/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.IncorrectEmail;
import com.atlassian.migration.agent.entity.SortOrder;
import com.atlassian.migration.agent.entity.UserBaseScanSortKey;
import com.atlassian.migration.agent.service.check.CheckType;
import java.util.List;

public interface IncorrectEmailStore {
    public void save(IncorrectEmail var1);

    public void deleteAll();

    public List<IncorrectEmail> getIncorrectEmailsByCheckType(String var1, CheckType var2);

    public List<IncorrectEmail> getIncorrectEmailsByCheckType(String var1, CheckType var2, int var3, int var4, UserBaseScanSortKey var5, SortOrder var6);

    public long countIncorrectEmailsByCheckType(String var1, CheckType var2);
}

