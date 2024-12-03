/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.Component;
import java.text.NumberFormat;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class NumberCellRenderer
extends DefaultTableCellRenderer {
    public NumberCellRenderer() {
        this.setHorizontalAlignment(4);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setFont(null);
        NumberFormat nf = NumberFormat.getNumberInstance();
        if (value != null) {
            this.setText(nf.format(value));
        } else {
            this.setText("");
        }
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
        } else {
            this.setBackground(null);
        }
        return this;
    }
}

