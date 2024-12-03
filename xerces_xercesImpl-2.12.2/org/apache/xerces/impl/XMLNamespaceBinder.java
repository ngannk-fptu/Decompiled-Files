/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLDocumentSource;

public class XMLNamespaceBinder
implements XMLComponent,
XMLDocumentFilter {
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/namespaces"};
    private static final Boolean[] FEATURE_DEFAULTS = new Boolean[]{null};
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter"};
    private static final Object[] PROPERTY_DEFAULTS = new Object[]{null, null};
    protected boolean fNamespaces;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;
    protected boolean fOnlyPassPrefixMappingEvents;
    private NamespaceContext fNamespaceContext;
    private final QName fAttributeQName = new QName();

    public void setOnlyPassPrefixMappingEvents(boolean bl) {
        this.fOnlyPassPrefixMappingEvents = bl;
    }

    public boolean getOnlyPassPrefixMappingEvents() {
        return this.fOnlyPassPrefixMappingEvents;
    }

    @Override
    public void reset(XMLComponentManager xMLComponentManager) throws XNIException {
        try {
            this.fNamespaces = xMLComponentManager.getFeature(NAMESPACES);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fNamespaces = true;
        }
        this.fSymbolTable = (SymbolTable)xMLComponentManager.getProperty(SYMBOL_TABLE);
        this.fErrorReporter = (XMLErrorReporter)xMLComponentManager.getProperty(ERROR_REPORTER);
    }

    @Override
    public String[] getRecognizedFeatures() {
        return (String[])RECOGNIZED_FEATURES.clone();
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
    }

    @Override
    public String[] getRecognizedProperties() {
        return (String[])RECOGNIZED_PROPERTIES.clone();
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        if (string.startsWith("http://apache.org/xml/properties/")) {
            int n = string.length() - "http://apache.org/xml/properties/".length();
            if (n == "internal/symbol-table".length() && string.endsWith("internal/symbol-table")) {
                this.fSymbolTable = (SymbolTable)object;
            } else if (n == "internal/error-reporter".length() && string.endsWith("internal/error-reporter")) {
                this.fErrorReporter = (XMLErrorReporter)object;
            }
            return;
        }
    }

    @Override
    public Boolean getFeatureDefault(String string) {
        for (int i = 0; i < RECOGNIZED_FEATURES.length; ++i) {
            if (!RECOGNIZED_FEATURES[i].equals(string)) continue;
            return FEATURE_DEFAULTS[i];
        }
        return null;
    }

    @Override
    public Object getPropertyDefault(String string) {
        for (int i = 0; i < RECOGNIZED_PROPERTIES.length; ++i) {
            if (!RECOGNIZED_PROPERTIES[i].equals(string)) continue;
            return PROPERTY_DEFAULTS[i];
        }
        return null;
    }

    @Override
    public void setDocumentHandler(XMLDocumentHandler xMLDocumentHandler) {
        this.fDocumentHandler = xMLDocumentHandler;
    }

    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }

    @Override
    public void setDocumentSource(XMLDocumentSource xMLDocumentSource) {
        this.fDocumentSource = xMLDocumentSource;
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }

    @Override
    public void startGeneralEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.startGeneralEntity(string, xMLResourceIdentifier, string2, augmentations);
        }
    }

    @Override
    public void textDecl(String string, String string2, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.textDecl(string, string2, augmentations);
        }
    }

    @Override
    public void startDocument(XMLLocator xMLLocator, String string, NamespaceContext namespaceContext, Augmentations augmentations) throws XNIException {
        this.fNamespaceContext = namespaceContext;
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.startDocument(xMLLocator, string, namespaceContext, augmentations);
        }
    }

    @Override
    public void xmlDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.xmlDecl(string, string2, string3, augmentations);
        }
    }

    @Override
    public void doctypeDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.doctypeDecl(string, string2, string3, augmentations);
        }
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.comment(xMLString, augmentations);
        }
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.processingInstruction(string, xMLString, augmentations);
        }
    }

    @Override
    public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        if (this.fNamespaces) {
            this.handleStartElement(qName, xMLAttributes, augmentations, false);
        } else if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startElement(qName, xMLAttributes, augmentations);
        }
    }

    @Override
    public void emptyElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        if (this.fNamespaces) {
            this.handleStartElement(qName, xMLAttributes, augmentations, true);
            this.handleEndElement(qName, augmentations, true);
        } else if (this.fDocumentHandler != null) {
            this.fDocumentHandler.emptyElement(qName, xMLAttributes, augmentations);
        }
    }

    @Override
    public void characters(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.characters(xMLString, augmentations);
        }
    }

    @Override
    public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.ignorableWhitespace(xMLString, augmentations);
        }
    }

    @Override
    public void endElement(QName qName, Augmentations augmentations) throws XNIException {
        if (this.fNamespaces) {
            this.handleEndElement(qName, augmentations, false);
        } else if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endElement(qName, augmentations);
        }
    }

    @Override
    public void startCDATA(Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.startCDATA(augmentations);
        }
    }

    @Override
    public void endCDATA(Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.endCDATA(augmentations);
        }
    }

    @Override
    public void endDocument(Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.endDocument(augmentations);
        }
    }

    @Override
    public void endGeneralEntity(String string, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.endGeneralEntity(string, augmentations);
        }
    }

    protected void handleStartElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations, boolean bl) throws XNIException {
        int n;
        String string;
        String string2;
        this.fNamespaceContext.pushContext();
        if (qName.prefix == XMLSymbols.PREFIX_XMLNS) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[]{qName.rawname}, (short)2);
        }
        int n2 = xMLAttributes.getLength();
        for (int i = 0; i < n2; ++i) {
            String string3 = xMLAttributes.getLocalName(i);
            string2 = xMLAttributes.getPrefix(i);
            if (string2 != XMLSymbols.PREFIX_XMLNS && (string2 != XMLSymbols.EMPTY_STRING || string3 != XMLSymbols.PREFIX_XMLNS)) continue;
            string = this.fSymbolTable.addSymbol(xMLAttributes.getValue(i));
            if (string2 == XMLSymbols.PREFIX_XMLNS && string3 == XMLSymbols.PREFIX_XMLNS) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[]{xMLAttributes.getQName(i)}, (short)2);
            }
            if (string == NamespaceContext.XMLNS_URI) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[]{xMLAttributes.getQName(i)}, (short)2);
            }
            if (string3 == XMLSymbols.PREFIX_XML) {
                if (string != NamespaceContext.XML_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[]{xMLAttributes.getQName(i)}, (short)2);
                }
            } else if (string == NamespaceContext.XML_URI) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[]{xMLAttributes.getQName(i)}, (short)2);
            }
            String string4 = string2 = string3 != XMLSymbols.PREFIX_XMLNS ? string3 : XMLSymbols.EMPTY_STRING;
            if (this.prefixBoundToNullURI(string, string3)) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "EmptyPrefixedAttName", new Object[]{xMLAttributes.getQName(i)}, (short)2);
                continue;
            }
            this.fNamespaceContext.declarePrefix(string2, string.length() != 0 ? string : null);
        }
        String string5 = qName.prefix != null ? qName.prefix : XMLSymbols.EMPTY_STRING;
        qName.uri = this.fNamespaceContext.getURI(string5);
        if (qName.prefix == null && qName.uri != null) {
            qName.prefix = XMLSymbols.EMPTY_STRING;
        }
        if (qName.prefix != null && qName.uri == null) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[]{qName.prefix, qName.rawname}, (short)2);
        }
        for (n = 0; n < n2; ++n) {
            xMLAttributes.getName(n, this.fAttributeQName);
            string2 = this.fAttributeQName.prefix != null ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
            string = this.fAttributeQName.rawname;
            if (string == XMLSymbols.PREFIX_XMLNS) {
                this.fAttributeQName.uri = this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS);
                xMLAttributes.setName(n, this.fAttributeQName);
                continue;
            }
            if (string2 == XMLSymbols.EMPTY_STRING) continue;
            this.fAttributeQName.uri = this.fNamespaceContext.getURI(string2);
            if (this.fAttributeQName.uri == null) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[]{qName.rawname, string, string2}, (short)2);
            }
            xMLAttributes.setName(n, this.fAttributeQName);
        }
        n = xMLAttributes.getLength();
        for (int i = 0; i < n - 1; ++i) {
            string = xMLAttributes.getURI(i);
            if (string == null || string == NamespaceContext.XMLNS_URI) continue;
            String string6 = xMLAttributes.getLocalName(i);
            for (int j = i + 1; j < n; ++j) {
                String string7 = xMLAttributes.getLocalName(j);
                String string8 = xMLAttributes.getURI(j);
                if (string6 != string7 || string != string8) continue;
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[]{qName.rawname, string6, string}, (short)2);
            }
        }
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            if (bl) {
                this.fDocumentHandler.emptyElement(qName, xMLAttributes, augmentations);
            } else {
                this.fDocumentHandler.startElement(qName, xMLAttributes, augmentations);
            }
        }
    }

    protected void handleEndElement(QName qName, Augmentations augmentations, boolean bl) throws XNIException {
        String string = qName.prefix != null ? qName.prefix : XMLSymbols.EMPTY_STRING;
        qName.uri = this.fNamespaceContext.getURI(string);
        if (qName.uri != null) {
            qName.prefix = string;
        }
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents && !bl) {
            this.fDocumentHandler.endElement(qName, augmentations);
        }
        this.fNamespaceContext.popContext();
    }

    protected boolean prefixBoundToNullURI(String string, String string2) {
        return string == XMLSymbols.EMPTY_STRING && string2 != XMLSymbols.PREFIX_XMLNS;
    }
}

