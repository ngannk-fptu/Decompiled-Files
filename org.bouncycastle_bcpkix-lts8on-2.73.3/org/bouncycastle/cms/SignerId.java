/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.util.Selector
 */
package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.util.Selector;

public class SignerId
implements Selector {
    private X509CertificateHolderSelector baseSelector;

    private SignerId(X509CertificateHolderSelector baseSelector) {
        this.baseSelector = baseSelector;
    }

    public SignerId(byte[] subjectKeyId) {
        this(null, null, subjectKeyId);
    }

    public SignerId(X500Name issuer, BigInteger serialNumber) {
        this(issuer, serialNumber, null);
    }

    public SignerId(X500Name issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        this(new X509CertificateHolderSelector(issuer, serialNumber, subjectKeyId));
    }

    public X500Name getIssuer() {
        return this.baseSelector.getIssuer();
    }

    public BigInteger getSerialNumber() {
        return this.baseSelector.getSerialNumber();
    }

    public byte[] getSubjectKeyIdentifier() {
        return this.baseSelector.getSubjectKeyIdentifier();
    }

    public int hashCode() {
        return this.baseSelector.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof SignerId)) {
            return false;
        }
        SignerId id = (SignerId)o;
        return this.baseSelector.equals(id.baseSelector);
    }

    public boolean match(Object obj) {
        if (obj instanceof SignerInformation) {
            return ((SignerInformation)obj).getSID().equals(this);
        }
        return this.baseSelector.match(obj);
    }

    public Object clone() {
        return new SignerId(this.baseSelector);
    }
}

