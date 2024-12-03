/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.field;

import java.math.BigInteger;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface FiniteField {
    public BigInteger getCharacteristic();

    public int getDimension();
}

