/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.io.Serializable;

public interface SerializableState
extends Serializable {
    public Class getObjectClass();

    public Object getObject();
}

