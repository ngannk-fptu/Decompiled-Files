/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.util.Arrays;

public class PrivateKeyFactory {
    public static AsymmetricKeyParameter createKey(byte[] privateKeyInfoData) throws IOException {
        if (privateKeyInfoData == null) {
            throw new IllegalArgumentException("privateKeyInfoData array null");
        }
        if (privateKeyInfoData.length == 0) {
            throw new IllegalArgumentException("privateKeyInfoData array empty");
        }
        return PrivateKeyFactory.createKey(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(privateKeyInfoData)));
    }

    public static AsymmetricKeyParameter createKey(InputStream inStr) throws IOException {
        return PrivateKeyFactory.createKey(PrivateKeyInfo.getInstance(new ASN1InputStream(inStr).readObject()));
    }

    public static AsymmetricKeyParameter createKey(PrivateKeyInfo keyInfo) throws IOException {
        if (keyInfo == null) {
            throw new IllegalArgumentException("keyInfo argument null");
        }
        AlgorithmIdentifier algId = keyInfo.getPrivateKeyAlgorithm();
        ASN1ObjectIdentifier algOID = algId.getAlgorithm();
        if (algOID.equals(PKCSObjectIdentifiers.rsaEncryption) || algOID.equals(PKCSObjectIdentifiers.id_RSASSA_PSS) || algOID.equals(X509ObjectIdentifiers.id_ea_rsa)) {
            RSAPrivateKey keyStructure = RSAPrivateKey.getInstance(keyInfo.parsePrivateKey());
            return new RSAPrivateCrtKeyParameters(keyStructure.getModulus(), keyStructure.getPublicExponent(), keyStructure.getPrivateExponent(), keyStructure.getPrime1(), keyStructure.getPrime2(), keyStructure.getExponent1(), keyStructure.getExponent2(), keyStructure.getCoefficient());
        }
        if (algOID.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
            DHParameter params = DHParameter.getInstance(algId.getParameters());
            ASN1Integer derX = (ASN1Integer)keyInfo.parsePrivateKey();
            BigInteger lVal = params.getL();
            int l = lVal == null ? 0 : lVal.intValue();
            DHParameters dhParams = new DHParameters(params.getP(), params.getG(), null, l);
            return new DHPrivateKeyParameters(derX.getValue(), dhParams);
        }
        if (algOID.equals(OIWObjectIdentifiers.elGamalAlgorithm)) {
            ElGamalParameter params = ElGamalParameter.getInstance(algId.getParameters());
            ASN1Integer derX = (ASN1Integer)keyInfo.parsePrivateKey();
            return new ElGamalPrivateKeyParameters(derX.getValue(), new ElGamalParameters(params.getP(), params.getG()));
        }
        if (algOID.equals(X9ObjectIdentifiers.id_dsa)) {
            ASN1Integer derX = (ASN1Integer)keyInfo.parsePrivateKey();
            ASN1Encodable algParameters = algId.getParameters();
            DSAParameters parameters = null;
            if (algParameters != null) {
                DSAParameter params = DSAParameter.getInstance(algParameters.toASN1Primitive());
                parameters = new DSAParameters(params.getP(), params.getQ(), params.getG());
            }
            return new DSAPrivateKeyParameters(derX.getValue(), parameters);
        }
        if (algOID.equals(X9ObjectIdentifiers.id_ecPublicKey)) {
            ECDomainParameters dParams;
            X962Parameters params = X962Parameters.getInstance(algId.getParameters());
            if (params.isNamedCurve()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)params.getParameters();
                X9ECParameters x9 = CustomNamedCurves.getByOID(oid);
                if (x9 == null) {
                    x9 = ECNamedCurveTable.getByOID(oid);
                }
                dParams = new ECNamedDomainParameters(oid, x9);
            } else {
                X9ECParameters x9 = X9ECParameters.getInstance(params.getParameters());
                dParams = new ECDomainParameters(x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
            }
            ECPrivateKey ec = ECPrivateKey.getInstance(keyInfo.parsePrivateKey());
            BigInteger d = ec.getKey();
            return new ECPrivateKeyParameters(d, dParams);
        }
        if (algOID.equals(EdECObjectIdentifiers.id_X25519)) {
            if (32 == keyInfo.getPrivateKeyLength()) {
                return new X25519PrivateKeyParameters(keyInfo.getPrivateKey().getOctets());
            }
            return new X25519PrivateKeyParameters(PrivateKeyFactory.getRawKey(keyInfo));
        }
        if (algOID.equals(EdECObjectIdentifiers.id_X448)) {
            if (56 == keyInfo.getPrivateKeyLength()) {
                return new X448PrivateKeyParameters(keyInfo.getPrivateKey().getOctets());
            }
            return new X448PrivateKeyParameters(PrivateKeyFactory.getRawKey(keyInfo));
        }
        if (algOID.equals(EdECObjectIdentifiers.id_Ed25519)) {
            return new Ed25519PrivateKeyParameters(PrivateKeyFactory.getRawKey(keyInfo));
        }
        if (algOID.equals(EdECObjectIdentifiers.id_Ed448)) {
            return new Ed448PrivateKeyParameters(PrivateKeyFactory.getRawKey(keyInfo));
        }
        if (algOID.equals(CryptoProObjectIdentifiers.gostR3410_2001) || algOID.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512) || algOID.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256)) {
            ASN1Encodable algParameters = algId.getParameters();
            GOST3410PublicKeyAlgParameters gostParams = GOST3410PublicKeyAlgParameters.getInstance(algParameters);
            ECGOST3410Parameters ecSpec = null;
            BigInteger d = null;
            ASN1Primitive p = algParameters.toASN1Primitive();
            if (p instanceof ASN1Sequence && (ASN1Sequence.getInstance(p).size() == 2 || ASN1Sequence.getInstance(p).size() == 3)) {
                X9ECParameters ecP = ECGOST3410NamedCurves.getByOIDX9(gostParams.getPublicKeyParamSet());
                ecSpec = new ECGOST3410Parameters(new ECNamedDomainParameters(gostParams.getPublicKeyParamSet(), ecP), gostParams.getPublicKeyParamSet(), gostParams.getDigestParamSet(), gostParams.getEncryptionParamSet());
                int privateKeyLength = keyInfo.getPrivateKeyLength();
                if (privateKeyLength == 32 || privateKeyLength == 64) {
                    d = new BigInteger(1, Arrays.reverse(keyInfo.getPrivateKey().getOctets()));
                } else {
                    ASN1Encodable privKey = keyInfo.parsePrivateKey();
                    if (privKey instanceof ASN1Integer) {
                        d = ASN1Integer.getInstance(privKey).getPositiveValue();
                    } else {
                        byte[] dVal = Arrays.reverse(ASN1OctetString.getInstance(privKey).getOctets());
                        d = new BigInteger(1, dVal);
                    }
                }
            } else {
                X962Parameters params = X962Parameters.getInstance(algId.getParameters());
                if (params.isNamedCurve()) {
                    ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(params.getParameters());
                    X9ECParameters ecP = ECNamedCurveTable.getByOID(oid);
                    ecSpec = new ECGOST3410Parameters(new ECNamedDomainParameters(oid, ecP), gostParams.getPublicKeyParamSet(), gostParams.getDigestParamSet(), gostParams.getEncryptionParamSet());
                } else if (params.isImplicitlyCA()) {
                    ecSpec = null;
                } else {
                    X9ECParameters ecP = X9ECParameters.getInstance(params.getParameters());
                    ecSpec = new ECGOST3410Parameters(new ECNamedDomainParameters(algOID, ecP), gostParams.getPublicKeyParamSet(), gostParams.getDigestParamSet(), gostParams.getEncryptionParamSet());
                }
                ASN1Encodable privKey = keyInfo.parsePrivateKey();
                if (privKey instanceof ASN1Integer) {
                    ASN1Integer derD = ASN1Integer.getInstance(privKey);
                    d = derD.getValue();
                } else {
                    ECPrivateKey ec = ECPrivateKey.getInstance(privKey);
                    d = ec.getKey();
                }
            }
            return new ECPrivateKeyParameters(d, (ECDomainParameters)new ECGOST3410Parameters(ecSpec, gostParams.getPublicKeyParamSet(), gostParams.getDigestParamSet(), gostParams.getEncryptionParamSet()));
        }
        throw new RuntimeException("algorithm identifier in private key not recognised");
    }

    private static byte[] getRawKey(PrivateKeyInfo keyInfo) throws IOException {
        return ASN1OctetString.getInstance(keyInfo.parsePrivateKey()).getOctets();
    }
}

