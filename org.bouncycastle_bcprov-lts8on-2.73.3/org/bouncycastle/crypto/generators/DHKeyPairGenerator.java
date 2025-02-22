/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.generators.DHKeyGeneratorHelper;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;

public class DHKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private DHKeyGenerationParameters param;

    @Override
    public void init(KeyGenerationParameters param) {
        this.param = (DHKeyGenerationParameters)param;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("DHKeyGen", ConstraintUtils.bitsOfSecurityFor(this.param.getParameters().getP()), this.param.getParameters(), CryptoServicePurpose.KEYGEN));
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        DHKeyGeneratorHelper helper = DHKeyGeneratorHelper.INSTANCE;
        DHParameters dhp = this.param.getParameters();
        BigInteger x = helper.calculatePrivate(dhp, this.param.getRandom());
        BigInteger y = helper.calculatePublic(dhp, x);
        return new AsymmetricCipherKeyPair(new DHPublicKeyParameters(y, dhp), new DHPrivateKeyParameters(x, dhp));
    }
}

