/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.jcajce.interfaces.BCX509Certificate;

class PrincipalUtils {
    PrincipalUtils() {
    }

    static X500Name getCA(TrustAnchor trustAnchor) {
        return PrincipalUtils.getX500Name(PrincipalUtils.notNull(trustAnchor).getCA());
    }

    static X500Name getEncodedIssuerPrincipal(Object cert) {
        if (cert instanceof X509Certificate) {
            return PrincipalUtils.getIssuerPrincipal((X509Certificate)cert);
        }
        return null;
    }

    static X500Name getIssuerPrincipal(X509Certificate certificate) {
        if (certificate instanceof BCX509Certificate) {
            return PrincipalUtils.notNull(((BCX509Certificate)((Object)certificate)).getIssuerX500Name());
        }
        return PrincipalUtils.getX500Name(PrincipalUtils.notNull(certificate).getIssuerX500Principal());
    }

    static X500Name getIssuerPrincipal(X509CRL crl) {
        return PrincipalUtils.getX500Name(PrincipalUtils.notNull(crl).getIssuerX500Principal());
    }

    static X500Name getSubjectPrincipal(X509Certificate certificate) {
        if (certificate instanceof BCX509Certificate) {
            return PrincipalUtils.notNull(((BCX509Certificate)((Object)certificate)).getSubjectX500Name());
        }
        return PrincipalUtils.getX500Name(PrincipalUtils.notNull(certificate).getSubjectX500Principal());
    }

    static X500Name getX500Name(X500Principal principal) {
        X500Name name = X500Name.getInstance(PrincipalUtils.getEncoded(principal));
        return PrincipalUtils.notNull(name);
    }

    static X500Name getX500Name(X500NameStyle style, X500Principal principal) {
        X500Name name = X500Name.getInstance(style, PrincipalUtils.getEncoded(principal));
        return PrincipalUtils.notNull(name);
    }

    private static byte[] getEncoded(X500Principal principal) {
        byte[] encoding = PrincipalUtils.notNull(principal).getEncoded();
        return PrincipalUtils.notNull(encoding);
    }

    private static byte[] notNull(byte[] encoding) {
        if (null == encoding) {
            throw new IllegalStateException();
        }
        return encoding;
    }

    private static TrustAnchor notNull(TrustAnchor trustAnchor) {
        if (null == trustAnchor) {
            throw new IllegalStateException();
        }
        return trustAnchor;
    }

    private static X509Certificate notNull(X509Certificate certificate) {
        if (null == certificate) {
            throw new IllegalStateException();
        }
        return certificate;
    }

    private static X509CRL notNull(X509CRL crl) {
        if (null == crl) {
            throw new IllegalStateException();
        }
        return crl;
    }

    private static X500Name notNull(X500Name name) {
        if (null == name) {
            throw new IllegalStateException();
        }
        return name;
    }

    private static X500Principal notNull(X500Principal principal) {
        if (null == principal) {
            throw new IllegalStateException();
        }
        return principal;
    }
}

