/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.TargetAwareIdentifierAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class IdPropertyIdentifierAccessor
extends TargetAwareIdentifierAccessor {
    private final PersistentPropertyAccessor<?> accessor;
    private final PersistentProperty<?> idProperty;

    public IdPropertyIdentifierAccessor(PersistentEntity<?, ?> entity, Object target) {
        super(target);
        Assert.notNull(entity, (String)"PersistentEntity must not be null!");
        Assert.isTrue((boolean)entity.hasIdProperty(), (String)"PersistentEntity must have an identifier property!");
        Assert.notNull((Object)target, (String)"Target bean must not be null!");
        this.idProperty = entity.getRequiredIdProperty();
        this.accessor = entity.getPropertyAccessor(target);
    }

    @Override
    @Nullable
    public Object getIdentifier() {
        return this.accessor.getProperty(this.idProperty);
    }
}

