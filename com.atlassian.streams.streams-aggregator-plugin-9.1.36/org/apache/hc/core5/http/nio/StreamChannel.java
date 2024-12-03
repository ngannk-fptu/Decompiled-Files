/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio;

import java.io.IOException;
import java.nio.Buffer;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading=ThreadingBehavior.SAFE)
public interface StreamChannel<T extends Buffer> {
    public int write(T var1) throws IOException;

    public void endStream() throws IOException;
}

