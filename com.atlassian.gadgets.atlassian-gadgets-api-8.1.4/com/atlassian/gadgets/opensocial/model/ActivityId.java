/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.opensocial.model;

import net.jcip.annotations.Immutable;

@Immutable
public final class ActivityId {
    private final String id;

    public ActivityId(String id) {
        if (id == null) {
            throw new NullPointerException("id parameter must not be null when creating a new ActivityId");
        }
        this.id = id;
    }

    public String value() {
        return this.id;
    }

    public String toString() {
        return this.id;
    }

    public static ActivityId valueOf(String id) {
        return new ActivityId(id);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return this.id.equals(((ActivityId)o).id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }
}

