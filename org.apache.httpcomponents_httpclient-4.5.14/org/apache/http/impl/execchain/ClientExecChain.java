/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpException
 */
package org.apache.http.impl.execchain;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;

public interface ClientExecChain {
    public CloseableHttpResponse execute(HttpRoute var1, HttpRequestWrapper var2, HttpClientContext var3, HttpExecutionAware var4) throws IOException, HttpException;
}

