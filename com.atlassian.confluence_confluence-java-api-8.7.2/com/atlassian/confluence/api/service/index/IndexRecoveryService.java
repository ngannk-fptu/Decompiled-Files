/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.index;

import com.atlassian.confluence.api.model.index.IndexRecoverer;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;

public interface IndexRecoveryService {
    public boolean isIndexRecoveryRequired(JournalIdentifier var1, String var2);

    public boolean recoverIndex(JournalIdentifier var1, String var2);

    public boolean recoverIndexFromSharedHome(JournalIdentifier var1, String var2);

    public boolean createIndexBackup(JournalIdentifier var1, String var2, IndexRecoverer var3);
}

