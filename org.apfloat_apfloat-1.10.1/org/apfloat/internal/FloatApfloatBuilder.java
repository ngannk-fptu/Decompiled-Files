/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.io.IOException;
import java.io.PushbackReader;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.FloatApfloatImpl;
import org.apfloat.spi.ApfloatBuilder;
import org.apfloat.spi.ApfloatImpl;

public class FloatApfloatBuilder
implements ApfloatBuilder {
    @Override
    public ApfloatImpl createApfloat(String value, long precision, int radix, boolean isInteger) throws NumberFormatException, ApfloatRuntimeException {
        return new FloatApfloatImpl(value, precision, radix, isInteger);
    }

    @Override
    public ApfloatImpl createApfloat(long value, long precision, int radix) throws NumberFormatException, ApfloatRuntimeException {
        return new FloatApfloatImpl(value, precision, radix);
    }

    @Override
    public ApfloatImpl createApfloat(double value, long precision, int radix) throws NumberFormatException, ApfloatRuntimeException {
        return new FloatApfloatImpl(value, precision, radix);
    }

    @Override
    public ApfloatImpl createApfloat(PushbackReader in, long precision, int radix, boolean isInteger) throws IOException, NumberFormatException, ApfloatRuntimeException {
        return new FloatApfloatImpl(in, precision, radix, isInteger);
    }
}

