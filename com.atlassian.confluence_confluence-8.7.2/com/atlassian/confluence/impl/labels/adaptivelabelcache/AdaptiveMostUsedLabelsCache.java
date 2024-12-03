/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.labels.adaptivelabelcache;

import com.atlassian.confluence.labels.dto.LiteLabelSearchResult;
import java.util.List;

public interface AdaptiveMostUsedLabelsCache {
    public List<LiteLabelSearchResult> getSiteRecord(int var1);

    public List<LiteLabelSearchResult> getSpaceRecord(long var1, int var3);

    public List<LiteLabelSearchResult> getSpaceRecord(String var1, int var2);

    public void deleteAllPersistedRecords();

    public void deletePersistedRecord(long var1);

    public void deletePersistedRecordForSite();
}

