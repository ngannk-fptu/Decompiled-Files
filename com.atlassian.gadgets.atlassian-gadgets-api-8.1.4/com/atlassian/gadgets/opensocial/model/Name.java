/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.opensocial.model;

import net.jcip.annotations.Immutable;

@Immutable
public final class Name {
    private final String name;

    public Name(String name) {
        if (name == null) {
            throw new NullPointerException("name parameter to Name must not be null");
        }
        this.name = name.intern();
    }

    public String value() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public static Name valueOf(String name) {
        return new Name(name);
    }

    public boolean equals(Object obj) {
        return obj instanceof Name && this.name.equals(((Name)obj).value());
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

