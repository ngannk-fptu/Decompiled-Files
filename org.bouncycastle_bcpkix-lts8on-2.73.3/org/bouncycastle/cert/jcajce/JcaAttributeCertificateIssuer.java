/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x500.X500Name
 */
package org.bouncycastle.cert.jcajce;

import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.AttributeCertificateIssuer;

public class JcaAttributeCertificateIssuer
extends AttributeCertificateIssuer {
    public JcaAttributeCertificateIssuer(X509Certificate issuerCert) {
        this(issuerCert.getIssuerX500Principal());
    }

    public JcaAttributeCertificateIssuer(X500Principal issuerDN) {
        super(X500Name.getInstance((Object)issuerDN.getEncoded()));
    }
}

