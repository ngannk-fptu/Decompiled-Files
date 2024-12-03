/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import org.apache.axis.types.UnsignedShort;
import org.apache.axis.utils.Messages;

public class UnsignedByte
extends UnsignedShort {
    public UnsignedByte() {
    }

    public UnsignedByte(long sValue) throws NumberFormatException {
        this.setValue(sValue);
    }

    public UnsignedByte(String sValue) throws NumberFormatException {
        this.setValue(Long.parseLong(sValue));
    }

    public void setValue(long sValue) throws NumberFormatException {
        if (!UnsignedByte.isValid(sValue)) {
            throw new NumberFormatException(Messages.getMessage("badUnsignedByte00") + String.valueOf(sValue) + "]");
        }
        this.lValue = new Long(sValue);
    }

    public static boolean isValid(long sValue) {
        return sValue >= 0L && sValue <= 255L;
    }
}

