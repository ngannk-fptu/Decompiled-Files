/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x500.X500Name
 */
package org.bouncycastle.cms.jcajce;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.jcajce.CMSUtils;

public class JcaSignerId
extends SignerId {
    public JcaSignerId(X509Certificate certificate) {
        super(JcaSignerId.convertPrincipal(certificate.getIssuerX500Principal()), certificate.getSerialNumber(), CMSUtils.getSubjectKeyId(certificate));
    }

    public JcaSignerId(X500Principal issuer, BigInteger serialNumber) {
        super(JcaSignerId.convertPrincipal(issuer), serialNumber);
    }

    public JcaSignerId(X500Principal issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        super(JcaSignerId.convertPrincipal(issuer), serialNumber, subjectKeyId);
    }

    private static X500Name convertPrincipal(X500Principal issuer) {
        if (issuer == null) {
            return null;
        }
        return X500Name.getInstance((Object)issuer.getEncoded());
    }
}

