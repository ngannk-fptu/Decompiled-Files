/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.lease.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;
import org.springframework.vault.core.lease.domain.Lease;
import org.springframework.vault.core.lease.domain.RequestedSecret;

public abstract class SecretLeaseEvent
extends ApplicationEvent {
    private static final long serialVersionUID = 1L;
    @Nullable
    private final Lease lease;

    protected SecretLeaseEvent(RequestedSecret requestedSecret, @Nullable Lease lease) {
        super(requestedSecret);
        this.lease = lease;
    }

    @Override
    public RequestedSecret getSource() {
        return (RequestedSecret)super.getSource();
    }

    @Nullable
    public Lease getLease() {
        return this.lease;
    }
}

