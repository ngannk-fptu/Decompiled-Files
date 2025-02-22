/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.digest.BCMessageDigest;
import org.bouncycastle.jcajce.provider.digest.DigestAlgorithmProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

public class SM3 {
    private SM3() {
    }

    public static class Digest
    extends BCMessageDigest
    implements Cloneable {
        public Digest() {
            super(new SM3Digest());
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            Digest d = (Digest)super.clone();
            d.digest = new SM3Digest((SM3Digest)this.digest);
            return d;
        }
    }

    public static class HashMac
    extends BaseMac {
        public HashMac() {
            super(new HMac(new SM3Digest()));
        }
    }

    public static class KeyGenerator
    extends BaseKeyGenerator {
        public KeyGenerator() {
            super("HMACSM3", 256, new CipherKeyGenerator());
        }
    }

    public static class Mappings
    extends DigestAlgorithmProvider {
        private static final String PREFIX = SM3.class.getName();

        @Override
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("MessageDigest.SM3", PREFIX + "$Digest");
            provider.addAlgorithm("Alg.Alias.MessageDigest.SM3", "SM3");
            provider.addAlgorithm("Alg.Alias.MessageDigest.1.2.156.197.1.401", "SM3");
            provider.addAlgorithm("Alg.Alias.MessageDigest." + GMObjectIdentifiers.sm3, "SM3");
            this.addHMACAlgorithm(provider, "SM3", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
            this.addHMACAlias(provider, "SM3", GMObjectIdentifiers.hmac_sm3);
        }
    }
}

