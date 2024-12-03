/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.attachment.AttachmentMarshaller
 *  javax.xml.bind.attachment.AttachmentUnmarshaller
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.DatabindingException;
import com.sun.xml.ws.spi.db.RepeatedElementBridge;
import com.sun.xml.ws.spi.db.TypeInfo;
import com.sun.xml.ws.spi.db.WrapperComposite;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class WrapperBridge<T>
implements XMLBridge<T> {
    BindingContext parent;
    TypeInfo typeInfo;
    static final String WrapperPrefix = "w";
    static final String WrapperPrefixColon = "w:";

    public WrapperBridge(BindingContext p, TypeInfo ti) {
        this.parent = p;
        this.typeInfo = ti;
    }

    @Override
    public BindingContext context() {
        return this.parent;
    }

    @Override
    public TypeInfo getTypeInfo() {
        return this.typeInfo;
    }

    @Override
    public final void marshal(T object, ContentHandler contentHandler, AttachmentMarshaller am) throws JAXBException {
        WrapperComposite w = (WrapperComposite)object;
        Attributes att = new Attributes(){

            @Override
            public int getLength() {
                return 0;
            }

            @Override
            public String getURI(int index) {
                return null;
            }

            @Override
            public String getLocalName(int index) {
                return null;
            }

            @Override
            public String getQName(int index) {
                return null;
            }

            @Override
            public String getType(int index) {
                return null;
            }

            @Override
            public String getValue(int index) {
                return null;
            }

            @Override
            public int getIndex(String uri, String localName) {
                return 0;
            }

            @Override
            public int getIndex(String qName) {
                return 0;
            }

            @Override
            public String getType(String uri, String localName) {
                return null;
            }

            @Override
            public String getType(String qName) {
                return null;
            }

            @Override
            public String getValue(String uri, String localName) {
                return null;
            }

            @Override
            public String getValue(String qName) {
                return null;
            }
        };
        try {
            contentHandler.startPrefixMapping(WrapperPrefix, this.typeInfo.tagName.getNamespaceURI());
            contentHandler.startElement(this.typeInfo.tagName.getNamespaceURI(), this.typeInfo.tagName.getLocalPart(), WrapperPrefixColon + this.typeInfo.tagName.getLocalPart(), att);
        }
        catch (SAXException e) {
            throw new JAXBException((Throwable)e);
        }
        if (w.bridges != null) {
            for (int i = 0; i < w.bridges.length; ++i) {
                if (w.bridges[i] instanceof RepeatedElementBridge) {
                    RepeatedElementBridge rbridge = (RepeatedElementBridge)w.bridges[i];
                    Iterator itr = rbridge.collectionHandler().iterator(w.values[i]);
                    while (itr.hasNext()) {
                        rbridge.marshal(itr.next(), contentHandler, am);
                    }
                    continue;
                }
                w.bridges[i].marshal(w.values[i], contentHandler, am);
            }
        }
        try {
            contentHandler.endElement(this.typeInfo.tagName.getNamespaceURI(), this.typeInfo.tagName.getLocalPart(), null);
            contentHandler.endPrefixMapping(WrapperPrefix);
        }
        catch (SAXException e) {
            throw new JAXBException((Throwable)e);
        }
    }

    @Override
    public void marshal(T object, Node output) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void marshal(T object, OutputStream output, NamespaceContext nsContext, AttachmentMarshaller am) throws JAXBException {
    }

    @Override
    public final void marshal(T object, Result result) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void marshal(T object, XMLStreamWriter output, AttachmentMarshaller am) throws JAXBException {
        WrapperComposite w = (WrapperComposite)object;
        try {
            String prefix = output.getPrefix(this.typeInfo.tagName.getNamespaceURI());
            if (prefix == null) {
                prefix = WrapperPrefix;
            }
            output.writeStartElement(prefix, this.typeInfo.tagName.getLocalPart(), this.typeInfo.tagName.getNamespaceURI());
            output.writeNamespace(prefix, this.typeInfo.tagName.getNamespaceURI());
        }
        catch (XMLStreamException e) {
            e.printStackTrace();
            throw new DatabindingException(e);
        }
        if (w.bridges != null) {
            for (int i = 0; i < w.bridges.length; ++i) {
                if (w.bridges[i] instanceof RepeatedElementBridge) {
                    RepeatedElementBridge rbridge = (RepeatedElementBridge)w.bridges[i];
                    Iterator itr = rbridge.collectionHandler().iterator(w.values[i]);
                    while (itr.hasNext()) {
                        rbridge.marshal(itr.next(), output, am);
                    }
                    continue;
                }
                w.bridges[i].marshal(w.values[i], output, am);
            }
        }
        try {
            output.writeEndElement();
        }
        catch (XMLStreamException e) {
            throw new DatabindingException(e);
        }
    }

    @Override
    public final T unmarshal(InputStream in) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final T unmarshal(Node n, AttachmentUnmarshaller au) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final T unmarshal(Source in, AttachmentUnmarshaller au) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final T unmarshal(XMLStreamReader in, AttachmentUnmarshaller au) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportOutputStream() {
        return false;
    }
}

