/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.math.BigInteger;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHValidationParameters;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DHDomainParameterSpec
extends DHParameterSpec {
    private final BigInteger q;
    private final BigInteger j;
    private final int m;
    private DHValidationParameters validationParameters;

    public DHDomainParameterSpec(DHParameters domainParameters) {
        this(domainParameters.getP(), domainParameters.getQ(), domainParameters.getG(), domainParameters.getJ(), domainParameters.getM(), domainParameters.getL());
        this.validationParameters = domainParameters.getValidationParameters();
    }

    public DHDomainParameterSpec(BigInteger p, BigInteger q, BigInteger g) {
        this(p, q, g, null, 0);
    }

    public DHDomainParameterSpec(BigInteger p, BigInteger q, BigInteger g, int l) {
        this(p, q, g, null, l);
    }

    public DHDomainParameterSpec(BigInteger p, BigInteger q, BigInteger g, BigInteger j, int l) {
        this(p, q, g, j, 0, l);
    }

    public DHDomainParameterSpec(BigInteger p, BigInteger q, BigInteger g, BigInteger j, int m, int l) {
        super(p, g, l);
        this.q = q;
        this.j = j;
        this.m = m;
    }

    public BigInteger getQ() {
        return this.q;
    }

    public BigInteger getJ() {
        return this.j;
    }

    public int getM() {
        return this.m;
    }

    public DHParameters getDomainParameters() {
        return new DHParameters(this.getP(), this.getG(), this.q, this.m, this.getL(), this.j, this.validationParameters);
    }
}

