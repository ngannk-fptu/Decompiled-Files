/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;

class ECUtils {
    ECUtils() {
    }

    static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key) throws InvalidKeyException {
        return key instanceof BCECPublicKey ? ((BCECPublicKey)key).engineGetKeyParameters() : ECUtil.generatePublicKeyParameter(key);
    }

    static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey key) throws InvalidKeyException {
        return key instanceof BCECPrivateKey ? ((BCECPrivateKey)key).engineGetKeyParameters() : ECUtil.generatePrivateKeyParameter(key);
    }

    static X9ECParameters getDomainParametersFromGenSpec(ECGenParameterSpec genSpec, ProviderConfiguration configuration) {
        return ECUtils.getDomainParametersFromName(genSpec.getName(), configuration);
    }

    static X9ECParameters getDomainParametersFromName(String curveName, ProviderConfiguration configuration) {
        ASN1ObjectIdentifier oid;
        if (null == curveName || curveName.length() < 1) {
            return null;
        }
        int spacePos = curveName.indexOf(32);
        if (spacePos > 0) {
            curveName = curveName.substring(spacePos + 1);
        }
        if (null == (oid = ECUtils.getOID(curveName))) {
            return ECUtil.getNamedCurveByName(curveName);
        }
        X9ECParameters x9 = ECUtil.getNamedCurveByOid(oid);
        if (null == x9 && null != configuration) {
            Map extraCurves = configuration.getAdditionalECParameters();
            x9 = (X9ECParameters)extraCurves.get(oid);
        }
        return x9;
    }

    static X962Parameters getDomainParametersFromName(ECParameterSpec ecSpec, boolean withCompression) {
        X962Parameters params;
        if (ecSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier curveOid = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)ecSpec).getName());
            if (curveOid == null) {
                curveOid = new ASN1ObjectIdentifier(((ECNamedCurveSpec)ecSpec).getName());
            }
            params = new X962Parameters(curveOid);
        } else if (ecSpec == null) {
            params = new X962Parameters(DERNull.INSTANCE);
        } else {
            ECCurve curve = EC5Util.convertCurve(ecSpec.getCurve());
            X9ECParameters ecP = new X9ECParameters(curve, new X9ECPoint(EC5Util.convertPoint(curve, ecSpec.getGenerator()), withCompression), ecSpec.getOrder(), BigInteger.valueOf(ecSpec.getCofactor()), ecSpec.getCurve().getSeed());
            params = new X962Parameters(ecP);
        }
        return params;
    }

    private static ASN1ObjectIdentifier getOID(String curveName) {
        char firstChar = curveName.charAt(0);
        if (firstChar >= '0' && firstChar <= '2') {
            try {
                return new ASN1ObjectIdentifier(curveName);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }
}

