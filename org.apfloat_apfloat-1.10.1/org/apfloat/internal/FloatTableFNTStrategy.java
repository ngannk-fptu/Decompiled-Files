/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.ApfloatInternalException;
import org.apfloat.internal.FloatModConstants;
import org.apfloat.internal.FloatTableFNT;
import org.apfloat.internal.FloatWTables;
import org.apfloat.internal.TransformLengthExceededException;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.NTTStrategy;
import org.apfloat.spi.Util;

public class FloatTableFNTStrategy
extends FloatTableFNT
implements NTTStrategy {
    @Override
    public void transform(DataStorage dataStorage, int modulus) throws ApfloatRuntimeException {
        long length = dataStorage.getSize();
        if (length > 393216L) {
            throw new TransformLengthExceededException("Maximum transform length exceeded: " + length + " > " + 393216L);
        }
        if (length > Integer.MAX_VALUE) {
            throw new ApfloatInternalException("Maximum array length exceeded: " + length);
        }
        this.setModulus(FloatModConstants.MODULUS[modulus]);
        float[] wTable = FloatWTables.getWTable(modulus, (int)length);
        try (ArrayAccess arrayAccess = dataStorage.getArray(3, 0L, (int)length);){
            this.tableFNT(arrayAccess, wTable, null);
        }
    }

    @Override
    public void inverseTransform(DataStorage dataStorage, int modulus, long totalTransformLength) throws ApfloatRuntimeException {
        long length = dataStorage.getSize();
        if (Math.max(length, totalTransformLength) > 393216L) {
            throw new TransformLengthExceededException("Maximum transform length exceeded: " + Math.max(length, totalTransformLength) + " > " + 393216L);
        }
        if (length > Integer.MAX_VALUE) {
            throw new ApfloatInternalException("Maximum array length exceeded: " + length);
        }
        this.setModulus(FloatModConstants.MODULUS[modulus]);
        float[] wTable = FloatWTables.getInverseWTable(modulus, (int)length);
        try (ArrayAccess arrayAccess = dataStorage.getArray(3, 0L, (int)length);){
            this.inverseTableFNT(arrayAccess, wTable, null);
            this.divideElements(arrayAccess, totalTransformLength);
        }
    }

    @Override
    public long getTransformLength(long size) {
        return Util.round2up(size);
    }

    private void divideElements(ArrayAccess arrayAccess, float divisor) throws ApfloatRuntimeException {
        float inverseFactor = this.modDivide(1.0f, divisor);
        float[] data = arrayAccess.getFloatData();
        int length = arrayAccess.getLength();
        int offset = arrayAccess.getOffset();
        for (int i = 0; i < length; ++i) {
            data[i + offset] = this.modMultiply(data[i + offset], inverseFactor);
        }
    }
}

