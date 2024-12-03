/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.tsp.PartialHashtree
 */
package org.bouncycastle.tsp.ers;

import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.operator.DigestCalculator;

public interface ERSRootNodeCalculator {
    public byte[] computeRootHash(DigestCalculator var1, PartialHashtree[] var2);

    public PartialHashtree[] computePathToRoot(DigestCalculator var1, PartialHashtree var2, int var3);

    public byte[] recoverRootHash(DigestCalculator var1, PartialHashtree[] var2);
}

