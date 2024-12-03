/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.kisa.KISAObjectIdentifiers
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.ntt.NTTObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.operator.jcajce;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.OperatorHelper;
import org.bouncycastle.operator.jcajce.OperatorUtils;

public class JceSymmetricKeyWrapper
extends SymmetricKeyWrapper {
    private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
    private SecureRandom random;
    private SecretKey wrappingKey;

    public JceSymmetricKeyWrapper(SecretKey wrappingKey) {
        super(JceSymmetricKeyWrapper.determineKeyEncAlg(wrappingKey));
        this.wrappingKey = wrappingKey;
    }

    public JceSymmetricKeyWrapper setProvider(Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceSymmetricKeyWrapper setProvider(String providerName) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(providerName));
        return this;
    }

    public JceSymmetricKeyWrapper setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    @Override
    public byte[] generateWrappedKey(GenericKey encryptionKey) throws OperatorException {
        Key contentEncryptionKeySpec = OperatorUtils.getJceKey(encryptionKey);
        Cipher keyEncryptionCipher = this.helper.createSymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm());
        try {
            keyEncryptionCipher.init(3, (Key)this.wrappingKey, this.random);
            return keyEncryptionCipher.wrap(contentEncryptionKeySpec);
        }
        catch (GeneralSecurityException e) {
            throw new OperatorException("cannot wrap key: " + e.getMessage(), e);
        }
    }

    private static AlgorithmIdentifier determineKeyEncAlg(SecretKey key) {
        return JceSymmetricKeyWrapper.determineKeyEncAlg(key.getAlgorithm(), key.getEncoded().length * 8);
    }

    static AlgorithmIdentifier determineKeyEncAlg(String algorithm, int keySizeInBits) {
        if (algorithm.startsWith("DES") || algorithm.startsWith("TripleDES")) {
            return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, (ASN1Encodable)DERNull.INSTANCE);
        }
        if (algorithm.startsWith("RC2")) {
            return new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.3.7"), (ASN1Encodable)new ASN1Integer(58L));
        }
        if (algorithm.startsWith("AES") || algorithm.startsWith(NISTObjectIdentifiers.aes.getId())) {
            ASN1ObjectIdentifier wrapOid;
            if (keySizeInBits == 128) {
                wrapOid = NISTObjectIdentifiers.id_aes128_wrap;
            } else if (keySizeInBits == 192) {
                wrapOid = NISTObjectIdentifiers.id_aes192_wrap;
            } else if (keySizeInBits == 256) {
                wrapOid = NISTObjectIdentifiers.id_aes256_wrap;
            } else {
                throw new IllegalArgumentException("illegal keysize in AES");
            }
            return new AlgorithmIdentifier(wrapOid);
        }
        if (algorithm.startsWith("SEED")) {
            return new AlgorithmIdentifier(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap);
        }
        if (algorithm.startsWith("Camellia")) {
            ASN1ObjectIdentifier wrapOid;
            if (keySizeInBits == 128) {
                wrapOid = NTTObjectIdentifiers.id_camellia128_wrap;
            } else if (keySizeInBits == 192) {
                wrapOid = NTTObjectIdentifiers.id_camellia192_wrap;
            } else if (keySizeInBits == 256) {
                wrapOid = NTTObjectIdentifiers.id_camellia256_wrap;
            } else {
                throw new IllegalArgumentException("illegal keysize in Camellia");
            }
            return new AlgorithmIdentifier(wrapOid);
        }
        throw new IllegalArgumentException("unknown algorithm");
    }
}

