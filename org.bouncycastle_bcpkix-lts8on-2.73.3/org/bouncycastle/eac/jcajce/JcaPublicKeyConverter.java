/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.eac.EACObjectIdentifiers
 *  org.bouncycastle.asn1.eac.ECDSAPublicKey
 *  org.bouncycastle.asn1.eac.PublicKeyDataObject
 *  org.bouncycastle.asn1.eac.RSAPublicKey
 *  org.bouncycastle.math.ec.ECAlgorithms
 *  org.bouncycastle.math.ec.ECCurve
 *  org.bouncycastle.math.ec.ECCurve$Fp
 *  org.bouncycastle.math.ec.ECPoint
 *  org.bouncycastle.math.ec.ECPoint$Fp
 *  org.bouncycastle.math.field.FiniteField
 *  org.bouncycastle.math.field.Polynomial
 *  org.bouncycastle.math.field.PolynomialExtensionField
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.eac.jcajce;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.eac.ECDSAPublicKey;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.asn1.eac.RSAPublicKey;
import org.bouncycastle.eac.EACException;
import org.bouncycastle.eac.jcajce.DefaultEACHelper;
import org.bouncycastle.eac.jcajce.EACHelper;
import org.bouncycastle.eac.jcajce.NamedEACHelper;
import org.bouncycastle.eac.jcajce.ProviderEACHelper;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

public class JcaPublicKeyConverter {
    private EACHelper helper = new DefaultEACHelper();

    public JcaPublicKeyConverter setProvider(String providerName) {
        this.helper = new NamedEACHelper(providerName);
        return this;
    }

    public JcaPublicKeyConverter setProvider(Provider provider) {
        this.helper = new ProviderEACHelper(provider);
        return this;
    }

    public PublicKey getKey(PublicKeyDataObject publicKeyDataObject) throws EACException, InvalidKeySpecException {
        if (publicKeyDataObject.getUsage().on(EACObjectIdentifiers.id_TA_ECDSA)) {
            return this.getECPublicKeyPublicKey((ECDSAPublicKey)publicKeyDataObject);
        }
        RSAPublicKey pubKey = (RSAPublicKey)publicKeyDataObject;
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(pubKey.getModulus(), pubKey.getPublicExponent());
        try {
            KeyFactory factk = this.helper.createKeyFactory("RSA");
            return factk.generatePublic(pubKeySpec);
        }
        catch (NoSuchProviderException e) {
            throw new EACException("cannot find provider: " + e.getMessage(), e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new EACException("cannot find algorithm ECDSA: " + e.getMessage(), e);
        }
    }

    private PublicKey getECPublicKeyPublicKey(ECDSAPublicKey key) throws EACException, InvalidKeySpecException {
        KeyFactory factk;
        ECParameterSpec spec = this.getParams(key);
        ECPoint publicPoint = this.getPublicPoint(key);
        ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(publicPoint, spec);
        try {
            factk = this.helper.createKeyFactory("ECDSA");
        }
        catch (NoSuchProviderException e) {
            throw new EACException("cannot find provider: " + e.getMessage(), e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new EACException("cannot find algorithm ECDSA: " + e.getMessage(), e);
        }
        return factk.generatePublic(pubKeySpec);
    }

    private ECPoint getPublicPoint(ECDSAPublicKey key) {
        if (!key.hasParameters()) {
            throw new IllegalArgumentException("Public key does not contains EC Params");
        }
        BigInteger p = key.getPrimeModulusP();
        ECCurve.Fp curve = new ECCurve.Fp(p, key.getFirstCoefA(), key.getSecondCoefB(), key.getOrderOfBasePointR(), key.getCofactorF());
        ECPoint.Fp pubY = (ECPoint.Fp)curve.decodePoint(key.getPublicPointY());
        return new ECPoint(pubY.getAffineXCoord().toBigInteger(), pubY.getAffineYCoord().toBigInteger());
    }

    private ECParameterSpec getParams(ECDSAPublicKey key) {
        if (!key.hasParameters()) {
            throw new IllegalArgumentException("Public key does not contains EC Params");
        }
        BigInteger p = key.getPrimeModulusP();
        ECCurve.Fp curve = new ECCurve.Fp(p, key.getFirstCoefA(), key.getSecondCoefB(), key.getOrderOfBasePointR(), key.getCofactorF());
        org.bouncycastle.math.ec.ECPoint G = curve.decodePoint(key.getBasePointG());
        BigInteger order = key.getOrderOfBasePointR();
        BigInteger coFactor = key.getCofactorF();
        EllipticCurve jcaCurve = JcaPublicKeyConverter.convertCurve((ECCurve)curve);
        return new ECParameterSpec(jcaCurve, new ECPoint(G.getAffineXCoord().toBigInteger(), G.getAffineYCoord().toBigInteger()), order, coFactor.intValue());
    }

    public PublicKeyDataObject getPublicKeyDataObject(ASN1ObjectIdentifier usage, PublicKey publicKey) {
        if (publicKey instanceof java.security.interfaces.RSAPublicKey) {
            java.security.interfaces.RSAPublicKey pubKey = (java.security.interfaces.RSAPublicKey)publicKey;
            return new RSAPublicKey(usage, pubKey.getModulus(), pubKey.getPublicExponent());
        }
        ECPublicKey pubKey = (ECPublicKey)publicKey;
        ECParameterSpec params = pubKey.getParams();
        EllipticCurve c1 = params.getCurve();
        ECCurve c2 = JcaPublicKeyConverter.convertCurve(c1, params.getOrder(), params.getCofactor());
        org.bouncycastle.math.ec.ECPoint basePoint = JcaPublicKeyConverter.convertPoint(c2, params.getGenerator());
        org.bouncycastle.math.ec.ECPoint publicPoint = JcaPublicKeyConverter.convertPoint(c2, pubKey.getW());
        return new ECDSAPublicKey(usage, ((ECFieldFp)c1.getField()).getP(), c1.getA(), c1.getB(), basePoint.getEncoded(false), params.getOrder(), publicPoint.getEncoded(false), params.getCofactor());
    }

    private static org.bouncycastle.math.ec.ECPoint convertPoint(ECCurve curve, ECPoint point) {
        return curve.createPoint(point.getAffineX(), point.getAffineY());
    }

    private static ECCurve convertCurve(EllipticCurve ec, BigInteger order, int coFactor) {
        ECField field = ec.getField();
        BigInteger a = ec.getA();
        BigInteger b = ec.getB();
        if (field instanceof ECFieldFp) {
            return new ECCurve.Fp(((ECFieldFp)field).getP(), a, b, order, BigInteger.valueOf(coFactor));
        }
        throw new IllegalStateException("not implemented yet!!!");
    }

    private static EllipticCurve convertCurve(ECCurve curve) {
        ECField field = JcaPublicKeyConverter.convertField(curve.getField());
        BigInteger a = curve.getA().toBigInteger();
        BigInteger b = curve.getB().toBigInteger();
        return new EllipticCurve(field, a, b, null);
    }

    private static ECField convertField(FiniteField field) {
        if (ECAlgorithms.isFpField((FiniteField)field)) {
            return new ECFieldFp(field.getCharacteristic());
        }
        Polynomial poly = ((PolynomialExtensionField)field).getMinimalPolynomial();
        int[] exponents = poly.getExponentsPresent();
        int[] ks = Arrays.reverseInPlace((int[])Arrays.copyOfRange((int[])exponents, (int)1, (int)(exponents.length - 1)));
        return new ECFieldF2m(poly.getDegree(), ks);
    }
}

