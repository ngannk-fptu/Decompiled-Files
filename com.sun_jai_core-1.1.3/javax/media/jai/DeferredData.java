/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.io.Serializable;
import java.util.Observable;
import javax.media.jai.JaiI18N;

public abstract class DeferredData
extends Observable
implements Serializable {
    protected Class dataClass;
    protected transient Object data;

    protected DeferredData(Class dataClass) {
        if (dataClass == null) {
            throw new IllegalArgumentException(JaiI18N.getString("DeferredData0"));
        }
        this.dataClass = dataClass;
    }

    public Class getDataClass() {
        return this.dataClass;
    }

    public boolean isValid() {
        return this.data != null;
    }

    protected abstract Object computeData();

    public final synchronized Object getData() {
        if (this.data == null) {
            this.setData(this.computeData());
        }
        return this.data;
    }

    protected final void setData(Object data) {
        if (data != null && !this.dataClass.isInstance(data)) {
            throw new IllegalArgumentException(JaiI18N.getString("DeferredData1"));
        }
        if (this.data == null || !this.data.equals(data)) {
            Object oldData = this.data;
            this.data = data;
            this.setChanged();
            this.notifyObservers(oldData);
        }
    }
}

