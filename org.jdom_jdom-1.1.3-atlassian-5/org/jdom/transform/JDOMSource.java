/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.transform;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.sax.SAXSource;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.SAXOutputter;
import org.jdom.output.XMLOutputter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

public class JDOMSource
extends SAXSource {
    private static final String CVS_ID = "@(#) $RCSfile: JDOMSource.java,v $ $Revision: 1.20 $ $Date: 2007/11/10 05:29:02 $ $Name:  $";
    public static final String JDOM_FEATURE = "http://org.jdom.transform.JDOMSource/feature";
    private XMLReader xmlReader = null;
    private EntityResolver resolver = null;

    public JDOMSource(Document source) {
        this.setDocument(source);
    }

    public JDOMSource(List source) {
        this.setNodes(source);
    }

    public JDOMSource(Element source) {
        ArrayList<Element> nodes = new ArrayList<Element>();
        nodes.add(source);
        this.setNodes(nodes);
    }

    public JDOMSource(Document source, EntityResolver resolver) {
        this.setDocument(source);
        this.resolver = resolver;
    }

    public void setDocument(Document source) {
        super.setInputSource(new JDOMInputSource(source));
    }

    public Document getDocument() {
        Object src = ((JDOMInputSource)this.getInputSource()).getSource();
        Document doc = null;
        if (src instanceof Document) {
            doc = (Document)src;
        }
        return doc;
    }

    public void setNodes(List source) {
        super.setInputSource(new JDOMInputSource(source));
    }

    public List getNodes() {
        Object src = ((JDOMInputSource)this.getInputSource()).getSource();
        List nodes = null;
        if (src instanceof List) {
            nodes = (List)src;
        }
        return nodes;
    }

    @Override
    public void setInputSource(InputSource inputSource) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setXMLReader(XMLReader reader) throws UnsupportedOperationException {
        XMLFilter filter;
        if (reader instanceof XMLFilter) {
            filter = (XMLFilter)reader;
            while (filter.getParent() instanceof XMLFilter) {
                filter = (XMLFilter)filter.getParent();
            }
        } else {
            throw new UnsupportedOperationException();
        }
        filter.setParent(this.buildDocumentReader());
        this.xmlReader = reader;
    }

    @Override
    public XMLReader getXMLReader() {
        if (this.xmlReader == null) {
            this.xmlReader = this.buildDocumentReader();
        }
        return this.xmlReader;
    }

    private XMLReader buildDocumentReader() {
        DocumentReader reader = new DocumentReader();
        if (this.resolver != null) {
            reader.setEntityResolver(this.resolver);
        }
        return reader;
    }

    private static class DocumentReader
    extends SAXOutputter
    implements XMLReader {
        @Override
        public void parse(String systemId) throws SAXNotSupportedException {
            throw new SAXNotSupportedException("Only JDOM Documents are supported as input");
        }

        @Override
        public void parse(InputSource input) throws SAXException {
            if (input instanceof JDOMInputSource) {
                try {
                    Object source = ((JDOMInputSource)input).getSource();
                    if (source instanceof Document) {
                        this.output((Document)source);
                    }
                    this.output((List)source);
                }
                catch (JDOMException e) {
                    throw new SAXException(e.getMessage(), e);
                }
            } else {
                throw new SAXNotSupportedException("Only JDOM Documents are supported as input");
            }
        }
    }

    private static class JDOMInputSource
    extends InputSource {
        private Object source = null;

        public JDOMInputSource(Document document) {
            this.source = document;
        }

        public JDOMInputSource(List nodes) {
            this.source = nodes;
        }

        public Object getSource() {
            return this.source;
        }

        @Override
        public void setCharacterStream(Reader characterStream) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Reader getCharacterStream() {
            Object src = this.getSource();
            StringReader reader = null;
            if (src instanceof Document) {
                reader = new StringReader(new XMLOutputter().outputString((Document)src));
            } else if (src instanceof List) {
                reader = new StringReader(new XMLOutputter().outputString((List)src));
            }
            return reader;
        }

        @Override
        public void setByteStream(InputStream byteStream) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }
}

