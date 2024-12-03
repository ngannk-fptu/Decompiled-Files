/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.tika.fork.ForkObjectInputStream;
import org.apache.tika.fork.ForkResource;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.AbstractRecursiveParserWrapperHandler;
import org.apache.tika.sax.RecursiveParserWrapperHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class RecursiveMetadataContentHandlerResource
implements ForkResource {
    private static final ContentHandler DEFAULT_HANDLER = new DefaultHandler();
    private final AbstractRecursiveParserWrapperHandler handler;

    public RecursiveMetadataContentHandlerResource(RecursiveParserWrapperHandler handler) {
        this.handler = handler;
    }

    @Override
    public Throwable process(DataInputStream input, DataOutputStream output) throws IOException {
        try {
            this.internalProcess(input);
            return null;
        }
        catch (SAXException e) {
            return e;
        }
    }

    private void internalProcess(DataInputStream input) throws IOException, SAXException {
        byte embeddedOrMain = input.readByte();
        byte handlerAndMetadataOrMetadataOnly = input.readByte();
        ContentHandler localContentHandler = DEFAULT_HANDLER;
        if (handlerAndMetadataOrMetadataOnly == 3) {
            localContentHandler = (ContentHandler)this.readObject(input);
        } else if (handlerAndMetadataOrMetadataOnly != 4) {
            throw new IllegalArgumentException("Expected HANDLER_AND_METADATA or METADATA_ONLY, but got:" + handlerAndMetadataOrMetadataOnly);
        }
        Metadata metadata = (Metadata)this.readObject(input);
        if (embeddedOrMain == 1) {
            this.handler.endEmbeddedDocument(localContentHandler, metadata);
        } else if (embeddedOrMain == 2) {
            this.handler.endDocument(localContentHandler, metadata);
        } else {
            throw new IllegalArgumentException("Expected either 0x01 or 0x02, but got: " + embeddedOrMain);
        }
        byte isComplete = input.readByte();
        if (isComplete != 5) {
            throw new IOException("Expected the 'complete' signal, but got: " + isComplete);
        }
    }

    private Object readObject(DataInputStream inputStream) throws IOException {
        try {
            return ForkObjectInputStream.readObject(inputStream, this.getClass().getClassLoader());
        }
        catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}

