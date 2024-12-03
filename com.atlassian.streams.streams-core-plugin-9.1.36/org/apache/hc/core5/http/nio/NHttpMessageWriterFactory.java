/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio;

import org.apache.hc.core5.http.MessageHeaders;
import org.apache.hc.core5.http.nio.NHttpMessageWriter;

public interface NHttpMessageWriterFactory<T extends MessageHeaders> {
    public NHttpMessageWriter<T> create();
}

