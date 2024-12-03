/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.MarshalException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.UnmarshalException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package com.sun.xml.bind.v2.runtime;

import com.sun.istack.NotNull;
import com.sun.xml.bind.api.TypeReference;
import com.sun.xml.bind.v2.runtime.InternalBridge;
import com.sun.xml.bind.v2.runtime.MarshallerImpl;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

final class BridgeAdapter<OnWire, InMemory>
extends InternalBridge<InMemory> {
    private final InternalBridge<OnWire> core;
    private final Class<? extends XmlAdapter<OnWire, InMemory>> adapter;

    public BridgeAdapter(InternalBridge<OnWire> core, Class<? extends XmlAdapter<OnWire, InMemory>> adapter) {
        super(core.getContext());
        this.core = core;
        this.adapter = adapter;
    }

    @Override
    public void marshal(Marshaller m, InMemory inMemory, XMLStreamWriter output) throws JAXBException {
        this.core.marshal(m, this.adaptM(m, inMemory), output);
    }

    @Override
    public void marshal(Marshaller m, InMemory inMemory, OutputStream output, NamespaceContext nsc) throws JAXBException {
        this.core.marshal(m, this.adaptM(m, inMemory), output, nsc);
    }

    @Override
    public void marshal(Marshaller m, InMemory inMemory, Node output) throws JAXBException {
        this.core.marshal(m, this.adaptM(m, inMemory), output);
    }

    @Override
    public void marshal(Marshaller context, InMemory inMemory, ContentHandler contentHandler) throws JAXBException {
        this.core.marshal(context, this.adaptM(context, inMemory), contentHandler);
    }

    @Override
    public void marshal(Marshaller context, InMemory inMemory, Result result) throws JAXBException {
        this.core.marshal(context, this.adaptM(context, inMemory), result);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private OnWire adaptM(Marshaller m, InMemory v) throws JAXBException {
        XMLSerializer serializer = ((MarshallerImpl)m).serializer;
        serializer.pushCoordinator();
        try {
            OnWire OnWire = this._adaptM(serializer, v);
            return OnWire;
        }
        finally {
            serializer.popCoordinator();
        }
    }

    private OnWire _adaptM(XMLSerializer serializer, InMemory v) throws MarshalException {
        XmlAdapter<OnWire, InMemory> a = serializer.getAdapter(this.adapter);
        try {
            return (OnWire)a.marshal(v);
        }
        catch (Exception e) {
            serializer.handleError(e, v, null);
            throw new MarshalException((Throwable)e);
        }
    }

    @Override
    @NotNull
    public InMemory unmarshal(Unmarshaller u, XMLStreamReader in) throws JAXBException {
        return this.adaptU(u, this.core.unmarshal(u, in));
    }

    @Override
    @NotNull
    public InMemory unmarshal(Unmarshaller u, Source in) throws JAXBException {
        return this.adaptU(u, this.core.unmarshal(u, in));
    }

    @Override
    @NotNull
    public InMemory unmarshal(Unmarshaller u, InputStream in) throws JAXBException {
        return this.adaptU(u, this.core.unmarshal(u, in));
    }

    @Override
    @NotNull
    public InMemory unmarshal(Unmarshaller u, Node n) throws JAXBException {
        return this.adaptU(u, this.core.unmarshal(u, n));
    }

    @Override
    public TypeReference getTypeReference() {
        return this.core.getTypeReference();
    }

    @NotNull
    private InMemory adaptU(Unmarshaller _u, OnWire v) throws JAXBException {
        UnmarshallerImpl u = (UnmarshallerImpl)_u;
        XmlAdapter<OnWire, InMemory> a = u.coordinator.getAdapter(this.adapter);
        u.coordinator.pushCoordinator();
        try {
            Object object = a.unmarshal(v);
            return (InMemory)object;
        }
        catch (Exception e) {
            throw new UnmarshalException((Throwable)e);
        }
        finally {
            u.coordinator.popCoordinator();
        }
    }

    @Override
    void marshal(InMemory o, XMLSerializer out) throws IOException, SAXException, XMLStreamException {
        try {
            this.core.marshal(this._adaptM(XMLSerializer.getInstance(), o), out);
        }
        catch (MarshalException marshalException) {
            // empty catch block
        }
    }
}

