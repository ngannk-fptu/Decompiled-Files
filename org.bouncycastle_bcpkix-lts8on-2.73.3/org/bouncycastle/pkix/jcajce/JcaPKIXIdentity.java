/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 */
package org.bouncycastle.pkix.jcajce;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.pkix.PKIXIdentity;

public class JcaPKIXIdentity
extends PKIXIdentity {
    private final PrivateKey privKey;
    private final X509Certificate[] certs;

    private static PrivateKeyInfo getPrivateKeyInfo(PrivateKey privateKey) {
        try {
            return PrivateKeyInfo.getInstance((Object)privateKey.getEncoded());
        }
        catch (Exception e) {
            return null;
        }
    }

    private static X509CertificateHolder[] getCertificates(X509Certificate[] certs) {
        X509CertificateHolder[] certHldrs = new X509CertificateHolder[certs.length];
        try {
            for (int i = 0; i != certHldrs.length; ++i) {
                certHldrs[i] = new JcaX509CertificateHolder(certs[i]);
            }
            return certHldrs;
        }
        catch (CertificateEncodingException e) {
            throw new IllegalArgumentException("Unable to process certificates: " + e.getMessage());
        }
    }

    public JcaPKIXIdentity(PrivateKey privKey, X509Certificate[] certs) {
        super(JcaPKIXIdentity.getPrivateKeyInfo(privKey), JcaPKIXIdentity.getCertificates(certs));
        this.privKey = privKey;
        this.certs = new X509Certificate[certs.length];
        System.arraycopy(certs, 0, this.certs, 0, certs.length);
    }

    public JcaPKIXIdentity(PrivateKey privKey, X509Certificate cert) {
        this(privKey, new X509Certificate[]{cert});
    }

    public PrivateKey getPrivateKey() {
        return this.privKey;
    }

    public X509Certificate getX509Certificate() {
        return this.certs[0];
    }

    public X509Certificate[] getX509CertificateChain() {
        X509Certificate[] rv = new X509Certificate[this.certs.length];
        System.arraycopy(this.certs, 0, rv, 0, rv.length);
        return rv;
    }
}

