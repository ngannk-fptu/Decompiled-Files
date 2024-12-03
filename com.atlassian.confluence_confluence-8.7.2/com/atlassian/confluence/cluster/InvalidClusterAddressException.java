/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterException;
import java.net.InetAddress;

public class InvalidClusterAddressException
extends ClusterException {
    private final InetAddress address;

    public InvalidClusterAddressException(String message, Throwable cause) {
        super(message, cause);
        this.address = null;
    }

    public InvalidClusterAddressException(String message, InetAddress address) {
        super(message);
        this.address = address;
    }

    public InvalidClusterAddressException(String message, InetAddress address, Throwable cause) {
        super(message, cause);
        this.address = address;
    }

    public InetAddress getAddress() {
        return this.address;
    }
}

