/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.vault.core;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.vault.core.VaultKeyValueMetadataOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.support.Versioned;

public interface VaultVersionedKeyValueOperations
extends VaultKeyValueOperationsSupport {
    @Override
    @Nullable
    default public Versioned<Map<String, Object>> get(String path) {
        return this.get(path, Versioned.Version.unversioned());
    }

    @Nullable
    public <T> Versioned<T> get(String var1, Versioned.Version var2);

    @Nullable
    default public <T> Versioned<T> get(String path, Class<T> responseType) {
        return this.get(path, Versioned.Version.unversioned(), responseType);
    }

    @Nullable
    public <T> Versioned<T> get(String var1, Versioned.Version var2, Class<T> var3);

    public Versioned.Metadata put(String var1, Object var2);

    public void delete(String var1, Versioned.Version ... var2);

    public void undelete(String var1, Versioned.Version ... var2);

    public void destroy(String var1, Versioned.Version ... var2);

    public VaultKeyValueMetadataOperations opsForKeyValueMetadata();
}

