/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import java.io.IOException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.apache.xalan.processor.XSLTElementProcessor;
import org.apache.xalan.res.XSLMessages;
import org.apache.xml.utils.DOM2Helper;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.utils.TreeWalker;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class ProcessorInclude
extends XSLTElementProcessor {
    static final long serialVersionUID = -4570078731972673481L;
    private String m_href = null;

    public String getHref() {
        return this.m_href;
    }

    public void setHref(String baseIdent) {
        this.m_href = baseIdent;
    }

    protected int getStylesheetType() {
        return 2;
    }

    protected String getStylesheetInclErr() {
        return "ER_STYLESHEET_INCLUDES_ITSELF";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void startElement(StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes) throws SAXException {
        this.setPropertiesFromAttributes(handler, rawName, attributes, this);
        try {
            Source sourceFromURIResolver = this.getSourceFromUriResolver(handler);
            String hrefUrl = this.getBaseURIOfIncludedStylesheet(handler, sourceFromURIResolver);
            if (handler.importStackContains(hrefUrl)) {
                throw new SAXException(XSLMessages.createMessage(this.getStylesheetInclErr(), new Object[]{hrefUrl}));
            }
            handler.pushImportURL(hrefUrl);
            handler.pushImportSource(sourceFromURIResolver);
            int savedStylesheetType = handler.getStylesheetType();
            handler.setStylesheetType(this.getStylesheetType());
            handler.pushNewNamespaceSupport();
            try {
                this.parse(handler, uri, localName, rawName, attributes);
            }
            finally {
                handler.setStylesheetType(savedStylesheetType);
                handler.popImportURL();
                handler.popImportSource();
                handler.popNamespaceSupport();
            }
        }
        catch (TransformerException te) {
            handler.error(te.getMessage(), te);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void parse(StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes) throws SAXException {
        block23: {
            TransformerFactoryImpl processor = handler.getStylesheetProcessor();
            URIResolver uriresolver = processor.getURIResolver();
            try {
                Source source = null;
                if (null != uriresolver && null != (source = handler.peekSourceFromURIResolver()) && source instanceof DOMSource) {
                    Node node = ((DOMSource)source).getNode();
                    String systemId = handler.peekImportURL();
                    if (systemId != null) {
                        handler.pushBaseIndentifier(systemId);
                    }
                    TreeWalker walker = new TreeWalker(handler, new DOM2Helper(), systemId);
                    try {
                        walker.traverse(node);
                    }
                    catch (SAXException se) {
                        throw new TransformerException(se);
                    }
                    if (systemId != null) {
                        handler.popBaseIndentifier();
                    }
                    return;
                }
                if (null == source) {
                    String absURL = SystemIDResolver.getAbsoluteURI(this.getHref(), handler.getBaseIdentifier());
                    source = new StreamSource(absURL);
                }
                source = this.processSource(handler, source);
                XMLReader reader = null;
                if (source instanceof SAXSource) {
                    SAXSource saxSource = (SAXSource)source;
                    reader = saxSource.getXMLReader();
                }
                InputSource inputSource = SAXSource.sourceToInputSource(source);
                if (null == reader) {
                    try {
                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        if (handler.getStylesheetProcessor().isSecureProcessing()) {
                            try {
                                factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                            }
                            catch (SAXException se) {
                                // empty catch block
                            }
                        }
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
                }
                if (null == reader) {
                    reader = XMLReaderFactory.createXMLReader();
                }
                if (null == reader) break block23;
                reader.setContentHandler(handler);
                handler.pushBaseIndentifier(inputSource.getSystemId());
                try {
                    reader.parse(inputSource);
                }
                finally {
                    handler.popBaseIndentifier();
                }
            }
            catch (IOException ioe) {
                handler.error("ER_IOEXCEPTION", new Object[]{this.getHref()}, ioe);
            }
            catch (TransformerException te) {
                handler.error(te.getMessage(), te);
            }
        }
    }

    protected Source processSource(StylesheetHandler handler, Source source) {
        return source;
    }

    private Source getSourceFromUriResolver(StylesheetHandler handler) throws TransformerException {
        Source s = null;
        TransformerFactoryImpl processor = handler.getStylesheetProcessor();
        URIResolver uriresolver = processor.getURIResolver();
        if (uriresolver != null) {
            String href = this.getHref();
            String base = handler.getBaseIdentifier();
            s = uriresolver.resolve(href, base);
        }
        return s;
    }

    private String getBaseURIOfIncludedStylesheet(StylesheetHandler handler, Source s) throws TransformerException {
        String idFromUriResolverSource;
        String baseURI = s != null && (idFromUriResolverSource = s.getSystemId()) != null ? idFromUriResolverSource : SystemIDResolver.getAbsoluteURI(this.getHref(), handler.getBaseIdentifier());
        return baseURI;
    }
}

