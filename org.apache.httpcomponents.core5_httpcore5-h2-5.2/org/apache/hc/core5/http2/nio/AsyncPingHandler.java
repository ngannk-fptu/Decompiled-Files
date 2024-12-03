/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpException
 */
package org.apache.hc.core5.http2.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.hc.core5.http.HttpException;

public interface AsyncPingHandler {
    public ByteBuffer getData();

    public void consumeResponse(ByteBuffer var1) throws HttpException, IOException;

    public void failed(Exception var1);

    public void cancel();
}

