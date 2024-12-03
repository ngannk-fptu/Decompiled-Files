/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans;

import java.lang.reflect.Field;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

public interface TypeConverter {
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object var1, @Nullable Class<T> var2) throws TypeMismatchException;

    @Nullable
    public <T> T convertIfNecessary(@Nullable Object var1, @Nullable Class<T> var2, @Nullable MethodParameter var3) throws TypeMismatchException;

    @Nullable
    public <T> T convertIfNecessary(@Nullable Object var1, @Nullable Class<T> var2, @Nullable Field var3) throws TypeMismatchException;

    @Nullable
    default public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType, @Nullable TypeDescriptor typeDescriptor) throws TypeMismatchException {
        throw new UnsupportedOperationException("TypeDescriptor resolution not supported");
    }
}

