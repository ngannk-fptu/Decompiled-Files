/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import java.io.Serializable;
import java.lang.reflect.Field;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.property.access.spi.PropertyAccessSerializationException;

public abstract class AbstractFieldSerialForm
implements Serializable {
    private final Class declaringClass;
    private final String fieldName;

    protected AbstractFieldSerialForm(Field field) {
        this(field.getDeclaringClass(), field.getName());
    }

    protected AbstractFieldSerialForm(Class declaringClass, String fieldName) {
        this.declaringClass = declaringClass;
        this.fieldName = fieldName;
    }

    protected Field resolveField() {
        try {
            Field field = this.declaringClass.getDeclaredField(this.fieldName);
            ReflectHelper.ensureAccessibility(field);
            return field;
        }
        catch (NoSuchFieldException e) {
            throw new PropertyAccessSerializationException("Unable to resolve field on deserialization : " + this.declaringClass.getName() + "#" + this.fieldName);
        }
    }
}

