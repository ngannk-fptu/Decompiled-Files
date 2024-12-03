/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.dao.DataAccessException;

public interface JournalStateStore {
    public long getMostRecentId(@NonNull JournalIdentifier var1) throws DataAccessException;

    public void setMostRecentId(@NonNull JournalIdentifier var1, long var2) throws DataAccessException;

    public void resetAllJournalStates() throws DataAccessException;
}

