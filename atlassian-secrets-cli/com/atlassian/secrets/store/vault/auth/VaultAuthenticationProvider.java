/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.vault.auth;

import com.atlassian.secrets.store.vault.VaultParams;
import org.springframework.vault.authentication.ClientAuthentication;

public interface VaultAuthenticationProvider {
    public ClientAuthentication getAuthentication(VaultParams var1);
}

