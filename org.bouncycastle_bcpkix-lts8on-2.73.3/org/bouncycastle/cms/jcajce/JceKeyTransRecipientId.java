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
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.jcajce.CMSUtils;

public class JceKeyTransRecipientId
extends KeyTransRecipientId {
    public JceKeyTransRecipientId(X509Certificate certificate) {
        super(JceKeyTransRecipientId.convertPrincipal(certificate.getIssuerX500Principal()), certificate.getSerialNumber(), CMSUtils.getSubjectKeyId(certificate));
    }

    public JceKeyTransRecipientId(X500Principal issuer, BigInteger serialNumber) {
        super(JceKeyTransRecipientId.convertPrincipal(issuer), serialNumber);
    }

    public JceKeyTransRecipientId(X500Principal issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        super(JceKeyTransRecipientId.convertPrincipal(issuer), serialNumber, subjectKeyId);
    }

    private static X500Name convertPrincipal(X500Principal issuer) {
        if (issuer == null) {
            return null;
        }
        return X500Name.getInstance((Object)issuer.getEncoded());
    }
}

