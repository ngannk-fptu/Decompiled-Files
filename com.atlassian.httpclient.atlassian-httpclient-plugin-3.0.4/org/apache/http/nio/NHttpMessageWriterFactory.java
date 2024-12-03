/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio;

import org.apache.http.HttpMessage;
import org.apache.http.nio.NHttpMessageWriter;
import org.apache.http.nio.reactor.SessionOutputBuffer;

public interface NHttpMessageWriterFactory<T extends HttpMessage> {
    public NHttpMessageWriter<T> create(SessionOutputBuffer var1);
}

