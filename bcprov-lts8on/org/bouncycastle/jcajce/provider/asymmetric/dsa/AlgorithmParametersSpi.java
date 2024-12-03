/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.DSAParameter;

public class AlgorithmParametersSpi
extends java.security.AlgorithmParametersSpi {
    DSAParameterSpec currentSpec;

    protected boolean isASN1FormatString(String format) {
        return format == null || format.equals("ASN.1");
    }

    protected AlgorithmParameterSpec engineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
        if (paramSpec == null) {
            throw new NullPointerException("argument to getParameterSpec must not be null");
        }
        return this.localEngineGetParameterSpec(paramSpec);
    }

    @Override
    protected byte[] engineGetEncoded() {
        DSAParameter dsaP = new DSAParameter(this.currentSpec.getP(), this.currentSpec.getQ(), this.currentSpec.getG());
        try {
            return dsaP.getEncoded("DER");
        }
        catch (IOException e) {
            throw new RuntimeException("Error encoding DSAParameters");
        }
    }

    @Override
    protected byte[] engineGetEncoded(String format) {
        if (this.isASN1FormatString(format)) {
            return this.engineGetEncoded();
        }
        return null;
    }

    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
        if (paramSpec == DSAParameterSpec.class || paramSpec == AlgorithmParameterSpec.class) {
            return this.currentSpec;
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to DSA parameters object.");
    }

    @Override
    protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        if (!(paramSpec instanceof DSAParameterSpec)) {
            throw new InvalidParameterSpecException("DSAParameterSpec required to initialise a DSA algorithm parameters object");
        }
        this.currentSpec = (DSAParameterSpec)paramSpec;
    }

    @Override
    protected void engineInit(byte[] params) throws IOException {
        try {
            DSAParameter dsaP = DSAParameter.getInstance(ASN1Primitive.fromByteArray(params));
            this.currentSpec = new DSAParameterSpec(dsaP.getP(), dsaP.getQ(), dsaP.getG());
        }
        catch (ClassCastException e) {
            throw new IOException("Not a valid DSA Parameter encoding.");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Not a valid DSA Parameter encoding.");
        }
    }

    @Override
    protected void engineInit(byte[] params, String format) throws IOException {
        if (!this.isASN1FormatString(format) && !format.equalsIgnoreCase("X.509")) {
            throw new IOException("Unknown parameter format " + format);
        }
        this.engineInit(params);
    }

    @Override
    protected String engineToString() {
        return "DSA Parameters";
    }
}

