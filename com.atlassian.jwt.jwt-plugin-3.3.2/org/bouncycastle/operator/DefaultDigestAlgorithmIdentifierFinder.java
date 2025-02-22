/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class DefaultDigestAlgorithmIdentifierFinder
implements DigestAlgorithmIdentifierFinder {
    private static Map digestOids = new HashMap();
    private static Map digestNameToOids = new HashMap();
    private static Map digestOidToAlgIds = new HashMap();

    private static void addDigestAlgId(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl) {
        AlgorithmIdentifier algorithmIdentifier = bl ? new AlgorithmIdentifier(aSN1ObjectIdentifier, DERNull.INSTANCE) : new AlgorithmIdentifier(aSN1ObjectIdentifier);
        digestOidToAlgIds.put(aSN1ObjectIdentifier, algorithmIdentifier);
    }

    public AlgorithmIdentifier find(AlgorithmIdentifier algorithmIdentifier) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = algorithmIdentifier.getAlgorithm();
        if (aSN1ObjectIdentifier.equals(EdECObjectIdentifiers.id_Ed448)) {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake256_len, new ASN1Integer(512L));
        }
        ASN1ObjectIdentifier aSN1ObjectIdentifier2 = aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_RSASSA_PSS) ? RSASSAPSSparams.getInstance(algorithmIdentifier.getParameters()).getHashAlgorithm().getAlgorithm() : (aSN1ObjectIdentifier.equals(EdECObjectIdentifiers.id_Ed25519) ? NISTObjectIdentifiers.id_sha512 : (ASN1ObjectIdentifier)digestOids.get(algorithmIdentifier.getAlgorithm()));
        return this.find(aSN1ObjectIdentifier2);
    }

    public AlgorithmIdentifier find(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier == null) {
            throw new NullPointerException("digest OID is null");
        }
        AlgorithmIdentifier algorithmIdentifier = (AlgorithmIdentifier)digestOidToAlgIds.get(aSN1ObjectIdentifier);
        if (algorithmIdentifier == null) {
            return new AlgorithmIdentifier(aSN1ObjectIdentifier);
        }
        return algorithmIdentifier;
    }

    public AlgorithmIdentifier find(String string) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)digestNameToOids.get(string);
        if (aSN1ObjectIdentifier != null) {
            return this.find(aSN1ObjectIdentifier);
        }
        try {
            return this.find(new ASN1ObjectIdentifier(string));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    static {
        digestOids.put(OIWObjectIdentifiers.dsaWithSHA1, OIWObjectIdentifiers.idSHA1);
        digestOids.put(OIWObjectIdentifiers.md4WithRSAEncryption, PKCSObjectIdentifiers.md4);
        digestOids.put(OIWObjectIdentifiers.md4WithRSA, PKCSObjectIdentifiers.md4);
        digestOids.put(OIWObjectIdentifiers.sha1WithRSA, OIWObjectIdentifiers.idSHA1);
        digestOids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, NISTObjectIdentifiers.id_sha224);
        digestOids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, NISTObjectIdentifiers.id_sha256);
        digestOids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, NISTObjectIdentifiers.id_sha384);
        digestOids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, NISTObjectIdentifiers.id_sha512);
        digestOids.put(PKCSObjectIdentifiers.sha512_224WithRSAEncryption, NISTObjectIdentifiers.id_sha512_224);
        digestOids.put(PKCSObjectIdentifiers.sha512_256WithRSAEncryption, NISTObjectIdentifiers.id_sha512_256);
        digestOids.put(PKCSObjectIdentifiers.md2WithRSAEncryption, PKCSObjectIdentifiers.md2);
        digestOids.put(PKCSObjectIdentifiers.md4WithRSAEncryption, PKCSObjectIdentifiers.md4);
        digestOids.put(PKCSObjectIdentifiers.md5WithRSAEncryption, PKCSObjectIdentifiers.md5);
        digestOids.put(PKCSObjectIdentifiers.sha1WithRSAEncryption, OIWObjectIdentifiers.idSHA1);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA1, OIWObjectIdentifiers.idSHA1);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA224, NISTObjectIdentifiers.id_sha224);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA384, NISTObjectIdentifiers.id_sha384);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(X9ObjectIdentifiers.id_dsa_with_sha1, OIWObjectIdentifiers.idSHA1);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA1, OIWObjectIdentifiers.idSHA1);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA224, NISTObjectIdentifiers.id_sha224);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA384, NISTObjectIdentifiers.id_sha384);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, TeleTrusTObjectIdentifiers.ripemd160);
        digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, OIWObjectIdentifiers.idSHA1);
        digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, NISTObjectIdentifiers.id_sha224);
        digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, NISTObjectIdentifiers.id_sha384);
        digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.dsa_with_sha224, NISTObjectIdentifiers.id_sha224);
        digestOids.put(NISTObjectIdentifiers.dsa_with_sha256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(NISTObjectIdentifiers.dsa_with_sha384, NISTObjectIdentifiers.id_sha384);
        digestOids.put(NISTObjectIdentifiers.dsa_with_sha512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, NISTObjectIdentifiers.id_sha3_224);
        digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, NISTObjectIdentifiers.id_sha3_256);
        digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, NISTObjectIdentifiers.id_sha3_384);
        digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, NISTObjectIdentifiers.id_sha3_512);
        digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_224, NISTObjectIdentifiers.id_sha3_224);
        digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_256, NISTObjectIdentifiers.id_sha3_256);
        digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_384, NISTObjectIdentifiers.id_sha3_384);
        digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_512, NISTObjectIdentifiers.id_sha3_512);
        digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_224, NISTObjectIdentifiers.id_sha3_224);
        digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_256, NISTObjectIdentifiers.id_sha3_256);
        digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_384, NISTObjectIdentifiers.id_sha3_384);
        digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_512, NISTObjectIdentifiers.id_sha3_512);
        digestOids.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128, TeleTrusTObjectIdentifiers.ripemd128);
        digestOids.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160, TeleTrusTObjectIdentifiers.ripemd160);
        digestOids.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256, TeleTrusTObjectIdentifiers.ripemd256);
        digestOids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, CryptoProObjectIdentifiers.gostR3411);
        digestOids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, CryptoProObjectIdentifiers.gostR3411);
        digestOids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256);
        digestOids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512);
        digestOids.put(BCObjectIdentifiers.sphincs256_with_SHA3_512, NISTObjectIdentifiers.id_sha3_512);
        digestOids.put(BCObjectIdentifiers.sphincs256_with_SHA512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(GMObjectIdentifiers.sm2sign_with_sha256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(GMObjectIdentifiers.sm2sign_with_sm3, GMObjectIdentifiers.sm3);
        digestOids.put(CMSObjectIdentifiers.id_RSASSA_PSS_SHAKE128, NISTObjectIdentifiers.id_shake128);
        digestOids.put(CMSObjectIdentifiers.id_RSASSA_PSS_SHAKE256, NISTObjectIdentifiers.id_shake256);
        digestOids.put(CMSObjectIdentifiers.id_ecdsa_with_shake128, NISTObjectIdentifiers.id_shake128);
        digestOids.put(CMSObjectIdentifiers.id_ecdsa_with_shake256, NISTObjectIdentifiers.id_shake256);
        digestNameToOids.put("SHA-1", OIWObjectIdentifiers.idSHA1);
        digestNameToOids.put("SHA-224", NISTObjectIdentifiers.id_sha224);
        digestNameToOids.put("SHA-256", NISTObjectIdentifiers.id_sha256);
        digestNameToOids.put("SHA-384", NISTObjectIdentifiers.id_sha384);
        digestNameToOids.put("SHA-512", NISTObjectIdentifiers.id_sha512);
        digestNameToOids.put("SHA-512-224", NISTObjectIdentifiers.id_sha512_224);
        digestNameToOids.put("SHA-512-256", NISTObjectIdentifiers.id_sha512_256);
        digestNameToOids.put("SHA1", OIWObjectIdentifiers.idSHA1);
        digestNameToOids.put("SHA224", NISTObjectIdentifiers.id_sha224);
        digestNameToOids.put("SHA256", NISTObjectIdentifiers.id_sha256);
        digestNameToOids.put("SHA384", NISTObjectIdentifiers.id_sha384);
        digestNameToOids.put("SHA512", NISTObjectIdentifiers.id_sha512);
        digestNameToOids.put("SHA512-224", NISTObjectIdentifiers.id_sha512_224);
        digestNameToOids.put("SHA512-256", NISTObjectIdentifiers.id_sha512_256);
        digestNameToOids.put("SHA3-224", NISTObjectIdentifiers.id_sha3_224);
        digestNameToOids.put("SHA3-256", NISTObjectIdentifiers.id_sha3_256);
        digestNameToOids.put("SHA3-384", NISTObjectIdentifiers.id_sha3_384);
        digestNameToOids.put("SHA3-512", NISTObjectIdentifiers.id_sha3_512);
        digestNameToOids.put("SHAKE128", NISTObjectIdentifiers.id_shake128);
        digestNameToOids.put("SHAKE256", NISTObjectIdentifiers.id_shake256);
        digestNameToOids.put("SHAKE-128", NISTObjectIdentifiers.id_shake128);
        digestNameToOids.put("SHAKE-256", NISTObjectIdentifiers.id_shake256);
        digestNameToOids.put("GOST3411", CryptoProObjectIdentifiers.gostR3411);
        digestNameToOids.put("GOST3411-2012-256", RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256);
        digestNameToOids.put("GOST3411-2012-512", RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512);
        digestNameToOids.put("MD2", PKCSObjectIdentifiers.md2);
        digestNameToOids.put("MD4", PKCSObjectIdentifiers.md4);
        digestNameToOids.put("MD5", PKCSObjectIdentifiers.md5);
        digestNameToOids.put("RIPEMD128", TeleTrusTObjectIdentifiers.ripemd128);
        digestNameToOids.put("RIPEMD160", TeleTrusTObjectIdentifiers.ripemd160);
        digestNameToOids.put("RIPEMD256", TeleTrusTObjectIdentifiers.ripemd256);
        digestNameToOids.put("SM3", GMObjectIdentifiers.sm3);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(OIWObjectIdentifiers.idSHA1, true);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(NISTObjectIdentifiers.id_sha224, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(NISTObjectIdentifiers.id_sha256, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(NISTObjectIdentifiers.id_sha384, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(NISTObjectIdentifiers.id_sha512, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(NISTObjectIdentifiers.id_sha512_224, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(NISTObjectIdentifiers.id_sha512_256, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(NISTObjectIdentifiers.id_sha3_224, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(NISTObjectIdentifiers.id_sha3_256, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(NISTObjectIdentifiers.id_sha3_384, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(NISTObjectIdentifiers.id_sha3_512, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(NISTObjectIdentifiers.id_shake128, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(NISTObjectIdentifiers.id_shake256, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(CryptoProObjectIdentifiers.gostR3411, true);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, false);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(PKCSObjectIdentifiers.md2, true);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(PKCSObjectIdentifiers.md4, true);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(PKCSObjectIdentifiers.md5, true);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(TeleTrusTObjectIdentifiers.ripemd128, true);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(TeleTrusTObjectIdentifiers.ripemd160, true);
        DefaultDigestAlgorithmIdentifierFinder.addDigestAlgId(TeleTrusTObjectIdentifiers.ripemd256, true);
    }
}

