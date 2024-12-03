/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.mail.Header
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  org.bouncycastle.cms.CMSEnvelopedGenerator
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.mail.smime;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.activation.DataHandler;
import javax.crypto.KeyGenerator;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import org.bouncycastle.cms.CMSEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.util.Strings;

public class SMIMEGenerator {
    private static Map BASE_CIPHER_NAMES = new HashMap();
    protected boolean useBase64 = true;
    protected String encoding = "base64";

    protected SMIMEGenerator() {
    }

    public void setContentTransferEncoding(String encoding) {
        this.encoding = encoding;
        this.useBase64 = Strings.toLowerCase((String)encoding).equals("base64");
    }

    protected MimeBodyPart makeContentBodyPart(MimeBodyPart content) throws SMIMEException {
        try {
            Header hdr;
            MimeMessage msg = new MimeMessage(null){

                protected void updateMessageID() throws MessagingException {
                }
            };
            Enumeration e = content.getAllHeaders();
            msg.setDataHandler(content.getDataHandler());
            while (e.hasMoreElements()) {
                hdr = (Header)e.nextElement();
                msg.setHeader(hdr.getName(), hdr.getValue());
            }
            msg.saveChanges();
            e = msg.getAllHeaders();
            while (e.hasMoreElements()) {
                hdr = (Header)e.nextElement();
                if (!Strings.toLowerCase((String)hdr.getName()).startsWith("content-")) continue;
                content.setHeader(hdr.getName(), hdr.getValue());
            }
        }
        catch (MessagingException e) {
            throw new SMIMEException("exception saving message state.", (Exception)((Object)e));
        }
        return content;
    }

    protected MimeBodyPart makeContentBodyPart(MimeMessage message) throws SMIMEException {
        MimeBodyPart content = new MimeBodyPart();
        try {
            try {
                if (message.getContent() instanceof Multipart) {
                    content.setContent((Object)message.getRawInputStream(), message.getContentType());
                    this.extractHeaders(content, message);
                    return content;
                }
            }
            catch (MessagingException messagingException) {
                // empty catch block
            }
            content.setContent(message.getContent(), message.getContentType());
            content.setDataHandler(new DataHandler(message.getDataHandler().getDataSource()));
            this.extractHeaders(content, message);
        }
        catch (MessagingException e) {
            throw new SMIMEException("exception saving message state.", (Exception)((Object)e));
        }
        catch (IOException e) {
            throw new SMIMEException("exception getting message content.", e);
        }
        return content;
    }

    private void extractHeaders(MimeBodyPart content, MimeMessage message) throws MessagingException {
        Enumeration e = message.getAllHeaders();
        while (e.hasMoreElements()) {
            Header hdr = (Header)e.nextElement();
            if (hdr.getName().equals("Message-Id")) {
                content.addHeader("Message-ID", hdr.getValue());
                continue;
            }
            if (hdr.getName().equals("Mime-Version")) {
                content.addHeader("MIME-Version", hdr.getValue());
                continue;
            }
            content.addHeader(hdr.getName(), hdr.getValue());
        }
    }

    protected KeyGenerator createSymmetricKeyGenerator(String encryptionOID, Provider provider) throws NoSuchAlgorithmException {
        try {
            return this.createKeyGenerator(encryptionOID, provider);
        }
        catch (NoSuchAlgorithmException e) {
            try {
                String algName = (String)BASE_CIPHER_NAMES.get(encryptionOID);
                if (algName != null) {
                    return this.createKeyGenerator(algName, provider);
                }
            }
            catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                // empty catch block
            }
            if (provider != null) {
                return this.createSymmetricKeyGenerator(encryptionOID, null);
            }
            throw e;
        }
    }

    private KeyGenerator createKeyGenerator(String algName, Provider provider) throws NoSuchAlgorithmException {
        if (provider != null) {
            return KeyGenerator.getInstance(algName, provider);
        }
        return KeyGenerator.getInstance(algName);
    }

    static {
        BASE_CIPHER_NAMES.put(CMSEnvelopedGenerator.DES_EDE3_CBC, "DESEDE");
        BASE_CIPHER_NAMES.put(CMSEnvelopedGenerator.AES128_CBC, "AES");
        BASE_CIPHER_NAMES.put(CMSEnvelopedGenerator.AES192_CBC, "AES");
        BASE_CIPHER_NAMES.put(CMSEnvelopedGenerator.AES256_CBC, "AES");
    }
}

