/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;

public interface AdditionStrategy<T> {
    public T add(DataStorage.Iterator var1, DataStorage.Iterator var2, T var3, DataStorage.Iterator var4, long var5) throws ApfloatRuntimeException;

    public T subtract(DataStorage.Iterator var1, DataStorage.Iterator var2, T var3, DataStorage.Iterator var4, long var5) throws ApfloatRuntimeException;

    public T multiplyAdd(DataStorage.Iterator var1, DataStorage.Iterator var2, T var3, T var4, DataStorage.Iterator var5, long var6) throws ApfloatRuntimeException;

    public T divide(DataStorage.Iterator var1, T var2, T var3, DataStorage.Iterator var4, long var5) throws ApfloatRuntimeException;

    public T zero();
}

