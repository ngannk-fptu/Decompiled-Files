/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.core.NotExportable;
import com.google.common.base.Preconditions;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public class JournalEntry
implements NotExportable {
    private long id;
    private JournalIdentifier journalId;
    private Date creationDate;
    private String type;
    private String message;
    private int triedTimes;

    public JournalEntry(JournalIdentifier journalId, String type, @Nullable String message) {
        Preconditions.checkArgument((type.length() <= 255 ? 1 : 0) != 0, (Object)"type cannot exceed 255 characters");
        Preconditions.checkArgument((message == null || message.length() <= 2047 ? 1 : 0) != 0, (Object)"message cannot exceed 2047 characters");
        this.journalId = (JournalIdentifier)Preconditions.checkNotNull((Object)journalId);
        this.type = type;
        this.message = message;
    }

    JournalEntry() {
    }

    public long getId() {
        return this.id;
    }

    void setId(long id) {
        this.id = id;
    }

    public JournalIdentifier getJournalId() {
        return this.journalId;
    }

    public void setJournalId(JournalIdentifier journalId) {
        this.journalId = journalId;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getType() {
        return this.type;
    }

    void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return this.message;
    }

    void setMessage(String message) {
        this.message = message;
    }

    public int getTriedTimes() {
        return this.triedTimes;
    }

    void setTriedTimes(int triedTimes) {
        this.triedTimes = triedTimes;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JournalEntry that = (JournalEntry)o;
        return this.id == that.id;
    }

    public int hashCode() {
        return (int)(this.id ^ this.id >>> 32);
    }

    public String toString() {
        return "JournalEntry{id=" + this.id + ", journalId='" + this.journalId + "', creationDate=" + this.creationDate + ", type='" + this.type + "', message='" + this.message + "', triedTimes='" + this.triedTimes + "'}";
    }
}

