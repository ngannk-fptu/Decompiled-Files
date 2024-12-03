/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.lease.event;

import org.springframework.vault.core.lease.event.LeaseErrorListener;
import org.springframework.vault.core.lease.event.LeaseListener;
import org.springframework.vault.core.lease.event.SecretLeaseEvent;

public abstract class LeaseListenerAdapter
implements LeaseListener,
LeaseErrorListener {
    @Override
    public void onLeaseEvent(SecretLeaseEvent leaseEvent) {
    }

    @Override
    public void onLeaseError(SecretLeaseEvent leaseEvent, Exception exception) {
    }
}

