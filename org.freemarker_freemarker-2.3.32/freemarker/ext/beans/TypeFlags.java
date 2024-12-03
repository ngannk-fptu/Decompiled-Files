/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TypeFlags {
    static final int WIDENED_NUMERICAL_UNWRAPPING_HINT = 1;
    static final int BYTE = 4;
    static final int SHORT = 8;
    static final int INTEGER = 16;
    static final int LONG = 32;
    static final int FLOAT = 64;
    static final int DOUBLE = 128;
    static final int BIG_INTEGER = 256;
    static final int BIG_DECIMAL = 512;
    static final int UNKNOWN_NUMERICAL_TYPE = 1024;
    static final int ACCEPTS_NUMBER = 2048;
    static final int ACCEPTS_DATE = 4096;
    static final int ACCEPTS_STRING = 8192;
    static final int ACCEPTS_BOOLEAN = 16384;
    static final int ACCEPTS_MAP = 32768;
    static final int ACCEPTS_LIST = 65536;
    static final int ACCEPTS_SET = 131072;
    static final int ACCEPTS_ARRAY = 262144;
    static final int CHARACTER = 524288;
    static final int ACCEPTS_ANY_OBJECT = 522240;
    static final int MASK_KNOWN_INTEGERS = 316;
    static final int MASK_KNOWN_NONINTEGERS = 704;
    static final int MASK_ALL_KNOWN_NUMERICALS = 1020;
    static final int MASK_ALL_NUMERICALS = 2044;

    TypeFlags() {
    }

    static int classToTypeFlags(Class pClass) {
        if (pClass == Object.class) {
            return 522240;
        }
        if (pClass == String.class) {
            return 8192;
        }
        if (pClass.isPrimitive()) {
            if (pClass == Integer.TYPE) {
                return 2064;
            }
            if (pClass == Long.TYPE) {
                return 2080;
            }
            if (pClass == Double.TYPE) {
                return 2176;
            }
            if (pClass == Float.TYPE) {
                return 2112;
            }
            if (pClass == Byte.TYPE) {
                return 2052;
            }
            if (pClass == Short.TYPE) {
                return 2056;
            }
            if (pClass == Character.TYPE) {
                return 524288;
            }
            if (pClass == Boolean.TYPE) {
                return 16384;
            }
            return 0;
        }
        if (Number.class.isAssignableFrom(pClass)) {
            if (pClass == Integer.class) {
                return 2064;
            }
            if (pClass == Long.class) {
                return 2080;
            }
            if (pClass == Double.class) {
                return 2176;
            }
            if (pClass == Float.class) {
                return 2112;
            }
            if (pClass == Byte.class) {
                return 2052;
            }
            if (pClass == Short.class) {
                return 2056;
            }
            if (BigDecimal.class.isAssignableFrom(pClass)) {
                return 2560;
            }
            if (BigInteger.class.isAssignableFrom(pClass)) {
                return 2304;
            }
            return 3072;
        }
        if (pClass.isArray()) {
            return 262144;
        }
        int flags = 0;
        if (pClass.isAssignableFrom(String.class)) {
            flags |= 0x2000;
        }
        if (pClass.isAssignableFrom(Date.class)) {
            flags |= 0x1000;
        }
        if (pClass.isAssignableFrom(Boolean.class)) {
            flags |= 0x4000;
        }
        if (pClass.isAssignableFrom(Map.class)) {
            flags |= 0x8000;
        }
        if (pClass.isAssignableFrom(List.class)) {
            flags |= 0x10000;
        }
        if (pClass.isAssignableFrom(Set.class)) {
            flags |= 0x20000;
        }
        if (pClass == Character.class) {
            flags |= 0x80000;
        }
        return flags;
    }
}

