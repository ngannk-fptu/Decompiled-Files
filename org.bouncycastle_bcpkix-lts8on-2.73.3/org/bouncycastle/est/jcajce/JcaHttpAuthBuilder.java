/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est.jcajce;

import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.est.HttpAuth;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class JcaHttpAuthBuilder {
    private JcaDigestCalculatorProviderBuilder providerBuilder = new JcaDigestCalculatorProviderBuilder();
    private final String realm;
    private final String username;
    private final char[] password;
    private SecureRandom random = new SecureRandom();

    public JcaHttpAuthBuilder(String username, char[] password) {
        this(null, username, password);
    }

    public JcaHttpAuthBuilder(String realm, String username, char[] password) {
        this.realm = realm;
        this.username = username;
        this.password = password;
    }

    public JcaHttpAuthBuilder setProvider(Provider provider) {
        this.providerBuilder.setProvider(provider);
        return this;
    }

    public JcaHttpAuthBuilder setProvider(String providerName) {
        this.providerBuilder.setProvider(providerName);
        return this;
    }

    public JcaHttpAuthBuilder setNonceGenerator(SecureRandom random) {
        this.random = random;
        return this;
    }

    public HttpAuth build() throws OperatorCreationException {
        return new HttpAuth(this.realm, this.username, this.password, this.random, this.providerBuilder.build());
    }
}

