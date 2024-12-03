/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.interfaces;

import java.security.PublicKey;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.math.ec.ECPoint;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface ECPublicKey
extends ECKey,
PublicKey {
    public ECPoint getQ();
}

