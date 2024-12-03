/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

enum SQLServerSortOrder {
    ASCENDING(0),
    DESCENDING(1),
    UNSPECIFIED(-1);

    final int value;

    private SQLServerSortOrder(int sortOrderVal) {
        this.value = sortOrderVal;
    }
}

