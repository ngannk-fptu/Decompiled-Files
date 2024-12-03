/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio;

import java.io.IOException;

public interface IOControl {
    public void requestInput();

    public void suspendInput();

    public void requestOutput();

    public void suspendOutput();

    public void shutdown() throws IOException;
}

