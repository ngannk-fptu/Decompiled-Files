/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class EnumJavaTypeDescriptor<T extends Enum>
extends AbstractTypeDescriptor<T> {
    public EnumJavaTypeDescriptor(Class<T> type) {
        super(type, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(T value) {
        return value == null ? "<null>" : ((Enum)value).name();
    }

    @Override
    public T fromString(String string) {
        return string == null ? null : (T)Enum.valueOf(this.getJavaType(), string);
    }

    @Override
    public <X> X unwrap(T value, Class<X> type, WrapperOptions options) {
        if (String.class.equals(type)) {
            return (X)this.toName(value);
        }
        if (Integer.class.isInstance(type)) {
            return (X)this.toOrdinal((Enum)value);
        }
        return (X)value;
    }

    @Override
    public <X> T wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isInstance(value)) {
            return this.fromName((String)value);
        }
        if (Integer.class.isInstance(value)) {
            return (T)this.fromOrdinal((Integer)value);
        }
        return (T)((Enum)value);
    }

    public <E extends Enum> Integer toOrdinal(E domainForm) {
        if (domainForm == null) {
            return null;
        }
        return domainForm.ordinal();
    }

    public <E extends Enum> E fromOrdinal(Integer relationalForm) {
        if (relationalForm == null) {
            return null;
        }
        return (E)((Enum[])this.getJavaType().getEnumConstants())[relationalForm];
    }

    public T fromName(String relationalForm) {
        if (relationalForm == null) {
            return null;
        }
        return Enum.valueOf(this.getJavaType(), relationalForm.trim());
    }

    public String toName(T domainForm) {
        if (domainForm == null) {
            return null;
        }
        return ((Enum)domainForm).name();
    }
}

