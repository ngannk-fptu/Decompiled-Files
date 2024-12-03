/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cmp.CertConfirmContent
 *  org.bouncycastle.asn1.cmp.CertStatus
 *  org.bouncycastle.asn1.cmp.PKIBody
 */
package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.cmp.CertConfirmContent;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.cert.cmp.CertificateStatus;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class CertificateConfirmationContent {
    private DigestAlgorithmIdentifierFinder digestAlgFinder;
    private CertConfirmContent content;

    public CertificateConfirmationContent(CertConfirmContent content) {
        this(content, new DefaultDigestAlgorithmIdentifierFinder());
    }

    public CertificateConfirmationContent(CertConfirmContent content, DigestAlgorithmIdentifierFinder digestAlgFinder) {
        this.digestAlgFinder = digestAlgFinder;
        this.content = content;
    }

    public static CertificateConfirmationContent fromPKIBody(PKIBody pkiBody) {
        return CertificateConfirmationContent.fromPKIBody(pkiBody, new DefaultDigestAlgorithmIdentifierFinder());
    }

    public static CertificateConfirmationContent fromPKIBody(PKIBody pkiBody, DigestAlgorithmIdentifierFinder digestAlgFinder) {
        if (!CertificateConfirmationContent.isCertificateConfirmationContent(pkiBody.getType())) {
            throw new IllegalArgumentException("content of PKIBody wrong type: " + pkiBody.getType());
        }
        return new CertificateConfirmationContent(CertConfirmContent.getInstance((Object)pkiBody.getContent()), digestAlgFinder);
    }

    public static boolean isCertificateConfirmationContent(int bodyType) {
        switch (bodyType) {
            case 24: {
                return true;
            }
        }
        return false;
    }

    public CertConfirmContent toASN1Structure() {
        return this.content;
    }

    public CertificateStatus[] getStatusMessages() {
        CertStatus[] statusArray = this.content.toCertStatusArray();
        CertificateStatus[] ret = new CertificateStatus[statusArray.length];
        for (int i = 0; i != ret.length; ++i) {
            ret[i] = new CertificateStatus(this.digestAlgFinder, statusArray[i]);
        }
        return ret;
    }
}

