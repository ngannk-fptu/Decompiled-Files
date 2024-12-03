/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import org.springframework.vault.authentication.VaultSessionManagerException;

public class VaultTokenRenewalException
extends VaultSessionManagerException {
    public VaultTokenRenewalException(String msg) {
        super(msg);
    }

    public VaultTokenRenewalException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

