/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.awt.RenderingHints;
import java.io.Serializable;
import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.SerState;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.Serializer;
import javax.media.jai.remote.SerializerFactory;

class SerSerializer
implements Serializer {
    static /* synthetic */ Class class$java$io$Serializable;

    SerSerializer() {
    }

    public Class getSupportedClass() {
        return class$java$io$Serializable == null ? (class$java$io$Serializable = SerSerializer.class$("java.io.Serializable")) : class$java$io$Serializable;
    }

    public boolean permitsSubclasses() {
        return true;
    }

    public SerializableState getState(Object o, RenderingHints h) {
        if (o == null) {
            return SerializerFactory.NULL_STATE;
        }
        if (!(o instanceof Serializable)) {
            throw new IllegalArgumentException(JaiI18N.getString("SerializerFactory2"));
        }
        return new SerState((Serializable)o);
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

