/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.Extension
 */
package org.bouncycastle.cert.selector.jcajce;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class JcaX509CertificateHolderSelector
extends X509CertificateHolderSelector {
    public JcaX509CertificateHolderSelector(X509Certificate certificate) {
        super(JcaX509CertificateHolderSelector.convertPrincipal(certificate.getIssuerX500Principal()), certificate.getSerialNumber(), JcaX509CertificateHolderSelector.getSubjectKeyId(certificate));
    }

    public JcaX509CertificateHolderSelector(X500Principal issuer, BigInteger serialNumber) {
        super(JcaX509CertificateHolderSelector.convertPrincipal(issuer), serialNumber);
    }

    public JcaX509CertificateHolderSelector(X500Principal issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        super(JcaX509CertificateHolderSelector.convertPrincipal(issuer), serialNumber, subjectKeyId);
    }

    private static X500Name convertPrincipal(X500Principal issuer) {
        if (issuer == null) {
            return null;
        }
        return X500Name.getInstance((Object)issuer.getEncoded());
    }

    private static byte[] getSubjectKeyId(X509Certificate cert) {
        byte[] ext = cert.getExtensionValue(Extension.subjectKeyIdentifier.getId());
        if (ext != null) {
            return ASN1OctetString.getInstance((Object)ASN1OctetString.getInstance((Object)ext).getOctets()).getOctets();
        }
        return null;
    }
}

