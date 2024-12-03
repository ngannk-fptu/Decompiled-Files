/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.lease.event;

import java.util.Map;
import org.springframework.vault.core.lease.domain.Lease;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;

public class SecretLeaseRotatedEvent
extends SecretLeaseCreatedEvent {
    private final Lease previousLease;

    public SecretLeaseRotatedEvent(RequestedSecret requestedSecret, Lease previousLease, Lease currentLease, Map<String, Object> secrets) {
        super(requestedSecret, currentLease, secrets);
        this.previousLease = previousLease;
    }

    public Lease getPreviousLease() {
        return this.previousLease;
    }

    public Lease getCurrentLease() {
        return this.getLease();
    }
}

