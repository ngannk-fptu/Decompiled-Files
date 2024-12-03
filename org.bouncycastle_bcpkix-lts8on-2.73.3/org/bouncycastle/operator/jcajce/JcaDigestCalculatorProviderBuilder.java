/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Provider;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.OperatorHelper;

public class JcaDigestCalculatorProviderBuilder {
    private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());

    public JcaDigestCalculatorProviderBuilder setHelper(JcaJceHelper helper) {
        this.helper = new OperatorHelper(helper);
        return this;
    }

    public JcaDigestCalculatorProviderBuilder setProvider(Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }

    public JcaDigestCalculatorProviderBuilder setProvider(String providerName) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(providerName));
        return this;
    }

    public DigestCalculatorProvider build() throws OperatorCreationException {
        return new DigestCalculatorProvider(){

            @Override
            public DigestCalculator get(final AlgorithmIdentifier algorithm) throws OperatorCreationException {
                DigestOutputStream stream;
                try {
                    MessageDigest dig = JcaDigestCalculatorProviderBuilder.this.helper.createDigest(algorithm);
                    stream = new DigestOutputStream(dig);
                }
                catch (GeneralSecurityException e) {
                    throw new OperatorCreationException("exception on setup: " + e, e);
                }
                return new DigestCalculator(){

                    @Override
                    public AlgorithmIdentifier getAlgorithmIdentifier() {
                        return algorithm;
                    }

                    @Override
                    public OutputStream getOutputStream() {
                        return stream;
                    }

                    @Override
                    public byte[] getDigest() {
                        return stream.getDigest();
                    }
                };
            }
        };
    }

    private static class DigestOutputStream
    extends OutputStream {
        private MessageDigest dig;

        DigestOutputStream(MessageDigest dig) {
            this.dig = dig;
        }

        @Override
        public void write(byte[] bytes, int off, int len) throws IOException {
            this.dig.update(bytes, off, len);
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            this.dig.update(bytes);
        }

        @Override
        public void write(int b) throws IOException {
            this.dig.update((byte)b);
        }

        byte[] getDigest() {
            return this.dig.digest();
        }
    }
}

