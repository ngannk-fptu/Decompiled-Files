/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import java.io.CharConversionException;
import java.io.EOFException;
import java.io.IOException;
import org.apache.xerces.impl.ExternalSubsetResolver;
import org.apache.xerces.impl.XMLEntityHandler;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLScanner;
import org.apache.xerces.impl.io.MalformedByteSequenceException;
import org.apache.xerces.util.AugmentationsImpl;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentScanner;
import org.apache.xerces.xni.parser.XMLInputSource;

public class XMLDocumentFragmentScannerImpl
extends XMLScanner
implements XMLDocumentScanner,
XMLComponent,
XMLEntityHandler {
    protected static final int SCANNER_STATE_START_OF_MARKUP = 1;
    protected static final int SCANNER_STATE_COMMENT = 2;
    protected static final int SCANNER_STATE_PI = 3;
    protected static final int SCANNER_STATE_DOCTYPE = 4;
    protected static final int SCANNER_STATE_ROOT_ELEMENT = 6;
    protected static final int SCANNER_STATE_CONTENT = 7;
    protected static final int SCANNER_STATE_REFERENCE = 8;
    protected static final int SCANNER_STATE_END_OF_INPUT = 13;
    protected static final int SCANNER_STATE_TERMINATED = 14;
    protected static final int SCANNER_STATE_CDATA = 15;
    protected static final int SCANNER_STATE_TEXT_DECL = 16;
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/validation", "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://apache.org/xml/features/scanner/notify-char-refs"};
    private static final Boolean[] FEATURE_DEFAULTS = new Boolean[]{null, null, Boolean.FALSE, Boolean.FALSE};
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/entity-resolver"};
    private static final Object[] PROPERTY_DEFAULTS = new Object[]{null, null, null, null};
    private static final boolean DEBUG_SCANNER_STATE = false;
    private static final boolean DEBUG_DISPATCHER = false;
    protected static final boolean DEBUG_CONTENT_SCANNING = false;
    protected XMLDocumentHandler fDocumentHandler;
    protected int[] fEntityStack = new int[4];
    protected int fMarkupDepth;
    protected int fScannerState;
    protected boolean fInScanContent = false;
    protected boolean fHasExternalDTD;
    protected boolean fStandalone;
    protected boolean fIsEntityDeclaredVC;
    protected ExternalSubsetResolver fExternalSubsetResolver;
    protected QName fCurrentElement;
    protected final ElementStack fElementStack = new ElementStack();
    protected boolean fNotifyBuiltInRefs = false;
    protected Dispatcher fDispatcher;
    protected final Dispatcher fContentDispatcher = this.createContentDispatcher();
    protected final QName fElementQName = new QName();
    protected final QName fAttributeQName = new QName();
    protected final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
    protected final XMLString fTempString = new XMLString();
    protected final XMLString fTempString2 = new XMLString();
    private final String[] fStrings = new String[3];
    private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();
    private final XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
    private final QName fQName = new QName();
    private final char[] fSingleChar = new char[1];
    private boolean fSawSpace;
    private Augmentations fTempAugmentations = null;

    @Override
    public void setInputSource(XMLInputSource xMLInputSource) throws IOException {
        this.fEntityManager.setEntityHandler(this);
        this.fEntityManager.startEntity("$fragment$", xMLInputSource, false, true);
    }

    @Override
    public boolean scanDocument(boolean bl) throws IOException, XNIException {
        this.fEntityScanner = this.fEntityManager.getEntityScanner();
        this.fEntityManager.setEntityHandler(this);
        do {
            if (this.fDispatcher.dispatch(bl)) continue;
            return false;
        } while (bl);
        return true;
    }

    @Override
    public void reset(XMLComponentManager xMLComponentManager) throws XMLConfigurationException {
        super.reset(xMLComponentManager);
        this.fAttributes.setNamespaces(this.fNamespaces);
        this.fMarkupDepth = 0;
        this.fCurrentElement = null;
        this.fElementStack.clear();
        this.fHasExternalDTD = false;
        this.fStandalone = false;
        this.fIsEntityDeclaredVC = false;
        this.fInScanContent = false;
        this.setScannerState(7);
        this.setDispatcher(this.fContentDispatcher);
        if (this.fParserSettings) {
            try {
                this.fNotifyBuiltInRefs = xMLComponentManager.getFeature(NOTIFY_BUILTIN_REFS);
            }
            catch (XMLConfigurationException xMLConfigurationException) {
                this.fNotifyBuiltInRefs = false;
            }
            try {
                Object object = xMLComponentManager.getProperty(ENTITY_RESOLVER);
                this.fExternalSubsetResolver = object instanceof ExternalSubsetResolver ? (ExternalSubsetResolver)object : null;
            }
            catch (XMLConfigurationException xMLConfigurationException) {
                this.fExternalSubsetResolver = null;
            }
        }
    }

    @Override
    public String[] getRecognizedFeatures() {
        return (String[])RECOGNIZED_FEATURES.clone();
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
        int n;
        super.setFeature(string, bl);
        if (string.startsWith("http://apache.org/xml/features/") && (n = string.length() - "http://apache.org/xml/features/".length()) == "scanner/notify-builtin-refs".length() && string.endsWith("scanner/notify-builtin-refs")) {
            this.fNotifyBuiltInRefs = bl;
        }
    }

    @Override
    public String[] getRecognizedProperties() {
        return (String[])RECOGNIZED_PROPERTIES.clone();
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        super.setProperty(string, object);
        if (string.startsWith("http://apache.org/xml/properties/")) {
            int n = string.length() - "http://apache.org/xml/properties/".length();
            if (n == "internal/entity-manager".length() && string.endsWith("internal/entity-manager")) {
                this.fEntityManager = (XMLEntityManager)object;
                return;
            }
            if (n == "internal/entity-resolver".length() && string.endsWith("internal/entity-resolver")) {
                this.fExternalSubsetResolver = object instanceof ExternalSubsetResolver ? (ExternalSubsetResolver)object : null;
                return;
            }
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
    public void startEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        if (this.fEntityDepth == this.fEntityStack.length) {
            int[] nArray = new int[this.fEntityStack.length * 2];
            System.arraycopy(this.fEntityStack, 0, nArray, 0, this.fEntityStack.length);
            this.fEntityStack = nArray;
        }
        this.fEntityStack[this.fEntityDepth] = this.fMarkupDepth;
        super.startEntity(string, xMLResourceIdentifier, string2, augmentations);
        if (this.fStandalone && this.fEntityManager.isEntityDeclInExternalSubset(string)) {
            this.reportFatalError("MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[]{string});
        }
        if (this.fDocumentHandler != null && !this.fScanningAttribute && !string.equals("[xml]")) {
            this.fDocumentHandler.startGeneralEntity(string, xMLResourceIdentifier, string2, augmentations);
        }
    }

    @Override
    public void endEntity(String string, Augmentations augmentations) throws XNIException {
        if (this.fInScanContent && this.fStringBuffer.length != 0 && this.fDocumentHandler != null) {
            this.fDocumentHandler.characters(this.fStringBuffer, null);
            this.fStringBuffer.length = 0;
        }
        super.endEntity(string, augmentations);
        if (this.fMarkupDepth != this.fEntityStack[this.fEntityDepth]) {
            this.reportFatalError("MarkupEntityMismatch", null);
        }
        if (this.fDocumentHandler != null && !this.fScanningAttribute && !string.equals("[xml]")) {
            this.fDocumentHandler.endGeneralEntity(string, augmentations);
        }
    }

    protected Dispatcher createContentDispatcher() {
        return new FragmentContentDispatcher();
    }

    protected void scanXMLDeclOrTextDecl(boolean bl) throws IOException, XNIException {
        super.scanXMLDeclOrTextDecl(bl, this.fStrings);
        --this.fMarkupDepth;
        String string = this.fStrings[0];
        String string2 = this.fStrings[1];
        String string3 = this.fStrings[2];
        this.fStandalone = string3 != null && string3.equals("yes");
        this.fEntityManager.setStandalone(this.fStandalone);
        this.fEntityScanner.setXMLVersion(string);
        if (this.fDocumentHandler != null) {
            if (bl) {
                this.fDocumentHandler.textDecl(string, string2, null);
            } else {
                this.fDocumentHandler.xmlDecl(string, string2, string3, null);
            }
        }
        if (string2 != null && !this.fEntityScanner.fCurrentEntity.isEncodingExternallySpecified()) {
            this.fEntityScanner.setEncoding(string2);
        }
    }

    @Override
    protected void scanPIData(String string, XMLString xMLString) throws IOException, XNIException {
        super.scanPIData(string, xMLString);
        --this.fMarkupDepth;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.processingInstruction(string, xMLString, null);
        }
    }

    protected void scanComment() throws IOException, XNIException {
        this.scanComment(this.fStringBuffer);
        --this.fMarkupDepth;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.comment(this.fStringBuffer, null);
        }
    }

    protected boolean scanStartElement() throws IOException, XNIException {
        String string;
        if (this.fNamespaces) {
            this.fEntityScanner.scanQName(this.fElementQName);
        } else {
            string = this.fEntityScanner.scanName();
            this.fElementQName.setValues(null, string, string, null);
        }
        string = this.fElementQName.rawname;
        this.fCurrentElement = this.fElementStack.pushElement(this.fElementQName);
        boolean bl = false;
        this.fAttributes.removeAllAttributes();
        while (true) {
            boolean bl2 = this.fEntityScanner.skipSpaces();
            int n = this.fEntityScanner.peekChar();
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
            if (!(this.isValidNameStartChar(n) && bl2 || this.isValidNameStartHighSurrogate(n) && bl2)) {
                this.reportFatalError("ElementUnterminated", new Object[]{string});
            }
            this.scanAttribute(this.fAttributes);
        }
        if (this.fDocumentHandler != null) {
            if (bl) {
                --this.fMarkupDepth;
                if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
                    this.reportFatalError("ElementEntityMismatch", new Object[]{this.fCurrentElement.rawname});
                }
                this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
                this.fElementStack.popElement(this.fElementQName);
            } else {
                this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
            }
        }
        return bl;
    }

    protected void scanStartElementName() throws IOException, XNIException {
        if (this.fNamespaces) {
            this.fEntityScanner.scanQName(this.fElementQName);
        } else {
            String string = this.fEntityScanner.scanName();
            this.fElementQName.setValues(null, string, string, null);
        }
        this.fSawSpace = this.fEntityScanner.skipSpaces();
    }

    protected boolean scanStartElementAfterName() throws IOException, XNIException {
        String string = this.fElementQName.rawname;
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
            if (!(this.isValidNameStartChar(n) && this.fSawSpace || this.isValidNameStartHighSurrogate(n) && this.fSawSpace)) {
                this.reportFatalError("ElementUnterminated", new Object[]{string});
            }
            this.scanAttribute(this.fAttributes);
            this.fSawSpace = this.fEntityScanner.skipSpaces();
        }
        if (this.fDocumentHandler != null) {
            if (bl) {
                --this.fMarkupDepth;
                if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
                    this.reportFatalError("ElementEntityMismatch", new Object[]{this.fCurrentElement.rawname});
                }
                this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
                this.fElementStack.popElement(this.fElementQName);
            } else {
                this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
            }
        }
        return bl;
    }

    protected void scanAttribute(XMLAttributes xMLAttributes) throws IOException, XNIException {
        if (this.fNamespaces) {
            this.fEntityScanner.scanQName(this.fAttributeQName);
        } else {
            String string = this.fEntityScanner.scanName();
            this.fAttributeQName.setValues(null, string, string, null);
        }
        this.fEntityScanner.skipSpaces();
        if (!this.fEntityScanner.skipChar(61)) {
            this.reportFatalError("EqRequiredInAttribute", new Object[]{this.fCurrentElement.rawname, this.fAttributeQName.rawname});
        }
        this.fEntityScanner.skipSpaces();
        int n = xMLAttributes.getLength();
        int n2 = xMLAttributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
        if (n == xMLAttributes.getLength()) {
            this.reportFatalError("AttributeNotUnique", new Object[]{this.fCurrentElement.rawname, this.fAttributeQName.rawname});
        }
        boolean bl = this.scanAttributeValue(this.fTempString, this.fTempString2, this.fAttributeQName.rawname, this.fIsEntityDeclaredVC, this.fCurrentElement.rawname);
        xMLAttributes.setValue(n2, this.fTempString.toString());
        if (!bl) {
            xMLAttributes.setNonNormalizedValue(n2, this.fTempString2.toString());
        }
        xMLAttributes.setSpecified(n2, true);
    }

    protected int scanContent() throws IOException, XNIException {
        XMLString xMLString = this.fTempString;
        int n = this.fEntityScanner.scanContent(xMLString);
        if (n == 13) {
            this.fEntityScanner.scanChar();
            this.fStringBuffer.clear();
            this.fStringBuffer.append(this.fTempString);
            this.fStringBuffer.append((char)n);
            xMLString = this.fStringBuffer;
            n = -1;
        }
        if (this.fDocumentHandler != null && xMLString.length > 0) {
            this.fDocumentHandler.characters(xMLString, null);
        }
        if (n == 93 && this.fTempString.length == 0) {
            this.fStringBuffer.clear();
            this.fStringBuffer.append((char)this.fEntityScanner.scanChar());
            this.fInScanContent = true;
            if (this.fEntityScanner.skipChar(93)) {
                this.fStringBuffer.append(']');
                while (this.fEntityScanner.skipChar(93)) {
                    this.fStringBuffer.append(']');
                }
                if (this.fEntityScanner.skipChar(62)) {
                    this.reportFatalError("CDEndInContent", null);
                }
            }
            if (this.fDocumentHandler != null && this.fStringBuffer.length != 0) {
                this.fDocumentHandler.characters(this.fStringBuffer, null);
            }
            this.fInScanContent = false;
            n = -1;
        }
        return n;
    }

    protected boolean scanCDATASection(boolean bl) throws IOException, XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startCDATA(null);
        }
        while (true) {
            int n;
            this.fStringBuffer.clear();
            if (!this.fEntityScanner.scanData("]]", this.fStringBuffer)) {
                if (this.fDocumentHandler != null && this.fStringBuffer.length > 0) {
                    this.fDocumentHandler.characters(this.fStringBuffer, null);
                }
                n = 0;
                while (this.fEntityScanner.skipChar(93)) {
                    ++n;
                }
                if (this.fDocumentHandler != null && n > 0) {
                    int n2;
                    this.fStringBuffer.clear();
                    if (n > 2048) {
                        int n3;
                        n2 = n / 2048;
                        int n4 = n % 2048;
                        for (n3 = 0; n3 < 2048; ++n3) {
                            this.fStringBuffer.append(']');
                        }
                        for (n3 = 0; n3 < n2; ++n3) {
                            this.fDocumentHandler.characters(this.fStringBuffer, null);
                        }
                        if (n4 != 0) {
                            this.fStringBuffer.length = n4;
                            this.fDocumentHandler.characters(this.fStringBuffer, null);
                        }
                    } else {
                        for (n2 = 0; n2 < n; ++n2) {
                            this.fStringBuffer.append(']');
                        }
                        this.fDocumentHandler.characters(this.fStringBuffer, null);
                    }
                }
                if (this.fEntityScanner.skipChar(62)) break;
                if (this.fDocumentHandler == null) continue;
                this.fStringBuffer.clear();
                this.fStringBuffer.append("]]");
                this.fDocumentHandler.characters(this.fStringBuffer, null);
                continue;
            }
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.characters(this.fStringBuffer, null);
            }
            if ((n = this.fEntityScanner.peekChar()) == -1 || !this.isInvalidLiteral(n)) continue;
            if (XMLChar.isHighSurrogate(n)) {
                this.fStringBuffer.clear();
                this.scanSurrogates(this.fStringBuffer);
                if (this.fDocumentHandler == null) continue;
                this.fDocumentHandler.characters(this.fStringBuffer, null);
                continue;
            }
            this.reportFatalError("InvalidCharInCDSect", new Object[]{Integer.toString(n, 16)});
            this.fEntityScanner.scanChar();
        }
        --this.fMarkupDepth;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endCDATA(null);
        }
        return true;
    }

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
        }
        return this.fMarkupDepth;
    }

    protected void scanCharReference() throws IOException, XNIException {
        this.fStringBuffer2.clear();
        int n = this.scanCharReferenceValue(this.fStringBuffer2, null);
        --this.fMarkupDepth;
        if (n != -1 && this.fDocumentHandler != null) {
            if (this.fNotifyCharRefs) {
                this.fDocumentHandler.startGeneralEntity(this.fCharRefLiteral, null, null, null);
            }
            Augmentations augmentations = null;
            if (this.fValidation && n <= 32) {
                if (this.fTempAugmentations != null) {
                    this.fTempAugmentations.removeAllItems();
                } else {
                    this.fTempAugmentations = new AugmentationsImpl();
                }
                augmentations = this.fTempAugmentations;
                augmentations.putItem("CHAR_REF_PROBABLE_WS", Boolean.TRUE);
            }
            this.fDocumentHandler.characters(this.fStringBuffer2, augmentations);
            if (this.fNotifyCharRefs) {
                this.fDocumentHandler.endGeneralEntity(this.fCharRefLiteral, null);
            }
        }
    }

    protected void scanEntityReference() throws IOException, XNIException {
        String string = this.fEntityScanner.scanName();
        if (string == null) {
            this.reportFatalError("NameRequiredInReference", null);
            return;
        }
        if (!this.fEntityScanner.skipChar(59)) {
            this.reportFatalError("SemicolonRequiredInReference", new Object[]{string});
        }
        --this.fMarkupDepth;
        if (string == fAmpSymbol) {
            this.handleCharacter('&', fAmpSymbol);
        } else if (string == fLtSymbol) {
            this.handleCharacter('<', fLtSymbol);
        } else if (string == fGtSymbol) {
            this.handleCharacter('>', fGtSymbol);
        } else if (string == fQuotSymbol) {
            this.handleCharacter('\"', fQuotSymbol);
        } else if (string == fAposSymbol) {
            this.handleCharacter('\'', fAposSymbol);
        } else if (this.fEntityManager.isUnparsedEntity(string)) {
            this.reportFatalError("ReferenceToUnparsedEntity", new Object[]{string});
        } else {
            if (!this.fEntityManager.isDeclaredEntity(string)) {
                if (this.fIsEntityDeclaredVC) {
                    if (this.fValidation) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[]{string}, (short)1);
                    }
                } else {
                    this.reportFatalError("EntityNotDeclared", new Object[]{string});
                }
            }
            this.fEntityManager.startEntity(string, false);
        }
    }

    private void handleCharacter(char c, String string) throws XNIException {
        if (this.fDocumentHandler != null) {
            if (this.fNotifyBuiltInRefs) {
                this.fDocumentHandler.startGeneralEntity(string, null, null, null);
            }
            this.fSingleChar[0] = c;
            this.fTempString.setValues(this.fSingleChar, 0, 1);
            this.fDocumentHandler.characters(this.fTempString, null);
            if (this.fNotifyBuiltInRefs) {
                this.fDocumentHandler.endGeneralEntity(string, null);
            }
        }
    }

    protected int handleEndElement(QName qName, boolean bl) throws XNIException {
        --this.fMarkupDepth;
        if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
            this.reportFatalError("ElementEntityMismatch", new Object[]{this.fCurrentElement.rawname});
        }
        QName qName2 = this.fQName;
        this.fElementStack.popElement(qName2);
        if (qName.rawname != qName2.rawname) {
            this.reportFatalError("ETagRequired", new Object[]{qName2.rawname});
        }
        if (this.fNamespaces) {
            qName.uri = qName2.uri;
        }
        if (this.fDocumentHandler != null && !bl) {
            this.fDocumentHandler.endElement(qName, null);
        }
        return this.fMarkupDepth;
    }

    protected final void setScannerState(int n) {
        this.fScannerState = n;
    }

    protected final void setDispatcher(Dispatcher dispatcher) {
        this.fDispatcher = dispatcher;
    }

    protected String getScannerStateName(int n) {
        switch (n) {
            case 4: {
                return "SCANNER_STATE_DOCTYPE";
            }
            case 6: {
                return "SCANNER_STATE_ROOT_ELEMENT";
            }
            case 1: {
                return "SCANNER_STATE_START_OF_MARKUP";
            }
            case 2: {
                return "SCANNER_STATE_COMMENT";
            }
            case 3: {
                return "SCANNER_STATE_PI";
            }
            case 7: {
                return "SCANNER_STATE_CONTENT";
            }
            case 8: {
                return "SCANNER_STATE_REFERENCE";
            }
            case 13: {
                return "SCANNER_STATE_END_OF_INPUT";
            }
            case 14: {
                return "SCANNER_STATE_TERMINATED";
            }
            case 15: {
                return "SCANNER_STATE_CDATA";
            }
            case 16: {
                return "SCANNER_STATE_TEXT_DECL";
            }
        }
        return "??? (" + n + ')';
    }

    public String getDispatcherName(Dispatcher dispatcher) {
        return "null";
    }

    protected class FragmentContentDispatcher
    implements Dispatcher {
        protected FragmentContentDispatcher() {
        }

        @Override
        public boolean dispatch(boolean bl) throws IOException, XNIException {
            try {
                boolean bl2;
                do {
                    bl2 = false;
                    block1 : switch (XMLDocumentFragmentScannerImpl.this.fScannerState) {
                        case 7: {
                            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(60)) {
                                XMLDocumentFragmentScannerImpl.this.setScannerState(1);
                                bl2 = true;
                                break;
                            }
                            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(38)) {
                                XMLDocumentFragmentScannerImpl.this.setScannerState(8);
                                bl2 = true;
                                break;
                            }
                            do {
                                int n;
                                if ((n = XMLDocumentFragmentScannerImpl.this.scanContent()) == 60) {
                                    XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
                                    XMLDocumentFragmentScannerImpl.this.setScannerState(1);
                                    break block1;
                                }
                                if (n == 38) {
                                    XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
                                    XMLDocumentFragmentScannerImpl.this.setScannerState(8);
                                    break block1;
                                }
                                if (n == -1 || !XMLDocumentFragmentScannerImpl.this.isInvalidLiteral(n)) continue;
                                if (XMLChar.isHighSurrogate(n)) {
                                    XMLDocumentFragmentScannerImpl.this.fStringBuffer.clear();
                                    if (!XMLDocumentFragmentScannerImpl.this.scanSurrogates(XMLDocumentFragmentScannerImpl.this.fStringBuffer) || XMLDocumentFragmentScannerImpl.this.fDocumentHandler == null) continue;
                                    XMLDocumentFragmentScannerImpl.this.fDocumentHandler.characters(XMLDocumentFragmentScannerImpl.this.fStringBuffer, null);
                                    continue;
                                }
                                XMLDocumentFragmentScannerImpl.this.reportFatalError("InvalidCharInContent", new Object[]{Integer.toString(n, 16)});
                                XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
                            } while (bl);
                            break;
                        }
                        case 1: {
                            ++XMLDocumentFragmentScannerImpl.this.fMarkupDepth;
                            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(47)) {
                                if (XMLDocumentFragmentScannerImpl.this.scanEndElement() == 0 && this.elementDepthIsZeroHook()) {
                                    return true;
                                }
                                XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                                break;
                            }
                            if (XMLDocumentFragmentScannerImpl.this.isValidNameStartChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentFragmentScannerImpl.this.scanStartElement();
                                XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                                break;
                            }
                            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(33)) {
                                if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(45)) {
                                    if (!XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(45)) {
                                        XMLDocumentFragmentScannerImpl.this.reportFatalError("InvalidCommentStart", null);
                                    }
                                    XMLDocumentFragmentScannerImpl.this.setScannerState(2);
                                    bl2 = true;
                                    break;
                                }
                                if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipString("[CDATA[")) {
                                    XMLDocumentFragmentScannerImpl.this.setScannerState(15);
                                    bl2 = true;
                                    break;
                                }
                                if (this.scanForDoctypeHook()) break;
                                XMLDocumentFragmentScannerImpl.this.reportFatalError("MarkupNotRecognizedInContent", null);
                                break;
                            }
                            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(63)) {
                                XMLDocumentFragmentScannerImpl.this.setScannerState(3);
                                bl2 = true;
                                break;
                            }
                            if (XMLDocumentFragmentScannerImpl.this.isValidNameStartHighSurrogate(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentFragmentScannerImpl.this.scanStartElement();
                                XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                                break;
                            }
                            XMLDocumentFragmentScannerImpl.this.reportFatalError("MarkupNotRecognizedInContent", null);
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            break;
                        }
                        case 2: {
                            XMLDocumentFragmentScannerImpl.this.scanComment();
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            break;
                        }
                        case 3: {
                            XMLDocumentFragmentScannerImpl.this.scanPI();
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            break;
                        }
                        case 15: {
                            XMLDocumentFragmentScannerImpl.this.scanCDATASection(bl);
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            break;
                        }
                        case 8: {
                            ++XMLDocumentFragmentScannerImpl.this.fMarkupDepth;
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(35)) {
                                XMLDocumentFragmentScannerImpl.this.scanCharReference();
                                break;
                            }
                            XMLDocumentFragmentScannerImpl.this.scanEntityReference();
                            break;
                        }
                        case 16: {
                            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipString("<?xml")) {
                                ++XMLDocumentFragmentScannerImpl.this.fMarkupDepth;
                                if (XMLDocumentFragmentScannerImpl.this.isValidNameChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                    XMLDocumentFragmentScannerImpl.this.fStringBuffer.clear();
                                    XMLDocumentFragmentScannerImpl.this.fStringBuffer.append("xml");
                                    if (XMLDocumentFragmentScannerImpl.this.fNamespaces) {
                                        while (XMLDocumentFragmentScannerImpl.this.isValidNCName(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                            XMLDocumentFragmentScannerImpl.this.fStringBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar());
                                        }
                                    } else {
                                        while (XMLDocumentFragmentScannerImpl.this.isValidNameChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                            XMLDocumentFragmentScannerImpl.this.fStringBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar());
                                        }
                                    }
                                    String string = XMLDocumentFragmentScannerImpl.this.fSymbolTable.addSymbol(((XMLDocumentFragmentScannerImpl)XMLDocumentFragmentScannerImpl.this).fStringBuffer.ch, ((XMLDocumentFragmentScannerImpl)XMLDocumentFragmentScannerImpl.this).fStringBuffer.offset, ((XMLDocumentFragmentScannerImpl)XMLDocumentFragmentScannerImpl.this).fStringBuffer.length);
                                    XMLDocumentFragmentScannerImpl.this.scanPIData(string, XMLDocumentFragmentScannerImpl.this.fTempString);
                                } else {
                                    XMLDocumentFragmentScannerImpl.this.scanXMLDeclOrTextDecl(true);
                                }
                            }
                            XMLDocumentFragmentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            break;
                        }
                        case 6: {
                            if (this.scanRootElementHook()) {
                                return true;
                            }
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            break;
                        }
                        case 4: {
                            XMLDocumentFragmentScannerImpl.this.reportFatalError("DoctypeIllegalInContent", null);
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                        }
                    }
                } while (bl || bl2);
            }
            catch (MalformedByteSequenceException malformedByteSequenceException) {
                XMLDocumentFragmentScannerImpl.this.fErrorReporter.reportError(malformedByteSequenceException.getDomain(), malformedByteSequenceException.getKey(), malformedByteSequenceException.getArguments(), (short)2, malformedByteSequenceException);
                return false;
            }
            catch (CharConversionException charConversionException) {
                XMLDocumentFragmentScannerImpl.this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2, charConversionException);
                return false;
            }
            catch (EOFException eOFException) {
                this.endOfFileHook(eOFException);
                return false;
            }
            return true;
        }

        protected boolean scanForDoctypeHook() throws IOException, XNIException {
            return false;
        }

        protected boolean elementDepthIsZeroHook() throws IOException, XNIException {
            return false;
        }

        protected boolean scanRootElementHook() throws IOException, XNIException {
            return false;
        }

        protected void endOfFileHook(EOFException eOFException) throws IOException, XNIException {
            if (XMLDocumentFragmentScannerImpl.this.fMarkupDepth != 0) {
                XMLDocumentFragmentScannerImpl.this.reportFatalError("PrematureEOF", null);
            }
        }
    }

    protected static interface Dispatcher {
        public boolean dispatch(boolean var1) throws IOException, XNIException;
    }

    protected static class ElementStack {
        protected QName[] fElements = new QName[10];
        protected int fSize;

        public ElementStack() {
            for (int i = 0; i < this.fElements.length; ++i) {
                this.fElements[i] = new QName();
            }
        }

        public QName pushElement(QName qName) {
            if (this.fSize == this.fElements.length) {
                QName[] qNameArray = new QName[this.fElements.length * 2];
                System.arraycopy(this.fElements, 0, qNameArray, 0, this.fSize);
                this.fElements = qNameArray;
                for (int i = this.fSize; i < this.fElements.length; ++i) {
                    this.fElements[i] = new QName();
                }
            }
            this.fElements[this.fSize].setValues(qName);
            return this.fElements[this.fSize++];
        }

        public void popElement(QName qName) {
            qName.setValues(this.fElements[--this.fSize]);
        }

        public void clear() {
            this.fSize = 0;
        }
    }
}

