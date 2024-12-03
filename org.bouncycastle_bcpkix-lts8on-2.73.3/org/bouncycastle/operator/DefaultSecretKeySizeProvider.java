/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
 *  org.bouncycastle.asn1.kisa.KISAObjectIdentifiers
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.ntt.NTTObjectIdentifiers
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Integers
 */
package org.bouncycastle.operator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.util.Integers;

public class DefaultSecretKeySizeProvider
implements SecretKeySizeProvider {
    public static final SecretKeySizeProvider INSTANCE = new DefaultSecretKeySizeProvider();
    private static final Map KEY_SIZES;

    @Override
    public int getKeySize(AlgorithmIdentifier algorithmIdentifier) {
        int keySize = this.getKeySize(algorithmIdentifier.getAlgorithm());
        if (keySize > 0) {
            return keySize;
        }
        return -1;
    }

    @Override
    public int getKeySize(ASN1ObjectIdentifier algorithm) {
        Integer keySize = (Integer)KEY_SIZES.get(algorithm);
        if (keySize != null) {
            return keySize;
        }
        return -1;
    }

    static {
        HashMap<ASN1ObjectIdentifier, Integer> keySizes = new HashMap<ASN1ObjectIdentifier, Integer>();
        keySizes.put(new ASN1ObjectIdentifier("1.2.840.113533.7.66.10"), Integers.valueOf((int)128));
        keySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf((int)192));
        keySizes.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, Integers.valueOf((int)192));
        keySizes.put(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, Integers.valueOf((int)64));
        keySizes.put(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, Integers.valueOf((int)64));
        keySizes.put(NISTObjectIdentifiers.id_aes128_CBC, Integers.valueOf((int)128));
        keySizes.put(NISTObjectIdentifiers.id_aes192_CBC, Integers.valueOf((int)192));
        keySizes.put(NISTObjectIdentifiers.id_aes256_CBC, Integers.valueOf((int)256));
        keySizes.put(NISTObjectIdentifiers.id_aes128_GCM, Integers.valueOf((int)128));
        keySizes.put(NISTObjectIdentifiers.id_aes192_GCM, Integers.valueOf((int)192));
        keySizes.put(NISTObjectIdentifiers.id_aes256_GCM, Integers.valueOf((int)256));
        keySizes.put(NISTObjectIdentifiers.id_aes128_CCM, Integers.valueOf((int)128));
        keySizes.put(NISTObjectIdentifiers.id_aes192_CCM, Integers.valueOf((int)192));
        keySizes.put(NISTObjectIdentifiers.id_aes256_CCM, Integers.valueOf((int)256));
        keySizes.put(NISTObjectIdentifiers.id_aes128_wrap, Integers.valueOf((int)128));
        keySizes.put(NISTObjectIdentifiers.id_aes192_wrap, Integers.valueOf((int)192));
        keySizes.put(NISTObjectIdentifiers.id_aes256_wrap, Integers.valueOf((int)256));
        keySizes.put(NISTObjectIdentifiers.id_aes128_wrap_pad, Integers.valueOf((int)128));
        keySizes.put(NISTObjectIdentifiers.id_aes192_wrap_pad, Integers.valueOf((int)192));
        keySizes.put(NISTObjectIdentifiers.id_aes256_wrap_pad, Integers.valueOf((int)256));
        keySizes.put(NTTObjectIdentifiers.id_camellia128_cbc, Integers.valueOf((int)128));
        keySizes.put(NTTObjectIdentifiers.id_camellia192_cbc, Integers.valueOf((int)192));
        keySizes.put(NTTObjectIdentifiers.id_camellia256_cbc, Integers.valueOf((int)256));
        keySizes.put(NTTObjectIdentifiers.id_camellia128_wrap, Integers.valueOf((int)128));
        keySizes.put(NTTObjectIdentifiers.id_camellia192_wrap, Integers.valueOf((int)192));
        keySizes.put(NTTObjectIdentifiers.id_camellia256_wrap, Integers.valueOf((int)256));
        keySizes.put(KISAObjectIdentifiers.id_seedCBC, Integers.valueOf((int)128));
        keySizes.put(OIWObjectIdentifiers.desCBC, Integers.valueOf((int)64));
        keySizes.put(CryptoProObjectIdentifiers.gostR28147_gcfb, Integers.valueOf((int)256));
        KEY_SIZES = Collections.unmodifiableMap(keySizes);
    }
}

