/*
 * Decompiled with CFR 0.152.
 */
package groovy.model;

import groovy.model.ValueModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ValueHolder
implements ValueModel {
    private Object value;
    private final Class type;
    private PropertyChangeSupport propertyChangeSupport;
    private boolean editable = true;

    public ValueHolder() {
        this(Object.class);
    }

    public ValueHolder(Class type) {
        this.type = type;
    }

    public ValueHolder(Object value) {
        this.value = value;
        this.type = value != null ? value.getClass() : Object.class;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (this.propertyChangeSupport == null) {
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (this.propertyChangeSupport != null) {
            this.propertyChangeSupport.removePropertyChangeListener(listener);
        }
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void setValue(Object value) {
        Object oldValue = this.value;
        this.value = value;
        if (this.propertyChangeSupport != null) {
            this.propertyChangeSupport.firePropertyChange("value", oldValue, value);
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

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}

