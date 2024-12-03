/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.repository.query;

import java.util.Iterator;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

public interface ParameterAccessor
extends Iterable<Object> {
    public Pageable getPageable();

    public Sort getSort();

    @Deprecated
    public Optional<Class<?>> getDynamicProjection();

    @Nullable
    public Class<?> findDynamicProjection();

    public Object getBindableValue(int var1);

    public boolean hasBindableNullValue();

    @Override
    public Iterator<Object> iterator();
}

