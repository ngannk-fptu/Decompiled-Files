/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Objects;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.el.Util;

public class ArrayELResolver
extends ELResolver {
    private final boolean readOnly;

    public ArrayELResolver() {
        this.readOnly = false;
    }

    public ArrayELResolver(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null && base.getClass().isArray()) {
            context.setPropertyResolved(base, property);
            try {
                int idx = ArrayELResolver.coerce(property);
                ArrayELResolver.checkBounds(base, idx);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
            return base.getClass().getComponentType();
        }
        return null;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null && base.getClass().isArray()) {
            context.setPropertyResolved(base, property);
            int idx = ArrayELResolver.coerce(property);
            if (idx < 0 || idx >= Array.getLength(base)) {
                return null;
            }
            return Array.get(base, idx);
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base != null && base.getClass().isArray()) {
            context.setPropertyResolved(base, property);
            if (this.readOnly) {
                throw new PropertyNotWritableException(Util.message(context, "resolverNotWritable", base.getClass().getName()));
            }
            int idx = ArrayELResolver.coerce(property);
            ArrayELResolver.checkBounds(base, idx);
            if (value != null && !Util.isAssignableFrom(value.getClass(), base.getClass().getComponentType())) {
                throw new ClassCastException(Util.message(context, "objectNotAssignable", value.getClass().getName(), base.getClass().getComponentType().getName()));
            }
            Array.set(base, idx, value);
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null && base.getClass().isArray()) {
            context.setPropertyResolved(base, property);
            try {
                int idx = ArrayELResolver.coerce(property);
                ArrayELResolver.checkBounds(base, idx);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return this.readOnly;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base != null && base.getClass().isArray()) {
            return Integer.class;
        }
        return null;
    }

    private static void checkBounds(Object base, int idx) {
        if (idx < 0 || idx >= Array.getLength(base)) {
            throw new PropertyNotFoundException(new ArrayIndexOutOfBoundsException(idx).getMessage());
        }
    }

    private static int coerce(Object property) {
        if (property instanceof Number) {
            return ((Number)property).intValue();
        }
        if (property instanceof Character) {
            return ((Character)property).charValue();
        }
        if (property instanceof Boolean) {
            return (Boolean)property != false ? 1 : 0;
        }
        if (property instanceof String) {
            return Integer.parseInt((String)property);
        }
        throw new IllegalArgumentException(property != null ? property.toString() : "null");
    }
}

