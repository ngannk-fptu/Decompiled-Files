/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class AbstractECLookupTable
implements ECLookupTable {
    @Override
    public ECPoint lookupVar(int index) {
        return this.lookup(index);
    }
}

