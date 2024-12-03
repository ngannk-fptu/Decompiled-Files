/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.pool;

public final class DirContextType {
    private String name;
    public static final DirContextType READ_ONLY = new DirContextType("READ_ONLY");
    public static final DirContextType READ_WRITE = new DirContextType("READ_WRITE");

    private DirContextType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}

