/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.nio.client.AbstractClientExchangeHandler;
import org.apache.http.impl.nio.client.InternalState;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;

interface InternalClientExec {
    public void prepare(HttpHost var1, HttpRequest var2, InternalState var3, AbstractClientExchangeHandler var4) throws IOException, HttpException;

    public HttpRequest generateRequest(InternalState var1, AbstractClientExchangeHandler var2) throws IOException, HttpException;

    public void produceContent(InternalState var1, ContentEncoder var2, IOControl var3) throws IOException;

    public void requestCompleted(InternalState var1, AbstractClientExchangeHandler var2);

    public void responseReceived(HttpResponse var1, InternalState var2, AbstractClientExchangeHandler var3) throws IOException, HttpException;

    public void consumeContent(InternalState var1, ContentDecoder var2, IOControl var3) throws IOException;

    public void responseCompleted(InternalState var1, AbstractClientExchangeHandler var2) throws IOException, HttpException;
}

