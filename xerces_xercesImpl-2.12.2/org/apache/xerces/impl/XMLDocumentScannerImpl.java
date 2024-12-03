/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import java.io.CharConversionException;
import java.io.EOFException;
import java.io.IOException;
import org.apache.xerces.impl.XMLDocumentFragmentScannerImpl;
import org.apache.xerces.impl.dtd.XMLDTDDescription;
import org.apache.xerces.impl.io.MalformedByteSequenceException;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDTDScanner;
import org.apache.xerces.xni.parser.XMLInputSource;

public class XMLDocumentScannerImpl
extends XMLDocumentFragmentScannerImpl {
    protected static final int SCANNER_STATE_XML_DECL = 0;
    protected static final int SCANNER_STATE_PROLOG = 5;
    protected static final int SCANNER_STATE_TRAILING_MISC = 12;
    protected static final int SCANNER_STATE_DTD_INTERNAL_DECLS = 17;
    protected static final int SCANNER_STATE_DTD_EXTERNAL = 18;
    protected static final int SCANNER_STATE_DTD_EXTERNAL_DECLS = 19;
    protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    protected static final String DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
    protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://apache.org/xml/features/disallow-doctype-decl"};
    private static final Boolean[] FEATURE_DEFAULTS = new Boolean[]{Boolean.TRUE, Boolean.FALSE};
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/internal/namespace-context"};
    private static final Object[] PROPERTY_DEFAULTS = new Object[]{null, null, null};
    protected XMLDTDScanner fDTDScanner;
    protected ValidationManager fValidationManager;
    protected boolean fScanningDTD;
    protected String fDoctypeName;
    protected String fDoctypePublicId;
    protected String fDoctypeSystemId;
    protected NamespaceContext fNamespaceContext = new NamespaceSupport();
    protected boolean fLoadExternalDTD = true;
    protected boolean fDisallowDoctype = false;
    protected boolean fSeenDoctypeDecl;
    protected final XMLDocumentFragmentScannerImpl.Dispatcher fXMLDeclDispatcher = new XMLDeclDispatcher();
    protected final XMLDocumentFragmentScannerImpl.Dispatcher fPrologDispatcher = new PrologDispatcher();
    protected final XMLDocumentFragmentScannerImpl.Dispatcher fDTDDispatcher = new DTDDispatcher();
    protected final XMLDocumentFragmentScannerImpl.Dispatcher fTrailingMiscDispatcher = new TrailingMiscDispatcher();
    private final String[] fStrings = new String[3];
    private final XMLString fString = new XMLString();
    private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();
    private XMLInputSource fExternalSubsetSource = null;
    private final XMLDTDDescription fDTDDescription = new XMLDTDDescription(null, null, null, null, null);

    @Override
    public void setInputSource(XMLInputSource xMLInputSource) throws IOException {
        this.fEntityManager.setEntityHandler(this);
        this.fEntityManager.startDocumentEntity(xMLInputSource);
    }

    @Override
    public void reset(XMLComponentManager xMLComponentManager) throws XMLConfigurationException {
        super.reset(xMLComponentManager);
        this.fDoctypeName = null;
        this.fDoctypePublicId = null;
        this.fDoctypeSystemId = null;
        this.fSeenDoctypeDecl = false;
        this.fScanningDTD = false;
        this.fExternalSubsetSource = null;
        if (!this.fParserSettings) {
            this.fNamespaceContext.reset();
            this.setScannerState(0);
            this.setDispatcher(this.fXMLDeclDispatcher);
            return;
        }
        try {
            this.fLoadExternalDTD = xMLComponentManager.getFeature(LOAD_EXTERNAL_DTD);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fLoadExternalDTD = true;
        }
        try {
            this.fDisallowDoctype = xMLComponentManager.getFeature(DISALLOW_DOCTYPE_DECL_FEATURE);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fDisallowDoctype = false;
        }
        this.fDTDScanner = (XMLDTDScanner)xMLComponentManager.getProperty(DTD_SCANNER);
        try {
            this.fValidationManager = (ValidationManager)xMLComponentManager.getProperty(VALIDATION_MANAGER);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fValidationManager = null;
        }
        try {
            this.fNamespaceContext = (NamespaceContext)xMLComponentManager.getProperty(NAMESPACE_CONTEXT);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        if (this.fNamespaceContext == null) {
            this.fNamespaceContext = new NamespaceSupport();
        }
        this.fNamespaceContext.reset();
        this.setScannerState(0);
        this.setDispatcher(this.fXMLDeclDispatcher);
    }

    @Override
    public String[] getRecognizedFeatures() {
        String[] stringArray = super.getRecognizedFeatures();
        int n = stringArray != null ? stringArray.length : 0;
        String[] stringArray2 = new String[n + RECOGNIZED_FEATURES.length];
        if (stringArray != null) {
            System.arraycopy(stringArray, 0, stringArray2, 0, stringArray.length);
        }
        System.arraycopy(RECOGNIZED_FEATURES, 0, stringArray2, n, RECOGNIZED_FEATURES.length);
        return stringArray2;
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
        super.setFeature(string, bl);
        if (string.startsWith("http://apache.org/xml/features/")) {
            int n = string.length() - "http://apache.org/xml/features/".length();
            if (n == "nonvalidating/load-external-dtd".length() && string.endsWith("nonvalidating/load-external-dtd")) {
                this.fLoadExternalDTD = bl;
                return;
            }
            if (n == "disallow-doctype-decl".length() && string.endsWith("disallow-doctype-decl")) {
                this.fDisallowDoctype = bl;
                return;
            }
        }
    }

    @Override
    public String[] getRecognizedProperties() {
        String[] stringArray = super.getRecognizedProperties();
        int n = stringArray != null ? stringArray.length : 0;
        String[] stringArray2 = new String[n + RECOGNIZED_PROPERTIES.length];
        if (stringArray != null) {
            System.arraycopy(stringArray, 0, stringArray2, 0, stringArray.length);
        }
        System.arraycopy(RECOGNIZED_PROPERTIES, 0, stringArray2, n, RECOGNIZED_PROPERTIES.length);
        return stringArray2;
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        super.setProperty(string, object);
        if (string.startsWith("http://apache.org/xml/properties/")) {
            int n = string.length() - "http://apache.org/xml/properties/".length();
            if (n == "internal/dtd-scanner".length() && string.endsWith("internal/dtd-scanner")) {
                this.fDTDScanner = (XMLDTDScanner)object;
            }
            if (n == "internal/namespace-context".length() && string.endsWith("internal/namespace-context") && object != null) {
                this.fNamespaceContext = (NamespaceContext)object;
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
        return super.getFeatureDefault(string);
    }

    @Override
    public Object getPropertyDefault(String string) {
        for (int i = 0; i < RECOGNIZED_PROPERTIES.length; ++i) {
            if (!RECOGNIZED_PROPERTIES[i].equals(string)) continue;
            return PROPERTY_DEFAULTS[i];
        }
        return super.getPropertyDefault(string);
    }

    @Override
    public void startEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        super.startEntity(string, xMLResourceIdentifier, string2, augmentations);
        if (!string.equals("[xml]") && this.fEntityScanner.isExternal()) {
            this.setScannerState(16);
        }
        if (this.fDocumentHandler != null && string.equals("[xml]")) {
            this.fDocumentHandler.startDocument(this.fEntityScanner, string2, this.fNamespaceContext, null);
        }
    }

    @Override
    public void endEntity(String string, Augmentations augmentations) throws XNIException {
        super.endEntity(string, augmentations);
        if (this.fDocumentHandler != null && string.equals("[xml]")) {
            this.fDocumentHandler.endDocument(null);
        }
    }

    @Override
    protected XMLDocumentFragmentScannerImpl.Dispatcher createContentDispatcher() {
        return new ContentDispatcher();
    }

    protected boolean scanDoctypeDecl() throws IOException, XNIException {
        if (!this.fEntityScanner.skipSpaces()) {
            this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ROOT_ELEMENT_TYPE_IN_DOCTYPEDECL", null);
        }
        this.fDoctypeName = this.fEntityScanner.scanName();
        if (this.fDoctypeName == null) {
            this.reportFatalError("MSG_ROOT_ELEMENT_TYPE_REQUIRED", null);
        }
        if (this.fEntityScanner.skipSpaces()) {
            this.scanExternalID(this.fStrings, false);
            this.fDoctypeSystemId = this.fStrings[0];
            this.fDoctypePublicId = this.fStrings[1];
            this.fEntityScanner.skipSpaces();
        }
        boolean bl = this.fHasExternalDTD = this.fDoctypeSystemId != null;
        if (!this.fHasExternalDTD && this.fExternalSubsetResolver != null) {
            this.fDTDDescription.setValues(null, null, this.fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
            this.fDTDDescription.setRootName(this.fDoctypeName);
            this.fExternalSubsetSource = this.fExternalSubsetResolver.getExternalSubset(this.fDTDDescription);
            boolean bl2 = this.fHasExternalDTD = this.fExternalSubsetSource != null;
        }
        if (this.fDocumentHandler != null) {
            if (this.fExternalSubsetSource == null) {
                this.fDocumentHandler.doctypeDecl(this.fDoctypeName, this.fDoctypePublicId, this.fDoctypeSystemId, null);
            } else {
                this.fDocumentHandler.doctypeDecl(this.fDoctypeName, this.fExternalSubsetSource.getPublicId(), this.fExternalSubsetSource.getSystemId(), null);
            }
        }
        boolean bl3 = true;
        if (!this.fEntityScanner.skipChar(91)) {
            bl3 = false;
            this.fEntityScanner.skipSpaces();
            if (!this.fEntityScanner.skipChar(62)) {
                this.reportFatalError("DoctypedeclUnterminated", new Object[]{this.fDoctypeName});
            }
            --this.fMarkupDepth;
        }
        return bl3;
    }

    @Override
    protected String getScannerStateName(int n) {
        switch (n) {
            case 0: {
                return "SCANNER_STATE_XML_DECL";
            }
            case 5: {
                return "SCANNER_STATE_PROLOG";
            }
            case 12: {
                return "SCANNER_STATE_TRAILING_MISC";
            }
            case 17: {
                return "SCANNER_STATE_DTD_INTERNAL_DECLS";
            }
            case 18: {
                return "SCANNER_STATE_DTD_EXTERNAL";
            }
            case 19: {
                return "SCANNER_STATE_DTD_EXTERNAL_DECLS";
            }
        }
        return super.getScannerStateName(n);
    }

    protected final class TrailingMiscDispatcher
    implements XMLDocumentFragmentScannerImpl.Dispatcher {
        protected TrailingMiscDispatcher() {
        }

        @Override
        public boolean dispatch(boolean bl) throws IOException, XNIException {
            try {
                boolean bl2;
                do {
                    bl2 = false;
                    switch (XMLDocumentScannerImpl.this.fScannerState) {
                        case 12: {
                            XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(60)) {
                                XMLDocumentScannerImpl.this.setScannerState(1);
                                bl2 = true;
                                break;
                            }
                            XMLDocumentScannerImpl.this.setScannerState(7);
                            bl2 = true;
                            break;
                        }
                        case 1: {
                            ++XMLDocumentScannerImpl.this.fMarkupDepth;
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(63)) {
                                XMLDocumentScannerImpl.this.setScannerState(3);
                                bl2 = true;
                                break;
                            }
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(33)) {
                                XMLDocumentScannerImpl.this.setScannerState(2);
                                bl2 = true;
                                break;
                            }
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(47)) {
                                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                                bl2 = true;
                                break;
                            }
                            if (XMLDocumentScannerImpl.this.isValidNameStartChar(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                                XMLDocumentScannerImpl.this.scanStartElement();
                                XMLDocumentScannerImpl.this.setScannerState(7);
                                break;
                            }
                            if (XMLDocumentScannerImpl.this.isValidNameStartHighSurrogate(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                                XMLDocumentScannerImpl.this.scanStartElement();
                                XMLDocumentScannerImpl.this.setScannerState(7);
                                break;
                            }
                            XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                            break;
                        }
                        case 3: {
                            XMLDocumentScannerImpl.this.scanPI();
                            XMLDocumentScannerImpl.this.setScannerState(12);
                            break;
                        }
                        case 2: {
                            if (!XMLDocumentScannerImpl.this.fEntityScanner.skipString("--")) {
                                XMLDocumentScannerImpl.this.reportFatalError("InvalidCommentStart", null);
                            }
                            XMLDocumentScannerImpl.this.scanComment();
                            XMLDocumentScannerImpl.this.setScannerState(12);
                            break;
                        }
                        case 7: {
                            int n = XMLDocumentScannerImpl.this.fEntityScanner.peekChar();
                            if (n == -1) {
                                XMLDocumentScannerImpl.this.setScannerState(14);
                                return false;
                            }
                            XMLDocumentScannerImpl.this.reportFatalError("ContentIllegalInTrailingMisc", null);
                            XMLDocumentScannerImpl.this.fEntityScanner.scanChar();
                            XMLDocumentScannerImpl.this.setScannerState(12);
                            break;
                        }
                        case 8: {
                            XMLDocumentScannerImpl.this.reportFatalError("ReferenceIllegalInTrailingMisc", null);
                            XMLDocumentScannerImpl.this.setScannerState(12);
                            break;
                        }
                        case 14: {
                            return false;
                        }
                    }
                } while (bl || bl2);
            }
            catch (MalformedByteSequenceException malformedByteSequenceException) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError(malformedByteSequenceException.getDomain(), malformedByteSequenceException.getKey(), malformedByteSequenceException.getArguments(), (short)2, malformedByteSequenceException);
                return false;
            }
            catch (CharConversionException charConversionException) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2, charConversionException);
                return false;
            }
            catch (EOFException eOFException) {
                if (XMLDocumentScannerImpl.this.fMarkupDepth != 0) {
                    XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
                    return false;
                }
                XMLDocumentScannerImpl.this.setScannerState(14);
                return false;
            }
            return true;
        }
    }

    protected class ContentDispatcher
    extends XMLDocumentFragmentScannerImpl.FragmentContentDispatcher {
        protected ContentDispatcher() {
        }

        @Override
        protected boolean scanForDoctypeHook() throws IOException, XNIException {
            if (XMLDocumentScannerImpl.this.fEntityScanner.skipString("DOCTYPE")) {
                XMLDocumentScannerImpl.this.setScannerState(4);
                return true;
            }
            return false;
        }

        @Override
        protected boolean elementDepthIsZeroHook() throws IOException, XNIException {
            XMLDocumentScannerImpl.this.setScannerState(12);
            XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fTrailingMiscDispatcher);
            return true;
        }

        @Override
        protected boolean scanRootElementHook() throws IOException, XNIException {
            if (XMLDocumentScannerImpl.this.fExternalSubsetResolver != null && !XMLDocumentScannerImpl.this.fSeenDoctypeDecl && !XMLDocumentScannerImpl.this.fDisallowDoctype && (XMLDocumentScannerImpl.this.fValidation || XMLDocumentScannerImpl.this.fLoadExternalDTD)) {
                XMLDocumentScannerImpl.this.scanStartElementName();
                this.resolveExternalSubsetAndRead();
                if (XMLDocumentScannerImpl.this.scanStartElementAfterName()) {
                    XMLDocumentScannerImpl.this.setScannerState(12);
                    XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fTrailingMiscDispatcher);
                    return true;
                }
            } else if (XMLDocumentScannerImpl.this.scanStartElement()) {
                XMLDocumentScannerImpl.this.setScannerState(12);
                XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fTrailingMiscDispatcher);
                return true;
            }
            return false;
        }

        @Override
        protected void endOfFileHook(EOFException eOFException) throws IOException, XNIException {
            XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void resolveExternalSubsetAndRead() throws IOException, XNIException {
            XMLDocumentScannerImpl.this.fDTDDescription.setValues(null, null, XMLDocumentScannerImpl.this.fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
            XMLDocumentScannerImpl.this.fDTDDescription.setRootName(XMLDocumentScannerImpl.this.fElementQName.rawname);
            XMLInputSource xMLInputSource = XMLDocumentScannerImpl.this.fExternalSubsetResolver.getExternalSubset(XMLDocumentScannerImpl.this.fDTDDescription);
            if (xMLInputSource != null) {
                XMLDocumentScannerImpl.this.fDoctypeName = XMLDocumentScannerImpl.this.fElementQName.rawname;
                XMLDocumentScannerImpl.this.fDoctypePublicId = xMLInputSource.getPublicId();
                XMLDocumentScannerImpl.this.fDoctypeSystemId = xMLInputSource.getSystemId();
                if (XMLDocumentScannerImpl.this.fDocumentHandler != null) {
                    XMLDocumentScannerImpl.this.fDocumentHandler.doctypeDecl(XMLDocumentScannerImpl.this.fDoctypeName, XMLDocumentScannerImpl.this.fDoctypePublicId, XMLDocumentScannerImpl.this.fDoctypeSystemId, null);
                }
                try {
                    if (XMLDocumentScannerImpl.this.fValidationManager == null || !XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD()) {
                        XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(xMLInputSource);
                        while (XMLDocumentScannerImpl.this.fDTDScanner.scanDTDExternalSubset(true)) {
                        }
                    } else {
                        XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(null);
                    }
                }
                finally {
                    XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
                }
            }
        }
    }

    protected final class DTDDispatcher
    implements XMLDocumentFragmentScannerImpl.Dispatcher {
        protected DTDDispatcher() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean dispatch(boolean bl) throws IOException, XNIException {
            boolean bl2;
            XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(null);
            try {
                boolean bl3;
                block16: do {
                    bl3 = false;
                    switch (XMLDocumentScannerImpl.this.fScannerState) {
                        case 17: {
                            bl2 = true;
                            boolean bl4 = !(!XMLDocumentScannerImpl.this.fValidation && !XMLDocumentScannerImpl.this.fLoadExternalDTD || XMLDocumentScannerImpl.this.fValidationManager != null && XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD());
                            boolean bl5 = XMLDocumentScannerImpl.this.fDTDScanner.scanDTDInternalSubset(bl2, XMLDocumentScannerImpl.this.fStandalone, XMLDocumentScannerImpl.this.fHasExternalDTD && bl4);
                            if (bl5) continue block16;
                            if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(93)) {
                                XMLDocumentScannerImpl.this.reportFatalError("EXPECTED_SQUARE_BRACKET_TO_CLOSE_INTERNAL_SUBSET", null);
                            }
                            XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
                            if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(62)) {
                                XMLDocumentScannerImpl.this.reportFatalError("DoctypedeclUnterminated", new Object[]{XMLDocumentScannerImpl.this.fDoctypeName});
                            }
                            --XMLDocumentScannerImpl.this.fMarkupDepth;
                            if (XMLDocumentScannerImpl.this.fDoctypeSystemId != null) {
                                boolean bl6 = XMLDocumentScannerImpl.this.fIsEntityDeclaredVC = !XMLDocumentScannerImpl.this.fStandalone;
                                if (bl4) {
                                    XMLDocumentScannerImpl.this.setScannerState(18);
                                    break;
                                }
                            } else if (XMLDocumentScannerImpl.this.fExternalSubsetSource != null) {
                                boolean bl7 = XMLDocumentScannerImpl.this.fIsEntityDeclaredVC = !XMLDocumentScannerImpl.this.fStandalone;
                                if (bl4) {
                                    XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(XMLDocumentScannerImpl.this.fExternalSubsetSource);
                                    XMLDocumentScannerImpl.this.fExternalSubsetSource = null;
                                    XMLDocumentScannerImpl.this.setScannerState(19);
                                    break;
                                }
                            } else {
                                XMLDocumentScannerImpl.this.fIsEntityDeclaredVC = XMLDocumentScannerImpl.this.fEntityManager.hasPEReferences() && !XMLDocumentScannerImpl.this.fStandalone;
                            }
                            XMLDocumentScannerImpl.this.setScannerState(5);
                            XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fPrologDispatcher);
                            XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
                            boolean bl8 = true;
                            return bl8;
                        }
                        case 18: {
                            XMLDocumentScannerImpl.this.fDTDDescription.setValues(XMLDocumentScannerImpl.this.fDoctypePublicId, XMLDocumentScannerImpl.this.fDoctypeSystemId, null, null);
                            XMLDocumentScannerImpl.this.fDTDDescription.setRootName(XMLDocumentScannerImpl.this.fDoctypeName);
                            XMLInputSource xMLInputSource = XMLDocumentScannerImpl.this.fEntityManager.resolveEntity(XMLDocumentScannerImpl.this.fDTDDescription);
                            XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(xMLInputSource);
                            XMLDocumentScannerImpl.this.setScannerState(19);
                            bl3 = true;
                            break;
                        }
                        case 19: {
                            bl2 = true;
                            boolean bl4 = XMLDocumentScannerImpl.this.fDTDScanner.scanDTDExternalSubset(bl2);
                            if (bl4) continue block16;
                            XMLDocumentScannerImpl.this.setScannerState(5);
                            XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fPrologDispatcher);
                            XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
                            boolean bl5 = true;
                            return bl5;
                        }
                        default: {
                            throw new XNIException("DTDDispatcher#dispatch: scanner state=" + XMLDocumentScannerImpl.this.fScannerState + " (" + XMLDocumentScannerImpl.this.getScannerStateName(XMLDocumentScannerImpl.this.fScannerState) + ')');
                        }
                    }
                } while (bl || bl3);
            }
            catch (MalformedByteSequenceException malformedByteSequenceException) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError(malformedByteSequenceException.getDomain(), malformedByteSequenceException.getKey(), malformedByteSequenceException.getArguments(), (short)2, malformedByteSequenceException);
                bl2 = false;
                return bl2;
            }
            catch (CharConversionException charConversionException) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2, charConversionException);
                bl2 = false;
                return bl2;
            }
            catch (EOFException eOFException) {
                XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
                bl2 = false;
                return bl2;
            }
            finally {
                XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
            }
            return true;
        }
    }

    protected final class PrologDispatcher
    implements XMLDocumentFragmentScannerImpl.Dispatcher {
        protected PrologDispatcher() {
        }

        @Override
        public boolean dispatch(boolean bl) throws IOException, XNIException {
            try {
                boolean bl2;
                do {
                    bl2 = false;
                    switch (XMLDocumentScannerImpl.this.fScannerState) {
                        case 5: {
                            XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(60)) {
                                XMLDocumentScannerImpl.this.setScannerState(1);
                                bl2 = true;
                                break;
                            }
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(38)) {
                                XMLDocumentScannerImpl.this.setScannerState(8);
                                bl2 = true;
                                break;
                            }
                            XMLDocumentScannerImpl.this.setScannerState(7);
                            bl2 = true;
                            break;
                        }
                        case 1: {
                            ++XMLDocumentScannerImpl.this.fMarkupDepth;
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(33)) {
                                if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(45)) {
                                    if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(45)) {
                                        XMLDocumentScannerImpl.this.reportFatalError("InvalidCommentStart", null);
                                    }
                                    XMLDocumentScannerImpl.this.setScannerState(2);
                                    bl2 = true;
                                    break;
                                }
                                if (XMLDocumentScannerImpl.this.fEntityScanner.skipString("DOCTYPE")) {
                                    XMLDocumentScannerImpl.this.setScannerState(4);
                                    bl2 = true;
                                    break;
                                }
                                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInProlog", null);
                                break;
                            }
                            if (XMLDocumentScannerImpl.this.isValidNameStartChar(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentScannerImpl.this.setScannerState(6);
                                XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fContentDispatcher);
                                return true;
                            }
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(63)) {
                                XMLDocumentScannerImpl.this.setScannerState(3);
                                bl2 = true;
                                break;
                            }
                            if (XMLDocumentScannerImpl.this.isValidNameStartHighSurrogate(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentScannerImpl.this.setScannerState(6);
                                XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fContentDispatcher);
                                return true;
                            }
                            XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInProlog", null);
                            break;
                        }
                        case 2: {
                            XMLDocumentScannerImpl.this.scanComment();
                            XMLDocumentScannerImpl.this.setScannerState(5);
                            break;
                        }
                        case 3: {
                            XMLDocumentScannerImpl.this.scanPI();
                            XMLDocumentScannerImpl.this.setScannerState(5);
                            break;
                        }
                        case 4: {
                            if (XMLDocumentScannerImpl.this.fDisallowDoctype) {
                                XMLDocumentScannerImpl.this.reportFatalError("DoctypeNotAllowed", null);
                            }
                            if (XMLDocumentScannerImpl.this.fSeenDoctypeDecl) {
                                XMLDocumentScannerImpl.this.reportFatalError("AlreadySeenDoctype", null);
                            }
                            XMLDocumentScannerImpl.this.fSeenDoctypeDecl = true;
                            if (XMLDocumentScannerImpl.this.scanDoctypeDecl()) {
                                XMLDocumentScannerImpl.this.setScannerState(17);
                                XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fDTDDispatcher);
                                return true;
                            }
                            if (XMLDocumentScannerImpl.this.fDoctypeSystemId != null) {
                                boolean bl3 = XMLDocumentScannerImpl.this.fIsEntityDeclaredVC = !XMLDocumentScannerImpl.this.fStandalone;
                                if (!(!XMLDocumentScannerImpl.this.fValidation && !XMLDocumentScannerImpl.this.fLoadExternalDTD || XMLDocumentScannerImpl.this.fValidationManager != null && XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD())) {
                                    XMLDocumentScannerImpl.this.setScannerState(18);
                                    XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fDTDDispatcher);
                                    return true;
                                }
                            } else if (XMLDocumentScannerImpl.this.fExternalSubsetSource != null) {
                                boolean bl4 = XMLDocumentScannerImpl.this.fIsEntityDeclaredVC = !XMLDocumentScannerImpl.this.fStandalone;
                                if (!(!XMLDocumentScannerImpl.this.fValidation && !XMLDocumentScannerImpl.this.fLoadExternalDTD || XMLDocumentScannerImpl.this.fValidationManager != null && XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD())) {
                                    XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(XMLDocumentScannerImpl.this.fExternalSubsetSource);
                                    XMLDocumentScannerImpl.this.fExternalSubsetSource = null;
                                    XMLDocumentScannerImpl.this.setScannerState(19);
                                    XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fDTDDispatcher);
                                    return true;
                                }
                            }
                            XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(null);
                            XMLDocumentScannerImpl.this.setScannerState(5);
                            break;
                        }
                        case 7: {
                            XMLDocumentScannerImpl.this.reportFatalError("ContentIllegalInProlog", null);
                            XMLDocumentScannerImpl.this.fEntityScanner.scanChar();
                        }
                        case 8: {
                            XMLDocumentScannerImpl.this.reportFatalError("ReferenceIllegalInProlog", null);
                        }
                    }
                } while (bl || bl2);
                if (bl) {
                    if (XMLDocumentScannerImpl.this.fEntityScanner.scanChar() != 60) {
                        XMLDocumentScannerImpl.this.reportFatalError("RootElementRequired", null);
                    }
                    XMLDocumentScannerImpl.this.setScannerState(6);
                    XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fContentDispatcher);
                }
            }
            catch (MalformedByteSequenceException malformedByteSequenceException) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError(malformedByteSequenceException.getDomain(), malformedByteSequenceException.getKey(), malformedByteSequenceException.getArguments(), (short)2, malformedByteSequenceException);
                return false;
            }
            catch (CharConversionException charConversionException) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2, charConversionException);
                return false;
            }
            catch (EOFException eOFException) {
                XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
                return false;
            }
            return true;
        }
    }

    protected final class XMLDeclDispatcher
    implements XMLDocumentFragmentScannerImpl.Dispatcher {
        protected XMLDeclDispatcher() {
        }

        @Override
        public boolean dispatch(boolean bl) throws IOException, XNIException {
            XMLDocumentScannerImpl.this.setScannerState(5);
            XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fPrologDispatcher);
            try {
                if (XMLDocumentScannerImpl.this.fEntityScanner.skipString("<?xml")) {
                    ++XMLDocumentScannerImpl.this.fMarkupDepth;
                    if (XMLChar.isName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                        XMLDocumentScannerImpl.this.fStringBuffer.clear();
                        XMLDocumentScannerImpl.this.fStringBuffer.append("xml");
                        if (XMLDocumentScannerImpl.this.fNamespaces) {
                            while (XMLChar.isNCName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentScannerImpl.this.fStringBuffer.append((char)XMLDocumentScannerImpl.this.fEntityScanner.scanChar());
                            }
                        } else {
                            while (XMLChar.isName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentScannerImpl.this.fStringBuffer.append((char)XMLDocumentScannerImpl.this.fEntityScanner.scanChar());
                            }
                        }
                        String string = XMLDocumentScannerImpl.this.fSymbolTable.addSymbol(((XMLDocumentScannerImpl)XMLDocumentScannerImpl.this).fStringBuffer.ch, ((XMLDocumentScannerImpl)XMLDocumentScannerImpl.this).fStringBuffer.offset, ((XMLDocumentScannerImpl)XMLDocumentScannerImpl.this).fStringBuffer.length);
                        XMLDocumentScannerImpl.this.scanPIData(string, XMLDocumentScannerImpl.this.fString);
                    } else {
                        XMLDocumentScannerImpl.this.scanXMLDeclOrTextDecl(false);
                    }
                }
                XMLDocumentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
                return true;
            }
            catch (MalformedByteSequenceException malformedByteSequenceException) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError(malformedByteSequenceException.getDomain(), malformedByteSequenceException.getKey(), malformedByteSequenceException.getArguments(), (short)2, malformedByteSequenceException);
                return false;
            }
            catch (CharConversionException charConversionException) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2, charConversionException);
                return false;
            }
            catch (EOFException eOFException) {
                XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
                return false;
            }
        }
    }
}

