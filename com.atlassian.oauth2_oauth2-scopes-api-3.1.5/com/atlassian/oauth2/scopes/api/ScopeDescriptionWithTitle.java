/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.scopes.api;

import java.util.Objects;

public class ScopeDescriptionWithTitle {
    private final String title;
    private final String description;

    public ScopeDescriptionWithTitle(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ScopeDescriptionWithTitle that = (ScopeDescriptionWithTitle)o;
        return this.title.equals(that.title) && this.description.equals(that.description);
    }

    public int hashCode() {
        return Objects.hash(this.title, this.description);
    }
}

