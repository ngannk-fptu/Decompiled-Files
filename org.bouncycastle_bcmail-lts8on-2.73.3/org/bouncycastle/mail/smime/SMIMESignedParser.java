/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.CommandMap
 *  javax.activation.MailcapCommandMap
 *  javax.mail.BodyPart
 *  javax.mail.MessagingException
 *  javax.mail.Part
 *  javax.mail.Session
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimeMultipart
 *  org.bouncycastle.cms.CMSException
 *  org.bouncycastle.cms.CMSSignedDataParser
 *  org.bouncycastle.cms.CMSTypedStream
 *  org.bouncycastle.operator.DigestCalculatorProvider
 */
package org.bouncycastle.mail.smime;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedDataParser;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.operator.DigestCalculatorProvider;

public class SMIMESignedParser
extends CMSSignedDataParser {
    Object message;
    MimeBodyPart content;

    private static InputStream getInputStream(Part bodyPart) throws MessagingException {
        try {
            if (bodyPart.isMimeType("multipart/signed")) {
                throw new MessagingException("attempt to create signed data object from multipart content - use MimeMultipart constructor.");
            }
            return bodyPart.getInputStream();
        }
        catch (IOException e) {
            throw new MessagingException("can't extract input stream: " + e);
        }
    }

    private static File getTmpFile() throws MessagingException {
        try {
            return File.createTempFile("bcMail", ".mime");
        }
        catch (IOException e) {
            throw new MessagingException("can't extract input stream: " + e);
        }
    }

    private static CMSTypedStream getSignedInputStream(BodyPart bodyPart, String defaultContentTransferEncoding, File backingFile) throws MessagingException {
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(backingFile));
            SMIMEUtil.outputBodyPart(out, true, bodyPart, defaultContentTransferEncoding);
            ((OutputStream)out).close();
            TemporaryFileInputStream in = new TemporaryFileInputStream(backingFile);
            return new CMSTypedStream((InputStream)in);
        }
        catch (IOException e) {
            throw new MessagingException("can't extract input stream: " + e);
        }
    }

    public SMIMESignedParser(DigestCalculatorProvider digCalcProvider, MimeMultipart message) throws MessagingException, CMSException {
        this(digCalcProvider, message, SMIMESignedParser.getTmpFile());
    }

    public SMIMESignedParser(DigestCalculatorProvider digCalcProvider, MimeMultipart message, File backingFile) throws MessagingException, CMSException {
        this(digCalcProvider, message, "7bit", backingFile);
    }

    public SMIMESignedParser(DigestCalculatorProvider digCalcProvider, MimeMultipart message, String defaultContentTransferEncoding) throws MessagingException, CMSException {
        this(digCalcProvider, message, defaultContentTransferEncoding, SMIMESignedParser.getTmpFile());
    }

    public SMIMESignedParser(DigestCalculatorProvider digCalcProvider, MimeMultipart message, String defaultContentTransferEncoding, File backingFile) throws MessagingException, CMSException {
        super(digCalcProvider, SMIMESignedParser.getSignedInputStream(message.getBodyPart(0), defaultContentTransferEncoding, backingFile), SMIMESignedParser.getInputStream((Part)message.getBodyPart(1)));
        this.message = message;
        this.content = (MimeBodyPart)message.getBodyPart(0);
        this.drainContent();
    }

    public SMIMESignedParser(DigestCalculatorProvider digCalcProvider, Part message) throws MessagingException, CMSException, SMIMEException {
        super(digCalcProvider, SMIMESignedParser.getInputStream(message));
        this.message = message;
        CMSTypedStream cont = this.getSignedContent();
        if (cont != null) {
            this.content = SMIMEUtil.toWriteOnceBodyPart(cont);
        }
    }

    public SMIMESignedParser(DigestCalculatorProvider digCalcProvider, Part message, File file) throws MessagingException, CMSException, SMIMEException {
        super(digCalcProvider, SMIMESignedParser.getInputStream(message));
        this.message = message;
        CMSTypedStream cont = this.getSignedContent();
        if (cont != null) {
            this.content = SMIMEUtil.toMimeBodyPart(cont, file);
        }
    }

    public MimeBodyPart getContent() {
        return this.content;
    }

    public MimeMessage getContentAsMimeMessage(Session session) throws MessagingException, IOException {
        if (this.message instanceof MimeMultipart) {
            BodyPart bp = ((MimeMultipart)this.message).getBodyPart(0);
            return new MimeMessage(session, bp.getInputStream());
        }
        return new MimeMessage(session, this.getSignedContent().getContentStream());
    }

    public Object getContentWithSignature() {
        return this.message;
    }

    private void drainContent() throws CMSException {
        try {
            this.getSignedContent().drain();
        }
        catch (IOException e) {
            throw new CMSException("unable to read content for verification: " + e, (Exception)e);
        }
    }

    static {
        CommandMap commandMap = CommandMap.getDefaultCommandMap();
        if (commandMap instanceof MailcapCommandMap) {
            final MailcapCommandMap mc = (MailcapCommandMap)commandMap;
            mc.addMailcap("application/pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
            mc.addMailcap("application/pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
            mc.addMailcap("application/x-pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature");
            mc.addMailcap("application/x-pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
            mc.addMailcap("multipart/signed;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.multipart_signed");
            AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    CommandMap.setDefaultCommandMap((CommandMap)mc);
                    return null;
                }
            });
        }
    }

    private static class TemporaryFileInputStream
    extends BufferedInputStream {
        private final File _file;

        TemporaryFileInputStream(File file) throws FileNotFoundException {
            super(new FileInputStream(file));
            this._file = file;
        }

        @Override
        public void close() throws IOException {
            super.close();
            this._file.delete();
        }
    }
}

