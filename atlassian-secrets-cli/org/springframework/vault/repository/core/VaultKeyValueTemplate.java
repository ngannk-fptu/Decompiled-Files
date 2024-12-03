/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.keyvalue.core.KeyValueAdapter
 *  org.springframework.data.keyvalue.core.KeyValueTemplate
 *  org.springframework.data.mapping.context.MappingContext
 */
package org.springframework.vault.repository.core;

import org.springframework.data.keyvalue.core.KeyValueAdapter;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.vault.repository.mapping.VaultMappingContext;

public class VaultKeyValueTemplate
extends KeyValueTemplate {
    public VaultKeyValueTemplate(KeyValueAdapter adapter) {
        this(adapter, new VaultMappingContext());
    }

    public VaultKeyValueTemplate(KeyValueAdapter adapter, VaultMappingContext mappingContext) {
        super(adapter, (MappingContext)mappingContext);
    }

    public void destroy() throws Exception {
    }
}

