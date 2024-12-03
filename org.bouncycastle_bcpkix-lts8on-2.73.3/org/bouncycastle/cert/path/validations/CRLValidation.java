/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.util.Memoable
 *  org.bouncycastle.util.Selector
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.cert.path.validations;

import java.util.Collection;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

public class CRLValidation
implements CertPathValidation {
    private Store crls;
    private X500Name workingIssuerName;

    public CRLValidation(X500Name trustAnchorName, Store crls) {
        this.workingIssuerName = trustAnchorName;
        this.crls = crls;
    }

    @Override
    public void validate(CertPathValidationContext context, X509CertificateHolder certificate) throws CertPathValidationException {
        Collection matches = this.crls.getMatches(new Selector(){

            public boolean match(Object obj) {
                X509CRLHolder crl = (X509CRLHolder)obj;
                return crl.getIssuer().equals((Object)CRLValidation.this.workingIssuerName);
            }

            public Object clone() {
                return this;
            }
        });
        if (matches.isEmpty()) {
            throw new CertPathValidationException("CRL for " + this.workingIssuerName + " not found");
        }
        for (X509CRLHolder crl : matches) {
            if (crl.getRevokedCertificate(certificate.getSerialNumber()) == null) continue;
            throw new CertPathValidationException("Certificate revoked");
        }
        this.workingIssuerName = certificate.getSubject();
    }

    public Memoable copy() {
        return new CRLValidation(this.workingIssuerName, this.crls);
    }

    public void reset(Memoable other) {
        CRLValidation v = (CRLValidation)other;
        this.workingIssuerName = v.workingIssuerName;
        this.crls = v.crls;
    }
}

