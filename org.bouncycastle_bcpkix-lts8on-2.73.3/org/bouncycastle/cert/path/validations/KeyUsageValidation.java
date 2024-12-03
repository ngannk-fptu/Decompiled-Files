/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.KeyUsage
 *  org.bouncycastle.util.Memoable
 */
package org.bouncycastle.cert.path.validations;

import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Memoable;

public class KeyUsageValidation
implements CertPathValidation {
    private boolean isMandatory;

    public KeyUsageValidation() {
        this(true);
    }

    public KeyUsageValidation(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    @Override
    public void validate(CertPathValidationContext context, X509CertificateHolder certificate) throws CertPathValidationException {
        context.addHandledExtension(Extension.keyUsage);
        if (!context.isEndEntity()) {
            KeyUsage usage = KeyUsage.fromExtensions((Extensions)certificate.getExtensions());
            if (usage != null) {
                if (!usage.hasUsages(4)) {
                    throw new CertPathValidationException("Issuer certificate KeyUsage extension does not permit key signing");
                }
            } else if (this.isMandatory) {
                throw new CertPathValidationException("KeyUsage extension not present in CA certificate");
            }
        }
    }

    public Memoable copy() {
        return new KeyUsageValidation(this.isMandatory);
    }

    public void reset(Memoable other) {
        KeyUsageValidation v = (KeyUsageValidation)other;
        this.isMandatory = v.isMandatory;
    }
}

