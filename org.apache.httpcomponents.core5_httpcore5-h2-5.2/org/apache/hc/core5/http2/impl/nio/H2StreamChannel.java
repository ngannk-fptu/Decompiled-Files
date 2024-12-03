/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.concurrent.Cancellable
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.nio.AsyncPushProducer
 *  org.apache.hc.core5.http.nio.CapacityChannel
 *  org.apache.hc.core5.http.nio.DataStreamChannel
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.util.List;
import org.apache.hc.core5.concurrent.Cancellable;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.nio.AsyncPushProducer;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.http.nio.DataStreamChannel;

interface H2StreamChannel
extends DataStreamChannel,
CapacityChannel,
Cancellable {
    public void submit(List<Header> var1, boolean var2) throws HttpException, IOException;

    public void push(List<Header> var1, AsyncPushProducer var2) throws HttpException, IOException;
}

