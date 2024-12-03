/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.util.List;
import org.springframework.vault.support.VaultToken;

public interface VaultInitializationResponse {
    public List<String> getKeys();

    public VaultToken getRootToken();
}

