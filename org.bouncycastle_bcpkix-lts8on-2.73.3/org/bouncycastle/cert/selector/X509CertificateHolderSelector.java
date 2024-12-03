/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.cms.IssuerAndSerialNumber
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.Selector
 */
package org.bouncycastle.cert.selector;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.selector.MSOutlookKeyIdCalculator;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

public class X509CertificateHolderSelector
implements Selector {
    private byte[] subjectKeyId;
    private X500Name issuer;
    private BigInteger serialNumber;

    public X509CertificateHolderSelector(byte[] subjectKeyId) {
        this(null, null, subjectKeyId);
    }

    public X509CertificateHolderSelector(X500Name issuer, BigInteger serialNumber) {
        this(issuer, serialNumber, null);
    }

    public X509CertificateHolderSelector(X500Name issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        this.issuer = issuer;
        this.serialNumber = serialNumber;
        this.subjectKeyId = subjectKeyId;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }

    public byte[] getSubjectKeyIdentifier() {
        return Arrays.clone((byte[])this.subjectKeyId);
    }

    public int hashCode() {
        int code = Arrays.hashCode((byte[])this.subjectKeyId);
        if (this.serialNumber != null) {
            code ^= this.serialNumber.hashCode();
        }
        if (this.issuer != null) {
            code ^= this.issuer.hashCode();
        }
        return code;
    }

    public boolean equals(Object o) {
        if (!(o instanceof X509CertificateHolderSelector)) {
            return false;
        }
        X509CertificateHolderSelector id = (X509CertificateHolderSelector)o;
        return Arrays.areEqual((byte[])this.subjectKeyId, (byte[])id.subjectKeyId) && this.equalsObj(this.serialNumber, id.serialNumber) && this.equalsObj(this.issuer, id.issuer);
    }

    private boolean equalsObj(Object a, Object b) {
        return a != null ? a.equals(b) : b == null;
    }

    public boolean match(Object obj) {
        if (obj instanceof X509CertificateHolder) {
            X509CertificateHolder certHldr = (X509CertificateHolder)obj;
            if (this.getSerialNumber() != null) {
                IssuerAndSerialNumber iAndS = new IssuerAndSerialNumber(certHldr.toASN1Structure());
                return iAndS.getName().equals((Object)this.issuer) && iAndS.getSerialNumber().hasValue(this.serialNumber);
            }
            if (this.subjectKeyId != null) {
                Extension ext = certHldr.getExtension(Extension.subjectKeyIdentifier);
                if (ext == null) {
                    return Arrays.areEqual((byte[])this.subjectKeyId, (byte[])MSOutlookKeyIdCalculator.calculateKeyId(certHldr.getSubjectPublicKeyInfo()));
                }
                byte[] subKeyID = ASN1OctetString.getInstance((Object)ext.getParsedValue()).getOctets();
                return Arrays.areEqual((byte[])this.subjectKeyId, (byte[])subKeyID);
            }
        } else if (obj instanceof byte[]) {
            return Arrays.areEqual((byte[])this.subjectKeyId, (byte[])((byte[])obj));
        }
        return false;
    }

    public Object clone() {
        return new X509CertificateHolderSelector(this.issuer, this.serialNumber, this.subjectKeyId);
    }
}

