/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.TableXYDataset;

public class XYDatasetTableModel
extends AbstractTableModel
implements TableModel,
DatasetChangeListener {
    TableXYDataset model = null;

    public XYDatasetTableModel() {
    }

    public XYDatasetTableModel(TableXYDataset dataset) {
        this();
        this.model = dataset;
        this.model.addChangeListener(this);
    }

    public void setModel(TableXYDataset dataset) {
        this.model = dataset;
        this.model.addChangeListener(this);
        this.fireTableDataChanged();
    }

    public int getRowCount() {
        if (this.model == null) {
            return 0;
        }
        return this.model.getItemCount();
    }

    public int getColumnCount() {
        if (this.model == null) {
            return 0;
        }
        return this.model.getSeriesCount() + 1;
    }

    public String getColumnName(int column) {
        if (this.model == null) {
            return super.getColumnName(column);
        }
        if (column < 1) {
            return "X Value";
        }
        return this.model.getSeriesKey(column - 1).toString();
    }

    public Object getValueAt(int row, int column) {
        if (this.model == null) {
            return null;
        }
        if (column < 1) {
            return this.model.getX(0, row);
        }
        return this.model.getY(column - 1, row);
    }

    public void datasetChanged(DatasetChangeEvent event) {
        this.fireTableDataChanged();
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void setValueAt(Object value, int row, int column) {
        if (this.isCellEditable(row, column)) {
            // empty if block
        }
    }
}

