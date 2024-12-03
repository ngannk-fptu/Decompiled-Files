/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery;

public class DiscoveryException
extends RuntimeException {
    private static final long serialVersionUID = -2518293836976054070L;

    public DiscoveryException() {
    }

    public DiscoveryException(String message) {
        super(message);
    }

    public DiscoveryException(Throwable cause) {
        super(cause);
    }

    public DiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }
}

