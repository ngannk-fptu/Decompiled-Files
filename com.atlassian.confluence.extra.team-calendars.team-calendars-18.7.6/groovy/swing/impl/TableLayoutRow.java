/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.impl;

import groovy.swing.impl.TableLayout;
import groovy.swing.impl.TableLayoutCell;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;

public class TableLayoutRow {
    private final TableLayout parent;
    private final List<TableLayoutCell> cells = new ArrayList<TableLayoutCell>();
    private int rowIndex;

    public TableLayoutRow(TableLayout tableLayoutTag) {
        this.parent = tableLayoutTag;
    }

    public void addCell(TableLayoutCell tag) {
        int gridx = 0;
        for (TableLayoutCell cell : this.cells) {
            gridx += cell.getColspan();
        }
        tag.getConstraints().gridx = gridx;
        this.cells.add(tag);
    }

    public void addComponentsForRow() {
        this.rowIndex = this.parent.nextRowIndex();
        for (TableLayoutCell cell : this.cells) {
            GridBagConstraints c = cell.getConstraints();
            c.gridy = this.rowIndex;
            this.parent.addCell(cell);
        }
    }

    public int getRowIndex() {
        return this.rowIndex;
    }
}

