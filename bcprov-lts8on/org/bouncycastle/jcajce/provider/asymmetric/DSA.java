/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSAUtil;
import org.bouncycastle.jcajce.provider.asymmetric.dsa.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class DSA {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.dsa.";

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("AlgorithmParameters.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.AlgorithmParametersSpi");
            provider.addAlgorithm("AlgorithmParameterGenerator.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.AlgorithmParameterGeneratorSpi");
            provider.addAlgorithm("KeyPairGenerator.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.KeyPairGeneratorSpi");
            provider.addAlgorithm("KeyFactory.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.KeyFactorySpi");
            provider.addAlgorithm("Signature.DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$stdDSA");
            provider.addAlgorithm("Signature.NONEWITHDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$noneDSA");
            provider.addAlgorithm("Alg.Alias.Signature.RAWDSA", "NONEWITHDSA");
            provider.addAlgorithm("Signature.DETDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA");
            provider.addAlgorithm("Signature.SHA1WITHDETDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA");
            provider.addAlgorithm("Signature.SHA224WITHDETDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA224");
            provider.addAlgorithm("Signature.SHA256WITHDETDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA256");
            provider.addAlgorithm("Signature.SHA384WITHDETDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA384");
            provider.addAlgorithm("Signature.SHA512WITHDETDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA512");
            provider.addAlgorithm("Signature.DDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA");
            provider.addAlgorithm("Signature.SHA1WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA");
            provider.addAlgorithm("Signature.SHA224WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA224");
            provider.addAlgorithm("Signature.SHA256WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA256");
            provider.addAlgorithm("Signature.SHA384WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA384");
            provider.addAlgorithm("Signature.SHA512WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSA512");
            provider.addAlgorithm("Signature.SHA3-224WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_224");
            provider.addAlgorithm("Signature.SHA3-256WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_256");
            provider.addAlgorithm("Signature.SHA3-384WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_384");
            provider.addAlgorithm("Signature.SHA3-512WITHDDSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$detDSASha3_512");
            this.addSignatureAlgorithm(provider, "SHA224", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa224", NISTObjectIdentifiers.dsa_with_sha224);
            this.addSignatureAlgorithm(provider, "SHA256", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa256", NISTObjectIdentifiers.dsa_with_sha256);
            this.addSignatureAlgorithm(provider, "SHA384", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa384", NISTObjectIdentifiers.dsa_with_sha384);
            this.addSignatureAlgorithm(provider, "SHA512", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsa512", NISTObjectIdentifiers.dsa_with_sha512);
            this.addSignatureAlgorithm(provider, "SHA3-224", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_224", NISTObjectIdentifiers.id_dsa_with_sha3_224);
            this.addSignatureAlgorithm(provider, "SHA3-256", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_256", NISTObjectIdentifiers.id_dsa_with_sha3_256);
            this.addSignatureAlgorithm(provider, "SHA3-384", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_384", NISTObjectIdentifiers.id_dsa_with_sha3_384);
            this.addSignatureAlgorithm(provider, "SHA3-512", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaSha3_512", NISTObjectIdentifiers.id_dsa_with_sha3_512);
            provider.addAlgorithm("Alg.Alias.Signature.SHA/DSA", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.SHA1withDSA", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.SHA1WITHDSA", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.1.3.14.3.2.26with1.2.840.10040.4.1", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.1.3.14.3.2.26with1.2.840.10040.4.3", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.DSAwithSHA1", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.DSAWITHSHA1", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.SHA1WithDSA", "DSA");
            provider.addAlgorithm("Alg.Alias.Signature.DSAWithSHA1", "DSA");
            this.addSignatureAlgorithm(provider, "RIPEMD160", "DSA", "org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner$dsaRMD160");
            KeyFactorySpi keyFact = new KeyFactorySpi();
            for (int i = 0; i != DSAUtil.dsaOids.length; ++i) {
                provider.addAlgorithm("Alg.Alias.Signature." + DSAUtil.dsaOids[i], "DSA");
                this.registerOid(provider, DSAUtil.dsaOids[i], "DSA", keyFact);
                this.registerOidAlgorithmParameterGenerator(provider, DSAUtil.dsaOids[i], "DSA");
            }
        }
    }
}

