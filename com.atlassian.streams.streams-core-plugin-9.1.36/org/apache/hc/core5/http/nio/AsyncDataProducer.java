/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio;

import java.io.IOException;
import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.apache.hc.core5.http.nio.ResourceHolder;

public interface AsyncDataProducer
extends ResourceHolder {
    public int available();

    public void produce(DataStreamChannel var1) throws IOException;
}

