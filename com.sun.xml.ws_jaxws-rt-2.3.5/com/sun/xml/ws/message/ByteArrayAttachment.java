/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.activation.DataHandler
 *  javax.xml.soap.AttachmentPart
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.message;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.ws.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public final class ByteArrayAttachment
implements Attachment {
    private final String contentId;
    private byte[] data;
    private int start;
    private final int len;
    private final String mimeType;

    public ByteArrayAttachment(@NotNull String contentId, byte[] data, int start, int len, String mimeType) {
        this.contentId = contentId;
        this.data = data;
        this.start = start;
        this.len = len;
        this.mimeType = mimeType;
    }

    public ByteArrayAttachment(@NotNull String contentId, byte[] data, String mimeType) {
        this(contentId, data, 0, data.length, mimeType);
    }

    @Override
    public String getContentId() {
        return this.contentId;
    }

    @Override
    public String getContentType() {
        return this.mimeType;
    }

    @Override
    public byte[] asByteArray() {
        if (this.start != 0 || this.len != this.data.length) {
            byte[] exact = new byte[this.len];
            System.arraycopy(this.data, this.start, exact, 0, this.len);
            this.start = 0;
            this.data = exact;
        }
        return this.data;
    }

    @Override
    public DataHandler asDataHandler() {
        return new DataSourceStreamingDataHandler(new ByteArrayDataSource(this.data, this.start, this.len, this.getContentType()));
    }

    @Override
    public Source asSource() {
        return new StreamSource(this.asInputStream());
    }

    @Override
    public InputStream asInputStream() {
        return new ByteArrayInputStream(this.data, this.start, this.len);
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        os.write(this.asByteArray());
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        AttachmentPart part = saaj.createAttachmentPart();
        part.setDataHandler(this.asDataHandler());
        part.setContentId(this.contentId);
        saaj.addAttachmentPart(part);
    }
}

