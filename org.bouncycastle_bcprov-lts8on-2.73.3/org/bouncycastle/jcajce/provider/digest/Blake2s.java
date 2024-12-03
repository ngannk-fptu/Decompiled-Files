/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.crypto.digests.Blake2sDigest;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.digest.BCMessageDigest;
import org.bouncycastle.jcajce.provider.digest.DigestAlgorithmProvider;

public class Blake2s {
    private Blake2s() {
    }

    public static class Blake2s128
    extends BCMessageDigest
    implements Cloneable {
        public Blake2s128() {
            super(new Blake2sDigest(128));
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            Blake2s128 d = (Blake2s128)super.clone();
            d.digest = new Blake2sDigest((Blake2sDigest)this.digest);
            return d;
        }
    }

    public static class Blake2s160
    extends BCMessageDigest
    implements Cloneable {
        public Blake2s160() {
            super(new Blake2sDigest(160));
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            Blake2s160 d = (Blake2s160)super.clone();
            d.digest = new Blake2sDigest((Blake2sDigest)this.digest);
            return d;
        }
    }

    public static class Blake2s224
    extends BCMessageDigest
    implements Cloneable {
        public Blake2s224() {
            super(new Blake2sDigest(224));
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            Blake2s224 d = (Blake2s224)super.clone();
            d.digest = new Blake2sDigest((Blake2sDigest)this.digest);
            return d;
        }
    }

    public static class Blake2s256
    extends BCMessageDigest
    implements Cloneable {
        public Blake2s256() {
            super(new Blake2sDigest(256));
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            Blake2s256 d = (Blake2s256)super.clone();
            d.digest = new Blake2sDigest((Blake2sDigest)this.digest);
            return d;
        }
    }

    public static class Mappings
    extends DigestAlgorithmProvider {
        private static final String PREFIX = Blake2s.class.getName();

        @Override
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("MessageDigest.BLAKE2S-256", PREFIX + "$Blake2s256");
            provider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2s256, "BLAKE2S-256");
            provider.addAlgorithm("MessageDigest.BLAKE2S-224", PREFIX + "$Blake2s224");
            provider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2s224, "BLAKE2S-224");
            provider.addAlgorithm("MessageDigest.BLAKE2S-160", PREFIX + "$Blake2s160");
            provider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2s160, "BLAKE2S-160");
            provider.addAlgorithm("MessageDigest.BLAKE2S-128", PREFIX + "$Blake2s128");
            provider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2s128, "BLAKE2S-128");
        }
    }
}

