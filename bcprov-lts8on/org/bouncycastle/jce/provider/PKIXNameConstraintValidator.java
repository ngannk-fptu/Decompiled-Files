/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.NameConstraintValidatorException;
import org.bouncycastle.jce.provider.PKIXNameConstraintValidatorException;

public class PKIXNameConstraintValidator {
    org.bouncycastle.asn1.x509.PKIXNameConstraintValidator validator = new org.bouncycastle.asn1.x509.PKIXNameConstraintValidator();

    public int hashCode() {
        return this.validator.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof PKIXNameConstraintValidator)) {
            return false;
        }
        PKIXNameConstraintValidator constraintValidator = (PKIXNameConstraintValidator)o;
        return this.validator.equals(constraintValidator.validator);
    }

    public void checkPermittedDN(ASN1Sequence dns) throws PKIXNameConstraintValidatorException {
        try {
            this.validator.checkPermittedDN(X500Name.getInstance(dns));
        }
        catch (NameConstraintValidatorException e) {
            throw new PKIXNameConstraintValidatorException(e.getMessage(), e);
        }
    }

    public void checkExcludedDN(ASN1Sequence dns) throws PKIXNameConstraintValidatorException {
        try {
            this.validator.checkExcludedDN(X500Name.getInstance(dns));
        }
        catch (NameConstraintValidatorException e) {
            throw new PKIXNameConstraintValidatorException(e.getMessage(), e);
        }
    }

    public void checkPermitted(GeneralName name) throws PKIXNameConstraintValidatorException {
        try {
            this.validator.checkPermitted(name);
        }
        catch (NameConstraintValidatorException e) {
            throw new PKIXNameConstraintValidatorException(e.getMessage(), e);
        }
    }

    public void checkExcluded(GeneralName name) throws PKIXNameConstraintValidatorException {
        try {
            this.validator.checkExcluded(name);
        }
        catch (NameConstraintValidatorException e) {
            throw new PKIXNameConstraintValidatorException(e.getMessage(), e);
        }
    }

    public void intersectPermittedSubtree(GeneralSubtree permitted) {
        this.validator.intersectPermittedSubtree(permitted);
    }

    public void intersectPermittedSubtree(GeneralSubtree[] permitted) {
        this.validator.intersectPermittedSubtree(permitted);
    }

    public void intersectEmptyPermittedSubtree(int nameType) {
        this.validator.intersectEmptyPermittedSubtree(nameType);
    }

    public void addExcludedSubtree(GeneralSubtree subtree) {
        this.validator.addExcludedSubtree(subtree);
    }

    public String toString() {
        return this.validator.toString();
    }
}

