/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.jdom2.DefaultJDOMFactory;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.JDOMFactory;
import org.jdom2.Verifier;
import org.jdom2.input.sax.BuilderErrorHandler;
import org.jdom2.input.sax.DefaultSAXHandlerFactory;
import org.jdom2.input.sax.SAXBuilderEngine;
import org.jdom2.input.sax.SAXEngine;
import org.jdom2.input.sax.SAXHandler;
import org.jdom2.input.sax.SAXHandlerFactory;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.input.sax.XMLReaders;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

public class SAXBuilder
implements SAXEngine {
    private static final SAXHandlerFactory DEFAULTSAXHANDLERFAC = new DefaultSAXHandlerFactory();
    private static final JDOMFactory DEFAULTJDOMFAC = new DefaultJDOMFactory();
    private XMLReaderJDOMFactory readerfac = null;
    private SAXHandlerFactory handlerfac = null;
    private JDOMFactory jdomfac = null;
    private final HashMap<String, Boolean> features = new HashMap(5);
    private final HashMap<String, Object> properties = new HashMap(5);
    private ErrorHandler saxErrorHandler = null;
    private EntityResolver saxEntityResolver = null;
    private DTDHandler saxDTDHandler = null;
    private XMLFilter saxXMLFilter = null;
    private boolean ignoringWhite = false;
    private boolean ignoringBoundaryWhite = false;
    private boolean reuseParser = true;
    private SAXEngine engine = null;

    public SAXBuilder() {
        this(null, null, null);
    }

    @Deprecated
    public SAXBuilder(boolean validate) {
        this(validate ? XMLReaders.DTDVALIDATING : XMLReaders.NONVALIDATING, null, null);
    }

    @Deprecated
    public SAXBuilder(String saxDriverClass) {
        this(saxDriverClass, false);
    }

    @Deprecated
    public SAXBuilder(String saxDriverClass, boolean validate) {
        this(new XMLReaderSAX2Factory(validate, saxDriverClass), null, null);
    }

    public SAXBuilder(XMLReaderJDOMFactory readersouce) {
        this(readersouce, null, null);
    }

    public SAXBuilder(XMLReaderJDOMFactory xmlreaderfactory, SAXHandlerFactory handlerfactory, JDOMFactory jdomfactory) {
        this.setExpandEntities(true);
        this.readerfac = xmlreaderfactory == null ? XMLReaders.NONVALIDATING : xmlreaderfactory;
        this.handlerfac = handlerfactory == null ? DEFAULTSAXHANDLERFAC : handlerfactory;
        this.jdomfac = jdomfactory == null ? DEFAULTJDOMFAC : jdomfactory;
    }

    @Deprecated
    public String getDriverClass() {
        if (this.readerfac instanceof XMLReaderSAX2Factory) {
            return ((XMLReaderSAX2Factory)this.readerfac).getDriverClassName();
        }
        return null;
    }

    @Deprecated
    public JDOMFactory getFactory() {
        return this.getJDOMFactory();
    }

    public JDOMFactory getJDOMFactory() {
        return this.jdomfac;
    }

    @Deprecated
    public void setFactory(JDOMFactory factory) {
        this.setJDOMFactory(factory);
    }

    public void setJDOMFactory(JDOMFactory factory) {
        this.jdomfac = factory;
        this.engine = null;
    }

    public XMLReaderJDOMFactory getXMLReaderFactory() {
        return this.readerfac;
    }

    public void setXMLReaderFactory(XMLReaderJDOMFactory rfac) {
        this.readerfac = rfac == null ? XMLReaders.NONVALIDATING : rfac;
        this.engine = null;
    }

    public SAXHandlerFactory getSAXHandlerFactory() {
        return this.handlerfac;
    }

    public void setSAXHandlerFactory(SAXHandlerFactory factory) {
        this.handlerfac = factory == null ? DEFAULTSAXHANDLERFAC : factory;
        this.engine = null;
    }

    @Deprecated
    public boolean getValidation() {
        return this.isValidating();
    }

    public boolean isValidating() {
        return this.readerfac.isValidating();
    }

    @Deprecated
    public void setValidation(boolean validate) {
        this.setXMLReaderFactory(validate ? XMLReaders.DTDVALIDATING : XMLReaders.NONVALIDATING);
    }

    public ErrorHandler getErrorHandler() {
        return this.saxErrorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.saxErrorHandler = errorHandler;
        this.engine = null;
    }

    public EntityResolver getEntityResolver() {
        return this.saxEntityResolver;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.saxEntityResolver = entityResolver;
        this.engine = null;
    }

    public DTDHandler getDTDHandler() {
        return this.saxDTDHandler;
    }

    public void setDTDHandler(DTDHandler dtdHandler) {
        this.saxDTDHandler = dtdHandler;
        this.engine = null;
    }

    public XMLFilter getXMLFilter() {
        return this.saxXMLFilter;
    }

    public void setXMLFilter(XMLFilter xmlFilter) {
        this.saxXMLFilter = xmlFilter;
        this.engine = null;
    }

    public boolean getIgnoringElementContentWhitespace() {
        return this.ignoringWhite;
    }

    public void setIgnoringElementContentWhitespace(boolean ignoringWhite) {
        this.ignoringWhite = ignoringWhite;
        this.engine = null;
    }

    public boolean getIgnoringBoundaryWhitespace() {
        return this.ignoringBoundaryWhite;
    }

    public void setIgnoringBoundaryWhitespace(boolean ignoringBoundaryWhite) {
        this.ignoringBoundaryWhite = ignoringBoundaryWhite;
        this.engine = null;
    }

    public boolean getExpandEntities() {
        return Boolean.TRUE.equals(this.features.get("http://xml.org/sax/features/external-general-entities"));
    }

    public void setExpandEntities(boolean expand) {
        this.features.put("http://xml.org/sax/features/external-general-entities", expand ? Boolean.TRUE : Boolean.FALSE);
        this.engine = null;
    }

    public boolean getReuseParser() {
        return this.reuseParser;
    }

    public void setReuseParser(boolean reuseParser) {
        this.reuseParser = reuseParser;
        if (!reuseParser) {
            this.engine = null;
        }
    }

    @Deprecated
    public void setFastReconfigure(boolean fastReconfigure) {
    }

    public void setFeature(String name, boolean value) {
        this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
        if ("http://xml.org/sax/features/external-general-entities".equals(name)) {
            this.setExpandEntities(value);
        }
        this.engine = null;
    }

    public void setProperty(String name, Object value) {
        this.properties.put(name, value);
        this.engine = null;
    }

    public SAXEngine buildEngine() throws JDOMException {
        SAXHandler contentHandler = this.handlerfac.createSAXHandler(this.jdomfac);
        contentHandler.setExpandEntities(this.getExpandEntities());
        contentHandler.setIgnoringElementContentWhitespace(this.ignoringWhite);
        contentHandler.setIgnoringBoundaryWhitespace(this.ignoringBoundaryWhite);
        XMLReader parser = this.createParser();
        this.configureParser(parser, contentHandler);
        boolean valid = this.readerfac.isValidating();
        return new SAXBuilderEngine(parser, contentHandler, valid);
    }

    protected XMLReader createParser() throws JDOMException {
        XMLReader parser = this.readerfac.createXMLReader();
        if (this.saxXMLFilter != null) {
            XMLFilter root = this.saxXMLFilter;
            while (root.getParent() instanceof XMLFilter) {
                root = (XMLFilter)root.getParent();
            }
            root.setParent(parser);
            parser = this.saxXMLFilter;
        }
        return parser;
    }

    private SAXEngine getEngine() throws JDOMException {
        if (this.engine != null) {
            return this.engine;
        }
        this.engine = this.buildEngine();
        return this.engine;
    }

    protected void configureParser(XMLReader parser, SAXHandler contentHandler) throws JDOMException {
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
        boolean success = false;
        try {
            parser.setProperty("http://xml.org/sax/properties/lexical-handler", contentHandler);
            success = true;
        }
        catch (SAXNotSupportedException sAXNotSupportedException) {
        }
        catch (SAXNotRecognizedException sAXNotRecognizedException) {
            // empty catch block
        }
        if (!success) {
            try {
                parser.setProperty("http://xml.org/sax/handlers/LexicalHandler", contentHandler);
                success = true;
            }
            catch (SAXNotSupportedException sAXNotSupportedException) {
            }
            catch (SAXNotRecognizedException sAXNotRecognizedException) {
                // empty catch block
            }
        }
        for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
            this.internalSetProperty(parser, entry.getKey(), entry.getValue(), entry.getKey());
        }
        for (Map.Entry<String, Object> entry : this.features.entrySet()) {
            this.internalSetFeature(parser, entry.getKey(), (Boolean)entry.getValue(), entry.getKey());
        }
        if (!this.getExpandEntities()) {
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
    }

    private void internalSetFeature(XMLReader parser, String feature, boolean value, String displayName) throws JDOMException {
        try {
            parser.setFeature(feature, value);
        }
        catch (SAXNotSupportedException e) {
            throw new JDOMException(displayName + " feature " + feature + " not supported for SAX driver " + parser.getClass().getName());
        }
        catch (SAXNotRecognizedException e) {
            throw new JDOMException(displayName + " feature " + feature + " not recognized for SAX driver " + parser.getClass().getName());
        }
    }

    private void internalSetProperty(XMLReader parser, String property, Object value, String displayName) throws JDOMException {
        try {
            parser.setProperty(property, value);
        }
        catch (SAXNotSupportedException e) {
            throw new JDOMException(displayName + " property " + property + " not supported for SAX driver " + parser.getClass().getName());
        }
        catch (SAXNotRecognizedException e) {
            throw new JDOMException(displayName + " property " + property + " not recognized for SAX driver " + parser.getClass().getName());
        }
    }

    public Document build(InputSource in) throws JDOMException, IOException {
        try {
            Document document = this.getEngine().build(in);
            return document;
        }
        finally {
            if (!this.reuseParser) {
                this.engine = null;
            }
        }
    }

    public Document build(InputStream in) throws JDOMException, IOException {
        try {
            Document document = this.getEngine().build(in);
            return document;
        }
        finally {
            if (!this.reuseParser) {
                this.engine = null;
            }
        }
    }

    public Document build(File file) throws JDOMException, IOException {
        try {
            Document document = this.getEngine().build(file);
            return document;
        }
        finally {
            if (!this.reuseParser) {
                this.engine = null;
            }
        }
    }

    public Document build(URL url) throws JDOMException, IOException {
        try {
            Document document = this.getEngine().build(url);
            return document;
        }
        finally {
            if (!this.reuseParser) {
                this.engine = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Document build(InputStream in, String systemId) throws JDOMException, IOException {
        try {
            Document document = this.getEngine().build(in, systemId);
            return document;
        }
        finally {
            if (!this.reuseParser) {
                this.engine = null;
            }
        }
    }

    public Document build(Reader characterStream) throws JDOMException, IOException {
        try {
            Document document = this.getEngine().build(characterStream);
            return document;
        }
        finally {
            if (!this.reuseParser) {
                this.engine = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Document build(Reader characterStream, String systemId) throws JDOMException, IOException {
        try {
            Document document = this.getEngine().build(characterStream, systemId);
            return document;
        }
        finally {
            if (!this.reuseParser) {
                this.engine = null;
            }
        }
    }

    public Document build(String systemId) throws JDOMException, IOException {
        if (systemId == null) {
            throw new NullPointerException("Unable to build a URI from a null systemID.");
        }
        try {
            Document document = this.getEngine().build(systemId);
            return document;
        }
        catch (IOException ioe) {
            int i;
            int len = systemId.length();
            for (i = 0; i < len && Verifier.isXMLWhitespace(systemId.charAt(i)); ++i) {
            }
            if (i < len && '<' == systemId.charAt(i)) {
                MalformedURLException mx = new MalformedURLException("SAXBuilder.build(String) expects the String to be a systemID, but in this instance it appears to be actual XML data.");
                mx.initCause(ioe);
                throw mx;
            }
            throw ioe;
        }
        finally {
            if (!this.reuseParser) {
                this.engine = null;
            }
        }
    }
}

