/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.keyvalue.core.mapping.KeyValuePersistentEntity
 */
package org.springframework.vault.repository.mapping;

import org.springframework.data.keyvalue.core.mapping.KeyValuePersistentEntity;
import org.springframework.vault.repository.mapping.VaultPersistentProperty;

public interface VaultPersistentEntity<T>
extends KeyValuePersistentEntity<T, VaultPersistentProperty> {
    public String getSecretBackend();
}

