/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.MessagingException
 *  javax.mail.Part
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimePart
 *  org.bouncycastle.cms.CMSEnvelopedDataParser
 *  org.bouncycastle.cms.CMSException
 */
package org.bouncycastle.mail.smime;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSException;

public class SMIMEEnvelopedParser
extends CMSEnvelopedDataParser {
    private final MimePart message;

    private static InputStream getInputStream(Part bodyPart, int bufferSize) throws MessagingException {
        try {
            InputStream in = bodyPart.getInputStream();
            if (bufferSize == 0) {
                return new BufferedInputStream(in);
            }
            return new BufferedInputStream(in, bufferSize);
        }
        catch (IOException e) {
            throw new MessagingException("can't extract input stream: " + e);
        }
    }

    public SMIMEEnvelopedParser(MimeBodyPart message) throws IOException, MessagingException, CMSException {
        this(message, 0);
    }

    public SMIMEEnvelopedParser(MimeMessage message) throws IOException, MessagingException, CMSException {
        this(message, 0);
    }

    public SMIMEEnvelopedParser(MimeBodyPart message, int bufferSize) throws IOException, MessagingException, CMSException {
        super(SMIMEEnvelopedParser.getInputStream((Part)message, bufferSize));
        this.message = message;
    }

    public SMIMEEnvelopedParser(MimeMessage message, int bufferSize) throws IOException, MessagingException, CMSException {
        super(SMIMEEnvelopedParser.getInputStream((Part)message, bufferSize));
        this.message = message;
    }

    public MimePart getEncryptedContent() {
        return this.message;
    }
}

