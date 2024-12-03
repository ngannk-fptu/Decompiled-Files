/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import org.apache.xerces.parsers.XML11Configuration;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xinclude.XIncludeHandler;
import org.apache.xerces.xinclude.XIncludeNamespaceSupport;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentSource;

public class XIncludeAwareParserConfiguration
extends XML11Configuration {
    protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
    protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
    protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
    protected static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
    protected static final String XINCLUDE_HANDLER = "http://apache.org/xml/properties/internal/xinclude-handler";
    protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
    protected XIncludeHandler fXIncludeHandler;
    protected NamespaceSupport fNonXIncludeNSContext;
    protected XIncludeNamespaceSupport fXIncludeNSContext;
    protected NamespaceContext fCurrentNSContext;
    protected boolean fXIncludeEnabled = false;

    public XIncludeAwareParserConfiguration() {
        this(null, null, null);
    }

    public XIncludeAwareParserConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }

    public XIncludeAwareParserConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        this(symbolTable, xMLGrammarPool, null);
    }

    public XIncludeAwareParserConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool, XMLComponentManager xMLComponentManager) {
        super(symbolTable, xMLGrammarPool, xMLComponentManager);
        String[] stringArray = new String[]{ALLOW_UE_AND_NOTATION_EVENTS, XINCLUDE_FIXUP_BASE_URIS, XINCLUDE_FIXUP_LANGUAGE};
        this.addRecognizedFeatures(stringArray);
        String[] stringArray2 = new String[]{XINCLUDE_HANDLER, NAMESPACE_CONTEXT};
        this.addRecognizedProperties(stringArray2);
        this.setFeature(ALLOW_UE_AND_NOTATION_EVENTS, true);
        this.setFeature(XINCLUDE_FIXUP_BASE_URIS, true);
        this.setFeature(XINCLUDE_FIXUP_LANGUAGE, true);
        this.fNonXIncludeNSContext = new NamespaceSupport();
        this.fCurrentNSContext = this.fNonXIncludeNSContext;
        this.setProperty(NAMESPACE_CONTEXT, this.fNonXIncludeNSContext);
    }

    @Override
    protected void configurePipeline() {
        super.configurePipeline();
        if (this.fXIncludeEnabled) {
            if (this.fXIncludeHandler == null) {
                this.fXIncludeHandler = new XIncludeHandler();
                this.setProperty(XINCLUDE_HANDLER, this.fXIncludeHandler);
                this.addCommonComponent(this.fXIncludeHandler);
                this.fXIncludeHandler.reset(this);
            }
            if (this.fCurrentNSContext != this.fXIncludeNSContext) {
                if (this.fXIncludeNSContext == null) {
                    this.fXIncludeNSContext = new XIncludeNamespaceSupport();
                }
                this.fCurrentNSContext = this.fXIncludeNSContext;
                this.setProperty(NAMESPACE_CONTEXT, this.fXIncludeNSContext);
            }
            this.fDTDScanner.setDTDHandler(this.fDTDProcessor);
            this.fDTDProcessor.setDTDSource(this.fDTDScanner);
            this.fDTDProcessor.setDTDHandler(this.fXIncludeHandler);
            this.fXIncludeHandler.setDTDSource(this.fDTDProcessor);
            this.fXIncludeHandler.setDTDHandler(this.fDTDHandler);
            if (this.fDTDHandler != null) {
                this.fDTDHandler.setDTDSource(this.fXIncludeHandler);
            }
            XMLDocumentSource xMLDocumentSource = null;
            if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
                xMLDocumentSource = this.fSchemaValidator.getDocumentSource();
            } else {
                xMLDocumentSource = this.fLastComponent;
                this.fLastComponent = this.fXIncludeHandler;
            }
            XMLDocumentHandler xMLDocumentHandler = xMLDocumentSource.getDocumentHandler();
            xMLDocumentSource.setDocumentHandler(this.fXIncludeHandler);
            this.fXIncludeHandler.setDocumentSource(xMLDocumentSource);
            if (xMLDocumentHandler != null) {
                this.fXIncludeHandler.setDocumentHandler(xMLDocumentHandler);
                xMLDocumentHandler.setDocumentSource(this.fXIncludeHandler);
            }
        } else if (this.fCurrentNSContext != this.fNonXIncludeNSContext) {
            this.fCurrentNSContext = this.fNonXIncludeNSContext;
            this.setProperty(NAMESPACE_CONTEXT, this.fNonXIncludeNSContext);
        }
    }

    @Override
    protected void configureXML11Pipeline() {
        super.configureXML11Pipeline();
        if (this.fXIncludeEnabled) {
            if (this.fXIncludeHandler == null) {
                this.fXIncludeHandler = new XIncludeHandler();
                this.setProperty(XINCLUDE_HANDLER, this.fXIncludeHandler);
                this.addCommonComponent(this.fXIncludeHandler);
                this.fXIncludeHandler.reset(this);
            }
            if (this.fCurrentNSContext != this.fXIncludeNSContext) {
                if (this.fXIncludeNSContext == null) {
                    this.fXIncludeNSContext = new XIncludeNamespaceSupport();
                }
                this.fCurrentNSContext = this.fXIncludeNSContext;
                this.setProperty(NAMESPACE_CONTEXT, this.fXIncludeNSContext);
            }
            this.fXML11DTDScanner.setDTDHandler(this.fXML11DTDProcessor);
            this.fXML11DTDProcessor.setDTDSource(this.fXML11DTDScanner);
            this.fXML11DTDProcessor.setDTDHandler(this.fXIncludeHandler);
            this.fXIncludeHandler.setDTDSource(this.fXML11DTDProcessor);
            this.fXIncludeHandler.setDTDHandler(this.fDTDHandler);
            if (this.fDTDHandler != null) {
                this.fDTDHandler.setDTDSource(this.fXIncludeHandler);
            }
            XMLDocumentSource xMLDocumentSource = null;
            if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
                xMLDocumentSource = this.fSchemaValidator.getDocumentSource();
            } else {
                xMLDocumentSource = this.fLastComponent;
                this.fLastComponent = this.fXIncludeHandler;
            }
            XMLDocumentHandler xMLDocumentHandler = xMLDocumentSource.getDocumentHandler();
            xMLDocumentSource.setDocumentHandler(this.fXIncludeHandler);
            this.fXIncludeHandler.setDocumentSource(xMLDocumentSource);
            if (xMLDocumentHandler != null) {
                this.fXIncludeHandler.setDocumentHandler(xMLDocumentHandler);
                xMLDocumentHandler.setDocumentSource(this.fXIncludeHandler);
            }
        } else if (this.fCurrentNSContext != this.fNonXIncludeNSContext) {
            this.fCurrentNSContext = this.fNonXIncludeNSContext;
            this.setProperty(NAMESPACE_CONTEXT, this.fNonXIncludeNSContext);
        }
    }

    @Override
    public boolean getFeature(String string) throws XMLConfigurationException {
        if (string.equals("http://apache.org/xml/features/internal/parser-settings")) {
            return this.fConfigUpdated;
        }
        if (string.equals(XINCLUDE_FEATURE)) {
            return this.fXIncludeEnabled;
        }
        return super.getFeature0(string);
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
        if (string.equals(XINCLUDE_FEATURE)) {
            this.fXIncludeEnabled = bl;
            this.fConfigUpdated = true;
            return;
        }
        super.setFeature(string, bl);
    }
}

