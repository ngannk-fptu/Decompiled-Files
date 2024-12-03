/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.mapping.model.SimpleTypeHolder
 */
package org.springframework.vault.repository.mapping;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.vault.repository.convert.SecretDocument;

public abstract class VaultSimpleTypes {
    private static final Set<Class<?>> VAULT_SIMPLE_TYPES;
    public static final SimpleTypeHolder HOLDER;

    private VaultSimpleTypes() {
    }

    static {
        HashSet<Class<SecretDocument>> simpleTypes = new HashSet<Class<SecretDocument>>();
        simpleTypes.add(SecretDocument.class);
        VAULT_SIMPLE_TYPES = Collections.unmodifiableSet(simpleTypes);
        HOLDER = new SimpleTypeHolder(VAULT_SIMPLE_TYPES, true);
    }
}

