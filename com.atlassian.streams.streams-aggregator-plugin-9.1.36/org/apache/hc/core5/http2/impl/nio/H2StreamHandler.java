/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.ResourceHolder;

interface H2StreamHandler
extends ResourceHolder {
    public boolean isOutputReady();

    public void produceOutput() throws HttpException, IOException;

    public void consumePromise(List<Header> var1) throws HttpException, IOException;

    public void consumeHeader(List<Header> var1, boolean var2) throws HttpException, IOException;

    public void updateInputCapacity() throws IOException;

    public void consumeData(ByteBuffer var1, boolean var2) throws HttpException, IOException;

    public HandlerFactory<AsyncPushConsumer> getPushHandlerFactory();

    public void failed(Exception var1);

    public void handle(HttpException var1, boolean var2) throws HttpException, IOException;
}

