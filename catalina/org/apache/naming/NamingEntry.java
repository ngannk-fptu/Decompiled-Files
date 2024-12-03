/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming;

public class NamingEntry {
    public static final int ENTRY = 0;
    public static final int LINK_REF = 1;
    public static final int REFERENCE = 2;
    public static final int CONTEXT = 10;
    public int type;
    public final String name;
    public Object value;

    public NamingEntry(String name, Object value, int type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public boolean equals(Object obj) {
        if (obj instanceof NamingEntry) {
            return this.name.equals(((NamingEntry)obj).name);
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

