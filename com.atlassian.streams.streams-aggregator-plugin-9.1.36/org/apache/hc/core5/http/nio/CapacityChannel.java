/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio;

import java.io.IOException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading=ThreadingBehavior.SAFE)
public interface CapacityChannel {
    public void update(int var1) throws IOException;
}

