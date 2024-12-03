/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.about;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.jfree.ui.SortableTable;
import org.jfree.ui.about.SystemPropertiesTableModel;

public class SystemProperties {
    private SystemProperties() {
    }

    public static SortableTable createSystemPropertiesTable() {
        SystemPropertiesTableModel properties = new SystemPropertiesTableModel();
        SortableTable table = new SortableTable(properties);
        TableColumnModel model = table.getColumnModel();
        TableColumn column = model.getColumn(0);
        column.setPreferredWidth(200);
        column = model.getColumn(1);
        column.setPreferredWidth(350);
        table.setAutoResizeMode(2);
        return table;
    }
}

