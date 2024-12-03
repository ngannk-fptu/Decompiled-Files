/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.dynamictasklist2.model;

public class LegacyTask {
    private String name;
    private String completer;

    public LegacyTask() {
    }

    public LegacyTask(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCompleted() {
        return this.completer != null;
    }

    public String getCompleter() {
        return this.completer;
    }

    public void setCompleter(String completer) {
        this.completer = completer;
    }
}

