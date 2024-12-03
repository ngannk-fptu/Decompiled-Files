/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.io.Closeable;

public interface TrackedWebResource
extends Closeable {
    public Exception getCreatedBy();

    public String getName();
}

