/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping.model;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.mapping.model.SimplePersistentPropertyPathAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ConvertingPropertyAccessor<T>
extends SimplePersistentPropertyPathAccessor<T> {
    private final PersistentPropertyAccessor<T> accessor;
    private final ConversionService conversionService;

    public ConvertingPropertyAccessor(PersistentPropertyAccessor<T> accessor, ConversionService conversionService) {
        super(accessor);
        Assert.notNull(accessor, (String)"PersistentPropertyAccessor must not be null!");
        Assert.notNull((Object)conversionService, (String)"ConversionService must not be null!");
        this.accessor = accessor;
        this.conversionService = conversionService;
    }

    @Override
    public void setProperty(PersistentProperty<?> property, @Nullable Object value) {
        this.accessor.setProperty(property, this.convertIfNecessary(value, property.getType()));
    }

    @Override
    public void setProperty(PersistentPropertyPath<? extends PersistentProperty<?>> path, @Nullable Object value) {
        Object converted = this.convertIfNecessary(value, path.getRequiredLeafProperty().getType());
        super.setProperty(path, converted);
    }

    @Nullable
    public <S> S getProperty(PersistentProperty<?> property, Class<S> targetType) {
        Assert.notNull(property, (String)"PersistentProperty must not be null!");
        Assert.notNull(targetType, (String)"Target type must not be null!");
        return this.convertIfNecessary(this.getProperty((PersistentProperty)property), targetType);
    }

    @Override
    @Nullable
    protected <S> S getTypedProperty(PersistentProperty<?> property, Class<S> type) {
        return this.convertIfNecessary(super.getTypedProperty(property, type), type);
    }

    @Nullable
    private <S> S convertIfNecessary(@Nullable Object source, Class<S> type) {
        return (S)(source == null ? null : (type.isAssignableFrom(source.getClass()) ? source : this.conversionService.convert(source, type)));
    }
}

