/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Comparator;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.BinaryStream;
import org.hibernate.engine.jdbc.BlobImplementer;
import org.hibernate.engine.jdbc.BlobProxy;
import org.hibernate.engine.jdbc.WrappedBlob;
import org.hibernate.engine.jdbc.internal.BinaryStreamImpl;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.DataHelper;
import org.hibernate.type.descriptor.java.IncomparableComparator;
import org.hibernate.type.descriptor.java.MutabilityPlan;
import org.hibernate.type.descriptor.java.PrimitiveByteArrayTypeDescriptor;

public class BlobTypeDescriptor
extends AbstractTypeDescriptor<Blob> {
    public static final BlobTypeDescriptor INSTANCE = new BlobTypeDescriptor();

    public BlobTypeDescriptor() {
        super(Blob.class, BlobMutabilityPlan.INSTANCE);
    }

    @Override
    public String extractLoggableRepresentation(Blob value) {
        return value == null ? "null" : "{blob}";
    }

    @Override
    public String toString(Blob value) {
        byte[] bytes;
        try {
            bytes = DataHelper.extractBytes(value.getBinaryStream());
        }
        catch (SQLException e) {
            throw new HibernateException("Unable to access blob stream", e);
        }
        return PrimitiveByteArrayTypeDescriptor.INSTANCE.toString(bytes);
    }

    @Override
    public Blob fromString(String string) {
        return BlobProxy.generateProxy(PrimitiveByteArrayTypeDescriptor.INSTANCE.fromString(string));
    }

    @Override
    public Comparator<Blob> getComparator() {
        return IncomparableComparator.INSTANCE;
    }

    @Override
    public int extractHashCode(Blob value) {
        return System.identityHashCode(value);
    }

    @Override
    public boolean areEqual(Blob one, Blob another) {
        return one == another;
    }

    @Override
    public <X> X unwrap(Blob value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        try {
            if (BinaryStream.class.isAssignableFrom(type)) {
                if (BlobImplementer.class.isInstance(value)) {
                    return (X)((BlobImplementer)((Object)value)).getUnderlyingStream();
                }
                return (X)new BinaryStreamImpl(DataHelper.extractBytes(value.getBinaryStream()));
            }
            if (byte[].class.isAssignableFrom(type)) {
                if (BlobImplementer.class.isInstance(value)) {
                    return (X)((BlobImplementer)((Object)value)).getUnderlyingStream().getBytes();
                }
                return (X)DataHelper.extractBytes(value.getBinaryStream());
            }
            if (Blob.class.isAssignableFrom(type)) {
                Blob blob = WrappedBlob.class.isInstance(value) ? ((WrappedBlob)((Object)value)).getWrappedBlob() : value;
                return (X)blob;
            }
        }
        catch (SQLException e) {
            throw new HibernateException("Unable to access blob stream", e);
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Blob wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Blob.class.isAssignableFrom(value.getClass())) {
            return options.getLobCreator().wrap((Blob)value);
        }
        if (byte[].class.isAssignableFrom(value.getClass())) {
            return options.getLobCreator().createBlob((byte[])value);
        }
        if (InputStream.class.isAssignableFrom(value.getClass())) {
            InputStream inputStream = (InputStream)value;
            try {
                return options.getLobCreator().createBlob(inputStream, inputStream.available());
            }
            catch (IOException e) {
                throw this.unknownWrap(value.getClass());
            }
        }
        throw this.unknownWrap(value.getClass());
    }

    public static class BlobMutabilityPlan
    implements MutabilityPlan<Blob> {
        public static final BlobMutabilityPlan INSTANCE = new BlobMutabilityPlan();

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public Blob deepCopy(Blob value) {
            return value;
        }

        @Override
        public Serializable disassemble(Blob value) {
            throw new UnsupportedOperationException("Blobs are not cacheable");
        }

        @Override
        public Blob assemble(Serializable cached) {
            throw new UnsupportedOperationException("Blobs are not cacheable");
        }
    }
}

