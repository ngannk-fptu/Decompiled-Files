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
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;

public class SubjectPublicKeyInfoFactory {
    private static Set cryptoProOids = new HashSet(5);

    private SubjectPublicKeyInfoFactory() {
    }

    public static SubjectPublicKeyInfo createSubjectPublicKeyInfo(AsymmetricKeyParameter publicKey) throws IOException {
        if (publicKey instanceof RSAKeyParameters) {
            RSAKeyParameters pub = (RSAKeyParameters)publicKey;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), new RSAPublicKey(pub.getModulus(), pub.getExponent()));
        }
        if (publicKey instanceof DSAPublicKeyParameters) {
            DSAPublicKeyParameters pub = (DSAPublicKeyParameters)publicKey;
            DSAParameter params = null;
            DSAParameters dsaParams = pub.getParameters();
            if (dsaParams != null) {
                params = new DSAParameter(dsaParams.getP(), dsaParams.getQ(), dsaParams.getG());
            }
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, params), new ASN1Integer(pub.getY()));
        }
        if (publicKey instanceof ECPublicKeyParameters) {
            X962Parameters params;
            ECPublicKeyParameters pub = (ECPublicKeyParameters)publicKey;
            ECDomainParameters domainParams = pub.getParameters();
            if (domainParams == null) {
                params = new X962Parameters(DERNull.INSTANCE);
            } else {
                if (domainParams instanceof ECGOST3410Parameters) {
                    ASN1ObjectIdentifier algIdentifier;
                    int offset;
                    int encKeySize;
                    ECGOST3410Parameters gostParams = (ECGOST3410Parameters)domainParams;
                    BigInteger bX = pub.getQ().getAffineXCoord().toBigInteger();
                    BigInteger bY = pub.getQ().getAffineYCoord().toBigInteger();
                    GOST3410PublicKeyAlgParameters params2 = new GOST3410PublicKeyAlgParameters(gostParams.getPublicKeyParamSet(), gostParams.getDigestParamSet());
                    if (cryptoProOids.contains(gostParams.getPublicKeyParamSet())) {
                        encKeySize = 64;
                        offset = 32;
                        algIdentifier = CryptoProObjectIdentifiers.gostR3410_2001;
                    } else {
                        boolean is512;
                        boolean bl = is512 = bX.bitLength() > 256;
                        if (is512) {
                            encKeySize = 128;
                            offset = 64;
                            algIdentifier = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512;
                        } else {
                            encKeySize = 64;
                            offset = 32;
                            algIdentifier = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
                        }
                    }
                    byte[] encKey = new byte[encKeySize];
                    SubjectPublicKeyInfoFactory.extractBytes(encKey, encKeySize / 2, 0, bX);
                    SubjectPublicKeyInfoFactory.extractBytes(encKey, encKeySize / 2, offset, bY);
                    try {
                        return new SubjectPublicKeyInfo(new AlgorithmIdentifier(algIdentifier, params2), new DEROctetString(encKey));
                    }
                    catch (IOException e) {
                        return null;
                    }
                }
                if (domainParams instanceof ECNamedDomainParameters) {
                    params = new X962Parameters(((ECNamedDomainParameters)domainParams).getName());
                } else {
                    X9ECParameters ecP = new X9ECParameters(domainParams.getCurve(), new X9ECPoint(domainParams.getG(), false), domainParams.getN(), domainParams.getH(), domainParams.getSeed());
                    params = new X962Parameters(ecP);
                }
            }
            byte[] pubKeyOctets = pub.getQ().getEncoded(false);
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params), pubKeyOctets);
        }
        if (publicKey instanceof X448PublicKeyParameters) {
            X448PublicKeyParameters key = (X448PublicKeyParameters)publicKey;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_X448), key.getEncoded());
        }
        if (publicKey instanceof X25519PublicKeyParameters) {
            X25519PublicKeyParameters key = (X25519PublicKeyParameters)publicKey;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_X25519), key.getEncoded());
        }
        if (publicKey instanceof Ed448PublicKeyParameters) {
            Ed448PublicKeyParameters key = (Ed448PublicKeyParameters)publicKey;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed448), key.getEncoded());
        }
        if (publicKey instanceof Ed25519PublicKeyParameters) {
            Ed25519PublicKeyParameters key = (Ed25519PublicKeyParameters)publicKey;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519), key.getEncoded());
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

