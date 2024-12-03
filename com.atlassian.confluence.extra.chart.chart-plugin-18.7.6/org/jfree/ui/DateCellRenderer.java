/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.Component;
import java.text.DateFormat;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DateCellRenderer
extends DefaultTableCellRenderer {
    private DateFormat formatter;

    public DateCellRenderer() {
        this(DateFormat.getDateTimeInstance());
    }

    public DateCellRenderer(DateFormat formatter) {
        this.formatter = formatter;
        this.setHorizontalAlignment(0);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setFont(null);
        if (value != null) {
            this.setText(this.formatter.format(value));
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

