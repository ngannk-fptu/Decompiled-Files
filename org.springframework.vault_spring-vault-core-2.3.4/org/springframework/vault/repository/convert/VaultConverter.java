/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.convert.EntityConverter
 */
package org.springframework.vault.repository.convert;

import org.springframework.data.convert.EntityConverter;
import org.springframework.vault.repository.convert.SecretDocument;
import org.springframework.vault.repository.mapping.VaultPersistentEntity;
import org.springframework.vault.repository.mapping.VaultPersistentProperty;

public interface VaultConverter
extends EntityConverter<VaultPersistentEntity<?>, VaultPersistentProperty, Object, SecretDocument> {
}

