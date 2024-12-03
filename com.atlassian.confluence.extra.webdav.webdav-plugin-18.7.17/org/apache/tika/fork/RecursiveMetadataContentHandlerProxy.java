/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.tika.fork.ForkProxy;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.sax.ContentHandlerFactory;
import org.apache.tika.sax.RecursiveParserWrapperHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

class RecursiveMetadataContentHandlerProxy
extends RecursiveParserWrapperHandler
implements ForkProxy {
    public static final byte EMBEDDED_DOCUMENT = 1;
    public static final byte MAIN_DOCUMENT = 2;
    public static final byte HANDLER_AND_METADATA = 3;
    public static final byte METADATA_ONLY = 4;
    public static final byte COMPLETE = 5;
    private static final long serialVersionUID = 737511106054617524L;
    private final int resource;
    private transient DataOutputStream output;

    public RecursiveMetadataContentHandlerProxy(int resource, ContentHandlerFactory contentHandlerFactory) {
        super(contentHandlerFactory);
        this.resource = resource;
    }

    @Override
    public void init(DataInputStream input, DataOutputStream output) {
        this.output = output;
    }

    @Override
    public void endEmbeddedDocument(ContentHandler contentHandler, Metadata metadata) throws SAXException {
        this.proxyBackToClient(1, contentHandler, metadata);
    }

    @Override
    public void endDocument(ContentHandler contentHandler, Metadata metadata) throws SAXException {
        if (this.hasHitMaximumEmbeddedResources()) {
            metadata.set(EMBEDDED_RESOURCE_LIMIT_REACHED, "true");
        }
        this.proxyBackToClient(2, contentHandler, metadata);
    }

    private void proxyBackToClient(int embeddedOrMainDocument, ContentHandler contentHandler, Metadata metadata) throws SAXException {
        try {
            this.output.write(3);
            this.output.writeByte(this.resource);
            this.output.writeByte(embeddedOrMainDocument);
            boolean success = false;
            if (contentHandler instanceof Serializable) {
                byte[] bytes = null;
                try {
                    bytes = this.serialize(contentHandler);
                    success = true;
                }
                catch (NotSerializableException notSerializableException) {
                    // empty catch block
                }
                if (success) {
                    this.output.write(3);
                    this.sendBytes(bytes);
                    this.send(metadata);
                    this.output.writeByte(5);
                    return;
                }
            }
            metadata.set(TikaCoreProperties.TIKA_CONTENT, contentHandler.toString());
            this.output.writeByte(4);
            this.send(metadata);
            this.output.writeByte(5);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        finally {
            this.doneSending();
        }
    }

    private void send(Object object) throws IOException {
        byte[] bytes = this.serialize(object);
        this.sendBytes(bytes);
    }

    private void sendBytes(byte[] bytes) throws IOException {
        this.output.writeInt(bytes.length);
        this.output.write(bytes);
        this.output.flush();
    }

    private byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.flush();
        oos.close();
        return bos.toByteArray();
    }

    private void doneSending() throws SAXException {
        try {
            this.output.flush();
        }
        catch (IOException e) {
            throw new SAXException("Unexpected fork proxy problem", e);
        }
    }
}

