/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.table;

public class TableCell {
    private final String content;
    private boolean header;

    public TableCell(String content) {
        this(content, false);
    }

    public TableCell(String content, boolean header) {
        this.content = content;
        this.header = header;
    }

    public String getContent() {
        return this.content;
    }

    public boolean isHeader() {
        return this.header;
    }

    public String toString() {
        return "|" + (this.header ? "|" : "") + this.content;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TableCell)) {
            return false;
        }
        TableCell tableCell = (TableCell)o;
        if (this.header != tableCell.header) {
            return false;
        }
        return !(this.content != null ? !this.content.equals(tableCell.content) : tableCell.content != null);
    }

    public int hashCode() {
        int result = this.content != null ? this.content.hashCode() : 0;
        result = 29 * result + (this.header ? 1 : 0);
        return result;
    }
}

