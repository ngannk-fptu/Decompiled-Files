/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.vault.core;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;

public interface VaultKeyValueOperations
extends VaultKeyValueOperationsSupport {
    @Override
    @Nullable
    public VaultResponse get(String var1);

    @Nullable
    public <T> VaultResponseSupport<T> get(String var1, Class<T> var2);

    public boolean patch(String var1, Map<String, ?> var2);

    public void put(String var1, Object var2);
}

