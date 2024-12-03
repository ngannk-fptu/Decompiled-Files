/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.ArrayAccess;

public interface MatrixStrategy {
    public void transpose(ArrayAccess var1, int var2, int var3) throws ApfloatRuntimeException;

    public void transposeSquare(ArrayAccess var1, int var2, int var3) throws ApfloatRuntimeException;

    public void permuteToDoubleWidth(ArrayAccess var1, int var2, int var3) throws ApfloatRuntimeException;

    public void permuteToHalfWidth(ArrayAccess var1, int var2, int var3) throws ApfloatRuntimeException;
}

