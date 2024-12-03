/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class EC5Util {
    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static ECCurve getCurve(ProviderConfiguration configuration, X962Parameters params) {
        Set acceptableCurves = configuration.getAcceptableNamedCurves();
        if (params.isNamedCurve()) {
            X9ECParameters ecP;
            ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(params.getParameters());
            if (!acceptableCurves.isEmpty()) {
                if (!acceptableCurves.contains(oid)) throw new IllegalStateException("named curve not acceptable");
            }
            if ((ecP = ECUtil.getNamedCurveByOid(oid)) != null) return ecP.getCurve();
            ecP = (X9ECParameters)configuration.getAdditionalECParameters().get(oid);
            return ecP.getCurve();
        }
        if (params.isImplicitlyCA()) {
            return configuration.getEcImplicitlyCa().getCurve();
        }
        ASN1Sequence pSeq = ASN1Sequence.getInstance(params.getParameters());
        if (!acceptableCurves.isEmpty()) throw new IllegalStateException("encoded parameters not acceptable");
        if (pSeq.size() > 3) {
            X9ECParameters ecP = X9ECParameters.getInstance(pSeq);
            return ecP.getCurve();
        }
        ASN1ObjectIdentifier gostCurve = ASN1ObjectIdentifier.getInstance(pSeq.getObjectAt(0));
        return ECGOST3410NamedCurves.getByOIDX9(gostCurve).getCurve();
    }

    public static ECDomainParameters getDomainParameters(ProviderConfiguration configuration, ECParameterSpec params) {
        ECDomainParameters domainParameters;
        if (params == null) {
            org.bouncycastle.jce.spec.ECParameterSpec iSpec = configuration.getEcImplicitlyCa();
            domainParameters = new ECDomainParameters(iSpec.getCurve(), iSpec.getG(), iSpec.getN(), iSpec.getH(), iSpec.getSeed());
        } else {
            domainParameters = ECUtil.getDomainParameters(configuration, EC5Util.convertSpec(params));
        }
        return domainParameters;
    }

    public static ECParameterSpec convertToSpec(X962Parameters params, ECCurve curve) {
        ECParameterSpec ecSpec;
        if (params.isNamedCurve()) {
            Map additionalECParameters;
            ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)params.getParameters();
            X9ECParameters ecP = ECUtil.getNamedCurveByOid(oid);
            if (ecP == null && !(additionalECParameters = BouncyCastleProvider.CONFIGURATION.getAdditionalECParameters()).isEmpty()) {
                ecP = (X9ECParameters)additionalECParameters.get(oid);
            }
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, ecP.getSeed());
            ecSpec = new ECNamedCurveSpec(ECUtil.getCurveName(oid), ellipticCurve, EC5Util.convertPoint(ecP.getG()), ecP.getN(), ecP.getH());
        } else if (params.isImplicitlyCA()) {
            ecSpec = null;
        } else {
            ASN1Sequence pSeq = ASN1Sequence.getInstance(params.getParameters());
            if (pSeq.size() > 3) {
                X9ECParameters ecP = X9ECParameters.getInstance(pSeq);
                EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, ecP.getSeed());
                ecSpec = ecP.getH() != null ? new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(ecP.getG()), ecP.getN(), ecP.getH().intValue()) : new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(ecP.getG()), ecP.getN(), 1);
            } else {
                GOST3410PublicKeyAlgParameters gostParams = GOST3410PublicKeyAlgParameters.getInstance(pSeq);
                ECNamedCurveParameterSpec spec = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(gostParams.getPublicKeyParamSet()));
                curve = spec.getCurve();
                EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getSeed());
                ecSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(gostParams.getPublicKeyParamSet()), ellipticCurve, EC5Util.convertPoint(spec.getG()), spec.getN(), spec.getH());
            }
        }
        return ecSpec;
    }

    public static ECParameterSpec convertToSpec(X9ECParameters domainParameters) {
        return new ECParameterSpec(EC5Util.convertCurve(domainParameters.getCurve(), null), EC5Util.convertPoint(domainParameters.getG()), domainParameters.getN(), domainParameters.getH().intValue());
    }

    public static ECParameterSpec convertToSpec(ECDomainParameters domainParameters) {
        return new ECParameterSpec(EC5Util.convertCurve(domainParameters.getCurve(), null), EC5Util.convertPoint(domainParameters.getG()), domainParameters.getN(), domainParameters.getH().intValue());
    }

    public static EllipticCurve convertCurve(ECCurve curve, byte[] seed) {
        ECField field = EC5Util.convertField(curve.getField());
        BigInteger a = curve.getA().toBigInteger();
        BigInteger b = curve.getB().toBigInteger();
        return new EllipticCurve(field, a, b, null);
    }

    public static ECCurve convertCurve(EllipticCurve ec) {
        ECField field = ec.getField();
        BigInteger a = ec.getA();
        BigInteger b = ec.getB();
        if (field instanceof ECFieldFp) {
            return CustomCurves.substitute(new ECCurve.Fp(((ECFieldFp)field).getP(), a, b, null, null));
        }
        ECFieldF2m fieldF2m = (ECFieldF2m)field;
        int m = fieldF2m.getM();
        int[] ks = ECUtil.convertMidTerms(fieldF2m.getMidTermsOfReductionPolynomial());
        return new ECCurve.F2m(m, ks[0], ks[1], ks[2], a, b, null, null);
    }

    public static ECField convertField(FiniteField field) {
        if (ECAlgorithms.isFpField(field)) {
            return new ECFieldFp(field.getCharacteristic());
        }
        Polynomial poly = ((PolynomialExtensionField)field).getMinimalPolynomial();
        int[] exponents = poly.getExponentsPresent();
        int[] ks = Arrays.reverseInPlace(Arrays.copyOfRange(exponents, 1, exponents.length - 1));
        return new ECFieldF2m(poly.getDegree(), ks);
    }

    public static ECParameterSpec convertSpec(EllipticCurve ellipticCurve, org.bouncycastle.jce.spec.ECParameterSpec spec) {
        ECPoint g = EC5Util.convertPoint(spec.getG());
        if (spec instanceof ECNamedCurveParameterSpec) {
            String name = ((ECNamedCurveParameterSpec)spec).getName();
            return new ECNamedCurveSpec(name, ellipticCurve, g, spec.getN(), spec.getH());
        }
        return new ECParameterSpec(ellipticCurve, g, spec.getN(), spec.getH().intValue());
    }

    public static org.bouncycastle.jce.spec.ECParameterSpec convertSpec(ECParameterSpec ecSpec) {
        ECCurve curve = EC5Util.convertCurve(ecSpec.getCurve());
        org.bouncycastle.math.ec.ECPoint g = EC5Util.convertPoint(curve, ecSpec.getGenerator());
        BigInteger n = ecSpec.getOrder();
        BigInteger h = BigInteger.valueOf(ecSpec.getCofactor());
        byte[] seed = ecSpec.getCurve().getSeed();
        if (ecSpec instanceof ECNamedCurveSpec) {
            return new ECNamedCurveParameterSpec(((ECNamedCurveSpec)ecSpec).getName(), curve, g, n, h, seed);
        }
        return new org.bouncycastle.jce.spec.ECParameterSpec(curve, g, n, h, seed);
    }

    public static org.bouncycastle.math.ec.ECPoint convertPoint(ECParameterSpec ecSpec, ECPoint point) {
        return EC5Util.convertPoint(EC5Util.convertCurve(ecSpec.getCurve()), point);
    }

    public static org.bouncycastle.math.ec.ECPoint convertPoint(ECCurve curve, ECPoint point) {
        return curve.createPoint(point.getAffineX(), point.getAffineY());
    }

    public static ECPoint convertPoint(org.bouncycastle.math.ec.ECPoint point) {
        point = point.normalize();
        return new ECPoint(point.getAffineXCoord().toBigInteger(), point.getAffineYCoord().toBigInteger());
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class CustomCurves {
        private static Map CURVE_MAP = CustomCurves.createCurveMap();

        private CustomCurves() {
        }

        private static Map createCurveMap() {
            HashMap<ECCurve, ECCurve> map = new HashMap<ECCurve, ECCurve>();
            Enumeration e = CustomNamedCurves.getNames();
            while (e.hasMoreElements()) {
                ECCurve curve;
                String name = (String)e.nextElement();
                X9ECParametersHolder curveParams = ECNamedCurveTable.getByNameLazy(name);
                if (curveParams == null || !ECAlgorithms.isFpCurve(curve = curveParams.getCurve())) continue;
                map.put(curve, CustomNamedCurves.getByNameLazy(name).getCurve());
            }
            ECCurve c_25519 = CustomNamedCurves.getByNameLazy("Curve25519").getCurve();
            map.put(new ECCurve.Fp(c_25519.getField().getCharacteristic(), c_25519.getA().toBigInteger(), c_25519.getB().toBigInteger(), c_25519.getOrder(), c_25519.getCofactor(), true), c_25519);
            return map;
        }

        static ECCurve substitute(ECCurve c) {
            ECCurve custom = (ECCurve)CURVE_MAP.get(c);
            return null != custom ? custom : c;
        }
    }
}

