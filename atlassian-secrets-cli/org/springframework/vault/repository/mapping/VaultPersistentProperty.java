/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.keyvalue.core.mapping.KeyValuePersistentProperty
 *  org.springframework.data.mapping.PersistentEntity
 *  org.springframework.data.mapping.model.Property
 *  org.springframework.data.mapping.model.SimpleTypeHolder
 */
package org.springframework.vault.repository.mapping;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.keyvalue.core.mapping.KeyValuePersistentProperty;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;

public class VaultPersistentProperty
extends KeyValuePersistentProperty<VaultPersistentProperty> {
    private static final Set<String> SUPPORTED_ID_PROPERTY_NAMES = new HashSet<String>();

    public VaultPersistentProperty(Property property, PersistentEntity<?, VaultPersistentProperty> owner, SimpleTypeHolder simpleTypeHolder) {
        super(property, owner, simpleTypeHolder);
    }

    public boolean isIdProperty() {
        return super.isIdProperty() || SUPPORTED_ID_PROPERTY_NAMES.contains(this.getName());
    }

    static {
        SUPPORTED_ID_PROPERTY_NAMES.add("id");
    }
}

