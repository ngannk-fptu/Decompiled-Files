/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.MessagingException
 *  javax.mail.Part
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimePart
 *  org.bouncycastle.cms.CMSCompressedDataParser
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
import org.bouncycastle.cms.CMSCompressedDataParser;
import org.bouncycastle.cms.CMSException;

public class SMIMECompressedParser
extends CMSCompressedDataParser {
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

    public SMIMECompressedParser(MimeBodyPart message) throws MessagingException, CMSException {
        this(message, 0);
    }

    public SMIMECompressedParser(MimeMessage message) throws MessagingException, CMSException {
        this(message, 0);
    }

    public SMIMECompressedParser(MimeBodyPart message, int bufferSize) throws MessagingException, CMSException {
        super(SMIMECompressedParser.getInputStream((Part)message, bufferSize));
        this.message = message;
    }

    public SMIMECompressedParser(MimeMessage message, int bufferSize) throws MessagingException, CMSException {
        super(SMIMECompressedParser.getInputStream((Part)message, bufferSize));
        this.message = message;
    }

    public MimePart getCompressedContent() {
        return this.message;
    }
}

