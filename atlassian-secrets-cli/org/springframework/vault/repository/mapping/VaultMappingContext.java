/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.keyvalue.core.mapping.KeySpaceResolver
 *  org.springframework.data.keyvalue.core.mapping.context.KeyValueMappingContext
 *  org.springframework.data.mapping.PersistentEntity
 *  org.springframework.data.mapping.model.Property
 *  org.springframework.data.mapping.model.SimpleTypeHolder
 *  org.springframework.data.util.TypeInformation
 */
package org.springframework.vault.repository.mapping;

import org.springframework.data.keyvalue.core.mapping.KeySpaceResolver;
import org.springframework.data.keyvalue.core.mapping.context.KeyValueMappingContext;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.vault.repository.mapping.BasicVaultPersistentEntity;
import org.springframework.vault.repository.mapping.VaultPersistentEntity;
import org.springframework.vault.repository.mapping.VaultPersistentProperty;

public class VaultMappingContext
extends KeyValueMappingContext<VaultPersistentEntity<?>, VaultPersistentProperty> {
    private KeySpaceResolver fallbackKeySpaceResolver = SimpleClassNameKeySpaceResolver.INSTANCE;

    public KeySpaceResolver getFallbackKeySpaceResolver() {
        return this.fallbackKeySpaceResolver;
    }

    public void setFallbackKeySpaceResolver(KeySpaceResolver fallbackKeySpaceResolver) {
        this.fallbackKeySpaceResolver = fallbackKeySpaceResolver;
    }

    protected <T> VaultPersistentEntity<?> createPersistentEntity(TypeInformation<T> typeInformation) {
        return new BasicVaultPersistentEntity<T>(typeInformation, this.fallbackKeySpaceResolver);
    }

    protected VaultPersistentProperty createPersistentProperty(Property property, VaultPersistentEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
        return new VaultPersistentProperty(property, (PersistentEntity<?, VaultPersistentProperty>)owner, simpleTypeHolder);
    }

    static enum SimpleClassNameKeySpaceResolver implements KeySpaceResolver
    {
        INSTANCE;


        public String resolveKeySpace(Class<?> type) {
            Assert.notNull(type, "Type must not be null");
            return StringUtils.uncapitalize(ClassUtils.getUserClass(type).getSimpleName());
        }
    }
}

