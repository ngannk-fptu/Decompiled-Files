/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.io;

import java.io.Closeable;
import org.apache.hc.core5.io.CloseMode;

public interface ModalCloseable
extends Closeable {
    public void close(CloseMode var1);
}

