/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;
import org.hibernate.HibernateException;
import org.hibernate.annotations.Immutable;
import org.hibernate.engine.jdbc.BinaryStream;
import org.hibernate.engine.jdbc.internal.BinaryStreamImpl;
import org.hibernate.internal.util.SerializationHelper;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.DataHelper;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.java.MutabilityPlan;
import org.hibernate.type.descriptor.java.MutableMutabilityPlan;
import org.hibernate.type.descriptor.java.PrimitiveByteArrayTypeDescriptor;

public class SerializableTypeDescriptor<T extends Serializable>
extends AbstractTypeDescriptor<T> {
    public SerializableTypeDescriptor(Class<T> type) {
        super(type, SerializableTypeDescriptor.createMutabilityPlan(type));
    }

    private static <T> MutabilityPlan<T> createMutabilityPlan(Class<T> type) {
        if (type.isAnnotationPresent(Immutable.class)) {
            return ImmutableMutabilityPlan.INSTANCE;
        }
        return SerializableMutabilityPlan.INSTANCE;
    }

    @Override
    public String toString(T value) {
        return PrimitiveByteArrayTypeDescriptor.INSTANCE.toString(this.toBytes(value));
    }

    @Override
    public T fromString(String string) {
        return this.fromBytes(PrimitiveByteArrayTypeDescriptor.INSTANCE.fromString(string));
    }

    @Override
    public boolean areEqual(T one, T another) {
        if (one == another) {
            return true;
        }
        if (one == null || another == null) {
            return false;
        }
        return one.equals(another) || Arrays.equals(this.toBytes(one), this.toBytes(another));
    }

    @Override
    public int extractHashCode(T value) {
        return PrimitiveByteArrayTypeDescriptor.INSTANCE.extractHashCode(this.toBytes(value));
    }

    @Override
    public <X> X unwrap(T value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return (X)value;
        }
        if (byte[].class.isAssignableFrom(type)) {
            return (X)this.toBytes(value);
        }
        if (InputStream.class.isAssignableFrom(type)) {
            return (X)new ByteArrayInputStream(this.toBytes(value));
        }
        if (BinaryStream.class.isAssignableFrom(type)) {
            return (X)new BinaryStreamImpl(this.toBytes(value));
        }
        if (Blob.class.isAssignableFrom(type)) {
            return (X)options.getLobCreator().createBlob(this.toBytes(value));
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> T wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (byte[].class.isInstance(value)) {
            return this.fromBytes((byte[])value);
        }
        if (InputStream.class.isInstance(value)) {
            return this.fromBytes(DataHelper.extractBytes((InputStream)value));
        }
        if (Blob.class.isInstance(value)) {
            try {
                return this.fromBytes(DataHelper.extractBytes(((Blob)value).getBinaryStream()));
            }
            catch (SQLException e) {
                throw new HibernateException(e);
            }
        }
        if (this.getJavaType().isInstance(value)) {
            return (T)((Serializable)value);
        }
        throw this.unknownWrap(value.getClass());
    }

    protected byte[] toBytes(T value) {
        return SerializationHelper.serialize(value);
    }

    protected T fromBytes(byte[] bytes) {
        return (T)((Serializable)SerializationHelper.deserialize(bytes, this.getJavaType().getClassLoader()));
    }

    public static class SerializableMutabilityPlan<S extends Serializable>
    extends MutableMutabilityPlan<S> {
        public static final SerializableMutabilityPlan<Serializable> INSTANCE = new SerializableMutabilityPlan();

        private SerializableMutabilityPlan() {
        }

        @Override
        public S deepCopyNotNull(S value) {
            return (S)((Serializable)SerializationHelper.clone(value));
        }
    }
}

