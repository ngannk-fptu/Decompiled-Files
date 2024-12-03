/*
 * Decompiled with CFR 0.152.
 */
package groovy.model;

import groovy.lang.Closure;
import groovy.model.ClosureModel;
import groovy.model.DefaultTableColumn;
import groovy.model.PropertyModel;
import groovy.model.ValueHolder;
import groovy.model.ValueModel;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.codehaus.groovy.runtime.InvokerHelper;

public class DefaultTableModel
extends AbstractTableModel {
    private ValueModel rowModel;
    private ValueModel rowsModel;
    private MyTableColumnModel columnModel = new MyTableColumnModel();

    public DefaultTableModel(ValueModel rowsModel) {
        this(rowsModel, new ValueHolder());
    }

    public DefaultTableModel(ValueModel rowsModel, ValueModel rowModel) {
        this.rowModel = rowModel;
        this.rowsModel = rowsModel;
    }

    public List getColumnList() {
        return this.columnModel.getColumnList();
    }

    public TableColumnModel getColumnModel() {
        return this.columnModel;
    }

    public DefaultTableColumn addPropertyColumn(Object headerValue, String property, Class type) {
        return this.addColumn(headerValue, property, new PropertyModel(this.rowModel, property, type));
    }

    public DefaultTableColumn addPropertyColumn(Object headerValue, String property, Class type, boolean editable) {
        return this.addColumn(headerValue, property, new PropertyModel(this.rowModel, property, type, editable));
    }

    public DefaultTableColumn addClosureColumn(Object headerValue, Closure readClosure, Closure writeClosure, Class type) {
        return this.addColumn(headerValue, new ClosureModel(this.rowModel, readClosure, writeClosure, type));
    }

    public DefaultTableColumn addColumn(Object headerValue, ValueModel columnValueModel) {
        return this.addColumn(headerValue, headerValue, columnValueModel);
    }

    public DefaultTableColumn addColumn(Object headerValue, Object identifier, ValueModel columnValueModel) {
        DefaultTableColumn answer = new DefaultTableColumn(headerValue, identifier, columnValueModel);
        this.addColumn(answer);
        return answer;
    }

    public void addColumn(DefaultTableColumn column) {
        column.setModelIndex(this.columnModel.getColumnCount());
        this.columnModel.addColumn(column);
    }

    public void removeColumn(DefaultTableColumn column) {
        this.columnModel.removeColumn(column);
    }

    @Override
    public int getRowCount() {
        return this.getRows().size();
    }

    @Override
    public int getColumnCount() {
        return this.columnModel.getColumnCount();
    }

    @Override
    public String getColumnName(int columnIndex) {
        String answer = null;
        if (columnIndex < 0 || columnIndex >= this.columnModel.getColumnCount()) {
            return answer;
        }
        Object value = this.columnModel.getColumn(columnIndex).getHeaderValue();
        if (value != null) {
            return value.toString();
        }
        return answer;
    }

    public Class getColumnClass(int columnIndex) {
        return this.getColumnModel(columnIndex).getType();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return this.getColumnModel(columnIndex).isEditable();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        List rows = this.getRows();
        Object answer = null;
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return answer;
        }
        if (columnIndex < 0 || columnIndex >= this.columnModel.getColumnCount()) {
            return answer;
        }
        Object row = this.getRows().get(rowIndex);
        this.rowModel.setValue(row);
        DefaultTableColumn column = (DefaultTableColumn)this.columnModel.getColumn(columnIndex);
        if (row == null || column == null) {
            return answer;
        }
        return column.getValue(row, rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        List rows = this.getRows();
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return;
        }
        if (columnIndex < 0 || columnIndex >= this.columnModel.getColumnCount()) {
            return;
        }
        Object row = this.getRows().get(rowIndex);
        this.rowModel.setValue(row);
        DefaultTableColumn column = (DefaultTableColumn)this.columnModel.getColumn(columnIndex);
        if (row == null || column == null) {
            return;
        }
        column.setValue(row, value, rowIndex, columnIndex);
    }

    protected ValueModel getColumnModel(int columnIndex) {
        DefaultTableColumn column = (DefaultTableColumn)this.columnModel.getColumn(columnIndex);
        return column.getValueModel();
    }

    protected List getRows() {
        Object value = this.rowsModel.getValue();
        if (value == null) {
            return Collections.EMPTY_LIST;
        }
        return InvokerHelper.asList(value);
    }

    public ValueModel getRowModel() {
        return this.rowModel;
    }

    public ValueModel getRowsModel() {
        return this.rowsModel;
    }

    protected static class MyTableColumnModel
    extends DefaultTableColumnModel {
        protected MyTableColumnModel() {
        }

        public List getColumnList() {
            return this.tableColumns;
        }

        @Override
        public void removeColumn(TableColumn column) {
            super.removeColumn(column);
            this.renumberTableColumns();
        }

        @Override
        public void moveColumn(int columnIndex, int newIndex) {
            super.moveColumn(columnIndex, newIndex);
            this.renumberTableColumns();
        }

        public void renumberTableColumns() {
            for (int i = this.tableColumns.size() - 1; i >= 0; --i) {
                ((DefaultTableColumn)this.tableColumns.get(i)).setModelIndex(i);
            }
        }
    }
}

