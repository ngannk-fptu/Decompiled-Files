/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.columns;

public final class Epic {
    private final String key;
    private final String name;
    private final String colour;
    private final String status;

    public Epic(String key, String name, String colour, String status) {
        this.key = key;
        this.name = name;
        this.colour = colour;
        this.status = status;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getColour() {
        return this.colour;
    }

    public String getStatus() {
        return this.status;
    }

    public String toString() {
        return "Epic{key='" + this.key + "', name='" + this.name + "', colour='" + this.colour + "', status='" + this.status + "'}";
    }
}

