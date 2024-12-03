/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.PolicyConstraints
 *  org.bouncycastle.util.Memoable
 */
package org.bouncycastle.cert.path.validations;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.PolicyConstraints;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.cert.path.validations.ValidationUtils;
import org.bouncycastle.util.Memoable;

public class CertificatePoliciesValidation
implements CertPathValidation {
    private int explicitPolicy;
    private int policyMapping;
    private int inhibitAnyPolicy;

    CertificatePoliciesValidation(int pathLength) {
        this(pathLength, false, false, false);
    }

    CertificatePoliciesValidation(int pathLength, boolean isExplicitPolicyRequired, boolean isAnyPolicyInhibited, boolean isPolicyMappingInhibited) {
        this.explicitPolicy = isExplicitPolicyRequired ? 0 : pathLength + 1;
        this.inhibitAnyPolicy = isAnyPolicyInhibited ? 0 : pathLength + 1;
        this.policyMapping = isPolicyMappingInhibited ? 0 : pathLength + 1;
    }

    @Override
    public void validate(CertPathValidationContext context, X509CertificateHolder certificate) throws CertPathValidationException {
        context.addHandledExtension(Extension.policyConstraints);
        context.addHandledExtension(Extension.inhibitAnyPolicy);
        if (!context.isEndEntity() && !ValidationUtils.isSelfIssued(certificate)) {
            int extValue;
            Extension ext;
            this.explicitPolicy = this.countDown(this.explicitPolicy);
            this.policyMapping = this.countDown(this.policyMapping);
            this.inhibitAnyPolicy = this.countDown(this.inhibitAnyPolicy);
            PolicyConstraints policyConstraints = PolicyConstraints.fromExtensions((Extensions)certificate.getExtensions());
            if (policyConstraints != null) {
                BigInteger inhibitPolicyMapping;
                BigInteger requireExplicitPolicyMapping = policyConstraints.getRequireExplicitPolicyMapping();
                if (requireExplicitPolicyMapping != null && requireExplicitPolicyMapping.intValue() < this.explicitPolicy) {
                    this.explicitPolicy = requireExplicitPolicyMapping.intValue();
                }
                if ((inhibitPolicyMapping = policyConstraints.getInhibitPolicyMapping()) != null && inhibitPolicyMapping.intValue() < this.policyMapping) {
                    this.policyMapping = inhibitPolicyMapping.intValue();
                }
            }
            if ((ext = certificate.getExtension(Extension.inhibitAnyPolicy)) != null && (extValue = ASN1Integer.getInstance((Object)ext.getParsedValue()).intValueExact()) < this.inhibitAnyPolicy) {
                this.inhibitAnyPolicy = extValue;
            }
        }
    }

    private int countDown(int policyCounter) {
        if (policyCounter != 0) {
            return policyCounter - 1;
        }
        return 0;
    }

    public Memoable copy() {
        return new CertificatePoliciesValidation(0);
    }

    public void reset(Memoable other) {
        CertificatePoliciesValidation v = (CertificatePoliciesValidation)other;
    }
}

