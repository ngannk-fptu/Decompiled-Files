/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.tsp.ers;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.util.Arrays;

public abstract class ERSCachingData
implements ERSData {
    private Map<CacheIndex, byte[]> preCalcs = new HashMap<CacheIndex, byte[]>();

    @Override
    public byte[] getHash(DigestCalculator digestCalculator, byte[] previousChainHash) {
        CacheIndex digAlgID = new CacheIndex(digestCalculator.getAlgorithmIdentifier(), previousChainHash);
        if (this.preCalcs.containsKey(digAlgID)) {
            return this.preCalcs.get(digAlgID);
        }
        byte[] hash = this.calculateHash(digestCalculator, previousChainHash);
        this.preCalcs.put(digAlgID, hash);
        return hash;
    }

    protected abstract byte[] calculateHash(DigestCalculator var1, byte[] var2);

    private static class CacheIndex {
        final AlgorithmIdentifier algId;
        final byte[] chainHash;

        private CacheIndex(AlgorithmIdentifier algId, byte[] chainHash) {
            this.algId = algId;
            this.chainHash = chainHash;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CacheIndex)) {
                return false;
            }
            CacheIndex that = (CacheIndex)o;
            return this.algId.equals((Object)that.algId) && Arrays.areEqual((byte[])this.chainHash, (byte[])that.chainHash);
        }

        public int hashCode() {
            int result = this.algId.hashCode();
            return 31 * result + Arrays.hashCode((byte[])this.chainHash);
        }
    }
}

