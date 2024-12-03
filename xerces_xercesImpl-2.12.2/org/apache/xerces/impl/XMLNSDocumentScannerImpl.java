/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import java.io.IOException;
import org.apache.xerces.impl.XMLDocumentFragmentScannerImpl;
import org.apache.xerces.impl.XMLDocumentScannerImpl;
import org.apache.xerces.impl.dtd.XMLDTDValidatorFilter;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentSource;

public class XMLNSDocumentScannerImpl
extends XMLDocumentScannerImpl {
    protected boolean fBindNamespaces;
    protected boolean fPerformValidation;
    private XMLDTDValidatorFilter fDTDValidator;
    private boolean fSawSpace;

    public void setDTDValidator(XMLDTDValidatorFilter xMLDTDValidatorFilter) {
        this.fDTDValidator = xMLDTDValidatorFilter;
    }

    @Override
    protected boolean scanStartElement() throws IOException, XNIException {
        int n;
        this.fEntityScanner.scanQName(this.fElementQName);
        String string = this.fElementQName.rawname;
        if (this.fBindNamespaces) {
            this.fNamespaceContext.pushContext();
            if (this.fScannerState == 6 && this.fPerformValidation) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[]{string}, (short)1);
                if (this.fDoctypeName == null || !this.fDoctypeName.equals(string)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[]{this.fDoctypeName, string}, (short)1);
                }
            }
        }
        this.fCurrentElement = this.fElementStack.pushElement(this.fElementQName);
        boolean bl = false;
        this.fAttributes.removeAllAttributes();
        while (true) {
            boolean bl2 = this.fEntityScanner.skipSpaces();
            n = this.fEntityScanner.peekChar();
            if (n == 62) {
                this.fEntityScanner.scanChar();
                break;
            }
            if (n == 47) {
                this.fEntityScanner.scanChar();
                if (!this.fEntityScanner.skipChar(62)) {
                    this.reportFatalError("ElementUnterminated", new Object[]{string});
                }
                bl = true;
                break;
            }
            if (!this.isValidNameStartChar(n) || !bl2) {
                this.reportFatalError("ElementUnterminated", new Object[]{string});
            }
            this.scanAttribute(this.fAttributes);
        }
        if (this.fBindNamespaces) {
            QName qName;
            if (this.fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[]{this.fElementQName.rawname}, (short)2);
            }
            String string2 = this.fElementQName.prefix != null ? this.fElementQName.prefix : XMLSymbols.EMPTY_STRING;
            this.fCurrentElement.uri = this.fElementQName.uri = this.fNamespaceContext.getURI(string2);
            if (this.fElementQName.prefix == null && this.fElementQName.uri != null) {
                this.fElementQName.prefix = XMLSymbols.EMPTY_STRING;
                this.fCurrentElement.prefix = XMLSymbols.EMPTY_STRING;
            }
            if (this.fElementQName.prefix != null && this.fElementQName.uri == null) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[]{this.fElementQName.prefix, this.fElementQName.rawname}, (short)2);
            }
            n = this.fAttributes.getLength();
            for (int i = 0; i < n; ++i) {
                this.fAttributes.getName(i, this.fAttributeQName);
                String string3 = this.fAttributeQName.prefix != null ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
                String string4 = this.fNamespaceContext.getURI(string3);
                if (this.fAttributeQName.uri != null && this.fAttributeQName.uri == string4 || string3 == XMLSymbols.EMPTY_STRING) continue;
                this.fAttributeQName.uri = string4;
                if (string4 == null) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[]{this.fElementQName.rawname, this.fAttributeQName.rawname, string3}, (short)2);
                }
                this.fAttributes.setURI(i, string4);
            }
            if (n > 1 && (qName = this.fAttributes.checkDuplicatesNS()) != null) {
                if (qName.uri != null) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[]{this.fElementQName.rawname, qName.localpart, qName.uri}, (short)2);
                } else {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNotUnique", new Object[]{this.fElementQName.rawname, qName.rawname}, (short)2);
                }
            }
        }
        if (this.fDocumentHandler != null) {
            if (bl) {
                --this.fMarkupDepth;
                if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
                    this.reportFatalError("ElementEntityMismatch", new Object[]{this.fCurrentElement.rawname});
                }
                this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
                if (this.fBindNamespaces) {
                    this.fNamespaceContext.popContext();
                }
                this.fElementStack.popElement(this.fElementQName);
            } else {
                this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
            }
        }
        return bl;
    }

    @Override
    protected void scanStartElementName() throws IOException, XNIException {
        this.fEntityScanner.scanQName(this.fElementQName);
        this.fSawSpace = this.fEntityScanner.skipSpaces();
    }

    @Override
    protected boolean scanStartElementAfterName() throws IOException, XNIException {
        String string = this.fElementQName.rawname;
        if (this.fBindNamespaces) {
            this.fNamespaceContext.pushContext();
            if (this.fScannerState == 6 && this.fPerformValidation) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[]{string}, (short)1);
                if (this.fDoctypeName == null || !this.fDoctypeName.equals(string)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[]{this.fDoctypeName, string}, (short)1);
                }
            }
        }
        this.fCurrentElement = this.fElementStack.pushElement(this.fElementQName);
        boolean bl = false;
        this.fAttributes.removeAllAttributes();
        while (true) {
            int n;
            if ((n = this.fEntityScanner.peekChar()) == 62) {
                this.fEntityScanner.scanChar();
                break;
            }
            if (n == 47) {
                this.fEntityScanner.scanChar();
                if (!this.fEntityScanner.skipChar(62)) {
                    this.reportFatalError("ElementUnterminated", new Object[]{string});
                }
                bl = true;
                break;
            }
            if (!this.isValidNameStartChar(n) || !this.fSawSpace) {
                this.reportFatalError("ElementUnterminated", new Object[]{string});
            }
            this.scanAttribute(this.fAttributes);
            this.fSawSpace = this.fEntityScanner.skipSpaces();
        }
        if (this.fBindNamespaces) {
            QName qName;
            if (this.fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[]{this.fElementQName.rawname}, (short)2);
            }
            String string2 = this.fElementQName.prefix != null ? this.fElementQName.prefix : XMLSymbols.EMPTY_STRING;
            this.fCurrentElement.uri = this.fElementQName.uri = this.fNamespaceContext.getURI(string2);
            if (this.fElementQName.prefix == null && this.fElementQName.uri != null) {
                this.fElementQName.prefix = XMLSymbols.EMPTY_STRING;
                this.fCurrentElement.prefix = XMLSymbols.EMPTY_STRING;
            }
            if (this.fElementQName.prefix != null && this.fElementQName.uri == null) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[]{this.fElementQName.prefix, this.fElementQName.rawname}, (short)2);
            }
            int n = this.fAttributes.getLength();
            for (int i = 0; i < n; ++i) {
                this.fAttributes.getName(i, this.fAttributeQName);
                String string3 = this.fAttributeQName.prefix != null ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
                String string4 = this.fNamespaceContext.getURI(string3);
                if (this.fAttributeQName.uri != null && this.fAttributeQName.uri == string4 || string3 == XMLSymbols.EMPTY_STRING) continue;
                this.fAttributeQName.uri = string4;
                if (string4 == null) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[]{this.fElementQName.rawname, this.fAttributeQName.rawname, string3}, (short)2);
                }
                this.fAttributes.setURI(i, string4);
            }
            if (n > 1 && (qName = this.fAttributes.checkDuplicatesNS()) != null) {
                if (qName.uri != null) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[]{this.fElementQName.rawname, qName.localpart, qName.uri}, (short)2);
                } else {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNotUnique", new Object[]{this.fElementQName.rawname, qName.rawname}, (short)2);
                }
            }
        }
        if (this.fDocumentHandler != null) {
            if (bl) {
                --this.fMarkupDepth;
                if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
                    this.reportFatalError("ElementEntityMismatch", new Object[]{this.fCurrentElement.rawname});
                }
                this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
                if (this.fBindNamespaces) {
                    this.fNamespaceContext.popContext();
                }
                this.fElementStack.popElement(this.fElementQName);
            } else {
                this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
            }
        }
        return bl;
    }

    protected void scanAttribute(XMLAttributesImpl xMLAttributesImpl) throws IOException, XNIException {
        int n;
        int n2;
        this.fEntityScanner.scanQName(this.fAttributeQName);
        this.fEntityScanner.skipSpaces();
        if (!this.fEntityScanner.skipChar(61)) {
            this.reportFatalError("EqRequiredInAttribute", new Object[]{this.fCurrentElement.rawname, this.fAttributeQName.rawname});
        }
        this.fEntityScanner.skipSpaces();
        if (this.fBindNamespaces) {
            n2 = xMLAttributesImpl.getLength();
            xMLAttributesImpl.addAttributeNS(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
        } else {
            n = xMLAttributesImpl.getLength();
            n2 = xMLAttributesImpl.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
            if (n == xMLAttributesImpl.getLength()) {
                this.reportFatalError("AttributeNotUnique", new Object[]{this.fCurrentElement.rawname, this.fAttributeQName.rawname});
            }
        }
        n = this.scanAttributeValue(this.fTempString, this.fTempString2, this.fAttributeQName.rawname, this.fIsEntityDeclaredVC, this.fCurrentElement.rawname) ? 1 : 0;
        String string = this.fTempString.toString();
        xMLAttributesImpl.setValue(n2, string);
        if (n == 0) {
            xMLAttributesImpl.setNonNormalizedValue(n2, this.fTempString2.toString());
        }
        xMLAttributesImpl.setSpecified(n2, true);
        if (this.fBindNamespaces) {
            String string2;
            String string3 = this.fAttributeQName.localpart;
            String string4 = string2 = this.fAttributeQName.prefix != null ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
            if (string2 == XMLSymbols.PREFIX_XMLNS || string2 == XMLSymbols.EMPTY_STRING && string3 == XMLSymbols.PREFIX_XMLNS) {
                String string5 = this.fSymbolTable.addSymbol(string);
                if (string2 == XMLSymbols.PREFIX_XMLNS && string3 == XMLSymbols.PREFIX_XMLNS) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[]{this.fAttributeQName}, (short)2);
                }
                if (string5 == NamespaceContext.XMLNS_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[]{this.fAttributeQName}, (short)2);
                }
                if (string3 == XMLSymbols.PREFIX_XML) {
                    if (string5 != NamespaceContext.XML_URI) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[]{this.fAttributeQName}, (short)2);
                    }
                } else if (string5 == NamespaceContext.XML_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[]{this.fAttributeQName}, (short)2);
                }
                String string6 = string2 = string3 != XMLSymbols.PREFIX_XMLNS ? string3 : XMLSymbols.EMPTY_STRING;
                if (string5 == XMLSymbols.EMPTY_STRING && string3 != XMLSymbols.PREFIX_XMLNS) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "EmptyPrefixedAttName", new Object[]{this.fAttributeQName}, (short)2);
                }
                this.fNamespaceContext.declarePrefix(string2, string5.length() != 0 ? string5 : null);
                xMLAttributesImpl.setURI(n2, this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS));
            } else if (this.fAttributeQName.prefix != null) {
                xMLAttributesImpl.setURI(n2, this.fNamespaceContext.getURI(this.fAttributeQName.prefix));
            }
        }
    }

    @Override
    protected int scanEndElement() throws IOException, XNIException {
        this.fElementStack.popElement(this.fElementQName);
        if (!this.fEntityScanner.skipString(this.fElementQName.rawname)) {
            this.reportFatalError("ETagRequired", new Object[]{this.fElementQName.rawname});
        }
        this.fEntityScanner.skipSpaces();
        if (!this.fEntityScanner.skipChar(62)) {
            this.reportFatalError("ETagUnterminated", new Object[]{this.fElementQName.rawname});
        }
        --this.fMarkupDepth;
        --this.fMarkupDepth;
        if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
            this.reportFatalError("ElementEntityMismatch", new Object[]{this.fCurrentElement.rawname});
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endElement(this.fElementQName, null);
            if (this.fBindNamespaces) {
                this.fNamespaceContext.popContext();
            }
        }
        return this.fMarkupDepth;
    }

    @Override
    public void reset(XMLComponentManager xMLComponentManager) throws XMLConfigurationException {
        super.reset(xMLComponentManager);
        this.fPerformValidation = false;
        this.fBindNamespaces = false;
    }

    @Override
    protected XMLDocumentFragmentScannerImpl.Dispatcher createContentDispatcher() {
        return new NSContentDispatcher();
    }

    protected final class NSContentDispatcher
    extends XMLDocumentScannerImpl.ContentDispatcher {
        protected NSContentDispatcher() {
        }

        @Override
        protected boolean scanRootElementHook() throws IOException, XNIException {
            if (XMLNSDocumentScannerImpl.this.fExternalSubsetResolver != null && !XMLNSDocumentScannerImpl.this.fSeenDoctypeDecl && !XMLNSDocumentScannerImpl.this.fDisallowDoctype && (XMLNSDocumentScannerImpl.this.fValidation || XMLNSDocumentScannerImpl.this.fLoadExternalDTD)) {
                XMLNSDocumentScannerImpl.this.scanStartElementName();
                this.resolveExternalSubsetAndRead();
                this.reconfigurePipeline();
                if (XMLNSDocumentScannerImpl.this.scanStartElementAfterName()) {
                    XMLNSDocumentScannerImpl.this.setScannerState(12);
                    XMLNSDocumentScannerImpl.this.setDispatcher(XMLNSDocumentScannerImpl.this.fTrailingMiscDispatcher);
                    return true;
                }
            } else {
                this.reconfigurePipeline();
                if (XMLNSDocumentScannerImpl.this.scanStartElement()) {
                    XMLNSDocumentScannerImpl.this.setScannerState(12);
                    XMLNSDocumentScannerImpl.this.setDispatcher(XMLNSDocumentScannerImpl.this.fTrailingMiscDispatcher);
                    return true;
                }
            }
            return false;
        }

        private void reconfigurePipeline() {
            if (XMLNSDocumentScannerImpl.this.fDTDValidator == null) {
                XMLNSDocumentScannerImpl.this.fBindNamespaces = true;
            } else if (!XMLNSDocumentScannerImpl.this.fDTDValidator.hasGrammar()) {
                XMLNSDocumentScannerImpl.this.fBindNamespaces = true;
                XMLNSDocumentScannerImpl.this.fPerformValidation = XMLNSDocumentScannerImpl.this.fDTDValidator.validate();
                XMLDocumentSource xMLDocumentSource = XMLNSDocumentScannerImpl.this.fDTDValidator.getDocumentSource();
                XMLDocumentHandler xMLDocumentHandler = XMLNSDocumentScannerImpl.this.fDTDValidator.getDocumentHandler();
                xMLDocumentSource.setDocumentHandler(xMLDocumentHandler);
                if (xMLDocumentHandler != null) {
                    xMLDocumentHandler.setDocumentSource(xMLDocumentSource);
                }
                XMLNSDocumentScannerImpl.this.fDTDValidator.setDocumentSource(null);
                XMLNSDocumentScannerImpl.this.fDTDValidator.setDocumentHandler(null);
            }
        }
    }
}

