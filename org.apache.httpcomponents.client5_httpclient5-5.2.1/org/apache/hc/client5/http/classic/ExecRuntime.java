/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.concurrent.CancellableDependency
 *  org.apache.hc.core5.http.ClassicHttpRequest
 *  org.apache.hc.core5.http.ClassicHttpResponse
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.util.TimeValue
 */
package org.apache.hc.client5.http.classic;

import java.io.IOException;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.CancellableDependency;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.util.TimeValue;

@Internal
public interface ExecRuntime {
    public boolean isExecutionAborted();

    public boolean isEndpointAcquired();

    public void acquireEndpoint(String var1, HttpRoute var2, Object var3, HttpClientContext var4) throws IOException;

    public void releaseEndpoint();

    public void discardEndpoint();

    public boolean isEndpointConnected();

    public void disconnectEndpoint() throws IOException;

    public void connectEndpoint(HttpClientContext var1) throws IOException;

    public void upgradeTls(HttpClientContext var1) throws IOException;

    public ClassicHttpResponse execute(String var1, ClassicHttpRequest var2, HttpClientContext var3) throws IOException, HttpException;

    public boolean isConnectionReusable();

    public void markConnectionReusable(Object var1, TimeValue var2);

    public void markConnectionNonReusable();

    public ExecRuntime fork(CancellableDependency var1);
}

