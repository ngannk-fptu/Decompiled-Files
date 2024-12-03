/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention;

import com.atlassian.confluence.impl.retention.rules.ContentType;
import com.atlassian.confluence.impl.retention.rules.DeletedHistoricalVersionSummary;
import com.atlassian.confluence.impl.retention.rules.HistoricalVersion;
import java.util.List;

public interface HistoricalVersionService {
    public List<HistoricalVersion> find(long var1, int var3, ContentType var4);

    public DeletedHistoricalVersionSummary delete(List<HistoricalVersion> var1);
}

