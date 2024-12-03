/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.observation.transport.RequestReplySenderContext
 *  org.eclipse.jetty.client.api.Request
 *  org.eclipse.jetty.client.api.Result
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.observation.transport.RequestReplySenderContext;
import java.util.Objects;
import java.util.function.BiFunction;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Result;

public class JettyClientContext
extends RequestReplySenderContext<Request, Result> {
    private final BiFunction<Request, Result, String> uriPatternFunction;

    public JettyClientContext(Request request, BiFunction<Request, Result, String> uriPatternFunction) {
        super((carrier, key, value) -> Objects.requireNonNull(carrier).header(key, value));
        this.uriPatternFunction = uriPatternFunction;
        this.setCarrier(request);
    }

    public BiFunction<Request, Result, String> getUriPatternFunction() {
        return this.uriPatternFunction;
    }
}

