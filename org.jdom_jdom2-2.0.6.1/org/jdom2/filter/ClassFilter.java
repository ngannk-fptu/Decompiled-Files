/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.filter;

import org.jdom2.filter.AbstractFilter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ClassFilter<T>
extends AbstractFilter<T> {
    private static final long serialVersionUID = 200L;
    private final Class<? extends T> fclass;

    public ClassFilter(Class<? extends T> tclass) {
        this.fclass = tclass;
    }

    @Override
    public T filter(Object content) {
        return this.fclass.isInstance(content) ? (T)this.fclass.cast(content) : null;
    }

    public String toString() {
        return "[ClassFilter: Class " + this.fclass.getName() + "]";
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ClassFilter) {
            return this.fclass.equals(((ClassFilter)obj).fclass);
        }
        return false;
    }

    public int hashCode() {
        return this.fclass.hashCode();
    }
}

