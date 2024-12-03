/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs.datatypes;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface XSDecimal {
    public BigDecimal getBigDecimal();

    public BigInteger getBigInteger() throws NumberFormatException;

    public long getLong() throws NumberFormatException;

    public int getInt() throws NumberFormatException;

    public short getShort() throws NumberFormatException;

    public byte getByte() throws NumberFormatException;
}

