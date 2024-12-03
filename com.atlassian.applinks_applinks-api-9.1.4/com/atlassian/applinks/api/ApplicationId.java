/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api;

import java.io.Serializable;
import java.util.UUID;

public class ApplicationId
implements Serializable {
    private static final long serialVersionUID = 8493075922307807008L;
    private final String id;

    public ApplicationId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        try {
            UUID.fromString(id);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("id must be a valid java.util.UUID string: " + id, e);
        }
        this.id = id;
    }

    public String get() {
        return this.id;
    }

    public String toString() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ApplicationId that = (ApplicationId)o;
        return this.id.equals(that.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }
}

