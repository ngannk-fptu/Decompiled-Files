/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.impl;

import groovy.swing.impl.TableLayoutCell;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;

public class TableLayout
extends JPanel {
    private int rowCount;
    private int cellpadding;

    public TableLayout() {
        this.setLayout(new GridBagLayout());
    }

    public int getCellpadding() {
        return this.cellpadding;
    }

    public void setCellpadding(int cellpadding) {
        this.cellpadding = cellpadding;
    }

    public void addCell(TableLayoutCell cell) {
        GridBagConstraints constraints = cell.getConstraints();
        constraints.insets = new Insets(this.cellpadding, this.cellpadding, this.cellpadding, this.cellpadding);
        this.add(cell.getComponent(), constraints);
    }

    public int nextRowIndex() {
        return this.rowCount++;
    }
}

