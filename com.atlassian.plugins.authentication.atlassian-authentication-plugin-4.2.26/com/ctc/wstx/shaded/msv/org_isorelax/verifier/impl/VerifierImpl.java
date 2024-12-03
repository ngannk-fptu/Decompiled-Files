/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.verifier.impl;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFilter;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierHandler;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.impl.SAXEventGenerator;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.impl.VerifierFilterImpl;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public abstract class VerifierImpl
implements Verifier {
    protected XMLReader reader;
    protected ErrorHandler errorHandler = new ErrorHandler(){

        public void warning(SAXParseException e) {
        }

        public void error(SAXParseException e) {
        }

        public void fatalError(SAXParseException e) {
        }
    };
    protected EntityResolver entityResolver;
    private VerifierFilter filter;

    protected VerifierImpl() throws VerifierConfigurationException {
        this.prepareXMLReader();
    }

    protected void prepareXMLReader() throws VerifierConfigurationException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            this.reader = factory.newSAXParser().getXMLReader();
        }
        catch (SAXException e) {
            throw new VerifierConfigurationException(e);
        }
        catch (ParserConfigurationException pce) {
            throw new VerifierConfigurationException(pce);
        }
    }

    public boolean isFeature(String feature) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://www.iso-relax.org/verifier/handler".equals(feature) || "http://www.iso-relax.org/verifier/filter".equals(feature)) {
            return true;
        }
        throw new SAXNotRecognizedException(feature);
    }

    public void setFeature(String feature, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(feature);
    }

    public Object getProperty(String property) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(property);
    }

    public void setProperty(String property, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(property);
    }

    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    public void setEntityResolver(EntityResolver resolver) {
        this.entityResolver = resolver;
    }

    public boolean verify(String uri) throws SAXException, IOException {
        return this.verify(new InputSource(uri));
    }

    public boolean verify(InputSource source) throws SAXException, IOException {
        VerifierHandler handler = this.getVerifierHandler();
        this.reader.setErrorHandler(this.errorHandler);
        if (this.entityResolver != null) {
            this.reader.setEntityResolver(this.entityResolver);
        }
        this.reader.setContentHandler(handler);
        this.reader.parse(source);
        return handler.isValid();
    }

    public boolean verify(File f) throws SAXException, IOException {
        String uri = "file:" + f.getAbsolutePath();
        if (File.separatorChar == '\\') {
            uri = uri.replace('\\', '/');
        }
        return this.verify(new InputSource(uri));
    }

    public boolean verify(Node node) throws SAXException {
        SAXEventGenerator generator = new SAXEventGenerator(node);
        generator.setDocumentEmulation(true);
        generator.setErrorHandler(this.errorHandler);
        VerifierHandler handler = this.getVerifierHandler();
        generator.makeEvent(handler);
        return handler.isValid();
    }

    public abstract VerifierHandler getVerifierHandler() throws SAXException;

    public VerifierFilter getVerifierFilter() throws SAXException {
        if (this.filter == null) {
            this.filter = new VerifierFilterImpl(this);
        }
        return this.filter;
    }
}

