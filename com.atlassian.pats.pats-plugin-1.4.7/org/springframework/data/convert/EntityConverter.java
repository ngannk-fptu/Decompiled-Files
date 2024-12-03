/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.ConversionService
 */
package org.springframework.data.convert;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.convert.EntityReader;
import org.springframework.data.convert.EntityWriter;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;

public interface EntityConverter<E extends PersistentEntity<?, P>, P extends PersistentProperty<P>, T, S>
extends EntityReader<T, S>,
EntityWriter<T, S> {
    public MappingContext<? extends E, P> getMappingContext();

    public ConversionService getConversionService();
}

