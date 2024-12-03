/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.general;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;

public abstract class AbstractDataset
implements Dataset,
Cloneable,
Serializable,
ObjectInputValidation {
    private static final long serialVersionUID = 1918768939869230744L;
    private DatasetGroup group = new DatasetGroup();
    private transient EventListenerList listenerList = new EventListenerList();
    static /* synthetic */ Class class$org$jfree$data$general$DatasetChangeListener;

    protected AbstractDataset() {
    }

    public DatasetGroup getGroup() {
        return this.group;
    }

    public void setGroup(DatasetGroup group) {
        if (group == null) {
            throw new IllegalArgumentException("Null 'group' argument.");
        }
        this.group = group;
    }

    public void addChangeListener(DatasetChangeListener listener) {
        this.listenerList.add(class$org$jfree$data$general$DatasetChangeListener == null ? (class$org$jfree$data$general$DatasetChangeListener = AbstractDataset.class$("org.jfree.data.general.DatasetChangeListener")) : class$org$jfree$data$general$DatasetChangeListener, listener);
    }

    public void removeChangeListener(DatasetChangeListener listener) {
        this.listenerList.remove(class$org$jfree$data$general$DatasetChangeListener == null ? (class$org$jfree$data$general$DatasetChangeListener = AbstractDataset.class$("org.jfree.data.general.DatasetChangeListener")) : class$org$jfree$data$general$DatasetChangeListener, listener);
    }

    public boolean hasListener(EventListener listener) {
        List<Object> list = Arrays.asList(this.listenerList.getListenerList());
        return list.contains(listener);
    }

    protected void fireDatasetChanged() {
        this.notifyListeners(new DatasetChangeEvent(this, this));
    }

    protected void notifyListeners(DatasetChangeEvent event) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != (class$org$jfree$data$general$DatasetChangeListener == null ? AbstractDataset.class$("org.jfree.data.general.DatasetChangeListener") : class$org$jfree$data$general$DatasetChangeListener)) continue;
            ((DatasetChangeListener)listeners[i + 1]).datasetChanged(event);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        AbstractDataset clone = (AbstractDataset)super.clone();
        clone.listenerList = new EventListenerList();
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.listenerList = new EventListenerList();
        stream.registerValidation(this, 10);
    }

    public void validateObject() throws InvalidObjectException {
        this.fireDatasetChanged();
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

