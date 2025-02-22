/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ECKeyUtil {
    public static PublicKey publicToExplicitParameters(PublicKey key, String providerName) throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        Provider provider = Security.getProvider(providerName);
        if (provider == null) {
            throw new NoSuchProviderException("cannot find provider: " + providerName);
        }
        return ECKeyUtil.publicToExplicitParameters(key, provider);
    }

    public static PublicKey publicToExplicitParameters(PublicKey key, Provider provider) throws IllegalArgumentException, NoSuchAlgorithmException {
        try {
            X9ECParameters curveParams;
            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(key.getEncoded()));
            if (info.getAlgorithm().getAlgorithm().equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
                throw new IllegalArgumentException("cannot convert GOST key to explicit parameters.");
            }
            X962Parameters params = X962Parameters.getInstance(info.getAlgorithm().getParameters());
            if (params.isNamedCurve()) {
                ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(params.getParameters());
                curveParams = ECUtil.getNamedCurveByOid(oid);
                if (curveParams.hasSeed()) {
                    curveParams = new X9ECParameters(curveParams.getCurve(), curveParams.getBaseEntry(), curveParams.getN(), curveParams.getH());
                }
            } else if (params.isImplicitlyCA()) {
                curveParams = new X9ECParameters(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve(), new X9ECPoint(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getG(), false), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getN(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getH());
            } else {
                return key;
            }
            params = new X962Parameters(curveParams);
            info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params), info.getPublicKeyData().getBytes());
            KeyFactory keyFact = KeyFactory.getInstance(key.getAlgorithm(), provider);
            return keyFact.generatePublic(new X509EncodedKeySpec(info.getEncoded()));
        }
        catch (IllegalArgumentException e) {
            throw e;
        }
        catch (NoSuchAlgorithmException e) {
            throw e;
        }
        catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }

    public static PrivateKey privateToExplicitParameters(PrivateKey key, String providerName) throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        Provider provider = Security.getProvider(providerName);
        if (provider == null) {
            throw new NoSuchProviderException("cannot find provider: " + providerName);
        }
        return ECKeyUtil.privateToExplicitParameters(key, provider);
    }

    public static PrivateKey privateToExplicitParameters(PrivateKey key, Provider provider) throws IllegalArgumentException, NoSuchAlgorithmException {
        try {
            X9ECParameters curveParams;
            PrivateKeyInfo info = PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(key.getEncoded()));
            if (info.getPrivateKeyAlgorithm().getAlgorithm().equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
                throw new UnsupportedEncodingException("cannot convert GOST key to explicit parameters.");
            }
            X962Parameters params = X962Parameters.getInstance(info.getPrivateKeyAlgorithm().getParameters());
            if (params.isNamedCurve()) {
                ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(params.getParameters());
                curveParams = ECUtil.getNamedCurveByOid(oid);
                if (curveParams.hasSeed()) {
                    curveParams = new X9ECParameters(curveParams.getCurve(), curveParams.getBaseEntry(), curveParams.getN(), curveParams.getH());
                }
            } else if (params.isImplicitlyCA()) {
                curveParams = new X9ECParameters(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve(), new X9ECPoint(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getG(), false), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getN(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getH());
            } else {
                return key;
            }
            params = new X962Parameters(curveParams);
            info = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params), info.parsePrivateKey());
            KeyFactory keyFact = KeyFactory.getInstance(key.getAlgorithm(), provider);
            return keyFact.generatePrivate(new PKCS8EncodedKeySpec(info.getEncoded()));
        }
        catch (IllegalArgumentException e) {
            throw e;
        }
        catch (NoSuchAlgorithmException e) {
            throw e;
        }
        catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }

    private static class UnexpectedException
    extends RuntimeException {
        private Throwable cause;

        UnexpectedException(Throwable cause) {
            super(cause.toString());
            this.cause = cause;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}

