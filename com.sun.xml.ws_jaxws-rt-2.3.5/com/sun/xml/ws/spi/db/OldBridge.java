/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.bind.api.BridgeContext
 *  com.sun.xml.bind.v2.runtime.BridgeContextImpl
 *  com.sun.xml.bind.v2.runtime.JAXBContextImpl
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.attachment.AttachmentMarshaller
 *  javax.xml.bind.attachment.AttachmentUnmarshaller
 */
package com.sun.xml.ws.spi.db;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.bind.api.BridgeContext;
import com.sun.xml.bind.v2.runtime.BridgeContextImpl;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.TypeInfo;
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

public abstract class OldBridge<T> {
    protected final JAXBContextImpl context;

    protected OldBridge(JAXBContextImpl context) {
        this.context = context;
    }

    @NotNull
    public BindingContext getContext() {
        return null;
    }

    public final void marshal(T object, XMLStreamWriter output) throws JAXBException {
        this.marshal(object, output, null);
    }

    public final void marshal(T object, XMLStreamWriter output, AttachmentMarshaller am) throws JAXBException {
        Marshaller m = (Marshaller)this.context.marshallerPool.take();
        m.setAttachmentMarshaller(am);
        this.marshal(m, object, output);
        m.setAttachmentMarshaller(null);
        this.context.marshallerPool.recycle((Object)m);
    }

    public final void marshal(@NotNull BridgeContext context, T object, XMLStreamWriter output) throws JAXBException {
        this.marshal((Marshaller)((BridgeContextImpl)context).marshaller, object, output);
    }

    public abstract void marshal(@NotNull Marshaller var1, T var2, XMLStreamWriter var3) throws JAXBException;

    public void marshal(T object, OutputStream output, NamespaceContext nsContext) throws JAXBException {
        this.marshal(object, output, nsContext, null);
    }

    public void marshal(T object, OutputStream output, NamespaceContext nsContext, AttachmentMarshaller am) throws JAXBException {
        Marshaller m = (Marshaller)this.context.marshallerPool.take();
        m.setAttachmentMarshaller(am);
        this.marshal(m, object, output, nsContext);
        m.setAttachmentMarshaller(null);
        this.context.marshallerPool.recycle((Object)m);
    }

    public final void marshal(@NotNull BridgeContext context, T object, OutputStream output, NamespaceContext nsContext) throws JAXBException {
        this.marshal((Marshaller)((BridgeContextImpl)context).marshaller, object, output, nsContext);
    }

    public abstract void marshal(@NotNull Marshaller var1, T var2, OutputStream var3, NamespaceContext var4) throws JAXBException;

    public final void marshal(T object, Node output) throws JAXBException {
        Marshaller m = (Marshaller)this.context.marshallerPool.take();
        this.marshal(m, object, output);
        this.context.marshallerPool.recycle((Object)m);
    }

    public final void marshal(@NotNull BridgeContext context, T object, Node output) throws JAXBException {
        this.marshal((Marshaller)((BridgeContextImpl)context).marshaller, object, output);
    }

    public abstract void marshal(@NotNull Marshaller var1, T var2, Node var3) throws JAXBException;

    public final void marshal(T object, ContentHandler contentHandler) throws JAXBException {
        this.marshal(object, contentHandler, null);
    }

    public final void marshal(T object, ContentHandler contentHandler, AttachmentMarshaller am) throws JAXBException {
        Marshaller m = (Marshaller)this.context.marshallerPool.take();
        m.setAttachmentMarshaller(am);
        this.marshal(m, object, contentHandler);
        m.setAttachmentMarshaller(null);
        this.context.marshallerPool.recycle((Object)m);
    }

    public final void marshal(@NotNull BridgeContext context, T object, ContentHandler contentHandler) throws JAXBException {
        this.marshal((Marshaller)((BridgeContextImpl)context).marshaller, object, contentHandler);
    }

    public abstract void marshal(@NotNull Marshaller var1, T var2, ContentHandler var3) throws JAXBException;

    public final void marshal(T object, Result result) throws JAXBException {
        Marshaller m = (Marshaller)this.context.marshallerPool.take();
        this.marshal(m, object, result);
        this.context.marshallerPool.recycle((Object)m);
    }

    public final void marshal(@NotNull BridgeContext context, T object, Result result) throws JAXBException {
        this.marshal((Marshaller)((BridgeContextImpl)context).marshaller, object, result);
    }

    public abstract void marshal(@NotNull Marshaller var1, T var2, Result var3) throws JAXBException;

    private T exit(T r, Unmarshaller u) {
        u.setAttachmentUnmarshaller(null);
        this.context.unmarshallerPool.recycle((Object)u);
        return r;
    }

    @NotNull
    public final T unmarshal(@NotNull XMLStreamReader in) throws JAXBException {
        return this.unmarshal(in, null);
    }

    @NotNull
    public final T unmarshal(@NotNull XMLStreamReader in, @Nullable AttachmentUnmarshaller au) throws JAXBException {
        Unmarshaller u = (Unmarshaller)this.context.unmarshallerPool.take();
        u.setAttachmentUnmarshaller(au);
        return this.exit(this.unmarshal(u, in), u);
    }

    @NotNull
    public final T unmarshal(@NotNull BridgeContext context, @NotNull XMLStreamReader in) throws JAXBException {
        return this.unmarshal((Unmarshaller)((BridgeContextImpl)context).unmarshaller, in);
    }

    @NotNull
    public abstract T unmarshal(@NotNull Unmarshaller var1, @NotNull XMLStreamReader var2) throws JAXBException;

    @NotNull
    public final T unmarshal(@NotNull Source in) throws JAXBException {
        return this.unmarshal(in, null);
    }

    @NotNull
    public final T unmarshal(@NotNull Source in, @Nullable AttachmentUnmarshaller au) throws JAXBException {
        Unmarshaller u = (Unmarshaller)this.context.unmarshallerPool.take();
        u.setAttachmentUnmarshaller(au);
        return this.exit(this.unmarshal(u, in), u);
    }

    @NotNull
    public final T unmarshal(@NotNull BridgeContext context, @NotNull Source in) throws JAXBException {
        return this.unmarshal((Unmarshaller)((BridgeContextImpl)context).unmarshaller, in);
    }

    @NotNull
    public abstract T unmarshal(@NotNull Unmarshaller var1, @NotNull Source var2) throws JAXBException;

    @NotNull
    public final T unmarshal(@NotNull InputStream in) throws JAXBException {
        Unmarshaller u = (Unmarshaller)this.context.unmarshallerPool.take();
        return this.exit(this.unmarshal(u, in), u);
    }

    @NotNull
    public final T unmarshal(@NotNull BridgeContext context, @NotNull InputStream in) throws JAXBException {
        return this.unmarshal((Unmarshaller)((BridgeContextImpl)context).unmarshaller, in);
    }

    @NotNull
    public abstract T unmarshal(@NotNull Unmarshaller var1, @NotNull InputStream var2) throws JAXBException;

    @NotNull
    public final T unmarshal(@NotNull Node n) throws JAXBException {
        return this.unmarshal(n, null);
    }

    @NotNull
    public final T unmarshal(@NotNull Node n, @Nullable AttachmentUnmarshaller au) throws JAXBException {
        Unmarshaller u = (Unmarshaller)this.context.unmarshallerPool.take();
        u.setAttachmentUnmarshaller(au);
        return this.exit(this.unmarshal(u, n), u);
    }

    @NotNull
    public final T unmarshal(@NotNull BridgeContext context, @NotNull Node n) throws JAXBException {
        return this.unmarshal((Unmarshaller)((BridgeContextImpl)context).unmarshaller, n);
    }

    @NotNull
    public abstract T unmarshal(@NotNull Unmarshaller var1, @NotNull Node var2) throws JAXBException;

    public abstract TypeInfo getTypeReference();
}

