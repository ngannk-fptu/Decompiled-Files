/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.opensocial.model;

import net.jcip.annotations.Immutable;

@Immutable
public class PersonId {
    private final String id;

    public PersonId(String id) {
        if (id == null) {
            throw new NullPointerException("id parameter must not be null when creating a new PersonId");
        }
        if (!PersonId.isValidPersonId(id)) {
            throw new IllegalArgumentException("Invalid characters in person identifier: " + id + ". Identifiers may only contain alphanumeric characters, underscore, dot, or dash.");
        }
        this.id = id;
    }

    public String value() {
        return this.id;
    }

    public String toString() {
        return this.id;
    }

    public static PersonId valueOf(String id) {
        return new PersonId(id);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return this.id.equals(((PersonId)o).id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    private static boolean isValidPersonId(String str) {
        if (str.length() == 0) {
            return false;
        }
        for (char nextChar : str.toCharArray()) {
            if (Character.isLetterOrDigit(nextChar) || nextChar == '_' || nextChar == '.' || nextChar == '-') continue;
            return false;
        }
        return true;
    }
}

