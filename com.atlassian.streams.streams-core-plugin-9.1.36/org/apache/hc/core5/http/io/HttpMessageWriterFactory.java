/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.io;

import org.apache.hc.core5.http.MessageHeaders;
import org.apache.hc.core5.http.io.HttpMessageWriter;

public interface HttpMessageWriterFactory<T extends MessageHeaders> {
    public HttpMessageWriter<T> create();
}

