/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.tasklist;

public enum TaskStatus {
    UNCHECKED("incomplete"),
    CHECKED("complete");

    private final String displayedText;

    private TaskStatus(String displayedText) {
        this.displayedText = displayedText;
    }

    public String getDisplayedText() {
        return this.displayedText;
    }
}

