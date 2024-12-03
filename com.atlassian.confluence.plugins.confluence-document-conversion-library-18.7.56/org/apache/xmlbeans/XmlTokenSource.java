/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlDocumentProperties;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public interface XmlTokenSource {
    public Object monitor();

    public XmlDocumentProperties documentProperties();

    public XmlCursor newCursor();

    public XMLStreamReader newXMLStreamReader();

    public String xmlText();

    public InputStream newInputStream();

    public Reader newReader();

    public Node newDomNode();

    public Node getDomNode();

    public void save(ContentHandler var1, LexicalHandler var2) throws SAXException;

    public void save(File var1) throws IOException;

    public void save(OutputStream var1) throws IOException;

    public void save(Writer var1) throws IOException;

    public XMLStreamReader newXMLStreamReader(XmlOptions var1);

    public String xmlText(XmlOptions var1);

    public InputStream newInputStream(XmlOptions var1);

    public Reader newReader(XmlOptions var1);

    public Node newDomNode(XmlOptions var1);

    public void save(ContentHandler var1, LexicalHandler var2, XmlOptions var3) throws SAXException;

    public void save(File var1, XmlOptions var2) throws IOException;

    public void save(OutputStream var1, XmlOptions var2) throws IOException;

    public void save(Writer var1, XmlOptions var2) throws IOException;

    public void dump();
}

