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
import io.micrometer.observation.transport.ReceiverContext;
import io.micrometer.observation.transport.ResponseContext;

public class RequestReplyReceiverContext<C, RES>
extends ReceiverContext<C>
implements ResponseContext<RES> {
    @Nullable
    private RES response;

    public RequestReplyReceiverContext(@NonNull Propagator.Getter<C> getter, @NonNull Kind kind) {
        super(getter, kind);
    }

    public RequestReplyReceiverContext(@NonNull Propagator.Getter<C> getter) {
        this(getter, Kind.SERVER);
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

