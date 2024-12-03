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
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.w3c.dom.Node;

public class AbstractDocumentFactory<T>
extends ElementFactory<T> {
    public AbstractDocumentFactory(SchemaTypeSystem typeSystem, String typeHandle) {
        super(typeSystem, typeHandle);
    }

    public T parse(String xmlAsString) throws XmlException {
        return (T)this.getTypeLoader().parse(xmlAsString, this.getType(), null);
    }

    public T parse(String xmlAsString, XmlOptions options) throws XmlException {
        return (T)this.getTypeLoader().parse(xmlAsString, this.getType(), options);
    }

    public T parse(File file) throws XmlException, IOException {
        return (T)this.getTypeLoader().parse(file, this.getType(), null);
    }

    public T parse(File file, XmlOptions options) throws XmlException, IOException {
        return (T)this.getTypeLoader().parse(file, this.getType(), options);
    }

    public T parse(URL u) throws XmlException, IOException {
        return (T)this.getTypeLoader().parse(u, this.getType(), null);
    }

    public T parse(URL u, XmlOptions options) throws XmlException, IOException {
        return (T)this.getTypeLoader().parse(u, this.getType(), options);
    }

    public T parse(InputStream is) throws XmlException, IOException {
        return (T)this.getTypeLoader().parse(is, this.getType(), null);
    }

    public T parse(InputStream is, XmlOptions options) throws XmlException, IOException {
        return (T)this.getTypeLoader().parse(is, this.getType(), options);
    }

    public T parse(Reader r) throws XmlException, IOException {
        return (T)this.getTypeLoader().parse(r, this.getType(), null);
    }

    public T parse(Reader r, XmlOptions options) throws XmlException, IOException {
        return (T)this.getTypeLoader().parse(r, this.getType(), options);
    }

    public T parse(XMLStreamReader sr) throws XmlException {
        return (T)this.getTypeLoader().parse(sr, this.getType(), null);
    }

    public T parse(XMLStreamReader sr, XmlOptions options) throws XmlException {
        return (T)this.getTypeLoader().parse(sr, this.getType(), options);
    }

    public T parse(Node node) throws XmlException {
        return (T)this.getTypeLoader().parse(node, this.getType(), null);
    }

    public T parse(Node node, XmlOptions options) throws XmlException {
        return (T)this.getTypeLoader().parse(node, this.getType(), options);
    }
}

