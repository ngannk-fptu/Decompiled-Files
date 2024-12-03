/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.table;

import com.atlassian.renderer.v2.components.table.TableRow;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Table {
    List<TableRow> rows = new LinkedList<TableRow>();

    public void addRow(String s) {
        this.addRow(TableRow.createRow(s));
    }

    public void addRow(TableRow row) {
        this.rows.add(row);
    }

    public List<TableRow> getRows() {
        return Collections.unmodifiableList(this.rows);
    }

    public static Table createTable(String raw) {
        Table t = new Table();
        StringTokenizer st = new StringTokenizer(raw, "\n");
        if (st.countTokens() > 0) {
            String currentRow = st.nextToken();
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (s.charAt(0) == '|') {
                    t.addRow(currentRow);
                    currentRow = s;
                    continue;
                }
                currentRow = currentRow + "\n" + s;
            }
            t.addRow(currentRow);
        }
        return t;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Table)) {
            return false;
        }
        Table table = (Table)o;
        return !(this.rows != null ? !this.rows.equals(table.rows) : table.rows != null);
    }

    public int hashCode() {
        return this.rows != null ? this.rows.hashCode() : 0;
    }
}

