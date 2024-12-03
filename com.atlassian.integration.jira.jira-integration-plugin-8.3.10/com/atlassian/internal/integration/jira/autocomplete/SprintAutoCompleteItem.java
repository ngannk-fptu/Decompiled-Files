/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.internal.integration.jira.autocomplete;

import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteItem;

public class SprintAutoCompleteItem
extends AutoCompleteItem {
    private static final String BOARD = "board";
    private static final String STATE = "state";
    private static final String SUGGESTION = "suggestion";

    public SprintAutoCompleteItem(String id, String name, boolean suggestion, String state, String board) {
        super(id, name);
        this.put(BOARD, board);
        this.put(STATE, state);
        this.put(SUGGESTION, suggestion);
    }

    public String getBoard() {
        return (String)this.get(BOARD);
    }

    public String getState() {
        return (String)this.get(STATE);
    }

    public boolean isSuggestion() {
        return (Boolean)this.get(SUGGESTION);
    }
}

