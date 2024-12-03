/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cmp.CMPCertificate
 *  org.bouncycastle.asn1.cmp.CertResponse
 *  org.bouncycastle.asn1.cmp.CertifiedKeyPair
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 */
package org.bouncycastle.cert.crmf;

import java.util.Collection;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cmp.CertifiedKeyPair;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;

public class CertificateResponse {
    private final CertResponse certResponse;

    public CertificateResponse(CertResponse certResponse) {
        this.certResponse = certResponse;
    }

    public boolean hasEncryptedCertificate() {
        return this.certResponse.getCertifiedKeyPair().getCertOrEncCert().hasEncryptedCertificate();
    }

    public CMSEnvelopedData getEncryptedCertificate() throws CMSException {
        if (!this.hasEncryptedCertificate()) {
            throw new IllegalStateException("encrypted certificate asked for, none found");
        }
        CertifiedKeyPair receivedKeyPair = this.certResponse.getCertifiedKeyPair();
        CMSEnvelopedData envelopedData = new CMSEnvelopedData(new ContentInfo(PKCSObjectIdentifiers.envelopedData, receivedKeyPair.getCertOrEncCert().getEncryptedCert().getValue()));
        if (envelopedData.getRecipientInfos().size() != 1) {
            throw new IllegalStateException("data encrypted for more than one recipient");
        }
        return envelopedData;
    }

    public CMPCertificate getCertificate(Recipient recipient) throws CMSException {
        CMSEnvelopedData encryptedCert = this.getEncryptedCertificate();
        RecipientInformationStore recipients = encryptedCert.getRecipientInfos();
        Collection<RecipientInformation> c = recipients.getRecipients();
        RecipientInformation recInfo = c.iterator().next();
        return CMPCertificate.getInstance((Object)recInfo.getContent(recipient));
    }

    public CMPCertificate getCertificate() throws CMSException {
        if (this.hasEncryptedCertificate()) {
            throw new IllegalStateException("plaintext certificate asked for, none found");
        }
        return this.certResponse.getCertifiedKeyPair().getCertOrEncCert().getCertificate();
    }

    public CertResponse toASN1Structure() {
        return this.certResponse;
    }
}

