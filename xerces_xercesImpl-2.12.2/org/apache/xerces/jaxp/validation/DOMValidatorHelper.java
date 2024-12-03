/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import java.io.IOException;
import java.util.Enumeration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.validation.EntityState;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.impl.xs.XMLSchemaValidator;
import org.apache.xerces.impl.xs.util.SimpleLocator;
import org.apache.xerces.jaxp.validation.DOMDocumentHandler;
import org.apache.xerces.jaxp.validation.DOMResultAugmentor;
import org.apache.xerces.jaxp.validation.DOMResultBuilder;
import org.apache.xerces.jaxp.validation.JAXPValidationMessageFormatter;
import org.apache.xerces.jaxp.validation.Util;
import org.apache.xerces.jaxp.validation.ValidatorHelper;
import org.apache.xerces.jaxp.validation.XMLSchemaValidatorComponentManager;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParseException;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

final class DOMValidatorHelper
implements ValidatorHelper,
EntityState {
    private static final int CHUNK_SIZE = 1024;
    private static final int CHUNK_MASK = 1023;
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
    private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private final XMLErrorReporter fErrorReporter;
    private final NamespaceSupport fNamespaceContext;
    private final DOMNamespaceContext fDOMNamespaceContext = new DOMNamespaceContext();
    private final XMLSchemaValidator fSchemaValidator;
    private final SymbolTable fSymbolTable;
    private final ValidationManager fValidationManager;
    private final XMLSchemaValidatorComponentManager fComponentManager;
    private final SimpleLocator fXMLLocator = new SimpleLocator(null, null, -1, -1, -1);
    private DOMDocumentHandler fDOMValidatorHandler;
    private final DOMResultAugmentor fDOMResultAugmentor = new DOMResultAugmentor(this);
    private final DOMResultBuilder fDOMResultBuilder = new DOMResultBuilder();
    private NamedNodeMap fEntities = null;
    private final char[] fCharBuffer = new char[1024];
    private Node fRoot;
    private Node fCurrentElement;
    final QName fElementQName = new QName();
    final QName fAttributeQName = new QName();
    final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
    final XMLString fTempString = new XMLString();

    public DOMValidatorHelper(XMLSchemaValidatorComponentManager xMLSchemaValidatorComponentManager) {
        this.fComponentManager = xMLSchemaValidatorComponentManager;
        this.fErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty(ERROR_REPORTER);
        this.fNamespaceContext = (NamespaceSupport)this.fComponentManager.getProperty(NAMESPACE_CONTEXT);
        this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty(SCHEMA_VALIDATOR);
        this.fSymbolTable = (SymbolTable)this.fComponentManager.getProperty(SYMBOL_TABLE);
        this.fValidationManager = (ValidationManager)this.fComponentManager.getProperty(VALIDATION_MANAGER);
    }

    @Override
    public void validate(Source source, Result result) throws SAXException, IOException {
        if (result instanceof DOMResult || result == null) {
            Node node;
            DOMSource dOMSource = (DOMSource)source;
            DOMResult dOMResult = (DOMResult)result;
            this.fRoot = node = dOMSource.getNode();
            if (node != null) {
                this.fComponentManager.reset();
                this.fValidationManager.setEntityState(this);
                this.fDOMNamespaceContext.reset();
                String string = dOMSource.getSystemId();
                this.fXMLLocator.setLiteralSystemId(string);
                this.fXMLLocator.setExpandedSystemId(string);
                this.fErrorReporter.setDocumentLocator(this.fXMLLocator);
                try {
                    this.setupEntityMap(node.getNodeType() == 9 ? (Document)node : node.getOwnerDocument());
                    this.setupDOMResultHandler(dOMSource, dOMResult);
                    this.fSchemaValidator.startDocument(this.fXMLLocator, null, this.fDOMNamespaceContext, null);
                    this.validate(node);
                    this.fSchemaValidator.endDocument(null);
                }
                catch (XMLParseException xMLParseException) {
                    throw Util.toSAXParseException(xMLParseException);
                }
                catch (XNIException xNIException) {
                    throw Util.toSAXException(xNIException);
                }
                finally {
                    this.fRoot = null;
                    this.fCurrentElement = null;
                    this.fEntities = null;
                    if (this.fDOMValidatorHandler != null) {
                        this.fDOMValidatorHandler.setDOMResult(null);
                    }
                }
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[]{source.getClass().getName(), result.getClass().getName()}));
    }

    @Override
    public boolean isEntityDeclared(String string) {
        return false;
    }

    @Override
    public boolean isEntityUnparsed(String string) {
        Entity entity;
        if (this.fEntities != null && (entity = (Entity)this.fEntities.getNamedItem(string)) != null) {
            return entity.getNotationName() != null;
        }
        return false;
    }

    private void validate(Node node) {
        Node node2 = node;
        boolean bl = this.useIsSameNode(node2);
        while (node != null) {
            this.beginNode(node);
            Node node3 = node.getFirstChild();
            while (node3 == null) {
                this.finishNode(node);
                if (node2 == node) break;
                node3 = node.getNextSibling();
                if (node3 != null || (node = node.getParentNode()) != null && !(bl ? node2.isSameNode(node) : node2 == node)) continue;
                if (node != null) {
                    this.finishNode(node);
                }
                node3 = null;
                break;
            }
            node = node3;
        }
    }

    private void beginNode(Node node) {
        switch (node.getNodeType()) {
            case 1: {
                this.fCurrentElement = node;
                this.fNamespaceContext.pushContext();
                this.fillQName(this.fElementQName, node);
                this.processAttributes(node.getAttributes());
                this.fSchemaValidator.startElement(this.fElementQName, this.fAttributes, null);
                break;
            }
            case 3: {
                if (this.fDOMValidatorHandler != null) {
                    this.fDOMValidatorHandler.setIgnoringCharacters(true);
                    this.sendCharactersToValidator(node.getNodeValue());
                    this.fDOMValidatorHandler.setIgnoringCharacters(false);
                    this.fDOMValidatorHandler.characters((Text)node);
                    break;
                }
                this.sendCharactersToValidator(node.getNodeValue());
                break;
            }
            case 4: {
                if (this.fDOMValidatorHandler != null) {
                    this.fDOMValidatorHandler.setIgnoringCharacters(true);
                    this.fSchemaValidator.startCDATA(null);
                    this.sendCharactersToValidator(node.getNodeValue());
                    this.fSchemaValidator.endCDATA(null);
                    this.fDOMValidatorHandler.setIgnoringCharacters(false);
                    this.fDOMValidatorHandler.cdata((CDATASection)node);
                    break;
                }
                this.fSchemaValidator.startCDATA(null);
                this.sendCharactersToValidator(node.getNodeValue());
                this.fSchemaValidator.endCDATA(null);
                break;
            }
            case 7: {
                if (this.fDOMValidatorHandler == null) break;
                this.fDOMValidatorHandler.processingInstruction((ProcessingInstruction)node);
                break;
            }
            case 8: {
                if (this.fDOMValidatorHandler == null) break;
                this.fDOMValidatorHandler.comment((Comment)node);
                break;
            }
            case 10: {
                if (this.fDOMValidatorHandler == null) break;
                this.fDOMValidatorHandler.doctypeDecl((DocumentType)node);
                break;
            }
        }
    }

    private void finishNode(Node node) {
        if (node.getNodeType() == 1) {
            this.fCurrentElement = node;
            this.fillQName(this.fElementQName, node);
            this.fSchemaValidator.endElement(this.fElementQName, null);
            this.fNamespaceContext.popContext();
        }
    }

    private void setupEntityMap(Document document) {
        DocumentType documentType;
        if (document != null && (documentType = document.getDoctype()) != null) {
            this.fEntities = documentType.getEntities();
            return;
        }
        this.fEntities = null;
    }

    private void setupDOMResultHandler(DOMSource dOMSource, DOMResult dOMResult) throws SAXException {
        if (dOMResult == null) {
            this.fDOMValidatorHandler = null;
            this.fSchemaValidator.setDocumentHandler(null);
            return;
        }
        Node node = dOMResult.getNode();
        if (dOMSource.getNode() == node) {
            this.fDOMValidatorHandler = this.fDOMResultAugmentor;
            this.fDOMResultAugmentor.setDOMResult(dOMResult);
            this.fSchemaValidator.setDocumentHandler(this.fDOMResultAugmentor);
            return;
        }
        if (dOMResult.getNode() == null) {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setNamespaceAware(true);
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                dOMResult.setNode(documentBuilder.newDocument());
            }
            catch (ParserConfigurationException parserConfigurationException) {
                throw new SAXException(parserConfigurationException);
            }
        }
        this.fDOMValidatorHandler = this.fDOMResultBuilder;
        this.fDOMResultBuilder.setDOMResult(dOMResult);
        this.fSchemaValidator.setDocumentHandler(this.fDOMResultBuilder);
    }

    private void fillQName(QName qName, Node node) {
        String string = node.getPrefix();
        String string2 = node.getLocalName();
        String string3 = node.getNodeName();
        String string4 = node.getNamespaceURI();
        qName.prefix = string != null ? this.fSymbolTable.addSymbol(string) : XMLSymbols.EMPTY_STRING;
        qName.localpart = string2 != null ? this.fSymbolTable.addSymbol(string2) : XMLSymbols.EMPTY_STRING;
        qName.rawname = string3 != null ? this.fSymbolTable.addSymbol(string3) : XMLSymbols.EMPTY_STRING;
        qName.uri = string4 != null && string4.length() > 0 ? this.fSymbolTable.addSymbol(string4) : null;
    }

    private void processAttributes(NamedNodeMap namedNodeMap) {
        int n = namedNodeMap.getLength();
        this.fAttributes.removeAllAttributes();
        for (int i = 0; i < n; ++i) {
            Attr attr = (Attr)namedNodeMap.item(i);
            String string = attr.getValue();
            if (string == null) {
                string = XMLSymbols.EMPTY_STRING;
            }
            this.fillQName(this.fAttributeQName, attr);
            this.fAttributes.addAttributeNS(this.fAttributeQName, XMLSymbols.fCDATASymbol, string);
            this.fAttributes.setSpecified(i, attr.getSpecified());
            if (this.fAttributeQName.uri != NamespaceContext.XMLNS_URI) continue;
            if (this.fAttributeQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                this.fNamespaceContext.declarePrefix(this.fAttributeQName.localpart, string.length() != 0 ? this.fSymbolTable.addSymbol(string) : null);
                continue;
            }
            this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, string.length() != 0 ? this.fSymbolTable.addSymbol(string) : null);
        }
    }

    private void sendCharactersToValidator(String string) {
        if (string != null) {
            int n = string.length();
            int n2 = n & 0x3FF;
            if (n2 > 0) {
                string.getChars(0, n2, this.fCharBuffer, 0);
                this.fTempString.setValues(this.fCharBuffer, 0, n2);
                this.fSchemaValidator.characters(this.fTempString, null);
            }
            int n3 = n2;
            while (n3 < n) {
                string.getChars(n3, n3 += 1024, this.fCharBuffer, 0);
                this.fTempString.setValues(this.fCharBuffer, 0, 1024);
                this.fSchemaValidator.characters(this.fTempString, null);
            }
        }
    }

    private boolean useIsSameNode(Node node) {
        if (node instanceof NodeImpl) {
            return false;
        }
        Document document = node.getNodeType() == 9 ? (Document)node : node.getOwnerDocument();
        return document != null && document.getImplementation().hasFeature("Core", "3.0");
    }

    Node getCurrentElement() {
        return this.fCurrentElement;
    }

    final class DOMNamespaceContext
    implements NamespaceContext {
        protected String[] fNamespace = new String[32];
        protected int fNamespaceSize = 0;
        protected boolean fDOMContextBuilt = false;

        DOMNamespaceContext() {
        }

        @Override
        public void pushContext() {
            DOMValidatorHelper.this.fNamespaceContext.pushContext();
        }

        @Override
        public void popContext() {
            DOMValidatorHelper.this.fNamespaceContext.popContext();
        }

        @Override
        public boolean declarePrefix(String string, String string2) {
            return DOMValidatorHelper.this.fNamespaceContext.declarePrefix(string, string2);
        }

        @Override
        public String getURI(String string) {
            String string2 = DOMValidatorHelper.this.fNamespaceContext.getURI(string);
            if (string2 == null) {
                if (!this.fDOMContextBuilt) {
                    this.fillNamespaceContext();
                    this.fDOMContextBuilt = true;
                }
                if (this.fNamespaceSize > 0 && !DOMValidatorHelper.this.fNamespaceContext.containsPrefix(string)) {
                    string2 = this.getURI0(string);
                }
            }
            return string2;
        }

        @Override
        public String getPrefix(String string) {
            return DOMValidatorHelper.this.fNamespaceContext.getPrefix(string);
        }

        @Override
        public int getDeclaredPrefixCount() {
            return DOMValidatorHelper.this.fNamespaceContext.getDeclaredPrefixCount();
        }

        @Override
        public String getDeclaredPrefixAt(int n) {
            return DOMValidatorHelper.this.fNamespaceContext.getDeclaredPrefixAt(n);
        }

        @Override
        public Enumeration getAllPrefixes() {
            return DOMValidatorHelper.this.fNamespaceContext.getAllPrefixes();
        }

        @Override
        public void reset() {
            this.fDOMContextBuilt = false;
            this.fNamespaceSize = 0;
        }

        private void fillNamespaceContext() {
            if (DOMValidatorHelper.this.fRoot != null) {
                for (Node node = DOMValidatorHelper.this.fRoot.getParentNode(); node != null; node = node.getParentNode()) {
                    if (1 != node.getNodeType()) continue;
                    NamedNodeMap namedNodeMap = node.getAttributes();
                    int n = namedNodeMap.getLength();
                    for (int i = 0; i < n; ++i) {
                        Attr attr = (Attr)namedNodeMap.item(i);
                        String string = attr.getValue();
                        if (string == null) {
                            string = XMLSymbols.EMPTY_STRING;
                        }
                        DOMValidatorHelper.this.fillQName(DOMValidatorHelper.this.fAttributeQName, attr);
                        if (DOMValidatorHelper.this.fAttributeQName.uri != NamespaceContext.XMLNS_URI) continue;
                        if (DOMValidatorHelper.this.fAttributeQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                            this.declarePrefix0(DOMValidatorHelper.this.fAttributeQName.localpart, string.length() != 0 ? DOMValidatorHelper.this.fSymbolTable.addSymbol(string) : null);
                            continue;
                        }
                        this.declarePrefix0(XMLSymbols.EMPTY_STRING, string.length() != 0 ? DOMValidatorHelper.this.fSymbolTable.addSymbol(string) : null);
                    }
                }
            }
        }

        private void declarePrefix0(String string, String string2) {
            if (this.fNamespaceSize == this.fNamespace.length) {
                String[] stringArray = new String[this.fNamespaceSize * 2];
                System.arraycopy(this.fNamespace, 0, stringArray, 0, this.fNamespaceSize);
                this.fNamespace = stringArray;
            }
            this.fNamespace[this.fNamespaceSize++] = string;
            this.fNamespace[this.fNamespaceSize++] = string2;
        }

        private String getURI0(String string) {
            for (int i = 0; i < this.fNamespaceSize; i += 2) {
                if (this.fNamespace[i] != string) continue;
                return this.fNamespace[i + 1];
            }
            return null;
        }
    }
}

