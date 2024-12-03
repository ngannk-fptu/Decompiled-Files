/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.path;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathUtils;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.cert.path.CertPathValidationResult;
import org.bouncycastle.cert.path.CertPathValidationResultBuilder;

public class CertPath {
    private final X509CertificateHolder[] certificates;

    public CertPath(X509CertificateHolder[] certificates) {
        this.certificates = this.copyArray(certificates);
    }

    public X509CertificateHolder[] getCertificates() {
        return this.copyArray(this.certificates);
    }

    public CertPathValidationResult validate(CertPathValidation[] ruleSet) {
        CertPathValidationContext context = new CertPathValidationContext(CertPathUtils.getCriticalExtensionsOIDs(this.certificates));
        for (int i = 0; i != ruleSet.length; ++i) {
            for (int j = this.certificates.length - 1; j >= 0; --j) {
                try {
                    context.setIsEndEntity(j == 0);
                    ruleSet[i].validate(context, this.certificates[j]);
                    continue;
                }
                catch (CertPathValidationException e) {
                    return new CertPathValidationResult(context, j, i, e);
                }
            }
        }
        return new CertPathValidationResult(context);
    }

    public CertPathValidationResult evaluate(CertPathValidation[] ruleSet) {
        CertPathValidationContext context = new CertPathValidationContext(CertPathUtils.getCriticalExtensionsOIDs(this.certificates));
        CertPathValidationResultBuilder builder = new CertPathValidationResultBuilder(context);
        for (int i = 0; i != ruleSet.length; ++i) {
            for (int j = this.certificates.length - 1; j >= 0; --j) {
                try {
                    context.setIsEndEntity(j == 0);
                    ruleSet[i].validate(context, this.certificates[j]);
                    continue;
                }
                catch (CertPathValidationException e) {
                    builder.addException(j, i, e);
                }
            }
        }
        return builder.build();
    }

    private X509CertificateHolder[] copyArray(X509CertificateHolder[] array) {
        X509CertificateHolder[] rv = new X509CertificateHolder[array.length];
        System.arraycopy(array, 0, rv, 0, rv.length);
        return rv;
    }

    public int length() {
        return this.certificates.length;
    }
}

