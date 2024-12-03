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
import io.micrometer.observation.Observation;
import io.micrometer.observation.transport.Kind;
import io.micrometer.observation.transport.Propagator;
import java.util.Objects;

public class ReceiverContext<C>
extends Observation.Context {
    private final Propagator.Getter<C> getter;
    private final Kind kind;
    private C carrier;
    @Nullable
    private String remoteServiceName;
    @Nullable
    private String remoteServiceAddress;

    public ReceiverContext(@NonNull Propagator.Getter<C> getter, @NonNull Kind kind) {
        this.getter = Objects.requireNonNull(getter, "Getter must be set");
        this.kind = Objects.requireNonNull(kind, "Kind must be set");
    }

    public ReceiverContext(@NonNull Propagator.Getter<C> getter) {
        this(getter, Kind.CONSUMER);
    }

    public C getCarrier() {
        return this.carrier;
    }

    public void setCarrier(C carrier) {
        this.carrier = carrier;
    }

    public Propagator.Getter<C> getGetter() {
        return this.getter;
    }

    public Kind getKind() {
        return this.kind;
    }

    @Nullable
    public String getRemoteServiceName() {
        return this.remoteServiceName;
    }

    public void setRemoteServiceName(@Nullable String remoteServiceName) {
        this.remoteServiceName = remoteServiceName;
    }

    @Nullable
    public String getRemoteServiceAddress() {
        return this.remoteServiceAddress;
    }

    public void setRemoteServiceAddress(@Nullable String remoteServiceAddress) {
        this.remoteServiceAddress = remoteServiceAddress;
    }
}

