/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import org.apache.xerces.parsers.XML11Configuration;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xinclude.XIncludeHandler;
import org.apache.xerces.xinclude.XIncludeNamespaceSupport;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xpointer.XPointerHandler;

public class XPointerParserConfiguration
extends XML11Configuration {
    private XPointerHandler fXPointerHandler;
    private XIncludeHandler fXIncludeHandler = new XIncludeHandler();
    protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
    protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
    protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
    protected static final String XPOINTER_HANDLER = "http://apache.org/xml/properties/internal/xpointer-handler";
    protected static final String XINCLUDE_HANDLER = "http://apache.org/xml/properties/internal/xinclude-handler";
    protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";

    public XPointerParserConfiguration() {
        this(null, null, null);
    }

    public XPointerParserConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }

    public XPointerParserConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        this(symbolTable, xMLGrammarPool, null);
    }

    public XPointerParserConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool, XMLComponentManager xMLComponentManager) {
        super(symbolTable, xMLGrammarPool, xMLComponentManager);
        this.addCommonComponent(this.fXIncludeHandler);
        this.fXPointerHandler = new XPointerHandler();
        this.addCommonComponent(this.fXPointerHandler);
        String[] stringArray = new String[]{ALLOW_UE_AND_NOTATION_EVENTS, XINCLUDE_FIXUP_BASE_URIS, XINCLUDE_FIXUP_LANGUAGE};
        this.addRecognizedFeatures(stringArray);
        String[] stringArray2 = new String[]{XINCLUDE_HANDLER, XPOINTER_HANDLER, NAMESPACE_CONTEXT};
        this.addRecognizedProperties(stringArray2);
        this.setFeature(ALLOW_UE_AND_NOTATION_EVENTS, true);
        this.setFeature(XINCLUDE_FIXUP_BASE_URIS, true);
        this.setFeature(XINCLUDE_FIXUP_LANGUAGE, true);
        this.setProperty(XINCLUDE_HANDLER, this.fXIncludeHandler);
        this.setProperty(XPOINTER_HANDLER, this.fXPointerHandler);
        this.setProperty(NAMESPACE_CONTEXT, new XIncludeNamespaceSupport());
    }

    @Override
    protected void configurePipeline() {
        super.configurePipeline();
        this.fDTDScanner.setDTDHandler(this.fDTDProcessor);
        this.fDTDProcessor.setDTDSource(this.fDTDScanner);
        this.fDTDProcessor.setDTDHandler(this.fXIncludeHandler);
        this.fXIncludeHandler.setDTDSource(this.fDTDProcessor);
        this.fXIncludeHandler.setDTDHandler(this.fXPointerHandler);
        this.fXPointerHandler.setDTDSource(this.fXIncludeHandler);
        this.fXPointerHandler.setDTDHandler(this.fDTDHandler);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.setDTDSource(this.fXPointerHandler);
        }
        XMLDocumentSource xMLDocumentSource = null;
        if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
            xMLDocumentSource = this.fSchemaValidator.getDocumentSource();
        } else {
            xMLDocumentSource = this.fLastComponent;
            this.fLastComponent = this.fXPointerHandler;
        }
        XMLDocumentHandler xMLDocumentHandler = xMLDocumentSource.getDocumentHandler();
        xMLDocumentSource.setDocumentHandler(this.fXIncludeHandler);
        this.fXIncludeHandler.setDocumentSource(xMLDocumentSource);
        if (xMLDocumentHandler != null) {
            this.fXIncludeHandler.setDocumentHandler(xMLDocumentHandler);
            xMLDocumentHandler.setDocumentSource(this.fXIncludeHandler);
        }
        this.fXIncludeHandler.setDocumentHandler(this.fXPointerHandler);
        this.fXPointerHandler.setDocumentSource(this.fXIncludeHandler);
    }

    @Override
    protected void configureXML11Pipeline() {
        super.configureXML11Pipeline();
        this.fXML11DTDScanner.setDTDHandler(this.fXML11DTDProcessor);
        this.fXML11DTDProcessor.setDTDSource(this.fXML11DTDScanner);
        this.fDTDProcessor.setDTDHandler(this.fXIncludeHandler);
        this.fXIncludeHandler.setDTDSource(this.fXML11DTDProcessor);
        this.fXIncludeHandler.setDTDHandler(this.fXPointerHandler);
        this.fXPointerHandler.setDTDSource(this.fXIncludeHandler);
        this.fXPointerHandler.setDTDHandler(this.fDTDHandler);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.setDTDSource(this.fXPointerHandler);
        }
        XMLDocumentSource xMLDocumentSource = null;
        if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
            xMLDocumentSource = this.fSchemaValidator.getDocumentSource();
        } else {
            xMLDocumentSource = this.fLastComponent;
            this.fLastComponent = this.fXPointerHandler;
        }
        XMLDocumentHandler xMLDocumentHandler = xMLDocumentSource.getDocumentHandler();
        xMLDocumentSource.setDocumentHandler(this.fXIncludeHandler);
        this.fXIncludeHandler.setDocumentSource(xMLDocumentSource);
        if (xMLDocumentHandler != null) {
            this.fXIncludeHandler.setDocumentHandler(xMLDocumentHandler);
            xMLDocumentHandler.setDocumentSource(this.fXIncludeHandler);
        }
        this.fXIncludeHandler.setDocumentHandler(this.fXPointerHandler);
        this.fXPointerHandler.setDocumentSource(this.fXIncludeHandler);
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        super.setProperty(string, object);
    }
}

