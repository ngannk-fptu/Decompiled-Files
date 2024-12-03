/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 */
package com.atlassian.confluence.internal.index.lucene.snapshot;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshot;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface LuceneIndexSnapshotManager {
    public LuceneIndexSnapshot create(JournalIdentifier var1);

    public void restore(LuceneIndexSnapshot var1);

    public List<LuceneIndexSnapshot> findForJournal(JournalIdentifier var1);

    public Optional<LuceneIndexSnapshot> find(JournalIdentifier var1, long var2);

    public Optional<LuceneIndexSnapshot> find(JournalIdentifier var1, long var2, long var4) throws InterruptedException;

    public void delete(LuceneIndexSnapshot var1) throws IOException;

    public Optional<Path> getFile(LuceneIndexSnapshot var1);
}

