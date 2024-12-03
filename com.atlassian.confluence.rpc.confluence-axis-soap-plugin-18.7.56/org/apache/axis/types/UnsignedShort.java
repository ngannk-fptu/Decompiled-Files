/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import org.apache.axis.types.UnsignedInt;
import org.apache.axis.utils.Messages;

public class UnsignedShort
extends UnsignedInt {
    public UnsignedShort() {
    }

    public UnsignedShort(long sValue) throws NumberFormatException {
        this.setValue(sValue);
    }

    public UnsignedShort(String sValue) throws NumberFormatException {
        this.setValue(Long.parseLong(sValue));
    }

    public void setValue(long sValue) throws NumberFormatException {
        if (!UnsignedShort.isValid(sValue)) {
            throw new NumberFormatException(Messages.getMessage("badUnsignedShort00") + String.valueOf(sValue) + "]");
        }
        this.lValue = new Long(sValue);
    }

    public static boolean isValid(long sValue) {
        return sValue >= 0L && sValue <= 65535L;
    }
}

