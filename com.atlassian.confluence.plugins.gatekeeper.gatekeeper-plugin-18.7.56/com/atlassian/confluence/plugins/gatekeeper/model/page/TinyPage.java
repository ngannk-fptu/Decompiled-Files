/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.plugins.gatekeeper.model.page;

import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TinyPage {
    private long id;
    private String title;
    private String creator;
    private transient int level;

    public TinyPage(long id, String title, String creator, int level) {
        this.id = id;
        this.title = title;
        this.creator = creator;
        this.level = level;
    }

    @JsonProperty
    public long getId() {
        return this.id;
    }

    @JsonProperty
    public String getTitle() {
        return this.title;
    }

    @JsonProperty
    public String getCreator() {
        return this.creator;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean isCreatorMatchesUser(TinyOwner owner) {
        return owner != null && owner.getName().equals(this.creator);
    }

    public String toString() {
        return "TinyPage{id=" + this.id + ", title='" + this.title + "', creator='" + this.creator + "', level=" + this.level + "}";
    }
}

