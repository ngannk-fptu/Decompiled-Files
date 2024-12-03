/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.lease.event;

import org.springframework.vault.core.lease.event.SecretLeaseEvent;

@FunctionalInterface
public interface LeaseListener {
    public void onLeaseEvent(SecretLeaseEvent var1);
}

