/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x500.X500Name
 */
package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;
import org.bouncycastle.cms.KeyAgreeRecipientInformation;
import org.bouncycastle.cms.RecipientId;

public class KeyAgreeRecipientId
extends RecipientId {
    private X509CertificateHolderSelector baseSelector;

    private KeyAgreeRecipientId(X509CertificateHolderSelector baseSelector) {
        super(2);
        this.baseSelector = baseSelector;
    }

    public KeyAgreeRecipientId(byte[] subjectKeyId) {
        this(null, null, subjectKeyId);
    }

    public KeyAgreeRecipientId(X500Name issuer, BigInteger serialNumber) {
        this(issuer, serialNumber, null);
    }

    public KeyAgreeRecipientId(X500Name issuer, BigInteger serialNumber, byte[] subjectKeyId) {
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
        if (!(o instanceof KeyAgreeRecipientId)) {
            return false;
        }
        KeyAgreeRecipientId id = (KeyAgreeRecipientId)o;
        return this.baseSelector.equals(id.baseSelector);
    }

    @Override
    public Object clone() {
        return new KeyAgreeRecipientId(this.baseSelector);
    }

    public boolean match(Object obj) {
        if (obj instanceof KeyAgreeRecipientInformation) {
            return ((KeyAgreeRecipientInformation)obj).getRID().equals(this);
        }
        return this.baseSelector.match(obj);
    }
}

