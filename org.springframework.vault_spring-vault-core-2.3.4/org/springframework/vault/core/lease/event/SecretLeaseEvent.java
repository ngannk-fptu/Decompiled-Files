/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.lang.Nullable
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
        super((Object)requestedSecret);
        this.lease = lease;
    }

    public RequestedSecret getSource() {
        return (RequestedSecret)super.getSource();
    }

    @Nullable
    public Lease getLease() {
        return this.lease;
    }
}

