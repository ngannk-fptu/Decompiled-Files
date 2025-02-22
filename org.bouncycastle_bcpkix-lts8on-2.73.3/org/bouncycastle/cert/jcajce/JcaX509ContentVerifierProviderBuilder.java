/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cert.jcajce;

import java.security.Provider;
import java.security.cert.CertificateException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ContentVerifierProviderBuilder;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

public class JcaX509ContentVerifierProviderBuilder
implements X509ContentVerifierProviderBuilder {
    private JcaContentVerifierProviderBuilder builder = new JcaContentVerifierProviderBuilder();

    public JcaX509ContentVerifierProviderBuilder setProvider(Provider provider) {
        this.builder.setProvider(provider);
        return this;
    }

    public JcaX509ContentVerifierProviderBuilder setProvider(String providerName) {
        this.builder.setProvider(providerName);
        return this;
    }

    @Override
    public ContentVerifierProvider build(SubjectPublicKeyInfo validatingKeyInfo) throws OperatorCreationException {
        return this.builder.build(validatingKeyInfo);
    }

    @Override
    public ContentVerifierProvider build(X509CertificateHolder validatingKeyInfo) throws OperatorCreationException {
        try {
            return this.builder.build(validatingKeyInfo);
        }
        catch (CertificateException e) {
            throw new OperatorCreationException("Unable to process certificate: " + e.getMessage(), e);
        }
    }
}

