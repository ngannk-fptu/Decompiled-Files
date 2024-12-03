/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp;

import java.io.IOException;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;
import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.xs.opti.DefaultXMLDocumentHandler;
import org.apache.xerces.jaxp.TeeXMLDocumentFilterImpl;
import org.apache.xerces.util.AttributesProxy;
import org.apache.xerces.util.AugmentationsImpl;
import org.apache.xerces.util.ErrorHandlerProxy;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.util.LocatorProxy;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLResourceIdentifierImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

final class JAXPValidatorComponent
extends TeeXMLDocumentFilterImpl
implements XMLComponent {
    private static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private final ValidatorHandler validator;
    private final XNI2SAX xni2sax = new XNI2SAX();
    private final SAX2XNI sax2xni = new SAX2XNI();
    private final TypeInfoProvider typeInfoProvider;
    private Augmentations fCurrentAug;
    private XMLAttributes fCurrentAttributes;
    private SymbolTable fSymbolTable;
    private XMLErrorReporter fErrorReporter;
    private XMLEntityResolver fEntityResolver;
    private static final TypeInfoProvider noInfoProvider = new TypeInfoProvider(){

        @Override
        public TypeInfo getElementTypeInfo() {
            return null;
        }

        @Override
        public TypeInfo getAttributeTypeInfo(int n) {
            return null;
        }

        public TypeInfo getAttributeTypeInfo(String string) {
            return null;
        }

        public TypeInfo getAttributeTypeInfo(String string, String string2) {
            return null;
        }

        @Override
        public boolean isIdAttribute(int n) {
            return false;
        }

        @Override
        public boolean isSpecified(int n) {
            return false;
        }
    };

    public JAXPValidatorComponent(ValidatorHandler validatorHandler) {
        this.validator = validatorHandler;
        TypeInfoProvider typeInfoProvider = validatorHandler.getTypeInfoProvider();
        if (typeInfoProvider == null) {
            typeInfoProvider = noInfoProvider;
        }
        this.typeInfoProvider = typeInfoProvider;
        this.xni2sax.setContentHandler(this.validator);
        this.validator.setContentHandler(this.sax2xni);
        this.setSide(this.xni2sax);
        this.validator.setErrorHandler(new ErrorHandlerProxy(){

            @Override
            protected XMLErrorHandler getErrorHandler() {
                XMLErrorHandler xMLErrorHandler = JAXPValidatorComponent.this.fErrorReporter.getErrorHandler();
                if (xMLErrorHandler != null) {
                    return xMLErrorHandler;
                }
                return new ErrorHandlerWrapper(DraconianErrorHandler.getInstance());
            }
        });
        this.validator.setResourceResolver(new LSResourceResolver(){

            @Override
            public LSInput resolveResource(String string, String string2, String string3, String string4, String string5) {
                if (JAXPValidatorComponent.this.fEntityResolver == null) {
                    return null;
                }
                try {
                    XMLInputSource xMLInputSource = JAXPValidatorComponent.this.fEntityResolver.resolveEntity(new XMLResourceIdentifierImpl(string3, string4, string5, null));
                    if (xMLInputSource == null) {
                        return null;
                    }
                    DOMInputImpl dOMInputImpl = new DOMInputImpl();
                    dOMInputImpl.setBaseURI(xMLInputSource.getBaseSystemId());
                    dOMInputImpl.setByteStream(xMLInputSource.getByteStream());
                    dOMInputImpl.setCharacterStream(xMLInputSource.getCharacterStream());
                    dOMInputImpl.setEncoding(xMLInputSource.getEncoding());
                    dOMInputImpl.setPublicId(xMLInputSource.getPublicId());
                    dOMInputImpl.setSystemId(xMLInputSource.getSystemId());
                    return dOMInputImpl;
                }
                catch (IOException iOException) {
                    throw new XNIException(iOException);
                }
            }
        });
    }

    @Override
    public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        this.fCurrentAttributes = xMLAttributes;
        this.fCurrentAug = augmentations;
        this.xni2sax.startElement(qName, xMLAttributes, null);
        this.fCurrentAttributes = null;
    }

    @Override
    public void endElement(QName qName, Augmentations augmentations) throws XNIException {
        this.fCurrentAug = augmentations;
        this.xni2sax.endElement(qName, null);
    }

    @Override
    public void emptyElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        this.startElement(qName, xMLAttributes, augmentations);
        this.endElement(qName, augmentations);
    }

    @Override
    public void characters(XMLString xMLString, Augmentations augmentations) throws XNIException {
        this.fCurrentAug = augmentations;
        this.xni2sax.characters(xMLString, null);
    }

    @Override
    public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
        this.fCurrentAug = augmentations;
        this.xni2sax.ignorableWhitespace(xMLString, null);
    }

    @Override
    public void reset(XMLComponentManager xMLComponentManager) throws XMLConfigurationException {
        this.fSymbolTable = (SymbolTable)xMLComponentManager.getProperty(SYMBOL_TABLE);
        this.fErrorReporter = (XMLErrorReporter)xMLComponentManager.getProperty(ERROR_REPORTER);
        try {
            this.fEntityResolver = (XMLEntityResolver)xMLComponentManager.getProperty(ENTITY_MANAGER);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fEntityResolver = null;
        }
    }

    private void updateAttributes(Attributes attributes) {
        int n = attributes.getLength();
        for (int i = 0; i < n; ++i) {
            String string = attributes.getQName(i);
            int n2 = this.fCurrentAttributes.getIndex(string);
            String string2 = attributes.getValue(i);
            if (n2 == -1) {
                int n3 = string.indexOf(58);
                String string3 = n3 < 0 ? null : this.symbolize(string.substring(0, n3));
                n2 = this.fCurrentAttributes.addAttribute(new QName(string3, this.symbolize(attributes.getLocalName(i)), this.symbolize(string), this.symbolize(attributes.getURI(i))), attributes.getType(i), string2);
                continue;
            }
            if (string2.equals(this.fCurrentAttributes.getValue(n2))) continue;
            this.fCurrentAttributes.setValue(n2, string2);
        }
    }

    private String symbolize(String string) {
        return this.fSymbolTable.addSymbol(string);
    }

    @Override
    public String[] getRecognizedFeatures() {
        return null;
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
    }

    @Override
    public String[] getRecognizedProperties() {
        return new String[]{ENTITY_MANAGER, ERROR_REPORTER, SYMBOL_TABLE};
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
    }

    @Override
    public Boolean getFeatureDefault(String string) {
        return null;
    }

    @Override
    public Object getPropertyDefault(String string) {
        return null;
    }

    private static final class DraconianErrorHandler
    implements ErrorHandler {
        private static final DraconianErrorHandler ERROR_HANDLER_INSTANCE = new DraconianErrorHandler();

        private DraconianErrorHandler() {
        }

        public static DraconianErrorHandler getInstance() {
            return ERROR_HANDLER_INSTANCE;
        }

        @Override
        public void warning(SAXParseException sAXParseException) throws SAXException {
        }

        @Override
        public void error(SAXParseException sAXParseException) throws SAXException {
            throw sAXParseException;
        }

        @Override
        public void fatalError(SAXParseException sAXParseException) throws SAXException {
            throw sAXParseException;
        }
    }

    private static final class XNI2SAX
    extends DefaultXMLDocumentHandler {
        private ContentHandler fContentHandler;
        private String fVersion;
        protected NamespaceContext fNamespaceContext;
        private final AttributesProxy fAttributesProxy = new AttributesProxy(null);

        private XNI2SAX() {
        }

        public void setContentHandler(ContentHandler contentHandler) {
            this.fContentHandler = contentHandler;
        }

        public ContentHandler getContentHandler() {
            return this.fContentHandler;
        }

        @Override
        public void xmlDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
            this.fVersion = string;
        }

        @Override
        public void startDocument(XMLLocator xMLLocator, String string, NamespaceContext namespaceContext, Augmentations augmentations) throws XNIException {
            this.fNamespaceContext = namespaceContext;
            this.fContentHandler.setDocumentLocator(new LocatorProxy(xMLLocator));
            try {
                this.fContentHandler.startDocument();
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
        }

        @Override
        public void endDocument(Augmentations augmentations) throws XNIException {
            try {
                this.fContentHandler.endDocument();
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
        }

        @Override
        public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
            try {
                this.fContentHandler.processingInstruction(string, xMLString.toString());
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
        }

        @Override
        public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
            try {
                String string;
                String string2;
                int n = this.fNamespaceContext.getDeclaredPrefixCount();
                if (n > 0) {
                    string2 = null;
                    string = null;
                    for (int i = 0; i < n; ++i) {
                        string = this.fNamespaceContext.getURI(string2 = this.fNamespaceContext.getDeclaredPrefixAt(i));
                        this.fContentHandler.startPrefixMapping(string2, string == null ? "" : string);
                    }
                }
                string2 = qName.uri != null ? qName.uri : "";
                string = qName.localpart;
                this.fAttributesProxy.setAttributes(xMLAttributes);
                this.fContentHandler.startElement(string2, string, qName.rawname, this.fAttributesProxy);
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
        }

        @Override
        public void endElement(QName qName, Augmentations augmentations) throws XNIException {
            try {
                String string = qName.uri != null ? qName.uri : "";
                String string2 = qName.localpart;
                this.fContentHandler.endElement(string, string2, qName.rawname);
                int n = this.fNamespaceContext.getDeclaredPrefixCount();
                if (n > 0) {
                    for (int i = 0; i < n; ++i) {
                        this.fContentHandler.endPrefixMapping(this.fNamespaceContext.getDeclaredPrefixAt(i));
                    }
                }
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
        }

        @Override
        public void emptyElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
            this.startElement(qName, xMLAttributes, augmentations);
            this.endElement(qName, augmentations);
        }

        @Override
        public void characters(XMLString xMLString, Augmentations augmentations) throws XNIException {
            try {
                this.fContentHandler.characters(xMLString.ch, xMLString.offset, xMLString.length);
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
        }

        @Override
        public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
            try {
                this.fContentHandler.ignorableWhitespace(xMLString.ch, xMLString.offset, xMLString.length);
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
        }
    }

    private final class SAX2XNI
    extends DefaultHandler {
        private final Augmentations fAugmentations = new AugmentationsImpl();
        private final QName fQName = new QName();

        private SAX2XNI() {
        }

        @Override
        public void characters(char[] cArray, int n, int n2) throws SAXException {
            try {
                this.handler().characters(new XMLString(cArray, n, n2), this.aug());
            }
            catch (XNIException xNIException) {
                throw this.toSAXException(xNIException);
            }
        }

        @Override
        public void ignorableWhitespace(char[] cArray, int n, int n2) throws SAXException {
            try {
                this.handler().ignorableWhitespace(new XMLString(cArray, n, n2), this.aug());
            }
            catch (XNIException xNIException) {
                throw this.toSAXException(xNIException);
            }
        }

        @Override
        public void startElement(String string, String string2, String string3, Attributes attributes) throws SAXException {
            try {
                JAXPValidatorComponent.this.updateAttributes(attributes);
                this.handler().startElement(this.toQName(string, string2, string3), JAXPValidatorComponent.this.fCurrentAttributes, this.elementAug());
            }
            catch (XNIException xNIException) {
                throw this.toSAXException(xNIException);
            }
        }

        @Override
        public void endElement(String string, String string2, String string3) throws SAXException {
            try {
                this.handler().endElement(this.toQName(string, string2, string3), this.aug());
            }
            catch (XNIException xNIException) {
                throw this.toSAXException(xNIException);
            }
        }

        private Augmentations elementAug() {
            Augmentations augmentations = this.aug();
            return augmentations;
        }

        private Augmentations aug() {
            if (JAXPValidatorComponent.this.fCurrentAug != null) {
                Augmentations augmentations = JAXPValidatorComponent.this.fCurrentAug;
                JAXPValidatorComponent.this.fCurrentAug = null;
                return augmentations;
            }
            this.fAugmentations.removeAllItems();
            return this.fAugmentations;
        }

        private XMLDocumentHandler handler() {
            return JAXPValidatorComponent.this.getDocumentHandler();
        }

        private SAXException toSAXException(XNIException xNIException) {
            Exception exception = xNIException.getException();
            if (exception == null) {
                exception = xNIException;
            }
            if (exception instanceof SAXException) {
                return (SAXException)exception;
            }
            return new SAXException(exception);
        }

        private QName toQName(String string, String string2, String string3) {
            String string4 = null;
            int n = string3.indexOf(58);
            if (n > 0) {
                string4 = JAXPValidatorComponent.this.symbolize(string3.substring(0, n));
            }
            string2 = JAXPValidatorComponent.this.symbolize(string2);
            string3 = JAXPValidatorComponent.this.symbolize(string3);
            string = JAXPValidatorComponent.this.symbolize(string);
            this.fQName.setValues(string4, string2, string3, string);
            return this.fQName;
        }
    }
}

