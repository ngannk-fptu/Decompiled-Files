/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.ClassicHttpRequest
 *  org.apache.hc.core5.http.ClassicHttpResponse
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.classic;

import java.io.IOException;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.classic.ExecRuntime;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.STATELESS)
public interface ExecChain {
    public ClassicHttpResponse proceed(ClassicHttpRequest var1, Scope var2) throws IOException, HttpException;

    public static final class Scope {
        public final String exchangeId;
        public final HttpRoute route;
        public final ClassicHttpRequest originalRequest;
        public final ExecRuntime execRuntime;
        public final HttpClientContext clientContext;

        public Scope(String exchangeId, HttpRoute route, ClassicHttpRequest originalRequest, ExecRuntime execRuntime, HttpClientContext clientContext) {
            this.exchangeId = (String)Args.notNull((Object)exchangeId, (String)"Exchange id");
            this.route = (HttpRoute)Args.notNull((Object)route, (String)"Route");
            this.originalRequest = (ClassicHttpRequest)Args.notNull((Object)originalRequest, (String)"Original request");
            this.execRuntime = (ExecRuntime)Args.notNull((Object)execRuntime, (String)"Exec runtime");
            this.clientContext = clientContext != null ? clientContext : HttpClientContext.create();
        }
    }
}

