/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zipkin2.Endpoint
 */
package brave;

import brave.SpanCustomizer;
import brave.internal.Nullable;
import brave.propagation.TraceContext;
import zipkin2.Endpoint;

public abstract class Span
implements SpanCustomizer {
    public abstract boolean isNoop();

    public abstract TraceContext context();

    public abstract SpanCustomizer customizer();

    public abstract Span start();

    public abstract Span start(long var1);

    @Override
    public abstract Span name(String var1);

    public abstract Span kind(@Nullable Kind var1);

    @Override
    public abstract Span annotate(String var1);

    public abstract Span annotate(long var1, String var3);

    @Override
    public abstract Span tag(String var1, String var2);

    public abstract Span error(Throwable var1);

    @Deprecated
    public Span remoteEndpoint(Endpoint endpoint) {
        if (endpoint == null) {
            return this;
        }
        if (endpoint.serviceName() != null) {
            this.remoteServiceName(endpoint.serviceName());
        }
        String ip = endpoint.ipv6() != null ? endpoint.ipv6() : endpoint.ipv4();
        this.remoteIpAndPort(ip, endpoint.portAsInt());
        return this;
    }

    public abstract Span remoteServiceName(String var1);

    public abstract boolean remoteIpAndPort(@Nullable String var1, int var2);

    public abstract void finish();

    public abstract void abandon();

    public abstract void finish(long var1);

    public abstract void flush();

    Span() {
    }

    public static enum Kind {
        CLIENT,
        SERVER,
        PRODUCER,
        CONSUMER;

    }
}

