/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.lease.event;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.vault.core.lease.domain.Lease;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseEvent;

public class SecretLeaseCreatedEvent
extends SecretLeaseEvent {
    private static final long serialVersionUID = 1L;
    private final Map<String, Object> secrets;

    public SecretLeaseCreatedEvent(RequestedSecret requestedSecret, Lease lease, Map<String, Object> secrets) {
        super(requestedSecret, lease);
        this.secrets = Collections.unmodifiableMap(new LinkedHashMap<String, Object>(secrets));
    }

    public Map<String, Object> getSecrets() {
        return this.secrets;
    }
}

