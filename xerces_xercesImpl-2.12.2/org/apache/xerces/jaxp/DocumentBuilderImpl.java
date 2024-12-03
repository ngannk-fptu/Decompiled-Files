/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.Schema;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.impl.xs.XMLSchemaValidator;
import org.apache.xerces.jaxp.DefaultValidationErrorHandler;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.apache.xerces.jaxp.JAXPConstants;
import org.apache.xerces.jaxp.JAXPValidatorComponent;
import org.apache.xerces.jaxp.SchemaValidatorConfiguration;
import org.apache.xerces.jaxp.UnparsedEntityHandler;
import org.apache.xerces.jaxp.validation.XSGrammarPoolContainer;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class DocumentBuilderImpl
extends DocumentBuilder
implements JAXPConstants {
    private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
    private static final String INCLUDE_IGNORABLE_WHITESPACE = "http://apache.org/xml/features/dom/include-ignorable-whitespace";
    private static final String CREATE_ENTITY_REF_NODES_FEATURE = "http://apache.org/xml/features/dom/create-entity-ref-nodes";
    private static final String INCLUDE_COMMENTS_FEATURE = "http://apache.org/xml/features/include-comments";
    private static final String CREATE_CDATA_NODES_FEATURE = "http://apache.org/xml/features/create-cdata-nodes";
    private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
    private static final String XMLSCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
    private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private final DOMParser domParser = new DOMParser();
    private final Schema grammar;
    private final XMLComponent fSchemaValidator;
    private final XMLComponentManager fSchemaValidatorComponentManager;
    private final ValidationManager fSchemaValidationManager;
    private final UnparsedEntityHandler fUnparsedEntityHandler;
    private final ErrorHandler fInitErrorHandler;
    private final EntityResolver fInitEntityResolver;

    DocumentBuilderImpl(DocumentBuilderFactoryImpl documentBuilderFactoryImpl, Hashtable hashtable, Hashtable hashtable2) throws SAXNotRecognizedException, SAXNotSupportedException {
        this(documentBuilderFactoryImpl, hashtable, hashtable2, false);
    }

    DocumentBuilderImpl(DocumentBuilderFactoryImpl documentBuilderFactoryImpl, Hashtable hashtable, Hashtable hashtable2, boolean bl) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (documentBuilderFactoryImpl.isValidating()) {
            this.fInitErrorHandler = new DefaultValidationErrorHandler();
            this.setErrorHandler(this.fInitErrorHandler);
        } else {
            this.fInitErrorHandler = this.domParser.getErrorHandler();
        }
        this.domParser.setFeature(VALIDATION_FEATURE, documentBuilderFactoryImpl.isValidating());
        this.domParser.setFeature(NAMESPACES_FEATURE, documentBuilderFactoryImpl.isNamespaceAware());
        this.domParser.setFeature(INCLUDE_IGNORABLE_WHITESPACE, !documentBuilderFactoryImpl.isIgnoringElementContentWhitespace());
        this.domParser.setFeature(CREATE_ENTITY_REF_NODES_FEATURE, !documentBuilderFactoryImpl.isExpandEntityReferences());
        this.domParser.setFeature(INCLUDE_COMMENTS_FEATURE, !documentBuilderFactoryImpl.isIgnoringComments());
        this.domParser.setFeature(CREATE_CDATA_NODES_FEATURE, !documentBuilderFactoryImpl.isCoalescing());
        if (documentBuilderFactoryImpl.isXIncludeAware()) {
            this.domParser.setFeature(XINCLUDE_FEATURE, true);
        }
        if (bl) {
            this.domParser.setProperty(SECURITY_MANAGER, new SecurityManager());
        }
        this.grammar = documentBuilderFactoryImpl.getSchema();
        if (this.grammar != null) {
            XMLParserConfiguration xMLParserConfiguration = this.domParser.getXMLParserConfiguration();
            XMLDocumentFilter xMLDocumentFilter = null;
            if (this.grammar instanceof XSGrammarPoolContainer) {
                xMLDocumentFilter = new XMLSchemaValidator();
                this.fSchemaValidationManager = new ValidationManager();
                this.fUnparsedEntityHandler = new UnparsedEntityHandler(this.fSchemaValidationManager);
                xMLParserConfiguration.setDTDHandler(this.fUnparsedEntityHandler);
                this.fUnparsedEntityHandler.setDTDHandler(this.domParser);
                this.domParser.setDTDSource(this.fUnparsedEntityHandler);
                this.fSchemaValidatorComponentManager = new SchemaValidatorConfiguration(xMLParserConfiguration, (XSGrammarPoolContainer)((Object)this.grammar), this.fSchemaValidationManager);
            } else {
                xMLDocumentFilter = new JAXPValidatorComponent(this.grammar.newValidatorHandler());
                this.fSchemaValidationManager = null;
                this.fUnparsedEntityHandler = null;
                this.fSchemaValidatorComponentManager = xMLParserConfiguration;
            }
            xMLParserConfiguration.addRecognizedFeatures(xMLDocumentFilter.getRecognizedFeatures());
            xMLParserConfiguration.addRecognizedProperties(xMLDocumentFilter.getRecognizedProperties());
            xMLParserConfiguration.setDocumentHandler(xMLDocumentFilter);
            ((XMLDocumentSource)xMLDocumentFilter).setDocumentHandler(this.domParser);
            this.domParser.setDocumentSource(xMLDocumentFilter);
            this.fSchemaValidator = xMLDocumentFilter;
        } else {
            this.fSchemaValidationManager = null;
            this.fUnparsedEntityHandler = null;
            this.fSchemaValidatorComponentManager = null;
            this.fSchemaValidator = null;
        }
        this.setFeatures(hashtable2);
        this.setDocumentBuilderFactoryAttributes(hashtable);
        this.fInitEntityResolver = this.domParser.getEntityResolver();
    }

    private void setFeatures(Hashtable hashtable) throws SAXNotSupportedException, SAXNotRecognizedException {
        if (hashtable != null) {
            for (Map.Entry entry : hashtable.entrySet()) {
                String string = (String)entry.getKey();
                boolean bl = (Boolean)entry.getValue();
                this.domParser.setFeature(string, bl);
            }
        }
    }

    private void setDocumentBuilderFactoryAttributes(Hashtable hashtable) throws SAXNotSupportedException, SAXNotRecognizedException {
        if (hashtable == null) {
            return;
        }
        for (Map.Entry entry : hashtable.entrySet()) {
            String string = (String)entry.getKey();
            Object v = entry.getValue();
            if (v instanceof Boolean) {
                this.domParser.setFeature(string, (Boolean)v);
                continue;
            }
            if ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(string)) {
                if (!"http://www.w3.org/2001/XMLSchema".equals(v) || !this.isValidating()) continue;
                this.domParser.setFeature(XMLSCHEMA_VALIDATION_FEATURE, true);
                this.domParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
                continue;
            }
            if ("http://java.sun.com/xml/jaxp/properties/schemaSource".equals(string)) {
                if (!this.isValidating()) continue;
                String string2 = (String)hashtable.get("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
                if (string2 != null && "http://www.w3.org/2001/XMLSchema".equals(string2)) {
                    this.domParser.setProperty(string, v);
                    continue;
                }
                throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-order-not-supported", new Object[]{"http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://java.sun.com/xml/jaxp/properties/schemaSource"}));
            }
            this.domParser.setProperty(string, v);
        }
    }

    @Override
    public Document newDocument() {
        return new DocumentImpl();
    }

    @Override
    public DOMImplementation getDOMImplementation() {
        return DOMImplementationImpl.getDOMImplementation();
    }

    @Override
    public Document parse(InputSource inputSource) throws SAXException, IOException {
        if (inputSource == null) {
            throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-null-input-source", null));
        }
        if (this.fSchemaValidator != null) {
            if (this.fSchemaValidationManager != null) {
                this.fSchemaValidationManager.reset();
                this.fUnparsedEntityHandler.reset();
            }
            this.resetSchemaValidator();
        }
        this.domParser.parse(inputSource);
        Document document = this.domParser.getDocument();
        this.domParser.dropDocumentReferences();
        return document;
    }

    @Override
    public boolean isNamespaceAware() {
        try {
            return this.domParser.getFeature(NAMESPACES_FEATURE);
        }
        catch (SAXException sAXException) {
            throw new IllegalStateException(sAXException.getMessage());
        }
    }

    @Override
    public boolean isValidating() {
        try {
            return this.domParser.getFeature(VALIDATION_FEATURE);
        }
        catch (SAXException sAXException) {
            throw new IllegalStateException(sAXException.getMessage());
        }
    }

    @Override
    public boolean isXIncludeAware() {
        try {
            return this.domParser.getFeature(XINCLUDE_FEATURE);
        }
        catch (SAXException sAXException) {
            return false;
        }
    }

    @Override
    public void setEntityResolver(EntityResolver entityResolver) {
        this.domParser.setEntityResolver(entityResolver);
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.domParser.setErrorHandler(errorHandler);
    }

    @Override
    public Schema getSchema() {
        return this.grammar;
    }

    @Override
    public void reset() {
        if (this.domParser.getErrorHandler() != this.fInitErrorHandler) {
            this.domParser.setErrorHandler(this.fInitErrorHandler);
        }
        if (this.domParser.getEntityResolver() != this.fInitEntityResolver) {
            this.domParser.setEntityResolver(this.fInitEntityResolver);
        }
    }

    DOMParser getDOMParser() {
        return this.domParser;
    }

    private void resetSchemaValidator() throws SAXException {
        try {
            this.fSchemaValidator.reset(this.fSchemaValidatorComponentManager);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            throw new SAXException(xMLConfigurationException);
        }
    }
}

