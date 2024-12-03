/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.util.function.Supplier;

@FunctionalInterface
public interface CredentialSupplier
extends Supplier<String> {
    @Override
    public String get();

    default public CredentialSupplier cached() {
        String credential = this.get();
        return () -> credential;
    }
}

