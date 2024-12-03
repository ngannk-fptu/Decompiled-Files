/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class EdEC {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.edec.";
    private static final Map<String, String> edxAttributes = new HashMap<String, String>();

    static {
        edxAttributes.put("SupportedKeyClasses", "java.security.interfaces.ECPublicKey|java.security.interfaces.ECPrivateKey");
        edxAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
    }

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("KeyFactory.XDH", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$XDH");
            provider.addAlgorithm("KeyFactory.X448", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$X448");
            provider.addAlgorithm("KeyFactory.X25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$X25519");
            provider.addAlgorithm("KeyFactory.EDDSA", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$EdDSA");
            provider.addAlgorithm("KeyFactory.ED448", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$Ed448");
            provider.addAlgorithm("KeyFactory.ED25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$Ed25519");
            provider.addAlgorithm("Signature.EDDSA", "org.bouncycastle.jcajce.provider.asymmetric.edec.SignatureSpi$EdDSA");
            provider.addAlgorithm("Signature.ED448", "org.bouncycastle.jcajce.provider.asymmetric.edec.SignatureSpi$Ed448");
            provider.addAlgorithm("Signature.ED25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.SignatureSpi$Ed25519");
            provider.addAlgorithm("Alg.Alias.Signature", EdECObjectIdentifiers.id_Ed448, "ED448");
            provider.addAlgorithm("Alg.Alias.Signature", EdECObjectIdentifiers.id_Ed25519, "ED25519");
            provider.addAlgorithm("KeyPairGenerator.EDDSA", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$EdDSA");
            provider.addAlgorithm("KeyPairGenerator.ED448", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$Ed448");
            provider.addAlgorithm("KeyPairGenerator.ED25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$Ed25519");
            provider.addAlgorithm("KeyPairGenerator", EdECObjectIdentifiers.id_Ed448, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$Ed448");
            provider.addAlgorithm("KeyPairGenerator", EdECObjectIdentifiers.id_Ed25519, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$Ed25519");
            provider.addAlgorithm("KeyAgreement.XDH", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$XDH");
            provider.addAlgorithm("KeyAgreement.X448", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448");
            provider.addAlgorithm("KeyAgreement.X25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519");
            provider.addAlgorithm("KeyAgreement", EdECObjectIdentifiers.id_X448, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448");
            provider.addAlgorithm("KeyAgreement", EdECObjectIdentifiers.id_X25519, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519");
            provider.addAlgorithm("KeyAgreement.X25519WITHSHA256CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519withSHA256CKDF");
            provider.addAlgorithm("KeyAgreement.X25519WITHSHA384CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519withSHA384CKDF");
            provider.addAlgorithm("KeyAgreement.X25519WITHSHA512CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519withSHA512CKDF");
            provider.addAlgorithm("KeyAgreement.X448WITHSHA256CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448withSHA256CKDF");
            provider.addAlgorithm("KeyAgreement.X448WITHSHA384CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448withSHA384CKDF");
            provider.addAlgorithm("KeyAgreement.X448WITHSHA512CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448withSHA512CKDF");
            provider.addAlgorithm("KeyAgreement.X25519WITHSHA256KDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519withSHA256KDF");
            provider.addAlgorithm("KeyAgreement.X448WITHSHA512KDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448withSHA512KDF");
            provider.addAlgorithm("KeyAgreement.X25519UWITHSHA256KDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519UwithSHA256KDF");
            provider.addAlgorithm("KeyAgreement.X448UWITHSHA512KDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448UwithSHA512KDF");
            provider.addAlgorithm("KeyPairGenerator.XDH", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$XDH");
            provider.addAlgorithm("KeyPairGenerator.X448", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$X448");
            provider.addAlgorithm("KeyPairGenerator.X25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$X25519");
            provider.addAlgorithm("KeyPairGenerator", EdECObjectIdentifiers.id_X448, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$X448");
            provider.addAlgorithm("KeyPairGenerator", EdECObjectIdentifiers.id_X25519, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$X25519");
            provider.addAlgorithm("Cipher.XIES", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIES");
            provider.addAlgorithm("Cipher.XIESwithSHA1", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIES");
            provider.addAlgorithm("Cipher.XIESWITHSHA1", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIES");
            provider.addAlgorithm("Cipher.XIESwithSHA256", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA256");
            provider.addAlgorithm("Cipher.XIESWITHSHA256", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA256");
            provider.addAlgorithm("Cipher.XIESwithSHA384", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA384");
            provider.addAlgorithm("Cipher.XIESWITHSHA384", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA384");
            provider.addAlgorithm("Cipher.XIESwithSHA512", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA512");
            provider.addAlgorithm("Cipher.XIESWITHSHA512", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA512");
            provider.addAlgorithm("Cipher.XIESwithAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithAESCBC");
            provider.addAlgorithm("Cipher.XIESWITHAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithAESCBC");
            provider.addAlgorithm("Cipher.XIESwithSHA1andAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithAESCBC");
            provider.addAlgorithm("Cipher.XIESWITHSHA1ANDAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithAESCBC");
            provider.addAlgorithm("Cipher.XIESwithSHA256andAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA256andAESCBC");
            provider.addAlgorithm("Cipher.XIESWITHSHA256ANDAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA256andAESCBC");
            provider.addAlgorithm("Cipher.XIESwithSHA384andAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA384andAESCBC");
            provider.addAlgorithm("Cipher.XIESWITHSHA384ANDAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA384andAESCBC");
            provider.addAlgorithm("Cipher.XIESwithSHA512andAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA512andAESCBC");
            provider.addAlgorithm("Cipher.XIESWITHSHA512ANDAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA512andAESCBC");
            this.registerOid(provider, EdECObjectIdentifiers.id_X448, "XDH", new KeyFactorySpi.X448());
            this.registerOid(provider, EdECObjectIdentifiers.id_X25519, "XDH", new KeyFactorySpi.X25519());
            this.registerOid(provider, EdECObjectIdentifiers.id_Ed448, "EDDSA", new KeyFactorySpi.Ed448());
            this.registerOid(provider, EdECObjectIdentifiers.id_Ed25519, "EDDSA", new KeyFactorySpi.Ed25519());
        }
    }
}

