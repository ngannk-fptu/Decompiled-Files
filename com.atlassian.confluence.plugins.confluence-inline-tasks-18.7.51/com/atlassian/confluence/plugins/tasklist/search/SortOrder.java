/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlEnum
 *  javax.xml.bind.annotation.XmlType
 */
package com.atlassian.confluence.plugins.tasklist.search;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum
public enum SortOrder {
    ASCENDING("ASC"),
    DESCENDING("DESC");

    private final String sql;

    private SortOrder(String sql) {
        this.sql = sql;
    }

    public String toString() {
        return this.sql;
    }
}

