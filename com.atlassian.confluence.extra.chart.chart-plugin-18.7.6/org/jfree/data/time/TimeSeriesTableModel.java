/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.time;

import javax.swing.table.AbstractTableModel;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;

public class TimeSeriesTableModel
extends AbstractTableModel
implements SeriesChangeListener {
    private TimeSeries series;
    private boolean editable;
    private RegularTimePeriod newTimePeriod;
    private Number newValue;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Double;

    public TimeSeriesTableModel() {
        this(new TimeSeries((Comparable)((Object)"Untitled")));
    }

    public TimeSeriesTableModel(TimeSeries series) {
        this(series, false);
    }

    public TimeSeriesTableModel(TimeSeries series, boolean editable) {
        this.series = series;
        this.series.addChangeListener(this);
        this.editable = editable;
    }

    public int getColumnCount() {
        return 2;
    }

    public Class getColumnClass(int column) {
        if (column == 0) {
            return class$java$lang$String == null ? (class$java$lang$String = TimeSeriesTableModel.class$("java.lang.String")) : class$java$lang$String;
        }
        if (column == 1) {
            return class$java$lang$Double == null ? (class$java$lang$Double = TimeSeriesTableModel.class$("java.lang.Double")) : class$java$lang$Double;
        }
        return null;
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "Period:";
        }
        if (column == 1) {
            return "Value:";
        }
        return null;
    }

    public int getRowCount() {
        return this.series.getItemCount();
    }

    public Object getValueAt(int row, int column) {
        if (row < this.series.getItemCount()) {
            if (column == 0) {
                return this.series.getTimePeriod(row);
            }
            if (column == 1) {
                return this.series.getValue(row);
            }
            return null;
        }
        if (column == 0) {
            return this.newTimePeriod;
        }
        if (column == 1) {
            return this.newValue;
        }
        return null;
    }

    public boolean isCellEditable(int row, int column) {
        if (this.editable) {
            return column == 0 || column == 1;
        }
        return false;
    }

    public void setValueAt(Object value, int row, int column) {
        if (row < this.series.getItemCount()) {
            if (column == 1) {
                try {
                    Double v = Double.valueOf(value.toString());
                    this.series.update(row, (Number)v);
                }
                catch (NumberFormatException nfe) {
                    System.err.println("Number format exception");
                }
            }
        } else if (column == 0) {
            this.newTimePeriod = null;
        } else if (column == 1) {
            this.newValue = Double.valueOf(value.toString());
        }
    }

    public void seriesChanged(SeriesChangeEvent event) {
        this.fireTableDataChanged();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

