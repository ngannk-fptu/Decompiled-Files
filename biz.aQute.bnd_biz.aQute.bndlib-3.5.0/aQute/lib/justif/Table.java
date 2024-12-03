/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.justif;

import aQute.lib.justif.Justif;
import java.util.ArrayList;
import java.util.List;

public class Table {
    int row;
    int column;
    int maxColumn = 0;
    List<List<Justif>> matrix = new ArrayList<List<Justif>>();

    public Justif nextCell(String format, Object args) {
        return this.cell(this.row, this.column++);
    }

    public Justif firstCell() {
        this.column = 0;
        return this.cell(this.row++, 0);
    }

    private Justif cell(int row, int column) {
        while (this.matrix.size() <= row) {
            this.matrix.add(new ArrayList());
        }
        List<Justif> line = this.matrix.get(row);
        while (line.size() <= column) {
            line.add(new Justif());
            this.maxColumn = Math.max(line.size(), this.maxColumn);
        }
        return line.get(column);
    }

    public void append(Appendable app) {
        for (int r = 0; r < this.matrix.size(); ++r) {
            List<Justif> line = this.matrix.get(r);
            for (int c = 0; c < line.size(); ++c) {
            }
        }
    }
}

