/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

public abstract class ComparableType<T extends ComparableType>
implements Comparable<T> {
    private String name;

    public ComparableType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass().isAssignableFrom(obj.getClass())) {
            ComparableType other = (ComparableType)obj;
            if (this.getName().equals(other.getName())) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public int compareTo(T o) {
        return this.getName().compareTo(((ComparableType)o).getName());
    }
}

