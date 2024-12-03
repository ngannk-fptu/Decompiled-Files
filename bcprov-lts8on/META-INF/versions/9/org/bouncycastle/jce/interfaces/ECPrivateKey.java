/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;
import org.bouncycastle.jce.interfaces.ECKey;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface ECPrivateKey
extends ECKey,
PrivateKey {
    public BigInteger getD();
}

