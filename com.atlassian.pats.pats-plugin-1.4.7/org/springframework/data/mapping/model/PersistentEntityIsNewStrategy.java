/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.mapping.model;

import java.util.function.Function;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.support.IsNewStrategy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

class PersistentEntityIsNewStrategy
implements IsNewStrategy {
    private final Function<Object, Object> valueLookup;
    @Nullable
    private final Class<?> valueType;

    private PersistentEntityIsNewStrategy(PersistentEntity<?, ?> entity, boolean idOnly) {
        Assert.notNull(entity, (String)"PersistentEntity must not be null!");
        Function<Object, Object> function = this.valueLookup = entity.hasVersionProperty() && !idOnly ? source -> entity.getPropertyAccessor(source).getProperty((PersistentProperty<?>)entity.getRequiredVersionProperty()) : source -> entity.getIdentifierAccessor(source).getIdentifier();
        this.valueType = entity.hasVersionProperty() && !idOnly ? entity.getRequiredVersionProperty().getType() : (entity.hasIdProperty() ? entity.getRequiredIdProperty().getType() : null);
        Class<?> type = this.valueType;
        if (type != null && type.isPrimitive() && !ClassUtils.isAssignable(Number.class, type)) {
            throw new IllegalArgumentException(String.format("Only numeric primitives are supported as identifier / version field types! Got: %s.", this.valueType));
        }
    }

    public static PersistentEntityIsNewStrategy forIdOnly(PersistentEntity<?, ?> entity) {
        return new PersistentEntityIsNewStrategy(entity, true);
    }

    public static PersistentEntityIsNewStrategy of(PersistentEntity<?, ?> entity) {
        return new PersistentEntityIsNewStrategy(entity, false);
    }

    @Override
    public boolean isNew(Object entity) {
        Object value = this.valueLookup.apply(entity);
        if (value == null) {
            return true;
        }
        if (this.valueType != null && !this.valueType.isPrimitive()) {
            return false;
        }
        if (value instanceof Number) {
            return ((Number)value).longValue() == 0L;
        }
        throw new IllegalArgumentException(String.format("Could not determine whether %s is new! Unsupported identifier or version property!", entity));
    }
}

