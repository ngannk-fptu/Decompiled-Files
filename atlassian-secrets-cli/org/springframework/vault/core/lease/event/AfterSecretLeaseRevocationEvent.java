/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.lease.event;

import org.springframework.vault.core.lease.domain.Lease;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseEvent;

public class AfterSecretLeaseRevocationEvent
extends SecretLeaseEvent {
    private static final long serialVersionUID = 1L;

    public AfterSecretLeaseRevocationEvent(RequestedSecret requestedSecret, Lease lease) {
        super(requestedSecret, lease);
    }
}

