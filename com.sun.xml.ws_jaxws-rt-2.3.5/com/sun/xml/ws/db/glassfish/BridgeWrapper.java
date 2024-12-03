/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.api.Bridge
 *  com.sun.xml.bind.api.JAXBRIContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.attachment.AttachmentMarshaller
 *  javax.xml.bind.attachment.AttachmentUnmarshaller
 */
package com.sun.xml.ws.db.glassfish;

import com.sun.xml.bind.api.Bridge;
import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.ws.db.glassfish.JAXBRIContextWrapper;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.DatabindingException;
import com.sun.xml.ws.spi.db.TypeInfo;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class BridgeWrapper<T>
implements XMLBridge<T> {
    private JAXBRIContextWrapper parent;
    private Bridge<T> bridge;

    public BridgeWrapper(JAXBRIContextWrapper p, Bridge<T> b) {
        this.parent = p;
        this.bridge = b;
    }

    @Override
    public BindingContext context() {
        return this.parent;
    }

    Bridge getBridge() {
        return this.bridge;
    }

    public boolean equals(Object obj) {
        return this.bridge.equals(obj);
    }

    public JAXBRIContext getContext() {
        return this.bridge.getContext();
    }

    @Override
    public TypeInfo getTypeInfo() {
        return this.parent.typeInfo(this.bridge.getTypeReference());
    }

    public int hashCode() {
        return this.bridge.hashCode();
    }

    public void marshal(Marshaller m, T object, ContentHandler contentHandler) throws JAXBException {
        this.bridge.marshal(m, object, contentHandler);
    }

    public void marshal(Marshaller m, T object, Node output) throws JAXBException {
        this.bridge.marshal(m, object, output);
    }

    public void marshal(Marshaller m, T object, OutputStream output, NamespaceContext nsContext) throws JAXBException {
        this.bridge.marshal(m, object, output, nsContext);
    }

    public void marshal(Marshaller m, T object, Result result) throws JAXBException {
        this.bridge.marshal(m, object, result);
    }

    public void marshal(Marshaller m, T object, XMLStreamWriter output) throws JAXBException {
        this.bridge.marshal(m, object, output);
    }

    @Override
    public final void marshal(T object, ContentHandler contentHandler, AttachmentMarshaller am) throws JAXBException {
        this.bridge.marshal(object, contentHandler, am);
    }

    public void marshal(T object, ContentHandler contentHandler) throws JAXBException {
        this.bridge.marshal(object, contentHandler);
    }

    @Override
    public void marshal(T object, Node output) throws JAXBException {
        this.bridge.marshal(object, output);
    }

    @Override
    public void marshal(T object, OutputStream output, NamespaceContext nsContext, AttachmentMarshaller am) throws JAXBException {
        this.bridge.marshal(object, output, nsContext, am);
    }

    public void marshal(T object, OutputStream output, NamespaceContext nsContext) throws JAXBException {
        this.bridge.marshal(object, output, nsContext);
    }

    @Override
    public final void marshal(T object, Result result) throws JAXBException {
        this.bridge.marshal(object, result);
    }

    @Override
    public final void marshal(T object, XMLStreamWriter output, AttachmentMarshaller am) throws JAXBException {
        this.bridge.marshal(object, output, am);
    }

    public final void marshal(T object, XMLStreamWriter output) throws JAXBException {
        this.bridge.marshal(object, output);
    }

    public String toString() {
        return BridgeWrapper.class.getName() + " : " + this.bridge.toString();
    }

    @Override
    public final T unmarshal(InputStream in) throws JAXBException {
        return (T)this.bridge.unmarshal(in);
    }

    @Override
    public final T unmarshal(Node n, AttachmentUnmarshaller au) throws JAXBException {
        return (T)this.bridge.unmarshal(n, au);
    }

    public final T unmarshal(Node n) throws JAXBException {
        return (T)this.bridge.unmarshal(n);
    }

    @Override
    public final T unmarshal(Source in, AttachmentUnmarshaller au) throws JAXBException {
        return (T)this.bridge.unmarshal(in, au);
    }

    public final T unmarshal(Source in) throws DatabindingException {
        try {
            return (T)this.bridge.unmarshal(in);
        }
        catch (JAXBException e) {
            throw new DatabindingException(e);
        }
    }

    public T unmarshal(Unmarshaller u, InputStream in) throws JAXBException {
        return (T)this.bridge.unmarshal(u, in);
    }

    public T unmarshal(Unmarshaller context, Node n) throws JAXBException {
        return (T)this.bridge.unmarshal(context, n);
    }

    public T unmarshal(Unmarshaller u, Source in) throws JAXBException {
        return (T)this.bridge.unmarshal(u, in);
    }

    public T unmarshal(Unmarshaller u, XMLStreamReader in) throws JAXBException {
        return (T)this.bridge.unmarshal(u, in);
    }

    @Override
    public final T unmarshal(XMLStreamReader in, AttachmentUnmarshaller au) throws JAXBException {
        return (T)this.bridge.unmarshal(in, au);
    }

    public final T unmarshal(XMLStreamReader in) throws JAXBException {
        return (T)this.bridge.unmarshal(in);
    }

    @Override
    public boolean supportOutputStream() {
        return true;
    }
}

