/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.CertHelper;
import org.bouncycastle.cert.jcajce.DefaultCertHelper;
import org.bouncycastle.cert.jcajce.NamedCertHelper;
import org.bouncycastle.cert.jcajce.ProviderCertHelper;

public class JcaX509CertificateConverter {
    private CertHelper helper = new DefaultCertHelper();

    public JcaX509CertificateConverter setProvider(Provider provider) {
        this.helper = new ProviderCertHelper(provider);
        return this;
    }

    public JcaX509CertificateConverter setProvider(String providerName) {
        this.helper = new NamedCertHelper(providerName);
        return this;
    }

    public X509Certificate getCertificate(X509CertificateHolder certHolder) throws CertificateException {
        try {
            CertificateFactory cFact = this.helper.getCertificateFactory("X.509");
            return (X509Certificate)cFact.generateCertificate(new ByteArrayInputStream(certHolder.getEncoded()));
        }
        catch (IOException e) {
            throw new ExCertificateParsingException("exception parsing certificate: " + e.getMessage(), e);
        }
        catch (NoSuchProviderException e) {
            throw new ExCertificateException("cannot find required provider:" + e.getMessage(), e);
        }
    }

    private static class ExCertificateException
    extends CertificateException {
        private Throwable cause;

        public ExCertificateException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }

    private static class ExCertificateParsingException
    extends CertificateParsingException {
        private Throwable cause;

        public ExCertificateParsingException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}

