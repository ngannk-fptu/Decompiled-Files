/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlSaxHandler;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;

public class XmlObjectFactory<T>
extends DocumentFactory<T> {
    private final boolean isAnyType;

    public XmlObjectFactory(String typeHandle) {
        this(XmlBeans.getBuiltinTypeSystem(), typeHandle);
    }

    public XmlObjectFactory(SchemaTypeSystem typeSystem, String typeHandle) {
        super(typeSystem, typeHandle);
        this.isAnyType = "_BI_anyType".equals(typeHandle);
    }

    @Override
    public T newInstance() {
        return (T)XmlBeans.getContextTypeLoader().newInstance(this.getInnerType(), null);
    }

    @Override
    public T newInstance(XmlOptions options) {
        return (T)XmlBeans.getContextTypeLoader().newInstance(this.getInnerType(), options);
    }

    public T newValue(Object obj) {
        return (T)this.getType().newValue(obj);
    }

    @Override
    public T parse(String xmlAsString) throws XmlException {
        return (T)XmlBeans.getContextTypeLoader().parse(xmlAsString, this.getInnerType(), null);
    }

    @Override
    public T parse(String xmlAsString, XmlOptions options) throws XmlException {
        return (T)XmlBeans.getContextTypeLoader().parse(xmlAsString, this.getInnerType(), options);
    }

    @Override
    public T parse(File file) throws XmlException, IOException {
        return (T)XmlBeans.getContextTypeLoader().parse(file, this.getInnerType(), null);
    }

    @Override
    public T parse(File file, XmlOptions options) throws XmlException, IOException {
        return (T)XmlBeans.getContextTypeLoader().parse(file, this.getInnerType(), options);
    }

    @Override
    public T parse(URL u) throws XmlException, IOException {
        return (T)XmlBeans.getContextTypeLoader().parse(u, this.getInnerType(), null);
    }

    @Override
    public T parse(URL u, XmlOptions options) throws XmlException, IOException {
        return (T)XmlBeans.getContextTypeLoader().parse(u, this.getInnerType(), options);
    }

    @Override
    public T parse(InputStream is) throws XmlException, IOException {
        return (T)XmlBeans.getContextTypeLoader().parse(is, this.getInnerType(), null);
    }

    @Override
    public T parse(XMLStreamReader xsr) throws XmlException {
        return (T)XmlBeans.getContextTypeLoader().parse(xsr, this.getInnerType(), null);
    }

    @Override
    public T parse(InputStream is, XmlOptions options) throws XmlException, IOException {
        return (T)XmlBeans.getContextTypeLoader().parse(is, this.getInnerType(), options);
    }

    @Override
    public T parse(XMLStreamReader xsr, XmlOptions options) throws XmlException {
        return (T)XmlBeans.getContextTypeLoader().parse(xsr, this.getInnerType(), options);
    }

    @Override
    public T parse(Reader r) throws XmlException, IOException {
        return (T)XmlBeans.getContextTypeLoader().parse(r, this.getInnerType(), null);
    }

    @Override
    public T parse(Reader r, XmlOptions options) throws XmlException, IOException {
        return (T)XmlBeans.getContextTypeLoader().parse(r, this.getInnerType(), options);
    }

    @Override
    public T parse(Node node) throws XmlException {
        return (T)XmlBeans.getContextTypeLoader().parse(node, this.getInnerType(), null);
    }

    @Override
    public T parse(Node node, XmlOptions options) throws XmlException {
        return (T)XmlBeans.getContextTypeLoader().parse(node, this.getInnerType(), options);
    }

    public XmlSaxHandler newXmlSaxHandler() {
        return XmlBeans.getContextTypeLoader().newXmlSaxHandler(this.getInnerType(), null);
    }

    public XmlSaxHandler newXmlSaxHandler(XmlOptions options) {
        return XmlBeans.getContextTypeLoader().newXmlSaxHandler(this.getInnerType(), options);
    }

    public DOMImplementation newDomImplementation() {
        return XmlBeans.getContextTypeLoader().newDomImplementation(null);
    }

    public DOMImplementation newDomImplementation(XmlOptions options) {
        return XmlBeans.getContextTypeLoader().newDomImplementation(options);
    }

    private SchemaType getInnerType() {
        return this.isAnyType ? null : this.getType();
    }
}

