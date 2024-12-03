/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.query;

public enum SearchScope {
    OBJECT(0),
    ONELEVEL(1),
    SUBTREE(2);

    private final int id;

    private SearchScope(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}

