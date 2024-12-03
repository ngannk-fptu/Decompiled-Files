/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.validation.EntityState;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.impl.xs.XMLSchemaValidator;
import org.apache.xerces.jaxp.validation.JAXPValidationMessageFormatter;
import org.apache.xerces.jaxp.validation.StAXDocumentHandler;
import org.apache.xerces.jaxp.validation.StAXEventResultBuilder;
import org.apache.xerces.jaxp.validation.StAXStreamResultBuilder;
import org.apache.xerces.jaxp.validation.Util;
import org.apache.xerces.jaxp.validation.ValidatorHelper;
import org.apache.xerces.jaxp.validation.XMLSchemaValidatorComponentManager;
import org.apache.xerces.util.JAXPNamespaceContextWrapper;
import org.apache.xerces.util.StAXLocationWrapper;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParseException;
import org.xml.sax.SAXException;

final class StAXValidatorHelper
implements ValidatorHelper,
EntityState {
    private static final String STRING_INTERNING = "javax.xml.stream.isInterning";
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private final XMLErrorReporter fErrorReporter;
    private final XMLSchemaValidator fSchemaValidator;
    private final SymbolTable fSymbolTable;
    private final ValidationManager fValidationManager;
    private final XMLSchemaValidatorComponentManager fComponentManager;
    private final JAXPNamespaceContextWrapper fNamespaceContext;
    private final StAXLocationWrapper fStAXLocationWrapper = new StAXLocationWrapper();
    private final XMLStreamReaderLocation fXMLStreamReaderLocation = new XMLStreamReaderLocation();
    private HashMap fEntities = null;
    private boolean fStringsInternalized = false;
    private StreamHelper fStreamHelper;
    private EventHelper fEventHelper;
    private StAXDocumentHandler fStAXValidatorHandler;
    private StAXStreamResultBuilder fStAXStreamResultBuilder;
    private StAXEventResultBuilder fStAXEventResultBuilder;
    private int fDepth = 0;
    private XMLEvent fCurrentEvent = null;
    final org.apache.xerces.xni.QName fElementQName = new org.apache.xerces.xni.QName();
    final org.apache.xerces.xni.QName fAttributeQName = new org.apache.xerces.xni.QName();
    final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
    final ArrayList fDeclaredPrefixes = new ArrayList();
    final XMLString fTempString = new XMLString();
    final XMLStringBuffer fStringBuffer = new XMLStringBuffer();

    public StAXValidatorHelper(XMLSchemaValidatorComponentManager xMLSchemaValidatorComponentManager) {
        this.fComponentManager = xMLSchemaValidatorComponentManager;
        this.fErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty(ERROR_REPORTER);
        this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty(SCHEMA_VALIDATOR);
        this.fSymbolTable = (SymbolTable)this.fComponentManager.getProperty(SYMBOL_TABLE);
        this.fValidationManager = (ValidationManager)this.fComponentManager.getProperty(VALIDATION_MANAGER);
        this.fNamespaceContext = new JAXPNamespaceContextWrapper(this.fSymbolTable);
        this.fNamespaceContext.setDeclaredPrefixes(this.fDeclaredPrefixes);
    }

    @Override
    public void validate(Source source, Result result) throws SAXException, IOException {
        if (result instanceof StAXResult || result == null) {
            StAXSource stAXSource = (StAXSource)source;
            StAXResult stAXResult = (StAXResult)result;
            try {
                XMLStreamReader xMLStreamReader = stAXSource.getXMLStreamReader();
                if (xMLStreamReader != null) {
                    if (this.fStreamHelper == null) {
                        this.fStreamHelper = new StreamHelper();
                    }
                    this.fStreamHelper.validate(xMLStreamReader, stAXResult);
                } else {
                    if (this.fEventHelper == null) {
                        this.fEventHelper = new EventHelper();
                    }
                    this.fEventHelper.validate(stAXSource.getXMLEventReader(), stAXResult);
                }
            }
            catch (XMLStreamException xMLStreamException) {
                throw new SAXException(xMLStreamException);
            }
            catch (XMLParseException xMLParseException) {
                throw Util.toSAXParseException(xMLParseException);
            }
            catch (XNIException xNIException) {
                throw Util.toSAXException(xNIException);
            }
            finally {
                this.fCurrentEvent = null;
                this.fStAXLocationWrapper.setLocation(null);
                this.fXMLStreamReaderLocation.setXMLStreamReader(null);
                if (this.fStAXValidatorHandler != null) {
                    this.fStAXValidatorHandler.setStAXResult(null);
                }
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[]{source.getClass().getName(), result.getClass().getName()}));
    }

    @Override
    public boolean isEntityDeclared(String string) {
        if (this.fEntities != null) {
            return this.fEntities.containsKey(string);
        }
        return false;
    }

    @Override
    public boolean isEntityUnparsed(String string) {
        EntityDeclaration entityDeclaration;
        if (this.fEntities != null && (entityDeclaration = (EntityDeclaration)this.fEntities.get(string)) != null) {
            return entityDeclaration.getNotationName() != null;
        }
        return false;
    }

    final EntityDeclaration getEntityDeclaration(String string) {
        return this.fEntities != null ? (EntityDeclaration)this.fEntities.get(string) : null;
    }

    final XMLEvent getCurrentEvent() {
        return this.fCurrentEvent;
    }

    final void fillQName(org.apache.xerces.xni.QName qName, String string, String string2, String string3) {
        if (!this.fStringsInternalized) {
            string = string != null && string.length() > 0 ? this.fSymbolTable.addSymbol(string) : null;
            string2 = string2 != null ? this.fSymbolTable.addSymbol(string2) : XMLSymbols.EMPTY_STRING;
            string3 = string3 != null && string3.length() > 0 ? this.fSymbolTable.addSymbol(string3) : XMLSymbols.EMPTY_STRING;
        } else {
            if (string != null && string.length() == 0) {
                string = null;
            }
            if (string2 == null) {
                string2 = XMLSymbols.EMPTY_STRING;
            }
            if (string3 == null) {
                string3 = XMLSymbols.EMPTY_STRING;
            }
        }
        String string4 = string2;
        if (string3 != XMLSymbols.EMPTY_STRING) {
            this.fStringBuffer.clear();
            this.fStringBuffer.append(string3);
            this.fStringBuffer.append(':');
            this.fStringBuffer.append(string2);
            string4 = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
        }
        qName.setValues(string3, string2, string4, string);
    }

    final void setup(Location location, StAXResult stAXResult, boolean bl) {
        this.fDepth = 0;
        this.fComponentManager.reset();
        this.setupStAXResultHandler(stAXResult);
        this.fValidationManager.setEntityState(this);
        if (this.fEntities != null && !this.fEntities.isEmpty()) {
            this.fEntities.clear();
        }
        this.fStAXLocationWrapper.setLocation(location);
        this.fErrorReporter.setDocumentLocator(this.fStAXLocationWrapper);
        this.fStringsInternalized = bl;
    }

    final void processEntityDeclarations(List list) {
        int n;
        int n2 = n = list != null ? list.size() : 0;
        if (n > 0) {
            if (this.fEntities == null) {
                this.fEntities = new HashMap();
            }
            for (int i = 0; i < n; ++i) {
                EntityDeclaration entityDeclaration = (EntityDeclaration)list.get(i);
                this.fEntities.put(entityDeclaration.getName(), entityDeclaration);
            }
        }
    }

    private void setupStAXResultHandler(StAXResult stAXResult) {
        if (stAXResult == null) {
            this.fStAXValidatorHandler = null;
            this.fSchemaValidator.setDocumentHandler(null);
            return;
        }
        XMLStreamWriter xMLStreamWriter = stAXResult.getXMLStreamWriter();
        if (xMLStreamWriter != null) {
            if (this.fStAXStreamResultBuilder == null) {
                this.fStAXStreamResultBuilder = new StAXStreamResultBuilder(this.fNamespaceContext);
            }
            this.fStAXValidatorHandler = this.fStAXStreamResultBuilder;
            this.fStAXStreamResultBuilder.setStAXResult(stAXResult);
        } else {
            if (this.fStAXEventResultBuilder == null) {
                this.fStAXEventResultBuilder = new StAXEventResultBuilder(this, this.fNamespaceContext);
            }
            this.fStAXValidatorHandler = this.fStAXEventResultBuilder;
            this.fStAXEventResultBuilder.setStAXResult(stAXResult);
        }
        this.fSchemaValidator.setDocumentHandler(this.fStAXValidatorHandler);
    }

    static final class XMLStreamReaderLocation
    implements Location {
        private XMLStreamReader reader;

        @Override
        public int getCharacterOffset() {
            Location location = this.getLocation();
            if (location != null) {
                return location.getCharacterOffset();
            }
            return -1;
        }

        @Override
        public int getColumnNumber() {
            Location location = this.getLocation();
            if (location != null) {
                return location.getColumnNumber();
            }
            return -1;
        }

        @Override
        public int getLineNumber() {
            Location location = this.getLocation();
            if (location != null) {
                return location.getLineNumber();
            }
            return -1;
        }

        @Override
        public String getPublicId() {
            Location location = this.getLocation();
            if (location != null) {
                return location.getPublicId();
            }
            return null;
        }

        @Override
        public String getSystemId() {
            Location location = this.getLocation();
            if (location != null) {
                return location.getSystemId();
            }
            return null;
        }

        public void setXMLStreamReader(XMLStreamReader xMLStreamReader) {
            this.reader = xMLStreamReader;
        }

        private Location getLocation() {
            return this.reader != null ? this.reader.getLocation() : null;
        }
    }

    final class EventHelper {
        private static final int CHUNK_SIZE = 1024;
        private static final int CHUNK_MASK = 1023;
        private final char[] fCharBuffer = new char[1024];

        EventHelper() {
        }

        /*
         * Enabled aggressive block sorting
         */
        final void validate(XMLEventReader xMLEventReader, StAXResult stAXResult) throws SAXException, XMLStreamException {
            StAXValidatorHelper.this.fCurrentEvent = xMLEventReader.peek();
            if (StAXValidatorHelper.this.fCurrentEvent != null) {
                int n = StAXValidatorHelper.this.fCurrentEvent.getEventType();
                if (n != 7 && n != 1) {
                    throw new SAXException(JAXPValidationMessageFormatter.formatMessage(StAXValidatorHelper.this.fComponentManager.getLocale(), "StAXIllegalInitialState", null));
                }
                StAXValidatorHelper.this.setup(null, stAXResult, false);
                StAXValidatorHelper.this.fSchemaValidator.startDocument(StAXValidatorHelper.this.fStAXLocationWrapper, null, StAXValidatorHelper.this.fNamespaceContext, null);
                block12: while (xMLEventReader.hasNext()) {
                    StAXValidatorHelper.this.fCurrentEvent = xMLEventReader.nextEvent();
                    n = StAXValidatorHelper.this.fCurrentEvent.getEventType();
                    switch (n) {
                        case 1: {
                            ++StAXValidatorHelper.this.fDepth;
                            StartElement startElement = StAXValidatorHelper.this.fCurrentEvent.asStartElement();
                            this.fillQName(StAXValidatorHelper.this.fElementQName, startElement.getName());
                            this.fillXMLAttributes(startElement);
                            this.fillDeclaredPrefixes(startElement);
                            StAXValidatorHelper.this.fNamespaceContext.setNamespaceContext(startElement.getNamespaceContext());
                            StAXValidatorHelper.this.fStAXLocationWrapper.setLocation(startElement.getLocation());
                            StAXValidatorHelper.this.fSchemaValidator.startElement(StAXValidatorHelper.this.fElementQName, StAXValidatorHelper.this.fAttributes, null);
                            break;
                        }
                        case 2: {
                            EndElement endElement = StAXValidatorHelper.this.fCurrentEvent.asEndElement();
                            this.fillQName(StAXValidatorHelper.this.fElementQName, endElement.getName());
                            this.fillDeclaredPrefixes(endElement);
                            StAXValidatorHelper.this.fStAXLocationWrapper.setLocation(endElement.getLocation());
                            StAXValidatorHelper.this.fSchemaValidator.endElement(StAXValidatorHelper.this.fElementQName, null);
                            if (--StAXValidatorHelper.this.fDepth > 0) break;
                            break block12;
                        }
                        case 4: 
                        case 6: {
                            XMLEvent xMLEvent;
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                xMLEvent = StAXValidatorHelper.this.fCurrentEvent.asCharacters();
                                StAXValidatorHelper.this.fStAXValidatorHandler.setIgnoringCharacters(true);
                                this.sendCharactersToValidator(xMLEvent.getData());
                                StAXValidatorHelper.this.fStAXValidatorHandler.setIgnoringCharacters(false);
                                StAXValidatorHelper.this.fStAXValidatorHandler.characters((Characters)xMLEvent);
                                break;
                            }
                            this.sendCharactersToValidator(StAXValidatorHelper.this.fCurrentEvent.asCharacters().getData());
                            break;
                        }
                        case 12: {
                            XMLEvent xMLEvent;
                            if (StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                                xMLEvent = StAXValidatorHelper.this.fCurrentEvent.asCharacters();
                                StAXValidatorHelper.this.fStAXValidatorHandler.setIgnoringCharacters(true);
                                StAXValidatorHelper.this.fSchemaValidator.startCDATA(null);
                                this.sendCharactersToValidator(StAXValidatorHelper.this.fCurrentEvent.asCharacters().getData());
                                StAXValidatorHelper.this.fSchemaValidator.endCDATA(null);
                                StAXValidatorHelper.this.fStAXValidatorHandler.setIgnoringCharacters(false);
                                StAXValidatorHelper.this.fStAXValidatorHandler.cdata((Characters)xMLEvent);
                                break;
                            }
                            StAXValidatorHelper.this.fSchemaValidator.startCDATA(null);
                            this.sendCharactersToValidator(StAXValidatorHelper.this.fCurrentEvent.asCharacters().getData());
                            StAXValidatorHelper.this.fSchemaValidator.endCDATA(null);
                            break;
                        }
                        case 7: {
                            ++StAXValidatorHelper.this.fDepth;
                            if (StAXValidatorHelper.this.fStAXValidatorHandler == null) break;
                            StAXValidatorHelper.this.fStAXValidatorHandler.startDocument((StartDocument)StAXValidatorHelper.this.fCurrentEvent);
                            break;
                        }
                        case 8: {
                            if (StAXValidatorHelper.this.fStAXValidatorHandler == null) break;
                            StAXValidatorHelper.this.fStAXValidatorHandler.endDocument((EndDocument)StAXValidatorHelper.this.fCurrentEvent);
                            break;
                        }
                        case 3: {
                            if (StAXValidatorHelper.this.fStAXValidatorHandler == null) break;
                            StAXValidatorHelper.this.fStAXValidatorHandler.processingInstruction((ProcessingInstruction)StAXValidatorHelper.this.fCurrentEvent);
                            break;
                        }
                        case 5: {
                            if (StAXValidatorHelper.this.fStAXValidatorHandler == null) break;
                            StAXValidatorHelper.this.fStAXValidatorHandler.comment((Comment)StAXValidatorHelper.this.fCurrentEvent);
                            break;
                        }
                        case 9: {
                            if (StAXValidatorHelper.this.fStAXValidatorHandler == null) break;
                            StAXValidatorHelper.this.fStAXValidatorHandler.entityReference((EntityReference)StAXValidatorHelper.this.fCurrentEvent);
                            break;
                        }
                        case 11: {
                            XMLEvent xMLEvent = (DTD)StAXValidatorHelper.this.fCurrentEvent;
                            StAXValidatorHelper.this.processEntityDeclarations(xMLEvent.getEntities());
                            if (StAXValidatorHelper.this.fStAXValidatorHandler == null) break;
                            StAXValidatorHelper.this.fStAXValidatorHandler.doctypeDecl((DTD)xMLEvent);
                        }
                    }
                }
                StAXValidatorHelper.this.fSchemaValidator.endDocument(null);
            }
        }

        private void fillQName(org.apache.xerces.xni.QName qName, QName qName2) {
            StAXValidatorHelper.this.fillQName(qName, qName2.getNamespaceURI(), qName2.getLocalPart(), qName2.getPrefix());
        }

        private void fillXMLAttributes(StartElement startElement) {
            StAXValidatorHelper.this.fAttributes.removeAllAttributes();
            Iterator<Attribute> iterator = startElement.getAttributes();
            while (iterator.hasNext()) {
                Attribute attribute = iterator.next();
                this.fillQName(StAXValidatorHelper.this.fAttributeQName, attribute.getName());
                String string = attribute.getDTDType();
                int n = StAXValidatorHelper.this.fAttributes.getLength();
                StAXValidatorHelper.this.fAttributes.addAttributeNS(StAXValidatorHelper.this.fAttributeQName, string != null ? string : XMLSymbols.fCDATASymbol, attribute.getValue());
                StAXValidatorHelper.this.fAttributes.setSpecified(n, attribute.isSpecified());
            }
        }

        private void fillDeclaredPrefixes(StartElement startElement) {
            this.fillDeclaredPrefixes(startElement.getNamespaces());
        }

        private void fillDeclaredPrefixes(EndElement endElement) {
            this.fillDeclaredPrefixes(endElement.getNamespaces());
        }

        private void fillDeclaredPrefixes(Iterator iterator) {
            StAXValidatorHelper.this.fDeclaredPrefixes.clear();
            while (iterator.hasNext()) {
                Namespace namespace = (Namespace)iterator.next();
                String string = namespace.getPrefix();
                StAXValidatorHelper.this.fDeclaredPrefixes.add(string != null ? string : "");
            }
        }

        private void sendCharactersToValidator(String string) {
            if (string != null) {
                int n = string.length();
                int n2 = n & 0x3FF;
                if (n2 > 0) {
                    string.getChars(0, n2, this.fCharBuffer, 0);
                    StAXValidatorHelper.this.fTempString.setValues(this.fCharBuffer, 0, n2);
                    StAXValidatorHelper.this.fSchemaValidator.characters(StAXValidatorHelper.this.fTempString, null);
                }
                int n3 = n2;
                while (n3 < n) {
                    string.getChars(n3, n3 += 1024, this.fCharBuffer, 0);
                    StAXValidatorHelper.this.fTempString.setValues(this.fCharBuffer, 0, 1024);
                    StAXValidatorHelper.this.fSchemaValidator.characters(StAXValidatorHelper.this.fTempString, null);
                }
            }
        }
    }

    final class StreamHelper {
        StreamHelper() {
        }

        final void validate(XMLStreamReader xMLStreamReader, StAXResult stAXResult) throws SAXException, XMLStreamException {
            if (xMLStreamReader.hasNext()) {
                int n = xMLStreamReader.getEventType();
                if (n != 7 && n != 1) {
                    throw new SAXException(JAXPValidationMessageFormatter.formatMessage(StAXValidatorHelper.this.fComponentManager.getLocale(), "StAXIllegalInitialState", null));
                }
                StAXValidatorHelper.this.fXMLStreamReaderLocation.setXMLStreamReader(xMLStreamReader);
                Object object = Boolean.FALSE;
                try {
                    object = xMLStreamReader.getProperty(StAXValidatorHelper.STRING_INTERNING);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                StAXValidatorHelper.this.setup(StAXValidatorHelper.this.fXMLStreamReaderLocation, stAXResult, Boolean.TRUE.equals(object));
                StAXValidatorHelper.this.fSchemaValidator.startDocument(StAXValidatorHelper.this.fStAXLocationWrapper, null, StAXValidatorHelper.this.fNamespaceContext, null);
                do {
                    switch (n) {
                        case 1: {
                            ++StAXValidatorHelper.this.fDepth;
                            StAXValidatorHelper.this.fillQName(StAXValidatorHelper.this.fElementQName, xMLStreamReader.getNamespaceURI(), xMLStreamReader.getLocalName(), xMLStreamReader.getPrefix());
                            this.fillXMLAttributes(xMLStreamReader);
                            this.fillDeclaredPrefixes(xMLStreamReader);
                            StAXValidatorHelper.this.fNamespaceContext.setNamespaceContext(xMLStreamReader.getNamespaceContext());
                            StAXValidatorHelper.this.fSchemaValidator.startElement(StAXValidatorHelper.this.fElementQName, StAXValidatorHelper.this.fAttributes, null);
                            break;
                        }
                        case 2: {
                            StAXValidatorHelper.this.fillQName(StAXValidatorHelper.this.fElementQName, xMLStreamReader.getNamespaceURI(), xMLStreamReader.getLocalName(), xMLStreamReader.getPrefix());
                            this.fillDeclaredPrefixes(xMLStreamReader);
                            StAXValidatorHelper.this.fNamespaceContext.setNamespaceContext(xMLStreamReader.getNamespaceContext());
                            StAXValidatorHelper.this.fSchemaValidator.endElement(StAXValidatorHelper.this.fElementQName, null);
                            --StAXValidatorHelper.this.fDepth;
                            break;
                        }
                        case 4: 
                        case 6: {
                            StAXValidatorHelper.this.fTempString.setValues(xMLStreamReader.getTextCharacters(), xMLStreamReader.getTextStart(), xMLStreamReader.getTextLength());
                            StAXValidatorHelper.this.fSchemaValidator.characters(StAXValidatorHelper.this.fTempString, null);
                            break;
                        }
                        case 12: {
                            StAXValidatorHelper.this.fSchemaValidator.startCDATA(null);
                            StAXValidatorHelper.this.fTempString.setValues(xMLStreamReader.getTextCharacters(), xMLStreamReader.getTextStart(), xMLStreamReader.getTextLength());
                            StAXValidatorHelper.this.fSchemaValidator.characters(StAXValidatorHelper.this.fTempString, null);
                            StAXValidatorHelper.this.fSchemaValidator.endCDATA(null);
                            break;
                        }
                        case 7: {
                            ++StAXValidatorHelper.this.fDepth;
                            if (StAXValidatorHelper.this.fStAXValidatorHandler == null) break;
                            StAXValidatorHelper.this.fStAXValidatorHandler.startDocument(xMLStreamReader);
                            break;
                        }
                        case 3: {
                            if (StAXValidatorHelper.this.fStAXValidatorHandler == null) break;
                            StAXValidatorHelper.this.fStAXValidatorHandler.processingInstruction(xMLStreamReader);
                            break;
                        }
                        case 5: {
                            if (StAXValidatorHelper.this.fStAXValidatorHandler == null) break;
                            StAXValidatorHelper.this.fStAXValidatorHandler.comment(xMLStreamReader);
                            break;
                        }
                        case 9: {
                            if (StAXValidatorHelper.this.fStAXValidatorHandler == null) break;
                            StAXValidatorHelper.this.fStAXValidatorHandler.entityReference(xMLStreamReader);
                            break;
                        }
                        case 11: {
                            StAXValidatorHelper.this.processEntityDeclarations((List)xMLStreamReader.getProperty("javax.xml.stream.entities"));
                        }
                    }
                    n = xMLStreamReader.next();
                } while (xMLStreamReader.hasNext() && StAXValidatorHelper.this.fDepth > 0);
                StAXValidatorHelper.this.fSchemaValidator.endDocument(null);
                if (n == 8 && StAXValidatorHelper.this.fStAXValidatorHandler != null) {
                    StAXValidatorHelper.this.fStAXValidatorHandler.endDocument(xMLStreamReader);
                }
            }
        }

        private void fillXMLAttributes(XMLStreamReader xMLStreamReader) {
            StAXValidatorHelper.this.fAttributes.removeAllAttributes();
            int n = xMLStreamReader.getAttributeCount();
            for (int i = 0; i < n; ++i) {
                StAXValidatorHelper.this.fillQName(StAXValidatorHelper.this.fAttributeQName, xMLStreamReader.getAttributeNamespace(i), xMLStreamReader.getAttributeLocalName(i), xMLStreamReader.getAttributePrefix(i));
                String string = xMLStreamReader.getAttributeType(i);
                StAXValidatorHelper.this.fAttributes.addAttributeNS(StAXValidatorHelper.this.fAttributeQName, string != null ? string : XMLSymbols.fCDATASymbol, xMLStreamReader.getAttributeValue(i));
                StAXValidatorHelper.this.fAttributes.setSpecified(i, xMLStreamReader.isAttributeSpecified(i));
            }
        }

        private void fillDeclaredPrefixes(XMLStreamReader xMLStreamReader) {
            StAXValidatorHelper.this.fDeclaredPrefixes.clear();
            int n = xMLStreamReader.getNamespaceCount();
            for (int i = 0; i < n; ++i) {
                String string = xMLStreamReader.getNamespacePrefix(i);
                StAXValidatorHelper.this.fDeclaredPrefixes.add(string != null ? string : "");
            }
        }
    }
}

