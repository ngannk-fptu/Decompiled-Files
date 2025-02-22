/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.DESKeyGenerator;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.util.AlgorithmIdentifierFactory;

public class CipherKeyGeneratorFactory {
    private CipherKeyGeneratorFactory() {
    }

    public static CipherKeyGenerator createKeyGenerator(ASN1ObjectIdentifier algorithm, SecureRandom random) throws IllegalArgumentException {
        if (NISTObjectIdentifiers.id_aes128_CBC.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 128);
        }
        if (NISTObjectIdentifiers.id_aes192_CBC.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 192);
        }
        if (NISTObjectIdentifiers.id_aes256_CBC.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 256);
        }
        if (NISTObjectIdentifiers.id_aes128_GCM.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 128);
        }
        if (NISTObjectIdentifiers.id_aes192_GCM.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 192);
        }
        if (NISTObjectIdentifiers.id_aes256_GCM.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 256);
        }
        if (NISTObjectIdentifiers.id_aes128_CCM.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 128);
        }
        if (NISTObjectIdentifiers.id_aes192_CCM.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 192);
        }
        if (NISTObjectIdentifiers.id_aes256_CCM.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 256);
        }
        if (PKCSObjectIdentifiers.des_EDE3_CBC.equals(algorithm)) {
            DESedeKeyGenerator keyGen = new DESedeKeyGenerator();
            keyGen.init(new KeyGenerationParameters(random, 192));
            return keyGen;
        }
        if (NTTObjectIdentifiers.id_camellia128_cbc.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 128);
        }
        if (NTTObjectIdentifiers.id_camellia192_cbc.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 192);
        }
        if (NTTObjectIdentifiers.id_camellia256_cbc.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 256);
        }
        if (KISAObjectIdentifiers.id_seedCBC.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 128);
        }
        if (AlgorithmIdentifierFactory.CAST5_CBC.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 128);
        }
        if (OIWObjectIdentifiers.desCBC.equals(algorithm)) {
            DESKeyGenerator keyGen = new DESKeyGenerator();
            keyGen.init(new KeyGenerationParameters(random, 64));
            return keyGen;
        }
        if (PKCSObjectIdentifiers.rc4.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 128);
        }
        if (PKCSObjectIdentifiers.RC2_CBC.equals(algorithm)) {
            return CipherKeyGeneratorFactory.createCipherKeyGenerator(random, 128);
        }
        throw new IllegalArgumentException("cannot recognise cipher: " + algorithm);
    }

    private static CipherKeyGenerator createCipherKeyGenerator(SecureRandom random, int keySize) {
        CipherKeyGenerator keyGen = new CipherKeyGenerator();
        keyGen.init(new KeyGenerationParameters(random, keySize));
        return keyGen;
    }
}

