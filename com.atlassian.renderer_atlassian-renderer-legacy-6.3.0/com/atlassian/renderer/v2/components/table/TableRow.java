/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.table;

import com.atlassian.renderer.v2.components.table.TableCell;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TableRow {
    List<TableCell> cells = new LinkedList<TableCell>();

    public static TableRow createRow(String s) {
        TableRow row = new TableRow();
        StringBuffer tmp = new StringBuffer();
        boolean isHeader = false;
        char prev = '\u0000';
        char[] chars = s.trim().toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (c == '|') {
                if (i != 0) {
                    if (prev == '|') {
                        isHeader = true;
                    } else if (prev == '\\') {
                        tmp.append(c);
                    } else {
                        row.addCell(new TableCell(tmp.toString(), isHeader));
                        tmp = new StringBuffer();
                        isHeader = false;
                    }
                }
            } else {
                tmp.append(c);
            }
            prev = c;
        }
        if (tmp.length() != 0) {
            row.addCell(new TableCell(tmp.toString(), isHeader));
        }
        return row;
    }

    public List<TableCell> getCells() {
        return Collections.unmodifiableList(this.cells);
    }

    public void addCell(TableCell tableCell) {
        this.cells.add(tableCell);
    }

    public String toString() {
        StringBuffer result = new StringBuffer("");
        for (TableCell tableCell : this.cells) {
            result.append("[");
            result.append(tableCell.toString());
            result.append("]");
        }
        return result.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TableRow)) {
            return false;
        }
        TableRow tableRow = (TableRow)o;
        return !(this.cells != null ? !this.cells.equals(tableRow.cells) : tableRow.cells != null);
    }

    public int hashCode() {
        return this.cells != null ? this.cells.hashCode() : 0;
    }
}

