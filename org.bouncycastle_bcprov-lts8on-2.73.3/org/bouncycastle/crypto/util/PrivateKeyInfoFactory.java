/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class PrivateKeyInfoFactory {
    private static Set cryptoProOids = new HashSet(5);

    private PrivateKeyInfoFactory() {
    }

    public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter privateKey) throws IOException {
        return PrivateKeyInfoFactory.createPrivateKeyInfo(privateKey, null);
    }

    public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter privateKey, ASN1Set attributes) throws IOException {
        if (privateKey instanceof RSAKeyParameters) {
            RSAPrivateCrtKeyParameters priv = (RSAPrivateCrtKeyParameters)privateKey;
            return new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), new RSAPrivateKey(priv.getModulus(), priv.getPublicExponent(), priv.getExponent(), priv.getP(), priv.getQ(), priv.getDP(), priv.getDQ(), priv.getQInv()), attributes);
        }
        if (privateKey instanceof DSAPrivateKeyParameters) {
            DSAPrivateKeyParameters priv = (DSAPrivateKeyParameters)privateKey;
            DSAParameters params = priv.getParameters();
            return new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(params.getP(), params.getQ(), params.getG())), new ASN1Integer(priv.getX()), attributes);
        }
        if (privateKey instanceof ECPrivateKeyParameters) {
            int orderBitLength;
            X962Parameters params;
            ECPrivateKeyParameters priv = (ECPrivateKeyParameters)privateKey;
            ECDomainParameters domainParams = priv.getParameters();
            if (domainParams == null) {
                params = new X962Parameters(DERNull.INSTANCE);
                orderBitLength = priv.getD().bitLength();
            } else {
                if (domainParams instanceof ECGOST3410Parameters) {
                    ASN1ObjectIdentifier identifier;
                    int size;
                    GOST3410PublicKeyAlgParameters gostParams = new GOST3410PublicKeyAlgParameters(((ECGOST3410Parameters)domainParams).getPublicKeyParamSet(), ((ECGOST3410Parameters)domainParams).getDigestParamSet(), ((ECGOST3410Parameters)domainParams).getEncryptionParamSet());
                    if (cryptoProOids.contains(gostParams.getPublicKeyParamSet())) {
                        size = 32;
                        identifier = CryptoProObjectIdentifiers.gostR3410_2001;
                    } else {
                        boolean is512 = priv.getD().bitLength() > 256;
                        identifier = is512 ? RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512 : RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
                        size = is512 ? 64 : 32;
                    }
                    byte[] encKey = new byte[size];
                    PrivateKeyInfoFactory.extractBytes(encKey, size, 0, priv.getD());
                    return new PrivateKeyInfo(new AlgorithmIdentifier(identifier, gostParams), new DEROctetString(encKey));
                }
                if (domainParams instanceof ECNamedDomainParameters) {
                    params = new X962Parameters(((ECNamedDomainParameters)domainParams).getName());
                    orderBitLength = domainParams.getN().bitLength();
                } else {
                    X9ECParameters ecP = new X9ECParameters(domainParams.getCurve(), new X9ECPoint(domainParams.getG(), false), domainParams.getN(), domainParams.getH(), domainParams.getSeed());
                    params = new X962Parameters(ecP);
                    orderBitLength = domainParams.getN().bitLength();
                }
            }
            ECPoint q = new FixedPointCombMultiplier().multiply(domainParams.getG(), priv.getD());
            DERBitString publicKey = new DERBitString(q.getEncoded(false));
            return new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params), new ECPrivateKey(orderBitLength, priv.getD(), publicKey, params), attributes);
        }
        if (privateKey instanceof X448PrivateKeyParameters) {
            X448PrivateKeyParameters key = (X448PrivateKeyParameters)privateKey;
            return new PrivateKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_X448), new DEROctetString(key.getEncoded()), attributes, key.generatePublicKey().getEncoded());
        }
        if (privateKey instanceof X25519PrivateKeyParameters) {
            X25519PrivateKeyParameters key = (X25519PrivateKeyParameters)privateKey;
            return new PrivateKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_X25519), new DEROctetString(key.getEncoded()), attributes, key.generatePublicKey().getEncoded());
        }
        if (privateKey instanceof Ed448PrivateKeyParameters) {
            Ed448PrivateKeyParameters key = (Ed448PrivateKeyParameters)privateKey;
            return new PrivateKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed448), new DEROctetString(key.getEncoded()), attributes, key.generatePublicKey().getEncoded());
        }
        if (privateKey instanceof Ed25519PrivateKeyParameters) {
            Ed25519PrivateKeyParameters key = (Ed25519PrivateKeyParameters)privateKey;
            return new PrivateKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519), new DEROctetString(key.getEncoded()), attributes, key.generatePublicKey().getEncoded());
        }
        throw new IOException("key parameters not recognized");
    }

    private static void extractBytes(byte[] encKey, int size, int offSet, BigInteger bI) {
        byte[] val = bI.toByteArray();
        if (val.length < size) {
            byte[] tmp = new byte[size];
            System.arraycopy(val, 0, tmp, tmp.length - val.length, val.length);
            val = tmp;
        }
        for (int i = 0; i != size; ++i) {
            encKey[offSet + i] = val[val.length - 1 - i];
        }
    }

    static {
        cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_A);
        cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_B);
        cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_C);
        cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchA);
        cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchB);
    }
}

