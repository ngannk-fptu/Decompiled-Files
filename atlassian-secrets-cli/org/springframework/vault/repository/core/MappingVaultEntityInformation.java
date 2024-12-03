/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.mapping.MappingException
 *  org.springframework.data.repository.core.support.PersistentEntityInformation
 */
package org.springframework.vault.repository.core;

import org.springframework.data.mapping.MappingException;
import org.springframework.data.repository.core.support.PersistentEntityInformation;
import org.springframework.vault.repository.core.VaultEntityInformation;
import org.springframework.vault.repository.mapping.VaultPersistentEntity;

public class MappingVaultEntityInformation<T, ID>
extends PersistentEntityInformation<T, ID>
implements VaultEntityInformation<T, ID> {
    public MappingVaultEntityInformation(VaultPersistentEntity<T> entity) {
        super(entity);
        if (!entity.hasIdProperty()) {
            throw new MappingException(String.format("Entity %s requires to have an explicit id field. Did you forget to provide one using @Id?", entity.getName()));
        }
    }
}

