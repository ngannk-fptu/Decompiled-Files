/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.io;

import java.io.IOException;

public interface IOCallback<T> {
    public void execute(T var1) throws IOException;
}

