/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.ApfloatInternalException;
import org.apfloat.internal.DoubleModConstants;
import org.apfloat.internal.DoubleTableFNT;
import org.apfloat.internal.DoubleWTables;
import org.apfloat.internal.TransformLengthExceededException;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.NTTStrategy;
import org.apfloat.spi.Util;

public class DoubleTableFNTStrategy
extends DoubleTableFNT
implements NTTStrategy {
    @Override
    public void transform(DataStorage dataStorage, int modulus) throws ApfloatRuntimeException {
        long length = dataStorage.getSize();
        if (length > 0x180000000000L) {
            throw new TransformLengthExceededException("Maximum transform length exceeded: " + length + " > " + 0x180000000000L);
        }
        if (length > Integer.MAX_VALUE) {
            throw new ApfloatInternalException("Maximum array length exceeded: " + length);
        }
        this.setModulus(DoubleModConstants.MODULUS[modulus]);
        double[] wTable = DoubleWTables.getWTable(modulus, (int)length);
        try (ArrayAccess arrayAccess = dataStorage.getArray(3, 0L, (int)length);){
            this.tableFNT(arrayAccess, wTable, null);
        }
    }

    @Override
    public void inverseTransform(DataStorage dataStorage, int modulus, long totalTransformLength) throws ApfloatRuntimeException {
        long length = dataStorage.getSize();
        if (Math.max(length, totalTransformLength) > 0x180000000000L) {
            throw new TransformLengthExceededException("Maximum transform length exceeded: " + Math.max(length, totalTransformLength) + " > " + 0x180000000000L);
        }
        if (length > Integer.MAX_VALUE) {
            throw new ApfloatInternalException("Maximum array length exceeded: " + length);
        }
        this.setModulus(DoubleModConstants.MODULUS[modulus]);
        double[] wTable = DoubleWTables.getInverseWTable(modulus, (int)length);
        try (ArrayAccess arrayAccess = dataStorage.getArray(3, 0L, (int)length);){
            this.inverseTableFNT(arrayAccess, wTable, null);
            this.divideElements(arrayAccess, totalTransformLength);
        }
    }

    @Override
    public long getTransformLength(long size) {
        return Util.round2up(size);
    }

    private void divideElements(ArrayAccess arrayAccess, double divisor) throws ApfloatRuntimeException {
        double inverseFactor = this.modDivide(1.0, divisor);
        double[] data = arrayAccess.getDoubleData();
        int length = arrayAccess.getLength();
        int offset = arrayAccess.getOffset();
        for (int i = 0; i < length; ++i) {
            data[i + offset] = this.modMultiply(data[i + offset], inverseFactor);
        }
    }
}

