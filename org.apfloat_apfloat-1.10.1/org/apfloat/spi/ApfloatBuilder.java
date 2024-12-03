/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import java.io.IOException;
import java.io.PushbackReader;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.ApfloatImpl;

public interface ApfloatBuilder {
    public ApfloatImpl createApfloat(String var1, long var2, int var4, boolean var5) throws NumberFormatException, ApfloatRuntimeException;

    public ApfloatImpl createApfloat(long var1, long var3, int var5) throws NumberFormatException, ApfloatRuntimeException;

    public ApfloatImpl createApfloat(double var1, long var3, int var5) throws NumberFormatException, ApfloatRuntimeException;

    public ApfloatImpl createApfloat(PushbackReader var1, long var2, int var4, boolean var5) throws IOException, NumberFormatException, ApfloatRuntimeException;
}

