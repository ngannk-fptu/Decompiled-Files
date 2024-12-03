/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.activation.DataHandler
 *  javax.xml.soap.AttachmentPart
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.message;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Attachment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;

public final class DataHandlerAttachment
implements Attachment {
    private final DataHandler dh;
    private final String contentId;
    String contentIdNoAngleBracket;

    public DataHandlerAttachment(@NotNull String contentId, @NotNull DataHandler dh) {
        this.dh = dh;
        this.contentId = contentId;
    }

    @Override
    public String getContentId() {
        if (this.contentIdNoAngleBracket == null) {
            this.contentIdNoAngleBracket = this.contentId;
            if (this.contentIdNoAngleBracket != null && this.contentIdNoAngleBracket.charAt(0) == '<') {
                this.contentIdNoAngleBracket = this.contentIdNoAngleBracket.substring(1, this.contentIdNoAngleBracket.length() - 1);
            }
        }
        return this.contentIdNoAngleBracket;
    }

    @Override
    public String getContentType() {
        return this.dh.getContentType();
    }

    @Override
    public byte[] asByteArray() {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            this.dh.writeTo((OutputStream)os);
            return os.toByteArray();
        }
        catch (IOException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    public DataHandler asDataHandler() {
        return this.dh;
    }

    @Override
    public Source asSource() {
        try {
            return new StreamSource(this.dh.getInputStream());
        }
        catch (IOException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    public InputStream asInputStream() {
        try {
            return this.dh.getInputStream();
        }
        catch (IOException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        this.dh.writeTo(os);
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        AttachmentPart part = saaj.createAttachmentPart();
        part.setDataHandler(this.dh);
        part.setContentId(this.contentId);
        saaj.addAttachmentPart(part);
    }
}

