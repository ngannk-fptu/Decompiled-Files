/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import org.springframework.vault.VaultException;
import org.springframework.vault.support.VaultToken;

@FunctionalInterface
public interface ClientAuthentication {
    public VaultToken login() throws VaultException;
}

