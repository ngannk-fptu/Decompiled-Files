/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x500.X500Name
 */
package org.bouncycastle.cert.jcajce;

import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLHolder;

public class JcaX509v2CRLBuilder
extends X509v2CRLBuilder {
    public JcaX509v2CRLBuilder(X500Principal issuer, Date now) {
        super(X500Name.getInstance((Object)issuer.getEncoded()), now);
    }

    public JcaX509v2CRLBuilder(X509Certificate issuerCert, Date now) {
        this(issuerCert.getSubjectX500Principal(), now);
    }

    public JcaX509v2CRLBuilder(X509CRL templateCRL) throws CRLException {
        super(new JcaX509CRLHolder(templateCRL));
    }
}

