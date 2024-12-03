/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;

public interface NTTStrategy {
    public void transform(DataStorage var1, int var2) throws ApfloatRuntimeException;

    public void inverseTransform(DataStorage var1, int var2, long var3) throws ApfloatRuntimeException;

    public long getTransformLength(long var1);
}

