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
import org.hibernate.internal.util.compare.RowVersionComparator;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ArrayMutabilityPlan;
import org.hibernate.type.descriptor.java.DataHelper;

public class RowVersionTypeDescriptor
extends AbstractTypeDescriptor<byte[]> {
    public static final RowVersionTypeDescriptor INSTANCE = new RowVersionTypeDescriptor();

    public RowVersionTypeDescriptor() {
        super(byte[].class, ArrayMutabilityPlan.INSTANCE);
    }

    @Override
    public boolean areEqual(byte[] one, byte[] another) {
        return one == another || one != null && another != null && Arrays.equals(one, another);
    }

    @Override
    public int extractHashCode(byte[] bytes) {
        int hashCode = 1;
        for (byte aByte : bytes) {
            hashCode = 31 * hashCode + aByte;
        }
        return hashCode;
    }

    @Override
    public String toString(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (byte aByte : bytes) {
            String hexStr = Integer.toHexString(aByte - -128);
            if (hexStr.length() == 1) {
                buf.append('0');
            }
            buf.append(hexStr);
        }
        return buf.toString();
    }

    @Override
    public String extractLoggableRepresentation(byte[] value) {
        return value == null ? super.extractLoggableRepresentation(null) : Arrays.toString(value);
    }

    @Override
    public byte[] fromString(String string) {
        if (string == null) {
            return null;
        }
        if (string.length() % 2 != 0) {
            throw new IllegalArgumentException("The string is not a valid string representation of a binary content.");
        }
        byte[] bytes = new byte[string.length() / 2];
        for (int i = 0; i < bytes.length; ++i) {
            String hexStr = string.substring(i * 2, (i + 1) * 2);
            bytes[i] = (byte)(Integer.parseInt(hexStr, 16) + -128);
        }
        return bytes;
    }

    @Override
    public Comparator<byte[]> getComparator() {
        return RowVersionComparator.INSTANCE;
    }

    @Override
    public <X> X unwrap(byte[] value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (byte[].class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (InputStream.class.isAssignableFrom(type)) {
            return (X)new ByteArrayInputStream(value);
        }
        if (BinaryStream.class.isAssignableFrom(type)) {
            return (X)new BinaryStreamImpl(value);
        }
        if (Blob.class.isAssignableFrom(type)) {
            return (X)options.getLobCreator().createBlob(value);
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> byte[] wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (byte[].class.isInstance(value)) {
            return (byte[])value;
        }
        if (InputStream.class.isInstance(value)) {
            return DataHelper.extractBytes((InputStream)value);
        }
        if (Blob.class.isInstance(value) || DataHelper.isNClob(value.getClass())) {
            try {
                return DataHelper.extractBytes(((Blob)value).getBinaryStream());
            }
            catch (SQLException e) {
                throw new HibernateException("Unable to access lob stream", e);
            }
        }
        throw this.unknownWrap(value.getClass());
    }
}

