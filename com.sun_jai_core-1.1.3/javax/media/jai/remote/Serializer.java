/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.awt.RenderingHints;
import javax.media.jai.remote.SerializableState;

public interface Serializer {
    public Class getSupportedClass();

    public boolean permitsSubclasses();

    public SerializableState getState(Object var1, RenderingHints var2);
}

