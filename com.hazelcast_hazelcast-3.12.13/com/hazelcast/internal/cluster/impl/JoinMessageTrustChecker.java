/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.internal.cluster.impl.JoinMessage;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.AddressUtil;
import java.util.Set;

final class JoinMessageTrustChecker {
    private final Set<String> trustedInterfaces;
    private final ILogger logger;

    JoinMessageTrustChecker(Set<String> trustedInterfaces, ILogger logger) {
        this.trustedInterfaces = trustedInterfaces;
        this.logger = logger;
    }

    boolean isTrusted(JoinMessage joinMessage) {
        if (this.trustedInterfaces.isEmpty()) {
            return true;
        }
        String host = joinMessage.getAddress().getHost();
        if (AddressUtil.matchAnyInterface(host, this.trustedInterfaces)) {
            return true;
        }
        if (this.logger.isFineEnabled()) {
            this.logger.fine(String.format("JoinMessage from %s is dropped because its sender is not a trusted interface", host));
        }
        return false;
    }
}

