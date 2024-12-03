/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.input;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.JDOMFactory;
import org.jdom.input.BuilderErrorHandler;
import org.jdom.input.JDOMParseException;
import org.jdom.input.SAXHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class SAXBuilder {
    private static final String CVS_ID = "@(#) $RCSfile: SAXBuilder.java,v $ $Revision: 1.93 $ $Date: 2009/07/23 06:26:26 $ $Name:  $";
    private static final String DEFAULT_SAX_DRIVER = "org.apache.xerces.parsers.SAXParser";
    private boolean validate;
    private boolean expand = true;
    private String saxDriverClass;
    private ErrorHandler saxErrorHandler = null;
    private EntityResolver saxEntityResolver = null;
    private DTDHandler saxDTDHandler = null;
    private XMLFilter saxXMLFilter = null;
    private JDOMFactory factory = new DefaultJDOMFactory();
    private boolean ignoringWhite = false;
    private boolean ignoringBoundaryWhite = false;
    private HashMap features = new HashMap(5);
    private HashMap properties = new HashMap(5);
    private boolean fastReconfigure = false;
    private boolean skipNextLexicalReportingConfig = false;
    private boolean skipNextEntityExpandConfig = false;
    private boolean reuseParser = true;
    private XMLReader saxParser = null;

    public SAXBuilder() {
        this(false);
    }

    public SAXBuilder(boolean validate) {
        this.validate = validate;
    }

    public SAXBuilder(String saxDriverClass) {
        this(saxDriverClass, false);
    }

    public SAXBuilder(String saxDriverClass, boolean validate) {
        this.saxDriverClass = saxDriverClass;
        this.validate = validate;
    }

    public String getDriverClass() {
        return this.saxDriverClass;
    }

    public JDOMFactory getFactory() {
        return this.factory;
    }

    public void setFactory(JDOMFactory factory) {
        this.factory = factory;
    }

    public boolean getValidation() {
        return this.validate;
    }

    public void setValidation(boolean validate) {
        this.validate = validate;
    }

    public ErrorHandler getErrorHandler() {
        return this.saxErrorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.saxErrorHandler = errorHandler;
    }

    public EntityResolver getEntityResolver() {
        return this.saxEntityResolver;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.saxEntityResolver = entityResolver;
    }

    public DTDHandler getDTDHandler() {
        return this.saxDTDHandler;
    }

    public void setDTDHandler(DTDHandler dtdHandler) {
        this.saxDTDHandler = dtdHandler;
    }

    public XMLFilter getXMLFilter() {
        return this.saxXMLFilter;
    }

    public void setXMLFilter(XMLFilter xmlFilter) {
        this.saxXMLFilter = xmlFilter;
    }

    public boolean getIgnoringElementContentWhitespace() {
        return this.ignoringWhite;
    }

    public void setIgnoringElementContentWhitespace(boolean ignoringWhite) {
        this.ignoringWhite = ignoringWhite;
    }

    public boolean getIgnoringBoundaryWhitespace() {
        return this.ignoringBoundaryWhite;
    }

    public void setIgnoringBoundaryWhitespace(boolean ignoringBoundaryWhite) {
        this.ignoringBoundaryWhite = ignoringBoundaryWhite;
    }

    public boolean getReuseParser() {
        return this.reuseParser;
    }

    public void setReuseParser(boolean reuseParser) {
        this.reuseParser = reuseParser;
        this.saxParser = null;
    }

    public void setFastReconfigure(boolean fastReconfigure) {
        if (this.reuseParser) {
            this.fastReconfigure = fastReconfigure;
        }
    }

    public void setFeature(String name, boolean value) {
        this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setProperty(String name, Object value) {
        this.properties.put(name, value);
    }

    public Document build(InputSource in) throws JDOMException, IOException {
        SAXHandler contentHandler = null;
        try {
            Object root;
            contentHandler = this.createContentHandler();
            this.configureContentHandler(contentHandler);
            XMLReader parser = this.saxParser;
            if (parser == null) {
                parser = this.createParser();
                if (this.saxXMLFilter != null) {
                    root = this.saxXMLFilter;
                    while (root.getParent() instanceof XMLFilter) {
                        root = (XMLFilter)root.getParent();
                    }
                    root.setParent(parser);
                    parser = this.saxXMLFilter;
                }
                this.configureParser(parser, contentHandler);
                if (this.reuseParser) {
                    this.saxParser = parser;
                }
            } else {
                this.configureParser(parser, contentHandler);
            }
            parser.parse(in);
            root = contentHandler.getDocument();
            return root;
        }
        catch (SAXParseException e) {
            String systemId;
            Document doc = contentHandler.getDocument();
            if (!doc.hasRootElement()) {
                doc = null;
            }
            if ((systemId = e.getSystemId()) != null) {
                throw new JDOMParseException("Error on line " + e.getLineNumber() + " of document " + systemId, e, doc);
            }
            throw new JDOMParseException("Error on line " + e.getLineNumber(), e, doc);
        }
        catch (SAXException e) {
            throw new JDOMParseException("Error in building: " + e.getMessage(), e, contentHandler.getDocument());
        }
        finally {
            contentHandler = null;
        }
    }

    protected SAXHandler createContentHandler() {
        SAXHandler contentHandler = new SAXHandler(this.factory);
        return contentHandler;
    }

    protected void configureContentHandler(SAXHandler contentHandler) {
        contentHandler.setExpandEntities(this.expand);
        contentHandler.setIgnoringElementContentWhitespace(this.ignoringWhite);
        contentHandler.setIgnoringBoundaryWhitespace(this.ignoringBoundaryWhite);
    }

    protected XMLReader createParser() throws JDOMException {
        XMLReader parser = null;
        if (this.saxDriverClass != null) {
            try {
                parser = XMLReaderFactory.createXMLReader(this.saxDriverClass);
                this.setFeaturesAndProperties(parser, true);
            }
            catch (SAXException e) {
                throw new JDOMException("Could not load " + this.saxDriverClass, e);
            }
        }
        try {
            Class<?> factoryClass = Class.forName("org.jdom.input.JAXPParserFactory");
            Method createParser = factoryClass.getMethod("createParser", Boolean.TYPE, Map.class, Map.class);
            parser = (XMLReader)createParser.invoke(null, this.validate ? Boolean.TRUE : Boolean.FALSE, this.features, this.properties);
            this.setFeaturesAndProperties(parser, false);
        }
        catch (JDOMException e) {
            throw e;
        }
        catch (NoClassDefFoundError e) {
        }
        catch (Exception e) {
            // empty catch block
        }
        if (parser == null) {
            try {
                parser = XMLReaderFactory.createXMLReader(DEFAULT_SAX_DRIVER);
                this.saxDriverClass = parser.getClass().getName();
                this.setFeaturesAndProperties(parser, true);
            }
            catch (SAXException e) {
                throw new JDOMException("Could not load default SAX parser: org.apache.xerces.parsers.SAXParser", e);
            }
        }
        return parser;
    }

    protected void configureParser(XMLReader parser, SAXHandler contentHandler) throws JDOMException {
        boolean success;
        parser.setContentHandler(contentHandler);
        if (this.saxEntityResolver != null) {
            parser.setEntityResolver(this.saxEntityResolver);
        }
        if (this.saxDTDHandler != null) {
            parser.setDTDHandler(this.saxDTDHandler);
        } else {
            parser.setDTDHandler(contentHandler);
        }
        if (this.saxErrorHandler != null) {
            parser.setErrorHandler(this.saxErrorHandler);
        } else {
            parser.setErrorHandler(new BuilderErrorHandler());
        }
        if (!this.skipNextLexicalReportingConfig) {
            success = false;
            try {
                parser.setProperty("http://xml.org/sax/handlers/LexicalHandler", contentHandler);
                success = true;
            }
            catch (SAXNotSupportedException sAXNotSupportedException) {
            }
            catch (SAXNotRecognizedException sAXNotRecognizedException) {
                // empty catch block
            }
            if (!success) {
                try {
                    parser.setProperty("http://xml.org/sax/properties/lexical-handler", contentHandler);
                    success = true;
                }
                catch (SAXNotSupportedException sAXNotSupportedException) {
                }
                catch (SAXNotRecognizedException sAXNotRecognizedException) {
                    // empty catch block
                }
            }
            if (!success && this.fastReconfigure) {
                this.skipNextLexicalReportingConfig = true;
            }
        }
        if (!this.skipNextEntityExpandConfig) {
            success = false;
            if (!this.expand) {
                try {
                    parser.setProperty("http://xml.org/sax/properties/declaration-handler", contentHandler);
                    success = true;
                }
                catch (SAXNotSupportedException sAXNotSupportedException) {
                }
                catch (SAXNotRecognizedException sAXNotRecognizedException) {
                    // empty catch block
                }
            }
            if (!success && this.fastReconfigure) {
                this.skipNextEntityExpandConfig = true;
            }
        }
    }

    private void setFeaturesAndProperties(XMLReader parser, boolean coreFeatures) throws JDOMException {
        for (String name : this.properties.keySet()) {
            this.internalSetProperty(parser, name, this.properties.get(name), name);
        }
        if (coreFeatures) {
            block9: {
                try {
                    this.internalSetFeature(parser, "http://xml.org/sax/features/validation", this.validate, "Validation");
                }
                catch (JDOMException e) {
                    if (!this.validate) break block9;
                    throw e;
                }
            }
            this.internalSetFeature(parser, "http://xml.org/sax/features/namespaces", true, "Namespaces");
            this.internalSetFeature(parser, "http://xml.org/sax/features/namespace-prefixes", true, "Namespace prefixes");
        }
        try {
            if (parser.getFeature("http://xml.org/sax/features/external-general-entities") != this.expand) {
                parser.setFeature("http://xml.org/sax/features/external-general-entities", this.expand);
            }
        }
        catch (SAXNotRecognizedException e) {
        }
        catch (SAXNotSupportedException e) {
            // empty catch block
        }
        for (String name : this.features.keySet()) {
            Boolean value = (Boolean)this.features.get(name);
            this.internalSetFeature(parser, name, value, name);
        }
    }

    private void internalSetFeature(XMLReader parser, String feature, boolean value, String displayName) throws JDOMException {
        try {
            parser.setFeature(feature, value);
        }
        catch (SAXNotSupportedException e) {
            throw new JDOMException(displayName + " feature not supported for SAX driver " + parser.getClass().getName());
        }
        catch (SAXNotRecognizedException e) {
            throw new JDOMException(displayName + " feature not recognized for SAX driver " + parser.getClass().getName());
        }
    }

    private void internalSetProperty(XMLReader parser, String property, Object value, String displayName) throws JDOMException {
        try {
            parser.setProperty(property, value);
        }
        catch (SAXNotSupportedException e) {
            throw new JDOMException(displayName + " property not supported for SAX driver " + parser.getClass().getName());
        }
        catch (SAXNotRecognizedException e) {
            throw new JDOMException(displayName + " property not recognized for SAX driver " + parser.getClass().getName());
        }
    }

    public Document build(InputStream in) throws JDOMException, IOException {
        return this.build(new InputSource(in));
    }

    public Document build(File file) throws JDOMException, IOException {
        try {
            URL url = SAXBuilder.fileToURL(file);
            return this.build(url);
        }
        catch (MalformedURLException e) {
            throw new JDOMException("Error in building", e);
        }
    }

    public Document build(URL url) throws JDOMException, IOException {
        String systemID = url.toExternalForm();
        return this.build(new InputSource(systemID));
    }

    public Document build(InputStream in, String systemId) throws JDOMException, IOException {
        InputSource src = new InputSource(in);
        src.setSystemId(systemId);
        return this.build(src);
    }

    public Document build(Reader characterStream) throws JDOMException, IOException {
        return this.build(new InputSource(characterStream));
    }

    public Document build(Reader characterStream, String systemId) throws JDOMException, IOException {
        InputSource src = new InputSource(characterStream);
        src.setSystemId(systemId);
        return this.build(src);
    }

    public Document build(String systemId) throws JDOMException, IOException {
        return this.build(new InputSource(systemId));
    }

    private static URL fileToURL(File file) throws MalformedURLException {
        StringBuffer buffer = new StringBuffer();
        String path = file.getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }
        if (!path.startsWith("/")) {
            buffer.append('/');
        }
        int len = path.length();
        for (int i = 0; i < len; ++i) {
            char c = path.charAt(i);
            if (c == ' ') {
                buffer.append("%20");
                continue;
            }
            if (c == '#') {
                buffer.append("%23");
                continue;
            }
            if (c == '%') {
                buffer.append("%25");
                continue;
            }
            if (c == '&') {
                buffer.append("%26");
                continue;
            }
            if (c == ';') {
                buffer.append("%3B");
                continue;
            }
            if (c == '<') {
                buffer.append("%3C");
                continue;
            }
            if (c == '=') {
                buffer.append("%3D");
                continue;
            }
            if (c == '>') {
                buffer.append("%3E");
                continue;
            }
            if (c == '?') {
                buffer.append("%3F");
                continue;
            }
            if (c == '~') {
                buffer.append("%7E");
                continue;
            }
            buffer.append(c);
        }
        if (!path.endsWith("/") && file.isDirectory()) {
            buffer.append('/');
        }
        return new URL("file", "", buffer.toString());
    }

    public boolean getExpandEntities() {
        return this.expand;
    }

    public void setExpandEntities(boolean expand) {
        this.expand = expand;
    }
}

