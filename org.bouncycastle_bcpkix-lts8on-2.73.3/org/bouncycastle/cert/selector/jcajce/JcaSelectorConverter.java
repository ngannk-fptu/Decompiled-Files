/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.x500.X500Name
 */
package org.bouncycastle.cert.selector.jcajce;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.X509CertSelector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class JcaSelectorConverter {
    public X509CertificateHolderSelector getCertificateHolderSelector(X509CertSelector certSelector) {
        try {
            X500Name issuer = X500Name.getInstance((Object)certSelector.getIssuerAsBytes());
            BigInteger serialNumber = certSelector.getSerialNumber();
            byte[] subjectKeyId = null;
            byte[] subjectKeyIdentifier = certSelector.getSubjectKeyIdentifier();
            if (subjectKeyIdentifier != null) {
                subjectKeyId = ASN1OctetString.getInstance((Object)subjectKeyIdentifier).getOctets();
            }
            return new X509CertificateHolderSelector(issuer, serialNumber, subjectKeyId);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("unable to convert issuer: " + e.getMessage());
        }
    }
}

