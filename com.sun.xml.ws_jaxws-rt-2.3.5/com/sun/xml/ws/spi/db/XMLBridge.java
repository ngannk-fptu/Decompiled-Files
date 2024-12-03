/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.attachment.AttachmentMarshaller
 *  javax.xml.bind.attachment.AttachmentUnmarshaller
 */
package com.sun.xml.ws.spi.db;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.TypeInfo;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public interface XMLBridge<T> {
    @NotNull
    public BindingContext context();

    public void marshal(T var1, XMLStreamWriter var2, AttachmentMarshaller var3) throws JAXBException;

    public void marshal(T var1, OutputStream var2, NamespaceContext var3, AttachmentMarshaller var4) throws JAXBException;

    public void marshal(T var1, Node var2) throws JAXBException;

    public void marshal(T var1, ContentHandler var2, AttachmentMarshaller var3) throws JAXBException;

    public void marshal(T var1, Result var2) throws JAXBException;

    @NotNull
    public T unmarshal(@NotNull XMLStreamReader var1, @Nullable AttachmentUnmarshaller var2) throws JAXBException;

    @NotNull
    public T unmarshal(@NotNull Source var1, @Nullable AttachmentUnmarshaller var2) throws JAXBException;

    @NotNull
    public T unmarshal(@NotNull InputStream var1) throws JAXBException;

    @NotNull
    public T unmarshal(@NotNull Node var1, @Nullable AttachmentUnmarshaller var2) throws JAXBException;

    public TypeInfo getTypeInfo();

    public boolean supportOutputStream();
}

