/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio;

import org.apache.hc.core5.http.MessageHeaders;
import org.apache.hc.core5.http.nio.NHttpMessageParser;

public interface NHttpMessageParserFactory<T extends MessageHeaders> {
    public NHttpMessageParser<T> create();
}

