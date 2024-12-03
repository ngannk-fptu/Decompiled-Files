/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.transform;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.sax.SAXSource;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.SAXOutputter;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JDOMSource
extends SAXSource {
    public static final String JDOM_FEATURE = "http://jdom.org/jdom2/transform/JDOMSource/feature";
    private XMLReader xmlReader = null;
    private EntityResolver resolver = null;

    public JDOMSource(Document source) {
        this(source, null);
    }

    public JDOMSource(List<? extends Content> source) {
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
        if (source != null && source.getBaseURI() != null) {
            super.setSystemId(source.getBaseURI());
        }
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

    public void setNodes(List<? extends Content> source) {
        super.setInputSource(new JDOMInputSource(source));
    }

    public List<? extends Content> getNodes() {
        return ((JDOMInputSource)this.getInputSource()).getListSource();
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
        public void parse(String systemId) throws SAXNotSupportedException {
            throw new SAXNotSupportedException("Only JDOM Documents are supported as input");
        }

        public void parse(InputSource input) throws SAXException {
            if (input instanceof JDOMInputSource) {
                try {
                    Document docsource = ((JDOMInputSource)input).getDocumentSource();
                    if (docsource != null) {
                        this.output(docsource);
                    }
                    this.output(((JDOMInputSource)input).getListSource());
                }
                catch (JDOMException e) {
                    throw new SAXException(e.getMessage(), e);
                }
            } else {
                throw new SAXNotSupportedException("Only JDOM Documents are supported as input");
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class JDOMInputSource
    extends InputSource {
        private Document docsource = null;
        private List<? extends Content> listsource = null;

        public JDOMInputSource(Document document) {
            this.docsource = document;
        }

        public JDOMInputSource(List<? extends Content> nodes) {
            this.listsource = nodes;
        }

        public Object getSource() {
            return this.docsource == null ? this.listsource : this.docsource;
        }

        @Override
        public void setCharacterStream(Reader characterStream) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Reader getCharacterStream() {
            StringReader reader = null;
            if (this.docsource != null) {
                reader = new StringReader(new XMLOutputter().outputString(this.docsource));
            } else if (this.listsource != null) {
                reader = new StringReader(new XMLOutputter().outputString(this.listsource));
            }
            return reader;
        }

        @Override
        public void setByteStream(InputStream byteStream) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public Document getDocumentSource() {
            return this.docsource;
        }

        public List<? extends Content> getListSource() {
            return this.listsource;
        }
    }
}

