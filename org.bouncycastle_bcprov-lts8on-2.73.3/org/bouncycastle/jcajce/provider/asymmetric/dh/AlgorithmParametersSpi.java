/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.asn1.pkcs.DHParameter;

public class AlgorithmParametersSpi
extends java.security.AlgorithmParametersSpi {
    DHParameterSpec currentSpec;

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
        DHParameter dhP = new DHParameter(this.currentSpec.getP(), this.currentSpec.getG(), this.currentSpec.getL());
        try {
            return dhP.getEncoded("DER");
        }
        catch (IOException e) {
            throw new RuntimeException("Error encoding DHParameters");
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
        if (paramSpec == DHParameterSpec.class || paramSpec == AlgorithmParameterSpec.class) {
            return this.currentSpec;
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to DH parameters object.");
    }

    @Override
    protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        if (!(paramSpec instanceof DHParameterSpec)) {
            throw new InvalidParameterSpecException("DHParameterSpec required to initialise a Diffie-Hellman algorithm parameters object");
        }
        this.currentSpec = (DHParameterSpec)paramSpec;
    }

    @Override
    protected void engineInit(byte[] params) throws IOException {
        try {
            DHParameter dhP = DHParameter.getInstance(params);
            this.currentSpec = dhP.getL() != null ? new DHParameterSpec(dhP.getP(), dhP.getG(), dhP.getL().intValue()) : new DHParameterSpec(dhP.getP(), dhP.getG());
        }
        catch (ClassCastException e) {
            throw new IOException("Not a valid DH Parameter encoding.");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Not a valid DH Parameter encoding.");
        }
    }

    @Override
    protected void engineInit(byte[] params, String format) throws IOException {
        if (!this.isASN1FormatString(format)) {
            throw new IOException("Unknown parameter format " + format);
        }
        this.engineInit(params);
    }

    @Override
    protected String engineToString() {
        return "Diffie-Hellman Parameters";
    }
}

