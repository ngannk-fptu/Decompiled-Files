/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.convert.ConversionException
 *  org.springframework.core.convert.ConverterNotFoundException
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.beans;

import java.lang.reflect.Field;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeConverterDelegate;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class TypeConverterSupport
extends PropertyEditorRegistrySupport
implements TypeConverter {
    @Nullable
    TypeConverterDelegate typeConverterDelegate;

    @Override
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType) throws TypeMismatchException {
        return this.convertIfNecessary(value, requiredType, TypeDescriptor.valueOf(requiredType));
    }

    @Override
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType, @Nullable MethodParameter methodParam) throws TypeMismatchException {
        return this.convertIfNecessary(value, requiredType, methodParam != null ? new TypeDescriptor(methodParam) : TypeDescriptor.valueOf(requiredType));
    }

    @Override
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType, @Nullable Field field) throws TypeMismatchException {
        return this.convertIfNecessary(value, requiredType, field != null ? new TypeDescriptor(field) : TypeDescriptor.valueOf(requiredType));
    }

    @Override
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType, @Nullable TypeDescriptor typeDescriptor) throws TypeMismatchException {
        Assert.state((this.typeConverterDelegate != null ? 1 : 0) != 0, (String)"No TypeConverterDelegate");
        try {
            return this.typeConverterDelegate.convertIfNecessary(null, null, value, requiredType, typeDescriptor);
        }
        catch (IllegalStateException | ConverterNotFoundException ex) {
            throw new ConversionNotSupportedException(value, requiredType, ex);
        }
        catch (IllegalArgumentException | ConversionException ex) {
            throw new TypeMismatchException(value, requiredType, ex);
        }
    }
}

