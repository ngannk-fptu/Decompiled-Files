/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.spi;

public class DuplicateFactoryRegistrationException
extends RuntimeException {
    public DuplicateFactoryRegistrationException() {
    }

    public DuplicateFactoryRegistrationException(String message) {
        super(message);
    }

    public DuplicateFactoryRegistrationException(String message, Throwable t) {
        super(message, t);
    }
}

