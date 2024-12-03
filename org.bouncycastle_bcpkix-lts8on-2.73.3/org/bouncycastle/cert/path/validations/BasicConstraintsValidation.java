/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.x509.BasicConstraints
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.util.Integers
 *  org.bouncycastle.util.Memoable
 */
package org.bouncycastle.cert.path.validations;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Memoable;

public class BasicConstraintsValidation
implements CertPathValidation {
    private boolean previousCertWasCA = true;
    private Integer maxPathLength = null;
    private boolean isMandatory = true;

    public BasicConstraintsValidation() {
        this(true);
    }

    public BasicConstraintsValidation(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    @Override
    public void validate(CertPathValidationContext context, X509CertificateHolder certificate) throws CertPathValidationException {
        ASN1Integer pathLenConstraint;
        context.addHandledExtension(Extension.basicConstraints);
        if (!this.previousCertWasCA) {
            throw new CertPathValidationException("Basic constraints violated: issuer is not a CA");
        }
        BasicConstraints bc = BasicConstraints.fromExtensions((Extensions)certificate.getExtensions());
        boolean bl = this.previousCertWasCA = bc != null && bc.isCA() || bc == null && !this.isMandatory;
        if (this.maxPathLength != null && !certificate.getSubject().equals((Object)certificate.getIssuer())) {
            if (this.maxPathLength < 0) {
                throw new CertPathValidationException("Basic constraints violated: path length exceeded");
            }
            this.maxPathLength = Integers.valueOf((int)(this.maxPathLength - 1));
        }
        if (bc != null && bc.isCA() && (pathLenConstraint = bc.getPathLenConstraintInteger()) != null) {
            int newPathLength = pathLenConstraint.intPositiveValueExact();
            if (this.maxPathLength == null || newPathLength < this.maxPathLength) {
                this.maxPathLength = Integers.valueOf((int)newPathLength);
            }
        }
    }

    public Memoable copy() {
        BasicConstraintsValidation result = new BasicConstraintsValidation();
        result.isMandatory = this.isMandatory;
        result.previousCertWasCA = this.previousCertWasCA;
        result.maxPathLength = this.maxPathLength;
        return result;
    }

    public void reset(Memoable other) {
        BasicConstraintsValidation otherBCV = (BasicConstraintsValidation)other;
        this.isMandatory = otherBCV.isMandatory;
        this.previousCertWasCA = otherBCV.previousCertWasCA;
        this.maxPathLength = otherBCV.maxPathLength;
    }
}

