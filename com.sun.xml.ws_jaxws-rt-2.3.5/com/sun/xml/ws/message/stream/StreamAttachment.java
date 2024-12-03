/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.xml.soap.AttachmentPart
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 *  org.jvnet.staxex.Base64Data
 */
package com.sun.xml.ws.message.stream;

import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.ws.util.ByteArrayBuffer;
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
import org.jvnet.staxex.Base64Data;

public class StreamAttachment
implements Attachment {
    private final String contentId;
    private final String contentType;
    private final ByteArrayBuffer byteArrayBuffer;
    private final byte[] data;
    private final int len;

    public StreamAttachment(ByteArrayBuffer buffer, String contentId, String contentType) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.byteArrayBuffer = buffer;
        this.data = this.byteArrayBuffer.getRawData();
        this.len = this.byteArrayBuffer.size();
    }

    @Override
    public String getContentId() {
        return this.contentId;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public byte[] asByteArray() {
        return this.byteArrayBuffer.toByteArray();
    }

    @Override
    public DataHandler asDataHandler() {
        return new DataSourceStreamingDataHandler(new ByteArrayDataSource(this.data, 0, this.len, this.getContentType()));
    }

    @Override
    public Source asSource() {
        return new StreamSource(new ByteArrayInputStream(this.data, 0, this.len));
    }

    @Override
    public InputStream asInputStream() {
        return this.byteArrayBuffer.newInputStream();
    }

    public Base64Data asBase64Data() {
        Base64Data base64Data = new Base64Data();
        base64Data.set(this.data, this.len, this.contentType);
        return base64Data;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        this.byteArrayBuffer.writeTo(os);
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        AttachmentPart part = saaj.createAttachmentPart();
        part.setRawContentBytes(this.data, 0, this.len, this.getContentType());
        part.setContentId(this.contentId);
        saaj.addAttachmentPart(part);
    }
}

