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
public enum SortColumn {
    DUE_DATE("due date"),
    ASSIGNEE("assignee"),
    PAGE_TITLE("page title");

    private final String column;

    private SortColumn(String column) {
        this.column = column;
    }

    public String toString() {
        return this.column;
    }
}

