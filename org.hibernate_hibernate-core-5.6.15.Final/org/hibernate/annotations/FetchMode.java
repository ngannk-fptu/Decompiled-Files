/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

public enum FetchMode {
    SELECT(org.hibernate.FetchMode.SELECT),
    JOIN(org.hibernate.FetchMode.JOIN),
    SUBSELECT(org.hibernate.FetchMode.SELECT);

    private final org.hibernate.FetchMode hibernateFetchMode;

    private FetchMode(org.hibernate.FetchMode hibernateFetchMode) {
        this.hibernateFetchMode = hibernateFetchMode;
    }

    public org.hibernate.FetchMode getHibernateFetchMode() {
        return this.hibernateFetchMode;
    }
}

