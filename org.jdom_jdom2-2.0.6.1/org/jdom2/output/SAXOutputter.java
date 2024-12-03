/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output;

import java.util.List;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.JDOMException;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.JDOMLocator;
import org.jdom2.output.support.AbstractSAXOutputProcessor;
import org.jdom2.output.support.SAXOutputProcessor;
import org.jdom2.output.support.SAXTarget;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SAXOutputter {
    private static final SAXOutputProcessor DEFAULT_PROCESSOR = new DefaultSAXOutputProcessor();
    private ContentHandler contentHandler;
    private ErrorHandler errorHandler;
    private DTDHandler dtdHandler;
    private EntityResolver entityResolver;
    private LexicalHandler lexicalHandler;
    private DeclHandler declHandler;
    private boolean declareNamespaces = false;
    private boolean reportDtdEvents = true;
    private SAXOutputProcessor processor = DEFAULT_PROCESSOR;
    private Format format = Format.getRawFormat();

    public SAXOutputter() {
    }

    public SAXOutputter(ContentHandler contentHandler) {
        this(contentHandler, null, null, null, null);
    }

    public SAXOutputter(ContentHandler contentHandler, ErrorHandler errorHandler, DTDHandler dtdHandler, EntityResolver entityResolver) {
        this(contentHandler, errorHandler, dtdHandler, entityResolver, null);
    }

    public SAXOutputter(ContentHandler contentHandler, ErrorHandler errorHandler, DTDHandler dtdHandler, EntityResolver entityResolver, LexicalHandler lexicalHandler) {
        this.contentHandler = contentHandler;
        this.errorHandler = errorHandler;
        this.dtdHandler = dtdHandler;
        this.entityResolver = entityResolver;
        this.lexicalHandler = lexicalHandler;
    }

    public SAXOutputter(SAXOutputProcessor processor, Format format, ContentHandler contentHandler, ErrorHandler errorHandler, DTDHandler dtdHandler, EntityResolver entityResolver, LexicalHandler lexicalHandler) {
        this.processor = processor == null ? DEFAULT_PROCESSOR : processor;
        this.format = format == null ? Format.getRawFormat() : format;
        this.contentHandler = contentHandler;
        this.errorHandler = errorHandler;
        this.dtdHandler = dtdHandler;
        this.entityResolver = entityResolver;
        this.lexicalHandler = lexicalHandler;
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void setDTDHandler(DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }

    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        this.lexicalHandler = lexicalHandler;
    }

    public LexicalHandler getLexicalHandler() {
        return this.lexicalHandler;
    }

    public void setDeclHandler(DeclHandler declHandler) {
        this.declHandler = declHandler;
    }

    public DeclHandler getDeclHandler() {
        return this.declHandler;
    }

    public boolean getReportNamespaceDeclarations() {
        return this.declareNamespaces;
    }

    public void setReportNamespaceDeclarations(boolean declareNamespaces) {
        this.declareNamespaces = declareNamespaces;
    }

    public boolean getReportDTDEvents() {
        return this.reportDtdEvents;
    }

    public void setReportDTDEvents(boolean reportDtdEvents) {
        this.reportDtdEvents = reportDtdEvents;
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            this.setReportNamespaceDeclarations(value);
        } else if ("http://xml.org/sax/features/namespaces".equals(name)) {
            if (!value) {
                throw new SAXNotSupportedException(name);
            }
        } else if ("http://xml.org/sax/features/validation".equals(name)) {
            this.setReportDTDEvents(value);
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            return this.declareNamespaces;
        }
        if ("http://xml.org/sax/features/namespaces".equals(name)) {
            return true;
        }
        if ("http://xml.org/sax/features/validation".equals(name)) {
            return this.reportDtdEvents;
        }
        throw new SAXNotRecognizedException(name);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name) || "http://xml.org/sax/handlers/LexicalHandler".equals(name)) {
            this.setLexicalHandler((LexicalHandler)value);
        } else if ("http://xml.org/sax/properties/declaration-handler".equals(name) || "http://xml.org/sax/handlers/DeclHandler".equals(name)) {
            this.setDeclHandler((DeclHandler)value);
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name) || "http://xml.org/sax/handlers/LexicalHandler".equals(name)) {
            return this.getLexicalHandler();
        }
        if ("http://xml.org/sax/properties/declaration-handler".equals(name) || "http://xml.org/sax/handlers/DeclHandler".equals(name)) {
            return this.getDeclHandler();
        }
        throw new SAXNotRecognizedException(name);
    }

    public SAXOutputProcessor getSAXOutputProcessor() {
        return this.processor;
    }

    public void setSAXOutputProcessor(SAXOutputProcessor processor) {
        this.processor = processor == null ? DEFAULT_PROCESSOR : processor;
    }

    public Format getFormat() {
        return this.format;
    }

    public void setFormat(Format format) {
        this.format = format == null ? Format.getRawFormat() : format;
    }

    private final SAXTarget buildTarget(Document doc) {
        DocType dt;
        String publicID = null;
        String systemID = null;
        if (doc != null && (dt = doc.getDocType()) != null) {
            publicID = dt.getPublicID();
            systemID = dt.getSystemID();
        }
        return new SAXTarget(this.contentHandler, this.errorHandler, this.dtdHandler, this.entityResolver, this.lexicalHandler, this.declHandler, this.declareNamespaces, this.reportDtdEvents, publicID, systemID);
    }

    public void output(Document document) throws JDOMException {
        this.processor.process(this.buildTarget(document), this.format, document);
    }

    public void output(List<? extends Content> nodes) throws JDOMException {
        this.processor.processAsDocument(this.buildTarget(null), this.format, nodes);
    }

    public void output(Element node) throws JDOMException {
        this.processor.processAsDocument(this.buildTarget(null), this.format, node);
    }

    public void outputFragment(List<? extends Content> nodes) throws JDOMException {
        if (nodes == null) {
            return;
        }
        this.processor.process(this.buildTarget(null), this.format, nodes);
    }

    public void outputFragment(Content node) throws JDOMException {
        if (node == null) {
            return;
        }
        SAXTarget out = this.buildTarget(null);
        switch (node.getCType()) {
            case CDATA: {
                this.processor.process(out, this.format, (CDATA)node);
                break;
            }
            case Comment: {
                this.processor.process(out, this.format, (Comment)node);
                break;
            }
            case Element: {
                this.processor.process(out, this.format, (Element)node);
                break;
            }
            case EntityRef: {
                this.processor.process(out, this.format, (EntityRef)node);
                break;
            }
            case ProcessingInstruction: {
                this.processor.process(out, this.format, (ProcessingInstruction)node);
                break;
            }
            case Text: {
                this.processor.process(out, this.format, (Text)node);
                break;
            }
            default: {
                this.handleError(new JDOMException("Invalid element content: " + node));
            }
        }
    }

    private void handleError(JDOMException exception) throws JDOMException {
        if (this.errorHandler != null) {
            try {
                this.errorHandler.error(new SAXParseException(exception.getMessage(), null, exception));
            }
            catch (SAXException se) {
                if (se.getException() instanceof JDOMException) {
                    throw (JDOMException)se.getException();
                }
                throw new JDOMException(se.getMessage(), se);
            }
        } else {
            throw exception;
        }
    }

    @Deprecated
    public JDOMLocator getLocator() {
        return null;
    }

    private static final class DefaultSAXOutputProcessor
    extends AbstractSAXOutputProcessor {
        private DefaultSAXOutputProcessor() {
        }
    }
}

