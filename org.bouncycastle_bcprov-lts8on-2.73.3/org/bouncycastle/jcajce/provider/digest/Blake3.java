/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.crypto.digests.Blake3Digest;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.digest.BCMessageDigest;
import org.bouncycastle.jcajce.provider.digest.DigestAlgorithmProvider;

public class Blake3 {
    private Blake3() {
    }

    public static class Blake3_256
    extends BCMessageDigest
    implements Cloneable {
        public Blake3_256() {
            super(new Blake3Digest(256));
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            Blake3_256 d = (Blake3_256)super.clone();
            d.digest = new Blake3Digest((Blake3Digest)this.digest);
            return d;
        }
    }

    public static class Mappings
    extends DigestAlgorithmProvider {
        private static final String PREFIX = Blake3.class.getName();

        @Override
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("MessageDigest.BLAKE3-256", PREFIX + "$Blake3_256");
            provider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.blake3_256, "BLAKE3-256");
        }
    }
}

