/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.core;

import org.objectweb.asm.Type;

public abstract class ClassInfo {
    protected ClassInfo() {
    }

    public abstract Type getType();

    public abstract Type getSuperType();

    public abstract Type[] getInterfaces();

    public abstract int getModifiers();

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof ClassInfo)) {
            return false;
        }
        return this.getType().equals(((ClassInfo)o).getType());
    }

    public int hashCode() {
        return this.getType().hashCode();
    }

    public String toString() {
        return this.getType().getClassName();
    }
}

