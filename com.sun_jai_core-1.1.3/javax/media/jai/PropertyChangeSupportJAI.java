/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.media.jai.JaiI18N;
import javax.media.jai.PropertyChangeEventJAI;

public final class PropertyChangeSupportJAI
extends PropertyChangeSupport {
    protected Object propertyChangeEventSource;

    public PropertyChangeSupportJAI(Object propertyChangeEventSource) {
        super(propertyChangeEventSource);
        this.propertyChangeEventSource = propertyChangeEventSource;
    }

    public Object getPropertyChangeEventSource() {
        return this.propertyChangeEventSource;
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        super.addPropertyChangeListener(propertyName.toLowerCase(), listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        super.removePropertyChangeListener(propertyName.toLowerCase(), listener);
    }

    public void firePropertyChange(PropertyChangeEvent evt) {
        if (!(evt instanceof PropertyChangeEventJAI)) {
            evt = new PropertyChangeEventJAI(evt.getSource(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }
        super.firePropertyChange(evt);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeEventJAI evt = new PropertyChangeEventJAI(this.propertyChangeEventSource, propertyName, oldValue, newValue);
        super.firePropertyChange(evt);
    }

    public synchronized boolean hasListeners(String propertyName) {
        return super.hasListeners(propertyName.toLowerCase());
    }
}

