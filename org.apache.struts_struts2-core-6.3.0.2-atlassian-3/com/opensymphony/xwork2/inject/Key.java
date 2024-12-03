/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject;

class Key<T> {
    final Class<T> type;
    final String name;
    final int hashCode;

    private Key(Class<T> type, String name) {
        if (type == null) {
            throw new NullPointerException("Type is null.");
        }
        if (name == null) {
            throw new NullPointerException("Name is null.");
        }
        this.type = type;
        this.name = name;
        this.hashCode = type.hashCode() * 31 + name.hashCode();
    }

    Class<T> getType() {
        return this.type;
    }

    String getName() {
        return this.name;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Key)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        Key other = (Key)o;
        return this.name.equals(other.name) && this.type.equals(other.type);
    }

    public String toString() {
        return "[type=" + this.type.getName() + ", name='" + this.name + "']";
    }

    static <T> Key<T> newInstance(Class<T> type, String name) {
        return new Key<T>(type, name);
    }
}

