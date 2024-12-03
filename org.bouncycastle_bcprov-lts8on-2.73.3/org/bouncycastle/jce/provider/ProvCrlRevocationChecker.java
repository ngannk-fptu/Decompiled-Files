/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.jcajce.PKIXCertRevocationChecker;
import org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.bouncycastle.jce.provider.RFC3280CertPathUtilities;

class ProvCrlRevocationChecker
implements PKIXCertRevocationChecker {
    private final JcaJceHelper helper;
    private PKIXCertRevocationCheckerParameters params;
    private Date currentDate = null;

    public ProvCrlRevocationChecker(JcaJceHelper helper) {
        this.helper = helper;
    }

    @Override
    public void setParameter(String name, Object value) {
    }

    @Override
    public void initialize(PKIXCertRevocationCheckerParameters params) {
        this.params = params;
        this.currentDate = new Date();
    }

    public void init(boolean forForward) throws CertPathValidatorException {
        if (forForward) {
            throw new CertPathValidatorException("forward checking not supported");
        }
        this.params = null;
        this.currentDate = new Date();
    }

    @Override
    public void check(Certificate certificate) throws CertPathValidatorException {
        try {
            RFC3280CertPathUtilities.checkCRLs(this.params, this.params.getParamsPKIX(), this.currentDate, this.params.getValidDate(), (X509Certificate)certificate, this.params.getSigningCert(), this.params.getWorkingPublicKey(), this.params.getCertPath().getCertificates(), this.helper);
        }
        catch (AnnotatedException e) {
            Throwable cause = e;
            if (null != e.getCause()) {
                cause = e.getCause();
            }
            throw new CertPathValidatorException(e.getMessage(), cause, this.params.getCertPath(), this.params.getIndex());
        }
    }
}

