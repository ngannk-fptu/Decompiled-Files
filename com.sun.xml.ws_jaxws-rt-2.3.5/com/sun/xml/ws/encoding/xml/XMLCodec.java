/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.encoding.xml;

import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.encoding.xml.XMLMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

public final class XMLCodec
implements Codec {
    public static final String XML_APPLICATION_MIME_TYPE = "application/xml";
    public static final String XML_TEXT_MIME_TYPE = "text/xml";
    private static final ContentType contentType = new ContentTypeImpl("text/xml");
    private WSFeatureList features;

    public XMLCodec(WSFeatureList f) {
        this.features = f;
    }

    @Override
    public String getMimeType() {
        return XML_APPLICATION_MIME_TYPE;
    }

    @Override
    public ContentType getStaticContentType(Packet packet) {
        return contentType;
    }

    @Override
    public ContentType encode(Packet packet, OutputStream out) {
        String encoding = (String)packet.invocationProperties.get("com.sun.jaxws.rest.contenttype");
        XMLStreamWriter writer = null;
        writer = encoding != null && encoding.length() > 0 ? XMLStreamWriterFactory.create(out, encoding) : XMLStreamWriterFactory.create(out);
        try {
            if (packet.getMessage().hasPayload()) {
                writer.writeStartDocument();
                packet.getMessage().writePayloadTo(writer);
                writer.flush();
            }
        }
        catch (XMLStreamException e) {
            throw new WebServiceException((Throwable)e);
        }
        return contentType;
    }

    @Override
    public ContentType encode(Packet packet, WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Codec copy() {
        return this;
    }

    @Override
    public void decode(InputStream in, String contentType, Packet packet) throws IOException {
        Message message = XMLMessage.create(contentType, in, this.features);
        packet.setMessage(message);
    }

    @Override
    public void decode(ReadableByteChannel in, String contentType, Packet packet) {
        throw new UnsupportedOperationException();
    }
}

