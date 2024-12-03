/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.async;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.async.AsyncExecCallback;
import org.apache.hc.client5.http.async.AsyncExecRuntime;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.CancellableDependency;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;

@Contract(threading=ThreadingBehavior.STATELESS)
public interface AsyncExecChain {
    public void proceed(HttpRequest var1, AsyncEntityProducer var2, Scope var3, AsyncExecCallback var4) throws HttpException, IOException;

    public static interface Scheduler {
        public void scheduleExecution(HttpRequest var1, AsyncEntityProducer var2, Scope var3, AsyncExecCallback var4, TimeValue var5);
    }

    public static final class Scope {
        public final String exchangeId;
        public final HttpRoute route;
        public final HttpRequest originalRequest;
        public final CancellableDependency cancellableDependency;
        public final HttpClientContext clientContext;
        public final AsyncExecRuntime execRuntime;
        public final Scheduler scheduler;
        public final AtomicInteger execCount;

        public Scope(String exchangeId, HttpRoute route, HttpRequest originalRequest, CancellableDependency cancellableDependency, HttpClientContext clientContext, AsyncExecRuntime execRuntime, Scheduler scheduler, AtomicInteger execCount) {
            this.exchangeId = Args.notBlank(exchangeId, "Exchange id");
            this.route = Args.notNull(route, "Route");
            this.originalRequest = Args.notNull(originalRequest, "Original request");
            this.cancellableDependency = Args.notNull(cancellableDependency, "Dependency");
            this.clientContext = clientContext != null ? clientContext : HttpClientContext.create();
            this.execRuntime = Args.notNull(execRuntime, "Exec runtime");
            this.scheduler = scheduler;
            this.execCount = execCount != null ? execCount : new AtomicInteger(1);
        }

        @Deprecated
        public Scope(String exchangeId, HttpRoute route, HttpRequest originalRequest, CancellableDependency cancellableDependency, HttpClientContext clientContext, AsyncExecRuntime execRuntime) {
            this(exchangeId, route, originalRequest, cancellableDependency, clientContext, execRuntime, null, new AtomicInteger(1));
        }
    }
}

