/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.DHGenParameterSpec;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.generators.ElGamalParametersGenerator;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAlgorithmParameterGeneratorSpi;

public class AlgorithmParameterGeneratorSpi
extends BaseAlgorithmParameterGeneratorSpi {
    protected SecureRandom random;
    protected int strength = 1024;
    private int l = 0;

    @Override
    protected void engineInit(int strength, SecureRandom random) {
        this.strength = strength;
        this.random = random;
    }

    @Override
    protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(genParamSpec instanceof DHGenParameterSpec)) {
            throw new InvalidAlgorithmParameterException("DH parameter generator requires a DHGenParameterSpec for initialisation");
        }
        DHGenParameterSpec spec = (DHGenParameterSpec)genParamSpec;
        this.strength = spec.getPrimeSize();
        this.l = spec.getExponentSize();
        this.random = random;
    }

    @Override
    protected AlgorithmParameters engineGenerateParameters() {
        AlgorithmParameters params;
        ElGamalParametersGenerator pGen = new ElGamalParametersGenerator();
        if (this.random != null) {
            pGen.init(this.strength, 20, this.random);
        } else {
            pGen.init(this.strength, 20, CryptoServicesRegistrar.getSecureRandom());
        }
        ElGamalParameters p = pGen.generateParameters();
        try {
            params = this.createParametersInstance("ElGamal");
            params.init(new DHParameterSpec(p.getP(), p.getG(), this.l));
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return params;
    }
}

