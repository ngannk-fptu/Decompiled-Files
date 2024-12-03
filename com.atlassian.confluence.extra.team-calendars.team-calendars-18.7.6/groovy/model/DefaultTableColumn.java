/*
 * Decompiled with CFR 0.152.
 */
package groovy.model;

import groovy.model.NestedValueModel;
import groovy.model.ValueModel;
import javax.swing.table.TableColumn;

public class DefaultTableColumn
extends TableColumn {
    private ValueModel valueModel;

    public DefaultTableColumn(ValueModel valueModel) {
        this.valueModel = valueModel;
    }

    public DefaultTableColumn(Object header, ValueModel valueModel) {
        this(valueModel);
        this.setHeaderValue(header);
    }

    public DefaultTableColumn(Object headerValue, Object identifier, ValueModel columnValueModel) {
        this(headerValue, columnValueModel);
        this.setIdentifier(identifier);
    }

    public String toString() {
        return super.toString() + "[header:" + this.getHeaderValue() + " valueModel:" + this.valueModel + "]";
    }

    public Object getValue(Object row, int rowIndex, int columnIndex) {
        if (this.valueModel instanceof NestedValueModel) {
            NestedValueModel nestedModel = (NestedValueModel)((Object)this.valueModel);
            nestedModel.getSourceModel().setValue(row);
        }
        return this.valueModel.getValue();
    }

    public void setValue(Object row, Object value, int rowIndex, int columnIndex) {
        if (this.valueModel instanceof NestedValueModel) {
            NestedValueModel nestedModel = (NestedValueModel)((Object)this.valueModel);
            nestedModel.getSourceModel().setValue(row);
        }
        this.valueModel.setValue(value);
    }

    public Class getType() {
        return this.valueModel.getType();
    }

    public ValueModel getValueModel() {
        return this.valueModel;
    }
}

