/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.beans;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.AbstractNestablePropertyAccessor;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyMatches;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

public class DirectFieldAccessor
extends AbstractNestablePropertyAccessor {
    private final Map<String, FieldPropertyHandler> fieldMap = new HashMap<String, FieldPropertyHandler>();

    public DirectFieldAccessor(Object object) {
        super(object);
    }

    protected DirectFieldAccessor(Object object, String nestedPath, DirectFieldAccessor parent) {
        super(object, nestedPath, parent);
    }

    @Override
    @Nullable
    protected FieldPropertyHandler getLocalPropertyHandler(String propertyName) {
        Field field;
        FieldPropertyHandler propertyHandler = this.fieldMap.get(propertyName);
        if (propertyHandler == null && (field = ReflectionUtils.findField(this.getWrappedClass(), (String)propertyName)) != null) {
            propertyHandler = new FieldPropertyHandler(field);
            this.fieldMap.put(propertyName, propertyHandler);
        }
        return propertyHandler;
    }

    @Override
    protected DirectFieldAccessor newNestedPropertyAccessor(Object object, String nestedPath) {
        return new DirectFieldAccessor(object, nestedPath, this);
    }

    @Override
    protected NotWritablePropertyException createNotWritablePropertyException(String propertyName) {
        PropertyMatches matches = PropertyMatches.forField(propertyName, this.getRootClass());
        throw new NotWritablePropertyException(this.getRootClass(), this.getNestedPath() + propertyName, matches.buildErrorMessage(), matches.getPossibleMatches());
    }

    private class FieldPropertyHandler
    extends AbstractNestablePropertyAccessor.PropertyHandler {
        private final Field field;

        public FieldPropertyHandler(Field field) {
            super(field.getType(), true, true);
            this.field = field;
        }

        @Override
        public TypeDescriptor toTypeDescriptor() {
            return new TypeDescriptor(this.field);
        }

        @Override
        public ResolvableType getResolvableType() {
            return ResolvableType.forField((Field)this.field);
        }

        @Override
        @Nullable
        public TypeDescriptor nested(int level) {
            return TypeDescriptor.nested((Field)this.field, (int)level);
        }

        @Override
        @Nullable
        public Object getValue() throws Exception {
            try {
                ReflectionUtils.makeAccessible((Field)this.field);
                return this.field.get(DirectFieldAccessor.this.getWrappedInstance());
            }
            catch (IllegalAccessException ex) {
                throw new InvalidPropertyException(DirectFieldAccessor.this.getWrappedClass(), this.field.getName(), "Field is not accessible", ex);
            }
        }

        @Override
        public void setValue(@Nullable Object value) throws Exception {
            try {
                ReflectionUtils.makeAccessible((Field)this.field);
                this.field.set(DirectFieldAccessor.this.getWrappedInstance(), value);
            }
            catch (IllegalAccessException ex) {
                throw new InvalidPropertyException(DirectFieldAccessor.this.getWrappedClass(), this.field.getName(), "Field is not accessible", ex);
            }
        }
    }
}

