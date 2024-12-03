/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.cert.X509CertificateHolder
 *  org.bouncycastle.cms.CMSEnvelopedData
 *  org.bouncycastle.cms.Recipient
 *  org.bouncycastle.cms.RecipientInformation
 *  org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient
 *  org.bouncycastle.cms.jcajce.JceKeyTransRecipient
 */
package com.lowagie.bouncycastle;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfObject;
import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Collection;
import java.util.List;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipient;

public class BouncyCastleHelper {
    public static void checkCertificateEncodingOrThrowException(Certificate certificate) {
        try {
            new X509CertificateHolder(certificate.getEncoded());
        }
        catch (IOException | CertificateEncodingException f) {
            throw new ExceptionConverter(f);
        }
    }

    public static byte[] getEnvelopedData(PdfArray recipients, List<PdfObject> strings, Certificate certificate, Key certificateKey, String certificateKeyProvider) {
        byte[] envelopedData = null;
        block2: for (PdfObject recipient : recipients.getElements()) {
            strings.remove(recipient);
            try {
                CMSEnvelopedData data = new CMSEnvelopedData(recipient.getBytes());
                Collection recipientInformations = data.getRecipientInfos().getRecipients();
                for (RecipientInformation recipientInfo : recipientInformations) {
                    if (!recipientInfo.getRID().match((Object)certificate)) continue;
                    JceKeyTransRecipient rec = new JceKeyTransEnvelopedRecipient((PrivateKey)certificateKey).setProvider(certificateKeyProvider);
                    envelopedData = recipientInfo.getContent((Recipient)rec);
                    continue block2;
                }
            }
            catch (Exception f) {
                throw new ExceptionConverter(f);
            }
        }
        return envelopedData;
    }
}

