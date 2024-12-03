/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaPersister
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.annotations.Internal;
import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaPersister;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.impl.journal.JournalStateStore;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.dao.DataAccessException;

@Internal
public class BandanaJournalStateStore
implements JournalStateStore {
    private final BandanaPersister bandanaPersister;
    private final BandanaContext bandanaContext;

    public BandanaJournalStateStore(BandanaPersister bandanaPersister, BandanaContext bandanaContext) {
        this.bandanaPersister = Objects.requireNonNull(bandanaPersister);
        this.bandanaContext = Objects.requireNonNull(bandanaContext);
    }

    @Override
    public long getMostRecentId(@NonNull JournalIdentifier journalId) throws DataAccessException {
        Object value = this.bandanaPersister.retrieve(this.bandanaContext, journalId.getJournalName());
        return value == null ? 0L : (Long)value;
    }

    @Override
    public void setMostRecentId(@NonNull JournalIdentifier journalId, long id) throws DataAccessException {
        this.bandanaPersister.store(this.bandanaContext, journalId.getJournalName(), (Object)id);
    }

    @Override
    public void resetAllJournalStates() throws DataAccessException {
        this.bandanaPersister.remove(this.bandanaContext);
    }
}

