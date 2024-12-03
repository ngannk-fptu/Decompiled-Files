/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.LoggerFactory
 */
package org.apache.avro;

import org.apache.avro.AvroRuntimeException;
import org.slf4j.LoggerFactory;

public class SystemLimitException
extends AvroRuntimeException {
    static final int MAX_ARRAY_VM_LIMIT = 0x7FFFFFF7;
    public static final String MAX_BYTES_LENGTH_PROPERTY = "org.apache.avro.limits.bytes.maxLength";
    public static final String MAX_COLLECTION_LENGTH_PROPERTY = "org.apache.avro.limits.collectionItems.maxLength";
    public static final String MAX_STRING_LENGTH_PROPERTY = "org.apache.avro.limits.string.maxLength";
    private static int maxBytesLength = 0x7FFFFFF7;
    private static int maxCollectionLength = 0x7FFFFFF7;
    private static int maxStringLength = 0x7FFFFFF7;

    public SystemLimitException(String message) {
        super(message);
    }

    private static int getLimitFromProperty(String property, int defaultValue) {
        String o = System.getProperty(property);
        int i = defaultValue;
        if (o != null) {
            try {
                i = Integer.parseUnsignedInt(o);
            }
            catch (NumberFormatException nfe) {
                LoggerFactory.getLogger(SystemLimitException.class).warn("Could not parse property " + property + ": " + o, (Throwable)nfe);
            }
        }
        return i;
    }

    public static int checkMaxBytesLength(long length) {
        if (length < 0L) {
            throw new AvroRuntimeException("Malformed data. Length is negative: " + length);
        }
        if (length > 0x7FFFFFF7L) {
            throw new UnsupportedOperationException("Cannot read arrays longer than 2147483639 bytes in Java library");
        }
        if (length > (long)maxBytesLength) {
            throw new SystemLimitException("Bytes length " + length + " exceeds maximum allowed");
        }
        return (int)length;
    }

    public static int checkMaxCollectionLength(long existing, long items) {
        long length = existing + items;
        if (existing < 0L) {
            throw new AvroRuntimeException("Malformed data. Length is negative: " + existing);
        }
        if (items < 0L) {
            throw new AvroRuntimeException("Malformed data. Length is negative: " + items);
        }
        if (length > 0x7FFFFFF7L || length < existing) {
            throw new UnsupportedOperationException("Cannot read collections larger than 2147483639 items in Java library");
        }
        if (length > (long)maxCollectionLength) {
            throw new SystemLimitException("Collection length " + length + " exceeds maximum allowed");
        }
        return (int)length;
    }

    public static int checkMaxStringLength(long length) {
        if (length < 0L) {
            throw new AvroRuntimeException("Malformed data. Length is negative: " + length);
        }
        if (length > 0x7FFFFFF7L) {
            throw new UnsupportedOperationException("Cannot read strings longer than 2147483639 bytes");
        }
        if (length > (long)maxStringLength) {
            throw new SystemLimitException("String length " + length + " exceeds maximum allowed");
        }
        return (int)length;
    }

    static void resetLimits() {
        maxBytesLength = SystemLimitException.getLimitFromProperty(MAX_BYTES_LENGTH_PROPERTY, 0x7FFFFFF7);
        maxCollectionLength = SystemLimitException.getLimitFromProperty(MAX_COLLECTION_LENGTH_PROPERTY, 0x7FFFFFF7);
        maxStringLength = SystemLimitException.getLimitFromProperty(MAX_STRING_LENGTH_PROPERTY, 0x7FFFFFF7);
    }

    static {
        SystemLimitException.resetLimits();
    }
}

