/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNull
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.observation.transport;

import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.transport.Kind;
import io.micrometer.observation.transport.Propagator;
import io.micrometer.observation.transport.ResponseContext;
import io.micrometer.observation.transport.SenderContext;

public class RequestReplySenderContext<C, RES>
extends SenderContext<C>
implements ResponseContext<RES> {
    @Nullable
    private RES response;

    public RequestReplySenderContext(@NonNull Propagator.Setter<C> setter, @NonNull Kind kind) {
        super(setter, kind);
    }

    public RequestReplySenderContext(@NonNull Propagator.Setter<C> setter) {
        this(setter, Kind.CLIENT);
    }

    @Override
    @Nullable
    public RES getResponse() {
        return this.response;
    }

    @Override
    public void setResponse(RES response) {
        this.response = response;
    }
}

