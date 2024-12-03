/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.mapping.model;

import org.springframework.data.domain.Persistable;
import org.springframework.data.mapping.TargetAwareIdentifierAccessor;
import org.springframework.lang.Nullable;

class PersistableIdentifierAccessor
extends TargetAwareIdentifierAccessor {
    private final Persistable<?> target;

    public PersistableIdentifierAccessor(Persistable<?> target) {
        super(target);
        this.target = target;
    }

    @Override
    @Nullable
    public Object getIdentifier() {
        return this.target.getId();
    }
}

