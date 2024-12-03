/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.JDOMFactory;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.sax.SAXEngine;
import org.jdom2.input.sax.SAXHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class SAXBuilderEngine
implements SAXEngine {
    private final XMLReader saxParser;
    private final SAXHandler saxHandler;
    private final boolean validating;

    public SAXBuilderEngine(XMLReader reader, SAXHandler handler, boolean validating) {
        this.saxParser = reader;
        this.saxHandler = handler;
        this.validating = validating;
    }

    public JDOMFactory getJDOMFactory() {
        return this.saxHandler.getFactory();
    }

    public boolean isValidating() {
        return this.validating;
    }

    public ErrorHandler getErrorHandler() {
        return this.saxParser.getErrorHandler();
    }

    public EntityResolver getEntityResolver() {
        return this.saxParser.getEntityResolver();
    }

    public DTDHandler getDTDHandler() {
        return this.saxParser.getDTDHandler();
    }

    public boolean getIgnoringElementContentWhitespace() {
        return this.saxHandler.getIgnoringElementContentWhitespace();
    }

    public boolean getIgnoringBoundaryWhitespace() {
        return this.saxHandler.getIgnoringBoundaryWhitespace();
    }

    public boolean getExpandEntities() {
        return this.saxHandler.getExpandEntities();
    }

    public Document build(InputSource in) throws JDOMException, IOException {
        try {
            this.saxParser.parse(in);
            Document document = this.saxHandler.getDocument();
            return document;
        }
        catch (SAXParseException e) {
            String systemId;
            Document doc = this.saxHandler.getDocument();
            if (!doc.hasRootElement()) {
                doc = null;
            }
            if ((systemId = e.getSystemId()) != null) {
                throw new JDOMParseException("Error on line " + e.getLineNumber() + " of document " + systemId + ": " + e.getMessage(), e, doc);
            }
            throw new JDOMParseException("Error on line " + e.getLineNumber() + ": " + e.getMessage(), e, doc);
        }
        catch (SAXException e) {
            throw new JDOMParseException("Error in building: " + e.getMessage(), e, this.saxHandler.getDocument());
        }
        finally {
            this.saxHandler.reset();
        }
    }

    public Document build(InputStream in) throws JDOMException, IOException {
        return this.build(new InputSource(in));
    }

    public Document build(File file) throws JDOMException, IOException {
        try {
            return this.build(SAXBuilderEngine.fileToURL(file));
        }
        catch (MalformedURLException e) {
            throw new JDOMException("Error in building", e);
        }
    }

    public Document build(URL url) throws JDOMException, IOException {
        return this.build(new InputSource(url.toExternalForm()));
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
        return file.getAbsoluteFile().toURI().toURL();
    }
}

