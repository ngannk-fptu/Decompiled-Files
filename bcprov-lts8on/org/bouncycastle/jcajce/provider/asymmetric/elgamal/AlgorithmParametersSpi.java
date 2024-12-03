/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;

public class AlgorithmParametersSpi
extends BaseAlgorithmParameters {
    ElGamalParameterSpec currentSpec;

    @Override
    protected byte[] engineGetEncoded() {
        ElGamalParameter elP = new ElGamalParameter(this.currentSpec.getP(), this.currentSpec.getG());
        try {
            return elP.getEncoded("DER");
        }
        catch (IOException e) {
            throw new RuntimeException("Error encoding ElGamalParameters");
        }
    }

    @Override
    protected byte[] engineGetEncoded(String format) {
        if (this.isASN1FormatString(format) || format.equalsIgnoreCase("X.509")) {
            return this.engineGetEncoded();
        }
        return null;
    }

    @Override
    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
        if (paramSpec == ElGamalParameterSpec.class || paramSpec == AlgorithmParameterSpec.class) {
            return this.currentSpec;
        }
        if (paramSpec == DHParameterSpec.class) {
            return new DHParameterSpec(this.currentSpec.getP(), this.currentSpec.getG());
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to ElGamal parameters object.");
    }

    @Override
    protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        if (!(paramSpec instanceof ElGamalParameterSpec) && !(paramSpec instanceof DHParameterSpec)) {
            throw new InvalidParameterSpecException("DHParameterSpec required to initialise a ElGamal algorithm parameters object");
        }
        if (paramSpec instanceof ElGamalParameterSpec) {
            this.currentSpec = (ElGamalParameterSpec)paramSpec;
        } else {
            DHParameterSpec s = (DHParameterSpec)paramSpec;
            this.currentSpec = new ElGamalParameterSpec(s.getP(), s.getG());
        }
    }

    @Override
    protected void engineInit(byte[] params) throws IOException {
        try {
            ElGamalParameter elP = ElGamalParameter.getInstance(ASN1Primitive.fromByteArray(params));
            this.currentSpec = new ElGamalParameterSpec(elP.getP(), elP.getG());
        }
        catch (ClassCastException e) {
            throw new IOException("Not a valid ElGamal Parameter encoding.");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Not a valid ElGamal Parameter encoding.");
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
        return "ElGamal Parameters";
    }
}

