/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading=ThreadingBehavior.SAFE)
public interface ResourceHolder {
    public void releaseResources();
}

