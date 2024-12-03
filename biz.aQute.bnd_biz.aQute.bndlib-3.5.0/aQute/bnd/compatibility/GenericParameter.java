/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.compatibility;

import aQute.bnd.compatibility.GenericType;

public class GenericParameter {
    String name;
    GenericType[] bounds;

    public GenericParameter(String name, GenericType[] bounds) {
        this.name = name;
        this.bounds = bounds;
        if (bounds == null || bounds.length == 0) {
            this.bounds = new GenericType[]{new GenericType(Object.class)};
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name);
        if (this.bounds != null && this.bounds.length > 0) {
            for (GenericType gtype : this.bounds) {
                sb.append(":");
                sb.append(gtype);
            }
        }
        return sb.toString();
    }
}

