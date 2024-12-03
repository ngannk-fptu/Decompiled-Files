/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.opensocial.model;

import net.jcip.annotations.Immutable;

@Immutable
public final class AppId {
    private final String id;

    public AppId(String id) {
        if (id == null) {
            throw new NullPointerException("id parameter must not be null when creating a new AppId");
        }
        this.id = id;
    }

    public String value() {
        return this.id;
    }

    public String toString() {
        return this.id;
    }

    public static AppId valueOf(String id) {
        return new AppId(id);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return this.id.equals(((AppId)o).id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }
}

