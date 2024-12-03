/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import java.io.IOException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.transformer.TrAXFilter;
import org.apache.xalan.transformer.TransformerIdentityImpl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.DOM2Helper;
import org.apache.xml.utils.DefaultErrorHandler;
import org.apache.xml.utils.StopParseException;
import org.apache.xml.utils.StylesheetPIHandler;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.utils.TreeWalker;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class TransformerFactoryImpl
extends SAXTransformerFactory {
    public static final String XSLT_PROPERTIES = "org/apache/xalan/res/XSLTInfo.properties";
    private boolean m_isSecureProcessing = false;
    public static final String FEATURE_INCREMENTAL = "http://xml.apache.org/xalan/features/incremental";
    public static final String FEATURE_OPTIMIZE = "http://xml.apache.org/xalan/features/optimize";
    public static final String FEATURE_SOURCE_LOCATION = "http://xml.apache.org/xalan/properties/source-location";
    private String m_DOMsystemID = null;
    private boolean m_optimize = true;
    private boolean m_source_location = false;
    private boolean m_incremental = false;
    URIResolver m_uriResolver;
    private ErrorListener m_errorListener = new DefaultErrorHandler(false);

    public Templates processFromNode(Node node) throws TransformerConfigurationException {
        try {
            TemplatesHandler builder = this.newTemplatesHandler();
            TreeWalker walker = new TreeWalker(builder, new DOM2Helper(), builder.getSystemId());
            walker.traverse(node);
            return builder.getTemplates();
        }
        catch (SAXException se) {
            if (this.m_errorListener != null) {
                try {
                    this.m_errorListener.fatalError(new TransformerException(se));
                }
                catch (TransformerConfigurationException ex) {
                    throw ex;
                }
                catch (TransformerException ex) {
                    throw new TransformerConfigurationException(ex);
                }
                return null;
            }
            throw new TransformerConfigurationException(XSLMessages.createMessage("ER_PROCESSFROMNODE_FAILED", null), se);
        }
        catch (TransformerConfigurationException tce) {
            throw tce;
        }
        catch (Exception e) {
            if (this.m_errorListener != null) {
                try {
                    this.m_errorListener.fatalError(new TransformerException(e));
                }
                catch (TransformerConfigurationException ex) {
                    throw ex;
                }
                catch (TransformerException ex) {
                    throw new TransformerConfigurationException(ex);
                }
                return null;
            }
            throw new TransformerConfigurationException(XSLMessages.createMessage("ER_PROCESSFROMNODE_FAILED", null), e);
        }
    }

    String getDOMsystemID() {
        return this.m_DOMsystemID;
    }

    Templates processFromNode(Node node, String systemID) throws TransformerConfigurationException {
        this.m_DOMsystemID = systemID;
        return this.processFromNode(node);
    }

    @Override
    public Source getAssociatedStylesheet(Source source, String media, String title, String charset) throws TransformerConfigurationException {
        StylesheetPIHandler handler;
        block18: {
            String baseID;
            InputSource isource = null;
            Node node = null;
            XMLReader reader = null;
            if (source instanceof DOMSource) {
                DOMSource dsource = (DOMSource)source;
                node = dsource.getNode();
                baseID = dsource.getSystemId();
            } else {
                isource = SAXSource.sourceToInputSource(source);
                baseID = isource.getSystemId();
            }
            handler = new StylesheetPIHandler(baseID, media, title, charset);
            if (this.m_uriResolver != null) {
                handler.setURIResolver(this.m_uriResolver);
            }
            try {
                if (null != node) {
                    TreeWalker walker = new TreeWalker(handler, new DOM2Helper(), baseID);
                    walker.traverse(node);
                    break block18;
                }
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    if (this.m_isSecureProcessing) {
                        try {
                            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                        }
                        catch (SAXException sAXException) {
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
                catch (NoSuchMethodError ex1) {
                }
                catch (AbstractMethodError ex1) {
                    // empty catch block
                }
                if (null == reader) {
                    reader = XMLReaderFactory.createXMLReader();
                }
                if (this.m_isSecureProcessing) {
                    reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
                }
                reader.setContentHandler(handler);
                reader.parse(isource);
            }
            catch (StopParseException ex1) {
            }
            catch (SAXException se) {
                throw new TransformerConfigurationException("getAssociatedStylesheets failed", se);
            }
            catch (IOException ioe) {
                throw new TransformerConfigurationException("getAssociatedStylesheets failed", ioe);
            }
        }
        return handler.getAssociatedStylesheet();
    }

    @Override
    public TemplatesHandler newTemplatesHandler() throws TransformerConfigurationException {
        return new StylesheetHandler(this);
    }

    @Override
    public void setFeature(String name, boolean value) throws TransformerConfigurationException {
        if (name == null) {
            throw new NullPointerException(XSLMessages.createMessage("ER_SET_FEATURE_NULL_NAME", null));
        }
        if (!name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            throw new TransformerConfigurationException(XSLMessages.createMessage("ER_UNSUPPORTED_FEATURE", new Object[]{name}));
        }
        this.m_isSecureProcessing = value;
    }

    @Override
    public boolean getFeature(String name) {
        if (name == null) {
            throw new NullPointerException(XSLMessages.createMessage("ER_GET_FEATURE_NULL_NAME", null));
        }
        if ("http://javax.xml.transform.dom.DOMResult/feature" == name || "http://javax.xml.transform.dom.DOMSource/feature" == name || "http://javax.xml.transform.sax.SAXResult/feature" == name || "http://javax.xml.transform.sax.SAXSource/feature" == name || "http://javax.xml.transform.stream.StreamResult/feature" == name || "http://javax.xml.transform.stream.StreamSource/feature" == name || "http://javax.xml.transform.sax.SAXTransformerFactory/feature" == name || "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter" == name) {
            return true;
        }
        if ("http://javax.xml.transform.dom.DOMResult/feature".equals(name) || "http://javax.xml.transform.dom.DOMSource/feature".equals(name) || "http://javax.xml.transform.sax.SAXResult/feature".equals(name) || "http://javax.xml.transform.sax.SAXSource/feature".equals(name) || "http://javax.xml.transform.stream.StreamResult/feature".equals(name) || "http://javax.xml.transform.stream.StreamSource/feature".equals(name) || "http://javax.xml.transform.sax.SAXTransformerFactory/feature".equals(name) || "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter".equals(name)) {
            return true;
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return this.m_isSecureProcessing;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void setAttribute(String name, Object value) throws IllegalArgumentException {
        if (name.equals(FEATURE_INCREMENTAL)) {
            if (value instanceof Boolean) {
                this.m_incremental = (Boolean)value;
                return;
            } else {
                if (!(value instanceof String)) throw new IllegalArgumentException(XSLMessages.createMessage("ER_BAD_VALUE", new Object[]{name, value}));
                this.m_incremental = new Boolean((String)value);
            }
            return;
        } else if (name.equals(FEATURE_OPTIMIZE)) {
            if (value instanceof Boolean) {
                this.m_optimize = (Boolean)value;
                return;
            } else {
                if (!(value instanceof String)) throw new IllegalArgumentException(XSLMessages.createMessage("ER_BAD_VALUE", new Object[]{name, value}));
                this.m_optimize = new Boolean((String)value);
            }
            return;
        } else {
            if (!name.equals(FEATURE_SOURCE_LOCATION)) throw new IllegalArgumentException(XSLMessages.createMessage("ER_NOT_SUPPORTED", new Object[]{name}));
            if (value instanceof Boolean) {
                this.m_source_location = (Boolean)value;
                return;
            } else {
                if (!(value instanceof String)) throw new IllegalArgumentException(XSLMessages.createMessage("ER_BAD_VALUE", new Object[]{name, value}));
                this.m_source_location = new Boolean((String)value);
            }
        }
    }

    @Override
    public Object getAttribute(String name) throws IllegalArgumentException {
        if (name.equals(FEATURE_INCREMENTAL)) {
            return this.m_incremental ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equals(FEATURE_OPTIMIZE)) {
            return this.m_optimize ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equals(FEATURE_SOURCE_LOCATION)) {
            return this.m_source_location ? Boolean.TRUE : Boolean.FALSE;
        }
        throw new IllegalArgumentException(XSLMessages.createMessage("ER_ATTRIB_VALUE_NOT_RECOGNIZED", new Object[]{name}));
    }

    @Override
    public XMLFilter newXMLFilter(Source src) throws TransformerConfigurationException {
        Templates templates = this.newTemplates(src);
        if (templates == null) {
            return null;
        }
        return this.newXMLFilter(templates);
    }

    @Override
    public XMLFilter newXMLFilter(Templates templates) throws TransformerConfigurationException {
        try {
            return new TrAXFilter(templates);
        }
        catch (TransformerConfigurationException ex) {
            if (this.m_errorListener != null) {
                try {
                    this.m_errorListener.fatalError(ex);
                    return null;
                }
                catch (TransformerConfigurationException ex1) {
                    throw ex1;
                }
                catch (TransformerException ex1) {
                    throw new TransformerConfigurationException(ex1);
                }
            }
            throw ex;
        }
    }

    @Override
    public TransformerHandler newTransformerHandler(Source src) throws TransformerConfigurationException {
        Templates templates = this.newTemplates(src);
        if (templates == null) {
            return null;
        }
        return this.newTransformerHandler(templates);
    }

    @Override
    public TransformerHandler newTransformerHandler(Templates templates) throws TransformerConfigurationException {
        try {
            TransformerImpl transformer = (TransformerImpl)templates.newTransformer();
            transformer.setURIResolver(this.m_uriResolver);
            TransformerHandler th = (TransformerHandler)transformer.getInputContentHandler(true);
            return th;
        }
        catch (TransformerConfigurationException ex) {
            if (this.m_errorListener != null) {
                try {
                    this.m_errorListener.fatalError(ex);
                    return null;
                }
                catch (TransformerConfigurationException ex1) {
                    throw ex1;
                }
                catch (TransformerException ex1) {
                    throw new TransformerConfigurationException(ex1);
                }
            }
            throw ex;
        }
    }

    @Override
    public TransformerHandler newTransformerHandler() throws TransformerConfigurationException {
        return new TransformerIdentityImpl(this.m_isSecureProcessing);
    }

    @Override
    public Transformer newTransformer(Source source) throws TransformerConfigurationException {
        try {
            Templates tmpl = this.newTemplates(source);
            if (tmpl == null) {
                return null;
            }
            Transformer transformer = tmpl.newTransformer();
            transformer.setURIResolver(this.m_uriResolver);
            return transformer;
        }
        catch (TransformerConfigurationException ex) {
            if (this.m_errorListener != null) {
                try {
                    this.m_errorListener.fatalError(ex);
                    return null;
                }
                catch (TransformerConfigurationException ex1) {
                    throw ex1;
                }
                catch (TransformerException ex1) {
                    throw new TransformerConfigurationException(ex1);
                }
            }
            throw ex;
        }
    }

    @Override
    public Transformer newTransformer() throws TransformerConfigurationException {
        return new TransformerIdentityImpl(this.m_isSecureProcessing);
    }

    @Override
    public Templates newTemplates(Source source) throws TransformerConfigurationException {
        String baseID = source.getSystemId();
        if (null != baseID) {
            baseID = SystemIDResolver.getAbsoluteURI(baseID);
        }
        if (source instanceof DOMSource) {
            DOMSource dsource = (DOMSource)source;
            Node node = dsource.getNode();
            if (null != node) {
                return this.processFromNode(node, baseID);
            }
            String messageStr = XSLMessages.createMessage("ER_ILLEGAL_DOMSOURCE_INPUT", null);
            throw new IllegalArgumentException(messageStr);
        }
        TemplatesHandler builder = this.newTemplatesHandler();
        builder.setSystemId(baseID);
        try {
            InputSource isource = SAXSource.sourceToInputSource(source);
            isource.setSystemId(baseID);
            XMLReader reader = null;
            if (source instanceof SAXSource) {
                reader = ((SAXSource)source).getXMLReader();
            }
            if (null == reader) {
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    if (this.m_isSecureProcessing) {
                        try {
                            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                        }
                        catch (SAXException sAXException) {
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
            reader.setContentHandler(builder);
            reader.parse(isource);
        }
        catch (SAXException se) {
            if (this.m_errorListener != null) {
                try {
                    this.m_errorListener.fatalError(new TransformerException(se));
                }
                catch (TransformerConfigurationException ex1) {
                    throw ex1;
                }
                catch (TransformerException ex1) {
                    throw new TransformerConfigurationException(ex1);
                }
            }
            throw new TransformerConfigurationException(se.getMessage(), se);
        }
        catch (Exception e) {
            if (this.m_errorListener != null) {
                try {
                    this.m_errorListener.fatalError(new TransformerException(e));
                    return null;
                }
                catch (TransformerConfigurationException ex1) {
                    throw ex1;
                }
                catch (TransformerException ex1) {
                    throw new TransformerConfigurationException(ex1);
                }
            }
            throw new TransformerConfigurationException(e.getMessage(), e);
        }
        return builder.getTemplates();
    }

    @Override
    public void setURIResolver(URIResolver resolver) {
        this.m_uriResolver = resolver;
    }

    @Override
    public URIResolver getURIResolver() {
        return this.m_uriResolver;
    }

    @Override
    public ErrorListener getErrorListener() {
        return this.m_errorListener;
    }

    @Override
    public void setErrorListener(ErrorListener listener) throws IllegalArgumentException {
        if (null == listener) {
            throw new IllegalArgumentException(XSLMessages.createMessage("ER_ERRORLISTENER", null));
        }
        this.m_errorListener = listener;
    }

    public boolean isSecureProcessing() {
        return this.m_isSecureProcessing;
    }
}

