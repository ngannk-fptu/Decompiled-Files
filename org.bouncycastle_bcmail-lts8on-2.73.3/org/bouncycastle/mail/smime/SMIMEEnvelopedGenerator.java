/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.CommandMap
 *  javax.activation.MailcapCommandMap
 *  javax.mail.MessagingException
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.cms.CMSEnvelopedDataGenerator
 *  org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator
 *  org.bouncycastle.cms.CMSException
 *  org.bouncycastle.cms.RecipientInfoGenerator
 *  org.bouncycastle.operator.OutputEncryptor
 */
package org.bouncycastle.mail.smime;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.mail.smime.MailcapUtil;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMEGenerator;
import org.bouncycastle.mail.smime.SMIMEStreamingProcessor;
import org.bouncycastle.operator.OutputEncryptor;

public class SMIMEEnvelopedGenerator
extends SMIMEGenerator {
    public static final String DES_EDE3_CBC = CMSEnvelopedDataGenerator.DES_EDE3_CBC;
    public static final String RC2_CBC = CMSEnvelopedDataGenerator.RC2_CBC;
    public static final String IDEA_CBC = CMSEnvelopedDataGenerator.IDEA_CBC;
    public static final String CAST5_CBC = CMSEnvelopedDataGenerator.CAST5_CBC;
    public static final String AES128_CBC = CMSEnvelopedDataGenerator.AES128_CBC;
    public static final String AES192_CBC = CMSEnvelopedDataGenerator.AES192_CBC;
    public static final String AES256_CBC = CMSEnvelopedDataGenerator.AES256_CBC;
    public static final String CAMELLIA128_CBC = CMSEnvelopedDataGenerator.CAMELLIA128_CBC;
    public static final String CAMELLIA192_CBC = CMSEnvelopedDataGenerator.CAMELLIA192_CBC;
    public static final String CAMELLIA256_CBC = CMSEnvelopedDataGenerator.CAMELLIA256_CBC;
    public static final String SEED_CBC = CMSEnvelopedDataGenerator.SEED_CBC;
    public static final String DES_EDE3_WRAP = CMSEnvelopedDataGenerator.DES_EDE3_WRAP;
    public static final String AES128_WRAP = CMSEnvelopedDataGenerator.AES128_WRAP;
    public static final String AES256_WRAP = CMSEnvelopedDataGenerator.AES256_WRAP;
    public static final String CAMELLIA128_WRAP = CMSEnvelopedDataGenerator.CAMELLIA128_WRAP;
    public static final String CAMELLIA192_WRAP = CMSEnvelopedDataGenerator.CAMELLIA192_WRAP;
    public static final String CAMELLIA256_WRAP = CMSEnvelopedDataGenerator.CAMELLIA256_WRAP;
    public static final String SEED_WRAP = CMSEnvelopedDataGenerator.SEED_WRAP;
    public static final String ECDH_SHA1KDF = CMSEnvelopedDataGenerator.ECDH_SHA1KDF;
    private static final String ENCRYPTED_CONTENT_TYPE = "application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data";
    private EnvelopedGenerator fact = new EnvelopedGenerator();

    public void addRecipientInfoGenerator(RecipientInfoGenerator recipientInfoGen) throws IllegalArgumentException {
        this.fact.addRecipientInfoGenerator(recipientInfoGen);
    }

    public void setBerEncodeRecipients(boolean berEncodeRecipientSet) {
        this.fact.setBEREncodeRecipients(berEncodeRecipientSet);
    }

    private MimeBodyPart make(MimeBodyPart content, OutputEncryptor encryptor) throws SMIMEException {
        try {
            MimeBodyPart data = new MimeBodyPart();
            data.setContent((Object)new ContentEncryptor(content, encryptor), ENCRYPTED_CONTENT_TYPE);
            data.addHeader("Content-Type", ENCRYPTED_CONTENT_TYPE);
            data.addHeader("Content-Disposition", "attachment; filename=\"smime.p7m\"");
            data.addHeader("Content-Description", "S/MIME Encrypted Message");
            data.addHeader("Content-Transfer-Encoding", this.encoding);
            return data;
        }
        catch (MessagingException e) {
            throw new SMIMEException("exception putting multi-part together.", (Exception)((Object)e));
        }
    }

    public MimeBodyPart generate(MimeBodyPart content, OutputEncryptor encryptor) throws SMIMEException {
        return this.make(this.makeContentBodyPart(content), encryptor);
    }

    public MimeBodyPart generate(MimeMessage message, OutputEncryptor encryptor) throws SMIMEException {
        try {
            message.saveChanges();
        }
        catch (MessagingException e) {
            throw new SMIMEException("unable to save message", (Exception)((Object)e));
        }
        return this.make(this.makeContentBodyPart(message), encryptor);
    }

    static {
        AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                CommandMap commandMap = CommandMap.getDefaultCommandMap();
                if (commandMap instanceof MailcapCommandMap) {
                    CommandMap.setDefaultCommandMap((CommandMap)MailcapUtil.addCommands((MailcapCommandMap)commandMap));
                }
                return null;
            }
        });
    }

    private class ContentEncryptor
    implements SMIMEStreamingProcessor {
        private final MimeBodyPart _content;
        private OutputEncryptor _encryptor;
        private boolean _firstTime = true;

        ContentEncryptor(MimeBodyPart content, OutputEncryptor encryptor) {
            this._content = content;
            this._encryptor = encryptor;
        }

        @Override
        public void write(OutputStream out) throws IOException {
            try {
                OutputStream encrypted;
                if (this._firstTime) {
                    encrypted = SMIMEEnvelopedGenerator.this.fact.open(out, this._encryptor);
                    this._firstTime = false;
                } else {
                    encrypted = SMIMEEnvelopedGenerator.this.fact.regenerate(out, this._encryptor);
                }
                CommandMap commandMap = CommandMap.getDefaultCommandMap();
                if (commandMap instanceof MailcapCommandMap) {
                    this._content.getDataHandler().setCommandMap((CommandMap)MailcapUtil.addCommands((MailcapCommandMap)commandMap));
                }
                this._content.writeTo(encrypted);
                encrypted.close();
            }
            catch (MessagingException e) {
                throw new WrappingIOException(e.toString(), e);
            }
            catch (CMSException e) {
                throw new WrappingIOException(e.toString(), e);
            }
        }
    }

    private static class EnvelopedGenerator
    extends CMSEnvelopedDataStreamGenerator {
        private ASN1ObjectIdentifier dataType;
        private ASN1EncodableVector recipientInfos;

        private EnvelopedGenerator() {
        }

        protected OutputStream open(ASN1ObjectIdentifier dataType, OutputStream out, ASN1EncodableVector recipientInfos, OutputEncryptor encryptor) throws IOException {
            this.dataType = dataType;
            this.recipientInfos = recipientInfos;
            return super.open(dataType, out, recipientInfos, encryptor);
        }

        OutputStream regenerate(OutputStream out, OutputEncryptor encryptor) throws IOException {
            return super.open(this.dataType, out, this.recipientInfos, encryptor);
        }
    }

    private static class WrappingIOException
    extends IOException {
        private Throwable cause;

        WrappingIOException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}

