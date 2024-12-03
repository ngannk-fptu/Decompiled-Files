/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.util.XMLChar;

public abstract class TypeValidator {
    private static final boolean USE_CODE_POINT_COUNT_FOR_STRING_LENGTH = AccessController.doPrivileged(new PrivilegedAction(){

        public Object run() {
            try {
                return Boolean.getBoolean("org.apache.xerces.impl.dv.xs.useCodePointCountForStringLength") ? Boolean.TRUE : Boolean.FALSE;
            }
            catch (SecurityException securityException) {
                return Boolean.FALSE;
            }
        }
    }) == Boolean.TRUE;
    public static final short LESS_THAN = -1;
    public static final short EQUAL = 0;
    public static final short GREATER_THAN = 1;
    public static final short INDETERMINATE = 2;

    public abstract short getAllowedFacets();

    public abstract Object getActualValue(String var1, ValidationContext var2) throws InvalidDatatypeValueException;

    public void checkExtraRules(Object object, ValidationContext validationContext) throws InvalidDatatypeValueException {
    }

    public boolean isIdentical(Object object, Object object2) {
        return object.equals(object2);
    }

    public int compare(Object object, Object object2) {
        return -1;
    }

    public int getDataLength(Object object) {
        if (object instanceof String) {
            String string = (String)object;
            if (!USE_CODE_POINT_COUNT_FOR_STRING_LENGTH) {
                return string.length();
            }
            return this.getCodePointLength(string);
        }
        return -1;
    }

    public int getTotalDigits(Object object) {
        return -1;
    }

    public int getFractionDigits(Object object) {
        return -1;
    }

    private int getCodePointLength(String string) {
        int n = string.length();
        int n2 = 0;
        for (int i = 0; i < n - 1; ++i) {
            if (!XMLChar.isHighSurrogate(string.charAt(i))) continue;
            if (XMLChar.isLowSurrogate(string.charAt(++i))) {
                ++n2;
                continue;
            }
            --i;
        }
        return n - n2;
    }

    public static final boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static final int getDigit(char c) {
        return TypeValidator.isDigit(c) ? c - 48 : -1;
    }
}

