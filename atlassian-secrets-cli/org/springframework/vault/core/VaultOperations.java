/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.RestOperationsCallback;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultPkiOperations;
import org.springframework.vault.core.VaultSysOperations;
import org.springframework.vault.core.VaultTokenOperations;
import org.springframework.vault.core.VaultTransformOperations;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.core.VaultWrappingOperations;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.web.client.RestClientException;

public interface VaultOperations {
    public VaultKeyValueOperations opsForKeyValue(String var1, VaultKeyValueOperationsSupport.KeyValueBackend var2);

    public VaultVersionedKeyValueOperations opsForVersionedKeyValue(String var1);

    public VaultPkiOperations opsForPki();

    public VaultPkiOperations opsForPki(String var1);

    public VaultSysOperations opsForSys();

    public VaultTokenOperations opsForToken();

    public VaultTransformOperations opsForTransform();

    public VaultTransformOperations opsForTransform(String var1);

    public VaultTransitOperations opsForTransit();

    public VaultTransitOperations opsForTransit(String var1);

    public VaultWrappingOperations opsForWrapping();

    @Nullable
    public VaultResponse read(String var1);

    @Nullable
    public <T> VaultResponseSupport<T> read(String var1, Class<T> var2);

    @Nullable
    public List<String> list(String var1);

    @Nullable
    default public VaultResponse write(String path) {
        return this.write(path, null);
    }

    @Nullable
    public VaultResponse write(String var1, @Nullable Object var2);

    public void delete(String var1);

    @Nullable
    public <T> T doWithVault(RestOperationsCallback<T> var1) throws VaultException, RestClientException;

    @Nullable
    public <T> T doWithSession(RestOperationsCallback<T> var1) throws VaultException, RestClientException;
}

