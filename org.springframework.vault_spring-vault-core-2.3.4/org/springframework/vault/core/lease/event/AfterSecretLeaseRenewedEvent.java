/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.lease.event;

import org.springframework.vault.core.lease.domain.Lease;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseEvent;

public class AfterSecretLeaseRenewedEvent
extends SecretLeaseEvent {
    private static final long serialVersionUID = 1L;

    public AfterSecretLeaseRenewedEvent(RequestedSecret requestedSecret, Lease lease) {
        super(requestedSecret, lease);
    }
}

