/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import org.apfloat.ApfloatRuntimeException;

public interface ApfloatImpl
extends Serializable {
    public ApfloatImpl addOrSubtract(ApfloatImpl var1, boolean var2) throws ApfloatRuntimeException;

    public ApfloatImpl multiply(ApfloatImpl var1) throws ApfloatRuntimeException;

    public boolean isShort() throws ApfloatRuntimeException;

    public ApfloatImpl divideShort(ApfloatImpl var1) throws ApfloatRuntimeException;

    public ApfloatImpl absFloor() throws ApfloatRuntimeException;

    public ApfloatImpl absCeil() throws ApfloatRuntimeException;

    public ApfloatImpl frac() throws ApfloatRuntimeException;

    public int radix();

    public long precision();

    public long size() throws ApfloatRuntimeException;

    public ApfloatImpl precision(long var1) throws ApfloatRuntimeException;

    public long scale() throws ApfloatRuntimeException;

    public int signum();

    public ApfloatImpl negate() throws ApfloatRuntimeException;

    public double doubleValue();

    public long longValue();

    public boolean isOne() throws ApfloatRuntimeException;

    public long equalDigits(ApfloatImpl var1) throws ApfloatRuntimeException;

    public int compareTo(ApfloatImpl var1) throws ApfloatRuntimeException;

    public int hashCode();

    public String toString(boolean var1) throws ApfloatRuntimeException;

    public void writeTo(Writer var1, boolean var2) throws IOException, ApfloatRuntimeException;
}

