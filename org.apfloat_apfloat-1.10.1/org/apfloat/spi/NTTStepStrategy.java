/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.ArrayAccess;

public interface NTTStepStrategy {
    public void multiplyElements(ArrayAccess var1, int var2, int var3, int var4, int var5, long var6, long var8, boolean var10, int var11) throws ApfloatRuntimeException;

    public void transformRows(ArrayAccess var1, int var2, int var3, boolean var4, boolean var5, int var6) throws ApfloatRuntimeException;

    public long getMaxTransformLength();
}

