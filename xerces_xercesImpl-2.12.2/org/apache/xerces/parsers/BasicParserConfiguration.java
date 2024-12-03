/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.apache.xerces.util.ParserConfigurationSettings;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

public abstract class BasicParserConfiguration
extends ParserConfigurationSettings
implements XMLParserConfiguration {
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected SymbolTable fSymbolTable;
    protected Locale fLocale;
    protected ArrayList fComponents = new ArrayList();
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDTDHandler fDTDHandler;
    protected XMLDTDContentModelHandler fDTDContentModelHandler;
    protected XMLDocumentSource fLastComponent;

    protected BasicParserConfiguration() {
        this(null, null);
    }

    protected BasicParserConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null);
    }

    protected BasicParserConfiguration(SymbolTable symbolTable, XMLComponentManager xMLComponentManager) {
        super(xMLComponentManager);
        this.fRecognizedFeatures = new ArrayList();
        this.fRecognizedProperties = new ArrayList();
        this.fFeatures = new HashMap();
        this.fProperties = new HashMap();
        String[] stringArray = new String[]{"http://apache.org/xml/features/internal/parser-settings", VALIDATION, NAMESPACES, EXTERNAL_GENERAL_ENTITIES, EXTERNAL_PARAMETER_ENTITIES};
        this.addRecognizedFeatures(stringArray);
        this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
        this.fFeatures.put(VALIDATION, Boolean.FALSE);
        this.fFeatures.put(NAMESPACES, Boolean.TRUE);
        this.fFeatures.put(EXTERNAL_GENERAL_ENTITIES, Boolean.TRUE);
        this.fFeatures.put(EXTERNAL_PARAMETER_ENTITIES, Boolean.TRUE);
        String[] stringArray2 = new String[]{XML_STRING, SYMBOL_TABLE, ERROR_HANDLER, ENTITY_RESOLVER};
        this.addRecognizedProperties(stringArray2);
        if (symbolTable == null) {
            symbolTable = new SymbolTable();
        }
        this.fSymbolTable = symbolTable;
        this.fProperties.put(SYMBOL_TABLE, this.fSymbolTable);
    }

    protected void addComponent(XMLComponent xMLComponent) {
        Object object;
        String string;
        int n;
        if (this.fComponents.contains(xMLComponent)) {
            return;
        }
        this.fComponents.add(xMLComponent);
        String[] stringArray = xMLComponent.getRecognizedFeatures();
        this.addRecognizedFeatures(stringArray);
        String[] stringArray2 = xMLComponent.getRecognizedProperties();
        this.addRecognizedProperties(stringArray2);
        if (stringArray != null) {
            for (n = 0; n < stringArray.length; ++n) {
                string = stringArray[n];
                object = xMLComponent.getFeatureDefault(string);
                if (object == null) continue;
                super.setFeature(string, (Boolean)object);
            }
        }
        if (stringArray2 != null) {
            for (n = 0; n < stringArray2.length; ++n) {
                string = stringArray2[n];
                object = xMLComponent.getPropertyDefault(string);
                if (object == null) continue;
                super.setProperty(string, object);
            }
        }
    }

    @Override
    public abstract void parse(XMLInputSource var1) throws XNIException, IOException;

    @Override
    public void setDocumentHandler(XMLDocumentHandler xMLDocumentHandler) {
        this.fDocumentHandler = xMLDocumentHandler;
        if (this.fLastComponent != null) {
            this.fLastComponent.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fLastComponent);
            }
        }
    }

    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }

    @Override
    public void setDTDHandler(XMLDTDHandler xMLDTDHandler) {
        this.fDTDHandler = xMLDTDHandler;
    }

    @Override
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }

    @Override
    public void setDTDContentModelHandler(XMLDTDContentModelHandler xMLDTDContentModelHandler) {
        this.fDTDContentModelHandler = xMLDTDContentModelHandler;
    }

    @Override
    public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return this.fDTDContentModelHandler;
    }

    @Override
    public void setEntityResolver(XMLEntityResolver xMLEntityResolver) {
        this.fProperties.put(ENTITY_RESOLVER, xMLEntityResolver);
    }

    @Override
    public XMLEntityResolver getEntityResolver() {
        return (XMLEntityResolver)this.fProperties.get(ENTITY_RESOLVER);
    }

    @Override
    public void setErrorHandler(XMLErrorHandler xMLErrorHandler) {
        this.fProperties.put(ERROR_HANDLER, xMLErrorHandler);
    }

    @Override
    public XMLErrorHandler getErrorHandler() {
        return (XMLErrorHandler)this.fProperties.get(ERROR_HANDLER);
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
        int n = this.fComponents.size();
        for (int i = 0; i < n; ++i) {
            XMLComponent xMLComponent = (XMLComponent)this.fComponents.get(i);
            xMLComponent.setFeature(string, bl);
        }
        super.setFeature(string, bl);
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        int n = this.fComponents.size();
        for (int i = 0; i < n; ++i) {
            XMLComponent xMLComponent = (XMLComponent)this.fComponents.get(i);
            xMLComponent.setProperty(string, object);
        }
        super.setProperty(string, object);
    }

    @Override
    public void setLocale(Locale locale) throws XNIException {
        this.fLocale = locale;
    }

    @Override
    public Locale getLocale() {
        return this.fLocale;
    }

    protected void reset() throws XNIException {
        int n = this.fComponents.size();
        for (int i = 0; i < n; ++i) {
            XMLComponent xMLComponent = (XMLComponent)this.fComponents.get(i);
            xMLComponent.reset(this);
        }
    }

    @Override
    protected void checkProperty(String string) throws XMLConfigurationException {
        int n;
        if (string.startsWith("http://xml.org/sax/properties/") && (n = string.length() - "http://xml.org/sax/properties/".length()) == "xml-string".length() && string.endsWith("xml-string")) {
            short s = 1;
            throw new XMLConfigurationException(s, string);
        }
        super.checkProperty(string);
    }

    @Override
    protected void checkFeature(String string) throws XMLConfigurationException {
        int n;
        if (string.startsWith("http://apache.org/xml/features/") && (n = string.length() - "http://apache.org/xml/features/".length()) == "internal/parser-settings".length() && string.endsWith("internal/parser-settings")) {
            short s = 1;
            throw new XMLConfigurationException(s, string);
        }
        super.checkFeature(string);
    }
}

