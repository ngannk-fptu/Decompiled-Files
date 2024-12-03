/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.general;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.swing.event.EventListenerList;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.util.ObjectUtilities;

public abstract class Series
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -6906561437538683581L;
    private Comparable key;
    private String description;
    private EventListenerList listeners;
    private PropertyChangeSupport propertyChangeSupport;
    private boolean notify;
    static /* synthetic */ Class class$org$jfree$data$general$SeriesChangeListener;

    protected Series(Comparable key) {
        this(key, null);
    }

    protected Series(Comparable key, String description) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        this.key = key;
        this.description = description;
        this.listeners = new EventListenerList();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.notify = true;
    }

    public Comparable getKey() {
        return this.key;
    }

    public void setKey(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        Comparable old = this.key;
        this.key = key;
        this.propertyChangeSupport.firePropertyChange("Key", old, key);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        String old = this.description;
        this.description = description;
        this.propertyChangeSupport.firePropertyChange("Description", old, description);
    }

    public boolean getNotify() {
        return this.notify;
    }

    public void setNotify(boolean notify) {
        if (this.notify != notify) {
            this.notify = notify;
            this.fireSeriesChanged();
        }
    }

    public boolean isEmpty() {
        return this.getItemCount() == 0;
    }

    public abstract int getItemCount();

    public Object clone() throws CloneNotSupportedException {
        Series clone = (Series)super.clone();
        clone.listeners = new EventListenerList();
        clone.propertyChangeSupport = new PropertyChangeSupport(clone);
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Series)) {
            return false;
        }
        Series that = (Series)obj;
        if (!this.getKey().equals(that.getKey())) {
            return false;
        }
        return ObjectUtilities.equal(this.getDescription(), that.getDescription());
    }

    public int hashCode() {
        int result = this.key.hashCode();
        result = 29 * result + (this.description != null ? this.description.hashCode() : 0);
        return result;
    }

    public void addChangeListener(SeriesChangeListener listener) {
        this.listeners.add(class$org$jfree$data$general$SeriesChangeListener == null ? (class$org$jfree$data$general$SeriesChangeListener = Series.class$("org.jfree.data.general.SeriesChangeListener")) : class$org$jfree$data$general$SeriesChangeListener, listener);
    }

    public void removeChangeListener(SeriesChangeListener listener) {
        this.listeners.remove(class$org$jfree$data$general$SeriesChangeListener == null ? (class$org$jfree$data$general$SeriesChangeListener = Series.class$("org.jfree.data.general.SeriesChangeListener")) : class$org$jfree$data$general$SeriesChangeListener, listener);
    }

    public void fireSeriesChanged() {
        if (this.notify) {
            this.notifyListeners(new SeriesChangeEvent(this));
        }
    }

    protected void notifyListeners(SeriesChangeEvent event) {
        Object[] listenerList = this.listeners.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] != (class$org$jfree$data$general$SeriesChangeListener == null ? Series.class$("org.jfree.data.general.SeriesChangeListener") : class$org$jfree$data$general$SeriesChangeListener)) continue;
            ((SeriesChangeListener)listenerList[i + 1]).seriesChanged(event);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String property, Object oldValue, Object newValue) {
        this.propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
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

