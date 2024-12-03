/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.spi;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.property.access.internal.AbstractFieldSerialForm;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccessException;

public class GetterFieldImpl
implements Getter {
    private final Class containerClass;
    private final String propertyName;
    private final Field field;
    private final Method getterMethod;

    public GetterFieldImpl(Class containerClass, String propertyName, Field field) {
        this.containerClass = containerClass;
        this.propertyName = propertyName;
        this.field = field;
        this.getterMethod = ReflectHelper.findGetterMethodForFieldAccess(field, propertyName);
    }

    @Override
    public Object get(Object owner) {
        try {
            Class<?> type = this.field.getType();
            if (type.isPrimitive()) {
                if (type == Boolean.TYPE) {
                    return this.field.getBoolean(owner);
                }
                if (type == Byte.TYPE) {
                    return this.field.getByte(owner);
                }
                if (type == Character.TYPE) {
                    return Character.valueOf(this.field.getChar(owner));
                }
                if (type == Integer.TYPE) {
                    return this.field.getInt(owner);
                }
                if (type == Long.TYPE) {
                    return this.field.getLong(owner);
                }
                if (type == Short.TYPE) {
                    return this.field.getShort(owner);
                }
            }
            return this.field.get(owner);
        }
        catch (Exception e) {
            throw new PropertyAccessException(String.format(Locale.ROOT, "Error accessing field [%s] by reflection for persistent property [%s#%s] : %s", this.field.toGenericString(), this.containerClass.getName(), this.propertyName, owner), e);
        }
    }

    @Override
    public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
        return this.get(owner);
    }

    @Override
    public Class getReturnType() {
        return this.field.getType();
    }

    @Override
    public Member getMember() {
        return this.field;
    }

    @Override
    public String getMethodName() {
        return this.getterMethod != null ? this.getterMethod.getName() : null;
    }

    @Override
    public Method getMethod() {
        return this.getterMethod;
    }

    private Object writeReplace() throws ObjectStreamException {
        return new SerialForm(this.containerClass, this.propertyName, this.field);
    }

    private static class SerialForm
    extends AbstractFieldSerialForm
    implements Serializable {
        private final Class containerClass;
        private final String propertyName;

        private SerialForm(Class containerClass, String propertyName, Field field) {
            super(field);
            this.containerClass = containerClass;
            this.propertyName = propertyName;
        }

        private Object readResolve() {
            return new GetterFieldImpl(this.containerClass, this.propertyName, this.resolveField());
        }
    }
}

