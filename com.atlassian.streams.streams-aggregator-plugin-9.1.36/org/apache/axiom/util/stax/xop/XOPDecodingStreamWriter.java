/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.util.stax.xop;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.util.stax.wrapper.XMLStreamWriterWrapper;
import org.apache.axiom.util.stax.xop.MimePartProvider;

public class XOPDecodingStreamWriter
extends XMLStreamWriterWrapper {
    private final MimePartProvider mimePartProvider;
    private final DataHandlerWriter dataHandlerWriter;
    private boolean inXOPInclude;
    private String contentID;

    public XOPDecodingStreamWriter(XMLStreamWriter parent, MimePartProvider mimePartProvider) {
        super(parent);
        this.mimePartProvider = mimePartProvider;
        this.dataHandlerWriter = (DataHandlerWriter)parent.getProperty(DataHandlerWriter.PROPERTY);
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        if (localName.equals("Include") && namespaceURI.equals("http://www.w3.org/2004/08/xop/include")) {
            this.inXOPInclude = true;
        } else {
            super.writeStartElement(prefix, localName, namespaceURI);
        }
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        if (localName.equals("Include") && namespaceURI.equals("http://www.w3.org/2004/08/xop/include")) {
            this.inXOPInclude = true;
        } else {
            super.writeStartElement(namespaceURI, localName);
        }
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        if (this.inXOPInclude) {
            this.processAttribute(namespaceURI, localName, value);
        } else {
            super.writeAttribute(prefix, namespaceURI, localName, value);
        }
    }

    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        if (this.inXOPInclude) {
            this.processAttribute(namespaceURI, localName, value);
        } else {
            super.writeAttribute(namespaceURI, localName, value);
        }
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        if (this.inXOPInclude) {
            this.processAttribute(null, localName, value);
        } else {
            super.writeAttribute(localName, value);
        }
    }

    private void processAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        if ((namespaceURI == null || namespaceURI.length() == 0) && localName.equals("href")) {
            if (!value.startsWith("cid:")) {
                throw new XMLStreamException("Expected href attribute containing a URL in the cid scheme");
            }
            try {
                this.contentID = URLDecoder.decode(value.substring(4), "ascii");
            }
            catch (UnsupportedEncodingException ex) {
                throw new XMLStreamException(ex);
            }
        } else {
            throw new XMLStreamException("Expected xop:Include element information item with a (single) href attribute");
        }
    }

    public void writeEndElement() throws XMLStreamException {
        if (this.inXOPInclude) {
            DataHandler dh;
            if (this.contentID == null) {
                throw new XMLStreamException("Encountered an xop:Include element without href attribute");
            }
            try {
                dh = this.mimePartProvider.getDataHandler(this.contentID);
            }
            catch (IOException ex) {
                throw new XMLStreamException("Error while fetching data handler", ex);
            }
            try {
                this.dataHandlerWriter.writeDataHandler(dh, this.contentID, true);
            }
            catch (IOException ex) {
                throw new XMLStreamException("Error while writing data handler", ex);
            }
            this.inXOPInclude = false;
            this.contentID = null;
        } else {
            super.writeEndElement();
        }
    }
}

