/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.Digest;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class Utils {
    Utils() {
    }

    static CryptoServiceProperties getDefaultProperties(Digest digest, CryptoServicePurpose purpose) {
        return new DefaultProperties(digest.getDigestSize() * 4, digest.getAlgorithmName(), purpose);
    }

    static CryptoServiceProperties getDefaultProperties(Digest digest, int prfBitsOfSecurity, CryptoServicePurpose purpose) {
        return new DefaultPropertiesWithPRF(digest.getDigestSize() * 4, prfBitsOfSecurity, digest.getAlgorithmName(), purpose);
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class DefaultProperties
    implements CryptoServiceProperties {
        private final int bitsOfSecurity;
        private final String algorithmName;
        private final CryptoServicePurpose purpose;

        public DefaultProperties(int bitsOfSecurity, String algorithmName, CryptoServicePurpose purpose) {
            this.bitsOfSecurity = bitsOfSecurity;
            this.algorithmName = algorithmName;
            this.purpose = purpose;
        }

        @Override
        public int bitsOfSecurity() {
            return this.bitsOfSecurity;
        }

        @Override
        public String getServiceName() {
            return this.algorithmName;
        }

        @Override
        public CryptoServicePurpose getPurpose() {
            return this.purpose;
        }

        @Override
        public Object getParams() {
            return null;
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class DefaultPropertiesWithPRF
    implements CryptoServiceProperties {
        private final int bitsOfSecurity;
        private final int prfBitsOfSecurity;
        private final String algorithmName;
        private final CryptoServicePurpose purpose;

        public DefaultPropertiesWithPRF(int bitsOfSecurity, int prfBitsOfSecurity, String algorithmName, CryptoServicePurpose purpose) {
            this.bitsOfSecurity = bitsOfSecurity;
            this.prfBitsOfSecurity = prfBitsOfSecurity;
            this.algorithmName = algorithmName;
            this.purpose = purpose;
        }

        @Override
        public int bitsOfSecurity() {
            if (this.purpose == CryptoServicePurpose.PRF) {
                return this.prfBitsOfSecurity;
            }
            return this.bitsOfSecurity;
        }

        @Override
        public String getServiceName() {
            return this.algorithmName;
        }

        @Override
        public CryptoServicePurpose getPurpose() {
            return this.purpose;
        }

        @Override
        public Object getParams() {
            return null;
        }
    }
}

