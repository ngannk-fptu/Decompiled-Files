/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.lease.event;

import org.springframework.vault.core.lease.event.SecretLeaseEvent;

@FunctionalInterface
public interface LeaseErrorListener {
    public void onLeaseError(SecretLeaseEvent var1, Exception var2);
}

