/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.lease.event;

import org.springframework.lang.Nullable;
import org.springframework.vault.core.lease.domain.Lease;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseEvent;

public class SecretLeaseErrorEvent
extends SecretLeaseEvent {
    private static final long serialVersionUID = 1L;
    private final Throwable exception;

    public SecretLeaseErrorEvent(RequestedSecret requestedSecret, @Nullable Lease lease, Throwable exception) {
        super(requestedSecret, lease);
        this.exception = exception;
    }

    public Throwable getException() {
        return this.exception;
    }
}

