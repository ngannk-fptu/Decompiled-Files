/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention;

import com.atlassian.confluence.impl.retention.rules.HistoricalVersion;
import java.util.List;

public interface ContentRetentionDao {
    public List<HistoricalVersion> findHistoricalPageVersions(long var1, int var3);

    public List<HistoricalVersion> findHistoricalAttachmentVersions(long var1, int var3);
}

