/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

public final class FastField {
    private final String name;
    private final String declaringClass;

    public FastField(String definedIn, String name) {
        this.name = name;
        this.declaringClass = definedIn;
    }

    public FastField(Class definedIn, String name) {
        this(definedIn == null ? null : definedIn.getName(), name);
    }

    public String getName() {
        return this.name;
    }

    public String getDeclaringClass() {
        return this.declaringClass;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof FastField) {
            FastField field = (FastField)obj;
            if (this.declaringClass == null && field.declaringClass != null || this.declaringClass != null && field.declaringClass == null) {
                return false;
            }
            return this.name.equals(field.getName()) && (this.declaringClass == null || this.declaringClass.equals(field.getDeclaringClass()));
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode() ^ (this.declaringClass == null ? 0 : this.declaringClass.hashCode());
    }

    public String toString() {
        return (this.declaringClass == null ? "" : this.declaringClass + ".") + this.name;
    }
}

