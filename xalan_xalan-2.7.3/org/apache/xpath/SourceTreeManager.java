/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath;

import java.io.IOException;
import java.util.Vector;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xpath.SourceTree;
import org.apache.xpath.XPathContext;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class SourceTreeManager {
    private Vector m_sourceTree = new Vector();
    URIResolver m_uriResolver;

    public void reset() {
        this.m_sourceTree = new Vector();
    }

    public void setURIResolver(URIResolver resolver) {
        this.m_uriResolver = resolver;
    }

    public URIResolver getURIResolver() {
        return this.m_uriResolver;
    }

    public String findURIFromDoc(int owner) {
        int n = this.m_sourceTree.size();
        for (int i = 0; i < n; ++i) {
            SourceTree sTree = (SourceTree)this.m_sourceTree.elementAt(i);
            if (owner != sTree.m_root) continue;
            return sTree.m_url;
        }
        return null;
    }

    public Source resolveURI(String base, String urlString, SourceLocator locator) throws TransformerException, IOException {
        Source source = null;
        if (null != this.m_uriResolver) {
            source = this.m_uriResolver.resolve(urlString, base);
        }
        if (null == source) {
            String uri = SystemIDResolver.getAbsoluteURI(urlString, base);
            source = new StreamSource(uri);
        }
        return source;
    }

    public void removeDocumentFromCache(int n) {
        if (-1 == n) {
            return;
        }
        for (int i = this.m_sourceTree.size() - 1; i >= 0; --i) {
            SourceTree st = (SourceTree)this.m_sourceTree.elementAt(i);
            if (st == null || st.m_root != n) continue;
            this.m_sourceTree.removeElementAt(i);
            return;
        }
    }

    public void putDocumentInCache(int n, Source source) {
        int cachedNode = this.getNode(source);
        if (-1 != cachedNode) {
            if (cachedNode != n) {
                throw new RuntimeException("Programmer's Error!  putDocumentInCache found reparse of doc: " + source.getSystemId());
            }
            return;
        }
        if (null != source.getSystemId()) {
            this.m_sourceTree.addElement(new SourceTree(n, source.getSystemId()));
        }
    }

    public int getNode(Source source) {
        String url = source.getSystemId();
        if (null == url) {
            return -1;
        }
        int n = this.m_sourceTree.size();
        for (int i = 0; i < n; ++i) {
            SourceTree sTree = (SourceTree)this.m_sourceTree.elementAt(i);
            if (!url.equals(sTree.m_url)) continue;
            return sTree.m_root;
        }
        return -1;
    }

    public int getSourceTree(String base, String urlString, SourceLocator locator, XPathContext xctxt) throws TransformerException {
        try {
            Source source = this.resolveURI(base, urlString, locator);
            return this.getSourceTree(source, locator, xctxt);
        }
        catch (IOException ioe) {
            throw new TransformerException(ioe.getMessage(), locator, ioe);
        }
    }

    public int getSourceTree(Source source, SourceLocator locator, XPathContext xctxt) throws TransformerException {
        int n = this.getNode(source);
        if (-1 != n) {
            return n;
        }
        n = this.parseToNode(source, locator, xctxt);
        if (-1 != n) {
            this.putDocumentInCache(n, source);
        }
        return n;
    }

    public int parseToNode(Source source, SourceLocator locator, XPathContext xctxt) throws TransformerException {
        try {
            Object xowner = xctxt.getOwnerObject();
            DTM dtm = null != xowner && xowner instanceof DTMWSFilter ? xctxt.getDTM(source, false, (DTMWSFilter)xowner, false, true) : xctxt.getDTM(source, false, null, false, true);
            return dtm.getDocument();
        }
        catch (Exception e) {
            throw new TransformerException(e.getMessage(), locator, e);
        }
    }

    public static XMLReader getXMLReader(Source inputSource, SourceLocator locator) throws TransformerException {
        try {
            XMLReader reader;
            XMLReader xMLReader = reader = inputSource instanceof SAXSource ? ((SAXSource)inputSource).getXMLReader() : null;
            if (null == reader) {
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    SAXParser jaxpParser = factory.newSAXParser();
                    reader = jaxpParser.getXMLReader();
                }
                catch (ParserConfigurationException ex) {
                    throw new SAXException(ex);
                }
                catch (FactoryConfigurationError ex1) {
                    throw new SAXException(ex1.toString());
                }
                catch (NoSuchMethodError noSuchMethodError) {
                }
                catch (AbstractMethodError abstractMethodError) {
                    // empty catch block
                }
                if (null == reader) {
                    reader = XMLReaderFactory.createXMLReader();
                }
            }
            try {
                reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            }
            catch (SAXException sAXException) {
                // empty catch block
            }
            return reader;
        }
        catch (SAXException se) {
            throw new TransformerException(se.getMessage(), locator, se);
        }
    }
}

