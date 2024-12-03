/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.SerializableTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarbinaryTypeDescriptor;

public class SerializableType<T extends Serializable>
extends AbstractSingleColumnStandardBasicType<T> {
    public static final SerializableType<Serializable> INSTANCE = new SerializableType<Serializable>(Serializable.class);
    private final Class<T> serializableClass;

    public SerializableType(Class<T> serializableClass) {
        super(VarbinaryTypeDescriptor.INSTANCE, new SerializableTypeDescriptor<T>(serializableClass));
        this.serializableClass = serializableClass;
    }

    @Override
    public String getName() {
        return this.serializableClass == Serializable.class ? "serializable" : this.serializableClass.getName();
    }
}

