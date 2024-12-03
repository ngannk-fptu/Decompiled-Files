/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.io.Serializable;
import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.SerializableState;

class SerState
implements SerializableState {
    private Serializable object;

    SerState(Serializable object) {
        if (object == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.object = object;
    }

    public Class getObjectClass() {
        return this.object.getClass();
    }

    public Object getObject() {
        return this.object;
    }
}

