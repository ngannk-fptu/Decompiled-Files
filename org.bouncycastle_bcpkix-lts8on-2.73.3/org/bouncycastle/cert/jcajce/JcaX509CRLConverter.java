/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.jcajce.CertHelper;
import org.bouncycastle.cert.jcajce.DefaultCertHelper;
import org.bouncycastle.cert.jcajce.NamedCertHelper;
import org.bouncycastle.cert.jcajce.ProviderCertHelper;

public class JcaX509CRLConverter {
    private CertHelper helper = new DefaultCertHelper();

    public JcaX509CRLConverter setProvider(Provider provider) {
        this.helper = new ProviderCertHelper(provider);
        return this;
    }

    public JcaX509CRLConverter setProvider(String providerName) {
        this.helper = new NamedCertHelper(providerName);
        return this;
    }

    public X509CRL getCRL(X509CRLHolder crlHolder) throws CRLException {
        try {
            CertificateFactory cFact = this.helper.getCertificateFactory("X.509");
            return (X509CRL)cFact.generateCRL(new ByteArrayInputStream(crlHolder.getEncoded()));
        }
        catch (IOException e) {
            throw new ExCRLException("exception parsing certificate: " + e.getMessage(), e);
        }
        catch (NoSuchProviderException e) {
            throw new ExCRLException("cannot find required provider:" + e.getMessage(), e);
        }
        catch (CertificateException e) {
            throw new ExCRLException("cannot create factory: " + e.getMessage(), e);
        }
    }

    private static class ExCRLException
    extends CRLException {
        private Throwable cause;

        public ExCRLException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}

