/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.model.journal;

import com.atlassian.annotations.PublicApi;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import java.util.Date;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
@PublicApi
public class JournalEntry {
    private final long id;
    private final JournalIdentifier journalId;
    private final Date creationDate;
    private final String type;
    private final String message;

    public JournalEntry(JournalIdentifier journalId, String type, @Nullable String message) {
        if (type.length() > 255) {
            throw new IllegalArgumentException("type cannot exceed 255 characters");
        }
        if (message != null && message.length() > 2047) {
            throw new IllegalArgumentException("message cannot exceed 2047 characters");
        }
        this.id = 0L;
        this.journalId = Objects.requireNonNull(journalId);
        this.creationDate = null;
        this.type = type;
        this.message = message;
    }

    public JournalEntry(long id, JournalIdentifier journalId, Date creationDate, String type, @Nullable String message) {
        if (id <= 0L) {
            throw new IllegalArgumentException("not a valid id");
        }
        if (type.length() > 255) {
            throw new IllegalArgumentException("type cannot exceed 255 characters");
        }
        if (message != null && message.length() > 2047) {
            throw new IllegalArgumentException("message cannot exceed 2047 characters");
        }
        this.id = id;
        this.journalId = Objects.requireNonNull(journalId);
        this.creationDate = new Date(Objects.requireNonNull(creationDate).getTime());
        this.type = type;
        this.message = message;
    }

    public long getId() {
        return this.id;
    }

    public JournalIdentifier getJournalId() {
        return this.journalId;
    }

    public Date getCreationDate() {
        return new Date(this.creationDate.getTime());
    }

    public String getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean equals(Object o) {
        if (o instanceof JournalEntry) {
            return this.id == ((JournalEntry)o).id;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.id);
    }

    public String toString() {
        return "JournalEntry{id=" + this.id + ", journalId=" + this.journalId + ", creationDate=" + this.creationDate + ", type='" + this.type + '\'' + ", message='" + this.message + '\'' + '}';
    }
}

