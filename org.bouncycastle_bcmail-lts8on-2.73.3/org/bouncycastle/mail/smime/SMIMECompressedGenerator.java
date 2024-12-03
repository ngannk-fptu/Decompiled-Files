/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.CommandMap
 *  javax.activation.MailcapCommandMap
 *  javax.mail.MessagingException
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  org.bouncycastle.cms.CMSCompressedDataGenerator
 *  org.bouncycastle.cms.CMSCompressedDataStreamGenerator
 *  org.bouncycastle.operator.OutputCompressor
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
import org.bouncycastle.cms.CMSCompressedDataGenerator;
import org.bouncycastle.cms.CMSCompressedDataStreamGenerator;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMEGenerator;
import org.bouncycastle.mail.smime.SMIMEStreamingProcessor;
import org.bouncycastle.operator.OutputCompressor;

public class SMIMECompressedGenerator
extends SMIMEGenerator {
    public static final String ZLIB = CMSCompressedDataGenerator.ZLIB;
    private static final String COMPRESSED_CONTENT_TYPE = "application/pkcs7-mime; name=\"smime.p7z\"; smime-type=compressed-data";

    private MimeBodyPart make(MimeBodyPart content, OutputCompressor compressor) throws SMIMEException {
        try {
            MimeBodyPart data = new MimeBodyPart();
            data.setContent((Object)new ContentCompressor(content, compressor), COMPRESSED_CONTENT_TYPE);
            data.addHeader("Content-Type", COMPRESSED_CONTENT_TYPE);
            data.addHeader("Content-Disposition", "attachment; filename=\"smime.p7z\"");
            data.addHeader("Content-Description", "S/MIME Compressed Message");
            data.addHeader("Content-Transfer-Encoding", this.encoding);
            return data;
        }
        catch (MessagingException e) {
            throw new SMIMEException("exception putting multi-part together.", (Exception)((Object)e));
        }
    }

    public MimeBodyPart generate(MimeBodyPart content, OutputCompressor compressor) throws SMIMEException {
        return this.make(this.makeContentBodyPart(content), compressor);
    }

    public MimeBodyPart generate(MimeMessage message, OutputCompressor compressor) throws SMIMEException {
        try {
            message.saveChanges();
        }
        catch (MessagingException e) {
            throw new SMIMEException("unable to save message", (Exception)((Object)e));
        }
        return this.make(this.makeContentBodyPart(message), compressor);
    }

    static {
        CommandMap commandMap = CommandMap.getDefaultCommandMap();
        if (commandMap instanceof MailcapCommandMap) {
            final MailcapCommandMap mc = (MailcapCommandMap)commandMap;
            mc.addMailcap("application/pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
            mc.addMailcap("application/x-pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
            AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    CommandMap.setDefaultCommandMap((CommandMap)mc);
                    return null;
                }
            });
        }
    }

    private static class ContentCompressor
    implements SMIMEStreamingProcessor {
        private final MimeBodyPart content;
        private final OutputCompressor compressor;

        ContentCompressor(MimeBodyPart content, OutputCompressor compressor) {
            this.content = content;
            this.compressor = compressor;
        }

        @Override
        public void write(OutputStream out) throws IOException {
            CMSCompressedDataStreamGenerator cGen = new CMSCompressedDataStreamGenerator();
            OutputStream compressed = cGen.open(out, this.compressor);
            try {
                this.content.writeTo(compressed);
                compressed.close();
            }
            catch (MessagingException e) {
                throw new IOException(e.toString());
            }
        }
    }
}

