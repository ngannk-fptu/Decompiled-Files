/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.path.validations;

import org.bouncycastle.cert.path.CertPath;
import org.bouncycastle.cert.path.validations.CertificatePoliciesValidation;

public class CertificatePoliciesValidationBuilder {
    private boolean isExplicitPolicyRequired;
    private boolean isAnyPolicyInhibited;
    private boolean isPolicyMappingInhibited;

    public void setAnyPolicyInhibited(boolean anyPolicyInhibited) {
        this.isAnyPolicyInhibited = anyPolicyInhibited;
    }

    public void setExplicitPolicyRequired(boolean explicitPolicyRequired) {
        this.isExplicitPolicyRequired = explicitPolicyRequired;
    }

    public void setPolicyMappingInhibited(boolean policyMappingInhibited) {
        this.isPolicyMappingInhibited = policyMappingInhibited;
    }

    public CertificatePoliciesValidation build(int pathLen) {
        return new CertificatePoliciesValidation(pathLen, this.isExplicitPolicyRequired, this.isAnyPolicyInhibited, this.isPolicyMappingInhibited);
    }

    public CertificatePoliciesValidation build(CertPath path) {
        return this.build(path.length());
    }
}

