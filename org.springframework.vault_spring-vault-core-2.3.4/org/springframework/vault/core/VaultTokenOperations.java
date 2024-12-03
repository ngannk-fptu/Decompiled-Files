/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import org.springframework.vault.VaultException;
import org.springframework.vault.support.VaultToken;
import org.springframework.vault.support.VaultTokenRequest;
import org.springframework.vault.support.VaultTokenResponse;

public interface VaultTokenOperations {
    public VaultTokenResponse create() throws VaultException;

    public VaultTokenResponse create(VaultTokenRequest var1) throws VaultException;

    public VaultTokenResponse createOrphan();

    public VaultTokenResponse createOrphan(VaultTokenRequest var1);

    public VaultTokenResponse renew(VaultToken var1);

    public void revoke(VaultToken var1);

    public void revokeOrphan(VaultToken var1);
}

