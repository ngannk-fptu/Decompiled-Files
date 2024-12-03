/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.crypto.digests.Haraka256Digest;
import org.bouncycastle.crypto.digests.Haraka512Digest;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.digest.BCMessageDigest;
import org.bouncycastle.jcajce.provider.digest.DigestAlgorithmProvider;

public class Haraka {
    private Haraka() {
    }

    public static class Digest256
    extends BCMessageDigest
    implements Cloneable {
        public Digest256() {
            super(new Haraka256Digest());
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            Digest256 d = (Digest256)super.clone();
            d.digest = new Haraka256Digest((Haraka256Digest)this.digest);
            return d;
        }
    }

    public static class Digest512
    extends BCMessageDigest
    implements Cloneable {
        public Digest512() {
            super(new Haraka512Digest());
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            Digest512 d = (Digest512)super.clone();
            d.digest = new Haraka512Digest((Haraka512Digest)this.digest);
            return d;
        }
    }

    public static class Mappings
    extends DigestAlgorithmProvider {
        private static final String PREFIX = Haraka.class.getName();

        @Override
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("MessageDigest.HARAKA-256", PREFIX + "$Digest256");
            provider.addAlgorithm("MessageDigest.HARAKA-512", PREFIX + "$Digest512");
        }
    }
}

