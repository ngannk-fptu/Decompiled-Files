/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;
import org.bouncycastle.math.ec.endo.ECEndomorphism;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface GLVEndomorphism
extends ECEndomorphism {
    public BigInteger[] decomposeScalar(BigInteger var1);
}

