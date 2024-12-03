/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.media.jai.DeferredData;
import javax.media.jai.JaiI18N;
import javax.media.jai.PropertyChangeEmitter;
import javax.media.jai.PropertySource;
import javax.media.jai.PropertySourceChangeEvent;
import javax.media.jai.RenderingChangeEvent;

public class DeferredProperty
extends DeferredData
implements PropertyChangeListener {
    protected transient PropertySource propertySource;
    protected String propertyName;

    public DeferredProperty(PropertySource propertySource, String propertyName, Class propertyClass) {
        super(propertyClass);
        if (propertySource == null || propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("DeferredData0"));
        }
        String[] propertyNames = propertySource.getPropertyNames();
        boolean isPropertyEmitted = false;
        if (propertyNames != null) {
            int length = propertyNames.length;
            for (int i = 0; i < length; ++i) {
                if (!propertyName.equalsIgnoreCase(propertyNames[i])) continue;
                isPropertyEmitted = true;
                break;
            }
        }
        if (!isPropertyEmitted) {
            throw new IllegalArgumentException(JaiI18N.getString("DeferredProperty0"));
        }
        if (propertySource instanceof PropertyChangeEmitter) {
            PropertyChangeEmitter pce = (PropertyChangeEmitter)((Object)propertySource);
            pce.addPropertyChangeListener(propertyName, this);
        }
        this.propertySource = propertySource;
        this.propertyName = propertyName;
    }

    public PropertySource getPropertySource() {
        return this.propertySource;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    protected Object computeData() {
        return this.propertySource.getProperty(this.propertyName);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DeferredProperty)) {
            return false;
        }
        DeferredProperty dp = (DeferredProperty)obj;
        return this.propertyName.equalsIgnoreCase(dp.getPropertyName()) && this.propertySource.equals(dp.getPropertySource()) && (!this.isValid() || !dp.isValid() || this.data.equals(dp.getData()));
    }

    public int hashCode() {
        return this.propertySource.hashCode() ^ this.propertyName.toLowerCase().hashCode();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == this.propertySource) {
            if (evt instanceof RenderingChangeEvent) {
                this.setData(null);
            } else if (evt instanceof PropertySourceChangeEvent && this.propertyName.equalsIgnoreCase(evt.getPropertyName())) {
                Object newValue = evt.getNewValue();
                this.setData(newValue == Image.UndefinedProperty ? null : newValue);
            }
        }
    }
}

