/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.CommandMap
 *  javax.activation.MailcapCommandMap
 *  javax.mail.MessagingException
 *  javax.mail.Part
 *  javax.mail.Session
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimeMultipart
 *  javax.mail.internet.MimePart
 *  org.bouncycastle.cms.CMSException
 *  org.bouncycastle.cms.CMSProcessable
 *  org.bouncycastle.cms.CMSSignedData
 *  org.bouncycastle.cms.CMSTypedData
 */
package org.bouncycastle.mail.smime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.mail.smime.CMSProcessableBodyPartInbound;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMEUtil;

public class SMIMESigned
extends CMSSignedData {
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

    public SMIMESigned(MimeMultipart message) throws MessagingException, CMSException {
        super((CMSProcessable)new CMSProcessableBodyPartInbound(message.getBodyPart(0)), SMIMESigned.getInputStream((Part)message.getBodyPart(1)));
        this.message = message;
        this.content = (MimeBodyPart)message.getBodyPart(0);
    }

    public SMIMESigned(MimeMultipart message, String defaultContentTransferEncoding) throws MessagingException, CMSException {
        super((CMSProcessable)new CMSProcessableBodyPartInbound(message.getBodyPart(0), defaultContentTransferEncoding), SMIMESigned.getInputStream((Part)message.getBodyPart(1)));
        this.message = message;
        this.content = (MimeBodyPart)message.getBodyPart(0);
    }

    public SMIMESigned(Part message) throws MessagingException, CMSException, SMIMEException {
        super(SMIMESigned.getInputStream(message));
        this.message = message;
        CMSTypedData cont = this.getSignedContent();
        if (cont != null) {
            byte[] contBytes = (byte[])cont.getContent();
            this.content = SMIMEUtil.toMimeBodyPart(contBytes);
        }
    }

    public MimeBodyPart getContent() {
        return this.content;
    }

    public MimeMessage getContentAsMimeMessage(Session session) throws MessagingException, IOException {
        Object content = this.getSignedContent().getContent();
        byte[] contentBytes = null;
        if (content instanceof byte[]) {
            contentBytes = (byte[])content;
        } else if (content instanceof MimePart) {
            MimePart part = (MimePart)content;
            ByteArrayOutputStream out = part.getSize() > 0 ? new ByteArrayOutputStream(part.getSize()) : new ByteArrayOutputStream();
            part.writeTo((OutputStream)out);
            contentBytes = out.toByteArray();
        } else {
            String type = "<null>";
            if (content != null) {
                type = content.getClass().getName();
            }
            throw new MessagingException("Could not transfrom content of type " + type + " into MimeMessage.");
        }
        if (contentBytes != null) {
            ByteArrayInputStream in = new ByteArrayInputStream(contentBytes);
            return new MimeMessage(session, (InputStream)in);
        }
        return null;
    }

    public Object getContentWithSignature() {
        return this.message;
    }

    static {
        final MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
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

