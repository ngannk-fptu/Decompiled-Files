/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.jcajce.provider.asymmetric.ec.ECUtils;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;

public class AlgorithmParametersSpi
extends java.security.AlgorithmParametersSpi {
    private ECParameterSpec ecParameterSpec;
    private String curveName;

    protected boolean isASN1FormatString(String format) {
        return format == null || format.equals("ASN.1");
    }

    @Override
    protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (algorithmParameterSpec instanceof ECGenParameterSpec) {
            ECGenParameterSpec ecGenParameterSpec = (ECGenParameterSpec)algorithmParameterSpec;
            ProviderConfiguration configuration = BouncyCastleProvider.CONFIGURATION;
            X9ECParameters params = ECUtils.getDomainParametersFromGenSpec(ecGenParameterSpec, configuration);
            if (null == params) {
                throw new InvalidParameterSpecException("EC curve name not recognized: " + ecGenParameterSpec.getName());
            }
            this.curveName = ecGenParameterSpec.getName();
            ECParameterSpec baseSpec = EC5Util.convertToSpec(params);
            this.ecParameterSpec = new ECNamedCurveSpec(this.curveName, baseSpec.getCurve(), baseSpec.getGenerator(), baseSpec.getOrder(), BigInteger.valueOf(baseSpec.getCofactor()));
        } else if (algorithmParameterSpec instanceof ECParameterSpec) {
            this.curveName = algorithmParameterSpec instanceof ECNamedCurveSpec ? ((ECNamedCurveSpec)algorithmParameterSpec).getName() : null;
            this.ecParameterSpec = (ECParameterSpec)algorithmParameterSpec;
        } else {
            throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + algorithmParameterSpec.getClass().getName());
        }
    }

    @Override
    protected void engineInit(byte[] bytes) throws IOException {
        this.engineInit(bytes, "ASN.1");
    }

    @Override
    protected void engineInit(byte[] bytes, String format) throws IOException {
        ECCurve curve;
        X962Parameters params;
        if (this.isASN1FormatString(format)) {
            params = X962Parameters.getInstance(bytes);
            curve = EC5Util.getCurve(BouncyCastleProvider.CONFIGURATION, params);
            if (params.isNamedCurve()) {
                ASN1ObjectIdentifier curveId = ASN1ObjectIdentifier.getInstance(params.getParameters());
                this.curveName = ECNamedCurveTable.getName(curveId);
                if (this.curveName == null) {
                    this.curveName = curveId.getId();
                }
            }
        } else {
            throw new IOException("Unknown encoded parameters format in AlgorithmParameters object: " + format);
        }
        this.ecParameterSpec = EC5Util.convertToSpec(params, curve);
    }

    @Override
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(Class<T> paramSpec) throws InvalidParameterSpecException {
        if (ECParameterSpec.class.isAssignableFrom(paramSpec) || paramSpec == AlgorithmParameterSpec.class) {
            return (T)this.ecParameterSpec;
        }
        if (ECGenParameterSpec.class.isAssignableFrom(paramSpec)) {
            if (this.curveName != null) {
                ASN1ObjectIdentifier namedCurveOid = ECUtil.getNamedCurveOid(this.curveName);
                if (namedCurveOid != null) {
                    return (T)new ECGenParameterSpec(namedCurveOid.getId());
                }
                return (T)new ECGenParameterSpec(this.curveName);
            }
            ASN1ObjectIdentifier namedCurveOid = ECUtil.getNamedCurveOid(EC5Util.convertSpec(this.ecParameterSpec));
            if (namedCurveOid != null) {
                return (T)new ECGenParameterSpec(namedCurveOid.getId());
            }
        }
        throw new InvalidParameterSpecException("EC AlgorithmParameters cannot convert to " + paramSpec.getName());
    }

    @Override
    protected byte[] engineGetEncoded() throws IOException {
        return this.engineGetEncoded("ASN.1");
    }

    @Override
    protected byte[] engineGetEncoded(String format) throws IOException {
        if (this.isASN1FormatString(format)) {
            X962Parameters params;
            if (this.ecParameterSpec == null) {
                params = new X962Parameters(DERNull.INSTANCE);
            } else if (this.curveName != null) {
                params = new X962Parameters(ECUtil.getNamedCurveOid(this.curveName));
            } else {
                org.bouncycastle.jce.spec.ECParameterSpec ecSpec = EC5Util.convertSpec(this.ecParameterSpec);
                X9ECParameters ecP = new X9ECParameters(ecSpec.getCurve(), new X9ECPoint(ecSpec.getG(), false), ecSpec.getN(), ecSpec.getH(), ecSpec.getSeed());
                params = new X962Parameters(ecP);
            }
            return params.getEncoded();
        }
        throw new IOException("Unknown parameters format in AlgorithmParameters object: " + format);
    }

    @Override
    protected String engineToString() {
        return "EC Parameters";
    }
}

