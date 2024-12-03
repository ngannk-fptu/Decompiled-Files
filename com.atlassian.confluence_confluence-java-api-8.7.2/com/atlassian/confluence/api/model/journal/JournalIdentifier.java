/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.confluence.api.model.journal;

import com.atlassian.annotations.PublicApi;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

@PublicApi
public class JournalIdentifier
implements Serializable {
    private static final Pattern JOURNAL_NAME_PATTERN = Pattern.compile("[a-z0-9_\\.]{1,255}");
    private final String journalName;

    public JournalIdentifier(String journalName) {
        if (!JOURNAL_NAME_PATTERN.matcher(journalName).matches()) {
            throw new IllegalArgumentException("journal name must match the following regular expression: " + JOURNAL_NAME_PATTERN.pattern());
        }
        this.journalName = journalName;
    }

    public String getJournalName() {
        return this.journalName;
    }

    public boolean equals(Object o) {
        if (o instanceof JournalIdentifier) {
            return Objects.equals(this.journalName, ((JournalIdentifier)o).journalName);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.journalName);
    }

    public String toString() {
        return "JournalIdentifier{journalName='" + this.journalName + '\'' + '}';
    }
}

