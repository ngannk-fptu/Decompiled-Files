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
import org.bouncycastle.cms.KeyTransRecipientInformation;
import org.bouncycastle.cms.RecipientId;

public class KeyTransRecipientId
extends RecipientId {
    private X509CertificateHolderSelector baseSelector;

    private KeyTransRecipientId(X509CertificateHolderSelector baseSelector) {
        super(0);
        this.baseSelector = baseSelector;
    }

    public KeyTransRecipientId(byte[] subjectKeyId) {
        this(null, null, subjectKeyId);
    }

    public KeyTransRecipientId(X500Name issuer, BigInteger serialNumber) {
        this(issuer, serialNumber, null);
    }

    public KeyTransRecipientId(X500Name issuer, BigInteger serialNumber, byte[] subjectKeyId) {
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
        if (!(o instanceof KeyTransRecipientId)) {
            return false;
        }
        KeyTransRecipientId id = (KeyTransRecipientId)o;
        return this.baseSelector.equals(id.baseSelector);
    }

    @Override
    public Object clone() {
        return new KeyTransRecipientId(this.baseSelector);
    }

    public boolean match(Object obj) {
        if (obj instanceof KeyTransRecipientInformation) {
            return ((KeyTransRecipientInformation)obj).getRID().equals(this);
        }
        return this.baseSelector.match(obj);
    }
}

