/*
 * Decompiled with CFR 0.152.
 */
package groovy.model;

import groovy.model.NestedValueModel;
import groovy.model.ValueModel;
import org.codehaus.groovy.runtime.InvokerHelper;

public class PropertyModel
implements ValueModel,
NestedValueModel {
    private ValueModel sourceModel;
    private String property;
    private Class type;
    boolean editable;

    public PropertyModel(ValueModel sourceModel, String property) {
        this(sourceModel, property, Object.class, true);
    }

    public PropertyModel(ValueModel sourceModel, String property, Class type) {
        this(sourceModel, property, type, true);
    }

    public PropertyModel(ValueModel sourceModel, String property, Class type, boolean editable) {
        this.sourceModel = sourceModel;
        this.property = property;
        this.type = type;
        this.editable = editable;
    }

    public String getProperty() {
        return this.property;
    }

    @Override
    public ValueModel getSourceModel() {
        return this.sourceModel;
    }

    @Override
    public Object getValue() {
        Object source = this.sourceModel.getValue();
        if (source != null) {
            return InvokerHelper.getProperty(source, this.property);
        }
        return null;
    }

    @Override
    public void setValue(Object value) {
        Object source = this.sourceModel.getValue();
        if (source != null) {
            InvokerHelper.setProperty(source, this.property, value);
        }
    }

    @Override
    public Class getType() {
        return this.type;
    }

    @Override
    public boolean isEditable() {
        return this.editable;
    }
}

