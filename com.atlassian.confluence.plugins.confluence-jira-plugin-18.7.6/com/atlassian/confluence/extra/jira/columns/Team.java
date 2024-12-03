/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.atlassian.confluence.extra.jira.columns;

import com.google.gson.annotations.SerializedName;

public class Team {
    @SerializedName(value="id")
    private final String id;
    @SerializedName(value="title")
    private final String name;

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}

