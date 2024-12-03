/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.mime.smime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeIOException;
import org.bouncycastle.mime.MimeWriter;
import org.bouncycastle.mime.encoding.Base64OutputStream;
import org.bouncycastle.mime.smime.SMimeUtils;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.Strings;

public class SMIMEEnvelopedWriter
extends MimeWriter {
    private final CMSEnvelopedDataStreamGenerator envGen;
    private final OutputEncryptor outEnc;
    private final OutputStream mimeOut;
    private final String contentTransferEncoding;

    private SMIMEEnvelopedWriter(Builder builder, OutputEncryptor outEnc, OutputStream mimeOut) {
        super(new Headers(SMIMEEnvelopedWriter.mapToLines(builder.headers), builder.contentTransferEncoding));
        this.envGen = builder.envGen;
        this.contentTransferEncoding = builder.contentTransferEncoding;
        this.outEnc = outEnc;
        this.mimeOut = mimeOut;
    }

    @Override
    public OutputStream getContentStream() throws IOException {
        this.headers.dumpHeaders(this.mimeOut);
        this.mimeOut.write(Strings.toByteArray((String)"\r\n"));
        try {
            OutputStream backing = this.mimeOut;
            if ("base64".equals(this.contentTransferEncoding)) {
                backing = new Base64OutputStream(backing);
            }
            OutputStream main = this.envGen.open(SMimeUtils.createUnclosable(backing), this.outEnc);
            return new ContentOutputStream(main, backing);
        }
        catch (CMSException e) {
            throw new MimeIOException(e.getMessage(), e);
        }
    }

    public static class Builder {
        private static final String[] stdHeaders = new String[]{"Content-Type", "Content-Disposition", "Content-Transfer-Encoding", "Content-Description"};
        private static final String[] stdValues = new String[]{"application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data", "attachment; filename=\"smime.p7m\"", "base64", "S/MIME Encrypted Message"};
        private final CMSEnvelopedDataStreamGenerator envGen = new CMSEnvelopedDataStreamGenerator();
        private final Map<String, String> headers = new LinkedHashMap<String, String>();
        String contentTransferEncoding = "base64";

        public Builder() {
            for (int i = 0; i != stdHeaders.length; ++i) {
                this.headers.put(stdHeaders[i], stdValues[i]);
            }
        }

        public Builder setBufferSize(int bufferSize) {
            this.envGen.setBufferSize(bufferSize);
            return this;
        }

        public Builder setUnprotectedAttributeGenerator(CMSAttributeTableGenerator unprotectedAttributeGenerator) {
            this.envGen.setUnprotectedAttributeGenerator(unprotectedAttributeGenerator);
            return this;
        }

        public Builder setOriginatorInfo(OriginatorInformation originatorInfo) {
            this.envGen.setOriginatorInfo(originatorInfo);
            return this;
        }

        public Builder withHeader(String headerName, String headerValue) {
            this.headers.put(headerName, headerValue);
            return this;
        }

        public Builder addRecipientInfoGenerator(RecipientInfoGenerator recipientGenerator) {
            this.envGen.addRecipientInfoGenerator(recipientGenerator);
            return this;
        }

        public SMIMEEnvelopedWriter build(OutputStream mimeOut, OutputEncryptor outEnc) {
            return new SMIMEEnvelopedWriter(this, outEnc, SMimeUtils.autoBuffer(mimeOut));
        }
    }

    private static class ContentOutputStream
    extends OutputStream {
        private final OutputStream main;
        private final OutputStream backing;

        ContentOutputStream(OutputStream main, OutputStream backing) {
            this.main = main;
            this.backing = backing;
        }

        @Override
        public void write(byte[] buf) throws IOException {
            this.main.write(buf);
        }

        @Override
        public void write(byte[] buf, int off, int len) throws IOException {
            this.main.write(buf, off, len);
        }

        @Override
        public void write(int i) throws IOException {
            this.main.write(i);
        }

        @Override
        public void close() throws IOException {
            this.main.close();
            if (this.backing != null) {
                this.backing.close();
            }
        }
    }
}

