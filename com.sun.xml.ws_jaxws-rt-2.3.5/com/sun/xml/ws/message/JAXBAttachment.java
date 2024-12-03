/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.xml.bind.JAXBException
 *  javax.xml.soap.AttachmentPart
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.message;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.ws.spi.db.XMLBridge;
import com.sun.xml.ws.util.ByteArrayBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;

public final class JAXBAttachment
implements Attachment,
DataSource {
    private final String contentId;
    private final String mimeType;
    private final Object jaxbObject;
    private final XMLBridge bridge;

    public JAXBAttachment(@NotNull String contentId, Object jaxbObject, XMLBridge bridge, String mimeType) {
        this.contentId = contentId;
        this.jaxbObject = jaxbObject;
        this.bridge = bridge;
        this.mimeType = mimeType;
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
        ByteArrayBuffer bab = new ByteArrayBuffer();
        try {
            this.writeTo(bab);
        }
        catch (IOException e) {
            throw new WebServiceException((Throwable)e);
        }
        return bab.getRawData();
    }

    @Override
    public DataHandler asDataHandler() {
        return new DataSourceStreamingDataHandler(this);
    }

    @Override
    public Source asSource() {
        return new StreamSource(this.asInputStream());
    }

    @Override
    public InputStream asInputStream() {
        ByteArrayBuffer bab = new ByteArrayBuffer();
        try {
            this.writeTo(bab);
        }
        catch (IOException e) {
            throw new WebServiceException((Throwable)e);
        }
        return bab.newInputStream();
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        try {
            this.bridge.marshal(this.jaxbObject, os, null, null);
        }
        catch (JAXBException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        AttachmentPart part = saaj.createAttachmentPart();
        part.setDataHandler(this.asDataHandler());
        part.setContentId(this.contentId);
        saaj.addAttachmentPart(part);
    }

    public InputStream getInputStream() throws IOException {
        return this.asInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return null;
    }
}

