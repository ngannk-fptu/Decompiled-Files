/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;

public class VaultTokenResponse
extends VaultResponse {
    public VaultToken getToken() {
        return VaultToken.of((String)this.getRequiredAuth().get("client_token"));
    }
}

