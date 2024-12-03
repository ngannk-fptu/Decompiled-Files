/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 */
package com.atlassian.confluence.internal.index;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;

public enum Index {
    MAIN_INDEX("main_index"),
    EDGE_INDEX("edge_index"),
    CHANGE_INDEX("change_index");

    private final JournalIdentifier journalIdentifier;

    private Index(String journalIdentifierName) {
        this.journalIdentifier = new JournalIdentifier(journalIdentifierName);
    }

    public JournalIdentifier getJournalIdentifier() {
        return this.journalIdentifier;
    }
}

