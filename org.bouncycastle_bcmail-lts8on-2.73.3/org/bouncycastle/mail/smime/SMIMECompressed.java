/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.MessagingException
 *  javax.mail.Part
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimePart
 *  org.bouncycastle.cms.CMSCompressedData
 *  org.bouncycastle.cms.CMSException
 */
package org.bouncycastle.mail.smime;

import java.io.IOException;
import java.io.InputStream;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import org.bouncycastle.cms.CMSCompressedData;
import org.bouncycastle.cms.CMSException;

public class SMIMECompressed
extends CMSCompressedData {
    MimePart message;

    private static InputStream getInputStream(Part bodyPart) throws MessagingException {
        try {
            return bodyPart.getInputStream();
        }
        catch (IOException e) {
            throw new MessagingException("can't extract input stream: " + e);
        }
    }

    public SMIMECompressed(MimeBodyPart message) throws MessagingException, CMSException {
        super(SMIMECompressed.getInputStream((Part)message));
        this.message = message;
    }

    public SMIMECompressed(MimeMessage message) throws MessagingException, CMSException {
        super(SMIMECompressed.getInputStream((Part)message));
        this.message = message;
    }

    public MimePart getCompressedContent() {
        return this.message;
    }
}

