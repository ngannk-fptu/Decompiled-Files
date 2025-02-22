/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.jcajce.PKIXExtendedParameters;

public class PKIXCertRevocationCheckerParameters {
    private final PKIXExtendedParameters paramsPKIX;
    private final Date validDate;
    private final CertPath certPath;
    private final int index;
    private final X509Certificate signingCert;
    private final PublicKey workingPublicKey;

    public PKIXCertRevocationCheckerParameters(PKIXExtendedParameters paramsPKIX, Date validDate, CertPath certPath, int index, X509Certificate signingCert, PublicKey workingPublicKey) {
        this.paramsPKIX = paramsPKIX;
        this.validDate = validDate;
        this.certPath = certPath;
        this.index = index;
        this.signingCert = signingCert;
        this.workingPublicKey = workingPublicKey;
    }

    public PKIXExtendedParameters getParamsPKIX() {
        return this.paramsPKIX;
    }

    public Date getValidDate() {
        return new Date(this.validDate.getTime());
    }

    public CertPath getCertPath() {
        return this.certPath;
    }

    public int getIndex() {
        return this.index;
    }

    public X509Certificate getSigningCert() {
        return this.signingCert;
    }

    public PublicKey getWorkingPublicKey() {
        return this.workingPublicKey;
    }
}

