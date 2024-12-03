/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.beans.PropertyChangeEvent;
import javax.media.jai.JaiI18N;

public class PropertyChangeEventJAI
extends PropertyChangeEvent {
    private String originalPropertyName;

    public PropertyChangeEventJAI(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName.toLowerCase(), oldValue, newValue);
        if (source == null) {
            throw new IllegalArgumentException(JaiI18N.getString("PropertyChangeEventJAI0"));
        }
        if (oldValue == null && newValue == null) {
            throw new IllegalArgumentException(JaiI18N.getString("PropertyChangeEventJAI1"));
        }
        this.originalPropertyName = propertyName.equals(this.getPropertyName()) ? this.getPropertyName() : propertyName;
    }

    public String getOriginalPropertyName() {
        return this.originalPropertyName;
    }
}

