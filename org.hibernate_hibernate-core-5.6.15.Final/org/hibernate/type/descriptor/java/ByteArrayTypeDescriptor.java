/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.BinaryStream;
import org.hibernate.engine.jdbc.internal.BinaryStreamImpl;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ArrayMutabilityPlan;
import org.hibernate.type.descriptor.java.DataHelper;
import org.hibernate.type.descriptor.java.IncomparableComparator;

public class ByteArrayTypeDescriptor
extends AbstractTypeDescriptor<Byte[]> {
    public static final ByteArrayTypeDescriptor INSTANCE = new ByteArrayTypeDescriptor();

    public ByteArrayTypeDescriptor() {
        super(Byte[].class, ArrayMutabilityPlan.INSTANCE);
    }

    @Override
    public boolean areEqual(Byte[] one, Byte[] another) {
        return one == another || one != null && another != null && Arrays.equals((Object[])one, (Object[])another);
    }

    @Override
    public int extractHashCode(Byte[] bytes) {
        int hashCode = 1;
        Byte[] byteArray = bytes;
        int n = byteArray.length;
        for (int i = 0; i < n; ++i) {
            byte aByte = byteArray[i];
            hashCode = 31 * hashCode + aByte;
        }
        return hashCode;
    }

    @Override
    public String toString(Byte[] bytes) {
        StringBuilder buf = new StringBuilder();
        for (Byte aByte : bytes) {
            String hexStr = Integer.toHexString(aByte - -128);
            if (hexStr.length() == 1) {
                buf.append('0');
            }
            buf.append(hexStr);
        }
        return buf.toString();
    }

    @Override
    public Byte[] fromString(String string) {
        if (string == null) {
            return null;
        }
        if (string.length() % 2 != 0) {
            throw new IllegalArgumentException("The string is not a valid string representation of a binary content.");
        }
        Byte[] bytes = new Byte[string.length() / 2];
        for (int i = 0; i < bytes.length; ++i) {
            String hexStr = string.substring(i * 2, (i + 1) * 2);
            bytes[i] = (byte)(Integer.parseInt(hexStr, 16) + -128);
        }
        return bytes;
    }

    @Override
    public Comparator<Byte[]> getComparator() {
        return IncomparableComparator.INSTANCE;
    }

    @Override
    public <X> X unwrap(Byte[] value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Byte[].class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (byte[].class.isAssignableFrom(type)) {
            return (X)this.unwrapBytes(value);
        }
        if (InputStream.class.isAssignableFrom(type)) {
            return (X)new ByteArrayInputStream(this.unwrapBytes(value));
        }
        if (BinaryStream.class.isAssignableFrom(type)) {
            return (X)new BinaryStreamImpl(this.unwrapBytes(value));
        }
        if (Blob.class.isAssignableFrom(type)) {
            return (X)options.getLobCreator().createBlob(this.unwrapBytes(value));
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Byte[] wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Byte[].class.isInstance(value)) {
            return (Byte[])value;
        }
        if (byte[].class.isInstance(value)) {
            return this.wrapBytes((byte[])value);
        }
        if (InputStream.class.isInstance(value)) {
            return this.wrapBytes(DataHelper.extractBytes((InputStream)value));
        }
        if (Blob.class.isInstance(value) || DataHelper.isNClob(value.getClass())) {
            try {
                return this.wrapBytes(DataHelper.extractBytes(((Blob)value).getBinaryStream()));
            }
            catch (SQLException e) {
                throw new HibernateException("Unable to access lob stream", e);
            }
        }
        throw this.unknownWrap(value.getClass());
    }

    private Byte[] wrapBytes(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        Byte[] result = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; ++i) {
            result[i] = bytes[i];
        }
        return result;
    }

    private byte[] unwrapBytes(Byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; ++i) {
            result[i] = bytes[i];
        }
        return result;
    }
}

