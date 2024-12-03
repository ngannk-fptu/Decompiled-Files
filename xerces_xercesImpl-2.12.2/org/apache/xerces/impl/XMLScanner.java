/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import java.io.IOException;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLEntityScanner;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLResourceIdentifierImpl;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;

public abstract class XMLScanner
implements XMLComponent {
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected static final boolean DEBUG_ATTR_NORMALIZATION = false;
    protected boolean fValidation = false;
    protected boolean fNamespaces;
    protected boolean fNotifyCharRefs = false;
    protected boolean fParserSettings = true;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityManager fEntityManager;
    protected XMLEntityScanner fEntityScanner;
    protected int fEntityDepth;
    protected String fCharRefLiteral = null;
    protected boolean fScanningAttribute;
    protected boolean fReportEntity;
    protected static final String fVersionSymbol = "version".intern();
    protected static final String fEncodingSymbol = "encoding".intern();
    protected static final String fStandaloneSymbol = "standalone".intern();
    protected static final String fAmpSymbol = "amp".intern();
    protected static final String fLtSymbol = "lt".intern();
    protected static final String fGtSymbol = "gt".intern();
    protected static final String fQuotSymbol = "quot".intern();
    protected static final String fAposSymbol = "apos".intern();
    private final XMLString fString = new XMLString();
    private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();
    private final XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
    private final XMLStringBuffer fStringBuffer3 = new XMLStringBuffer();
    protected final XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();

    @Override
    public void reset(XMLComponentManager xMLComponentManager) throws XMLConfigurationException {
        try {
            this.fParserSettings = xMLComponentManager.getFeature(PARSER_SETTINGS);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fParserSettings = true;
        }
        if (!this.fParserSettings) {
            this.init();
            return;
        }
        this.fSymbolTable = (SymbolTable)xMLComponentManager.getProperty(SYMBOL_TABLE);
        this.fErrorReporter = (XMLErrorReporter)xMLComponentManager.getProperty(ERROR_REPORTER);
        this.fEntityManager = (XMLEntityManager)xMLComponentManager.getProperty(ENTITY_MANAGER);
        try {
            this.fValidation = xMLComponentManager.getFeature(VALIDATION);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fValidation = false;
        }
        try {
            this.fNamespaces = xMLComponentManager.getFeature(NAMESPACES);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fNamespaces = true;
        }
        try {
            this.fNotifyCharRefs = xMLComponentManager.getFeature(NOTIFY_CHAR_REFS);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fNotifyCharRefs = false;
        }
        this.init();
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        if (string.startsWith("http://apache.org/xml/properties/")) {
            int n = string.length() - "http://apache.org/xml/properties/".length();
            if (n == "internal/symbol-table".length() && string.endsWith("internal/symbol-table")) {
                this.fSymbolTable = (SymbolTable)object;
            } else if (n == "internal/error-reporter".length() && string.endsWith("internal/error-reporter")) {
                this.fErrorReporter = (XMLErrorReporter)object;
            } else if (n == "internal/entity-manager".length() && string.endsWith("internal/entity-manager")) {
                this.fEntityManager = (XMLEntityManager)object;
            }
        }
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
        if (VALIDATION.equals(string)) {
            this.fValidation = bl;
        } else if (NOTIFY_CHAR_REFS.equals(string)) {
            this.fNotifyCharRefs = bl;
        }
    }

    public boolean getFeature(String string) throws XMLConfigurationException {
        if (VALIDATION.equals(string)) {
            return this.fValidation;
        }
        if (NOTIFY_CHAR_REFS.equals(string)) {
            return this.fNotifyCharRefs;
        }
        throw new XMLConfigurationException(0, string);
    }

    protected void reset() {
        this.init();
        this.fValidation = true;
        this.fNotifyCharRefs = false;
    }

    protected void scanXMLDeclOrTextDecl(boolean bl, String[] stringArray) throws IOException, XNIException {
        String string = null;
        String string2 = null;
        String string3 = null;
        int n = 0;
        boolean bl2 = false;
        boolean bl3 = this.fEntityScanner.skipDeclSpaces();
        XMLEntityManager.ScannedEntity scannedEntity = this.fEntityManager.getCurrentEntity();
        boolean bl4 = scannedEntity.literal;
        scannedEntity.literal = false;
        while (this.fEntityScanner.peekChar() != 63) {
            bl2 = true;
            String string4 = this.scanPseudoAttribute(bl, this.fString);
            switch (n) {
                case 0: {
                    if (string4 == fVersionSymbol) {
                        if (!bl3) {
                            this.reportFatalError(bl ? "SpaceRequiredBeforeVersionInTextDecl" : "SpaceRequiredBeforeVersionInXMLDecl", null);
                        }
                        string = this.fString.toString();
                        n = 1;
                        if (this.versionSupported(string)) break;
                        this.reportFatalError(this.getVersionNotSupportedKey(), new Object[]{string});
                        break;
                    }
                    if (string4 == fEncodingSymbol) {
                        if (!bl) {
                            this.reportFatalError("VersionInfoRequired", null);
                        }
                        if (!bl3) {
                            this.reportFatalError(bl ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null);
                        }
                        string2 = this.fString.toString();
                        n = bl ? 3 : 2;
                        break;
                    }
                    if (bl) {
                        this.reportFatalError("EncodingDeclRequired", null);
                        break;
                    }
                    this.reportFatalError("VersionInfoRequired", null);
                    break;
                }
                case 1: {
                    if (string4 == fEncodingSymbol) {
                        if (!bl3) {
                            this.reportFatalError(bl ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null);
                        }
                        string2 = this.fString.toString();
                        n = bl ? 3 : 2;
                        break;
                    }
                    if (!bl && string4 == fStandaloneSymbol) {
                        if (!bl3) {
                            this.reportFatalError("SpaceRequiredBeforeStandalone", null);
                        }
                        string3 = this.fString.toString();
                        n = 3;
                        if (string3.equals("yes") || string3.equals("no")) break;
                        this.reportFatalError("SDDeclInvalid", new Object[]{string3});
                        break;
                    }
                    this.reportFatalError("EncodingDeclRequired", null);
                    break;
                }
                case 2: {
                    if (string4 == fStandaloneSymbol) {
                        if (!bl3) {
                            this.reportFatalError("SpaceRequiredBeforeStandalone", null);
                        }
                        string3 = this.fString.toString();
                        n = 3;
                        if (string3.equals("yes") || string3.equals("no")) break;
                        this.reportFatalError("SDDeclInvalid", new Object[]{string3});
                        break;
                    }
                    this.reportFatalError("EncodingDeclRequired", null);
                    break;
                }
                default: {
                    this.reportFatalError("NoMorePseudoAttributes", null);
                }
            }
            bl3 = this.fEntityScanner.skipDeclSpaces();
        }
        if (bl4) {
            scannedEntity.literal = true;
        }
        if (bl && n != 3) {
            this.reportFatalError("MorePseudoAttributes", null);
        }
        if (bl) {
            if (!bl2 && string2 == null) {
                this.reportFatalError("EncodingDeclRequired", null);
            }
        } else if (!bl2 && string == null) {
            this.reportFatalError("VersionInfoRequired", null);
        }
        if (!this.fEntityScanner.skipChar(63)) {
            this.reportFatalError("XMLDeclUnterminated", null);
        }
        if (!this.fEntityScanner.skipChar(62)) {
            this.reportFatalError("XMLDeclUnterminated", null);
        }
        stringArray[0] = string;
        stringArray[1] = string2;
        stringArray[2] = string3;
    }

    public String scanPseudoAttribute(boolean bl, XMLString xMLString) throws IOException, XNIException {
        String string = this.scanPseudoAttributeName();
        XMLEntityManager.print(this.fEntityManager.getCurrentEntity());
        if (string == null) {
            this.reportFatalError("PseudoAttrNameExpected", null);
        }
        this.fEntityScanner.skipDeclSpaces();
        if (!this.fEntityScanner.skipChar(61)) {
            this.reportFatalError(bl ? "EqRequiredInTextDecl" : "EqRequiredInXMLDecl", new Object[]{string});
        }
        this.fEntityScanner.skipDeclSpaces();
        int n = this.fEntityScanner.peekChar();
        if (n != 39 && n != 34) {
            this.reportFatalError(bl ? "QuoteRequiredInTextDecl" : "QuoteRequiredInXMLDecl", new Object[]{string});
        }
        this.fEntityScanner.scanChar();
        int n2 = this.fEntityScanner.scanLiteral(n, xMLString);
        if (n2 != n) {
            this.fStringBuffer2.clear();
            do {
                this.fStringBuffer2.append(xMLString);
                if (n2 == -1) continue;
                if (n2 == 38 || n2 == 37 || n2 == 60 || n2 == 93) {
                    this.fStringBuffer2.append((char)this.fEntityScanner.scanChar());
                    continue;
                }
                if (XMLChar.isHighSurrogate(n2)) {
                    this.scanSurrogates(this.fStringBuffer2);
                    continue;
                }
                if (!this.isInvalidLiteral(n2)) continue;
                String string2 = bl ? "InvalidCharInTextDecl" : "InvalidCharInXMLDecl";
                this.reportFatalError(string2, new Object[]{Integer.toString(n2, 16)});
                this.fEntityScanner.scanChar();
            } while ((n2 = this.fEntityScanner.scanLiteral(n, xMLString)) != n);
            this.fStringBuffer2.append(xMLString);
            xMLString.setValues(this.fStringBuffer2);
        }
        if (!this.fEntityScanner.skipChar(n)) {
            this.reportFatalError(bl ? "CloseQuoteMissingInTextDecl" : "CloseQuoteMissingInXMLDecl", new Object[]{string});
        }
        return string;
    }

    private String scanPseudoAttributeName() throws IOException, XNIException {
        int n = this.fEntityScanner.peekChar();
        switch (n) {
            case 118: {
                if (!this.fEntityScanner.skipString(fVersionSymbol)) break;
                return fVersionSymbol;
            }
            case 101: {
                if (!this.fEntityScanner.skipString(fEncodingSymbol)) break;
                return fEncodingSymbol;
            }
            case 115: {
                if (!this.fEntityScanner.skipString(fStandaloneSymbol)) break;
                return fStandaloneSymbol;
            }
        }
        return null;
    }

    protected void scanPI() throws IOException, XNIException {
        this.fReportEntity = false;
        String string = null;
        string = this.fNamespaces ? this.fEntityScanner.scanNCName() : this.fEntityScanner.scanName();
        if (string == null) {
            this.reportFatalError("PITargetRequired", null);
        }
        this.scanPIData(string, this.fString);
        this.fReportEntity = true;
    }

    protected void scanPIData(String string, XMLString xMLString) throws IOException, XNIException {
        int n;
        if (string.length() == 3) {
            n = Character.toLowerCase(string.charAt(0));
            char c = Character.toLowerCase(string.charAt(1));
            char c2 = Character.toLowerCase(string.charAt(2));
            if (n == 120 && c == 'm' && c2 == 'l') {
                this.reportFatalError("ReservedPITarget", null);
            }
        }
        if (!this.fEntityScanner.skipSpaces()) {
            if (this.fEntityScanner.skipString("?>")) {
                xMLString.clear();
                return;
            }
            if (this.fNamespaces && this.fEntityScanner.peekChar() == 58) {
                this.fEntityScanner.scanChar();
                XMLStringBuffer xMLStringBuffer = new XMLStringBuffer(string);
                xMLStringBuffer.append(':');
                String string2 = this.fEntityScanner.scanName();
                if (string2 != null) {
                    xMLStringBuffer.append(string2);
                }
                this.reportFatalError("ColonNotLegalWithNS", new Object[]{xMLStringBuffer.toString()});
                this.fEntityScanner.skipSpaces();
            } else {
                this.reportFatalError("SpaceRequiredInPI", null);
            }
        }
        this.fStringBuffer.clear();
        if (this.fEntityScanner.scanData("?>", this.fStringBuffer)) {
            do {
                if ((n = this.fEntityScanner.peekChar()) == -1) continue;
                if (XMLChar.isHighSurrogate(n)) {
                    this.scanSurrogates(this.fStringBuffer);
                    continue;
                }
                if (!this.isInvalidLiteral(n)) continue;
                this.reportFatalError("InvalidCharInPI", new Object[]{Integer.toHexString(n)});
                this.fEntityScanner.scanChar();
            } while (this.fEntityScanner.scanData("?>", this.fStringBuffer));
        }
        xMLString.setValues(this.fStringBuffer);
    }

    protected void scanComment(XMLStringBuffer xMLStringBuffer) throws IOException, XNIException {
        xMLStringBuffer.clear();
        while (this.fEntityScanner.scanData("--", xMLStringBuffer)) {
            int n = this.fEntityScanner.peekChar();
            if (n == -1) continue;
            if (XMLChar.isHighSurrogate(n)) {
                this.scanSurrogates(xMLStringBuffer);
                continue;
            }
            if (!this.isInvalidLiteral(n)) continue;
            this.reportFatalError("InvalidCharInComment", new Object[]{Integer.toHexString(n)});
            this.fEntityScanner.scanChar();
        }
        if (!this.fEntityScanner.skipChar(62)) {
            this.reportFatalError("DashDashInComment", null);
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    protected boolean scanAttributeValue(XMLString xMLString, XMLString xMLString2, String string, boolean bl, String string2) throws IOException, XNIException {
        int n;
        block28: {
            n = this.fEntityScanner.peekChar();
            if (n != 39 && n != 34) {
                this.reportFatalError("OpenQuoteExpected", new Object[]{string2, string});
            }
            this.fEntityScanner.scanChar();
            int n2 = this.fEntityDepth;
            int n3 = this.fEntityScanner.scanLiteral(n, xMLString);
            int n4 = 0;
            if (n3 == n && (n4 = this.isUnchangedByNormalization(xMLString)) == -1) {
                xMLString2.setValues(xMLString);
                int n5 = this.fEntityScanner.scanChar();
                if (n5 != n) {
                    this.reportFatalError("CloseQuoteExpected", new Object[]{string2, string});
                }
                return true;
            }
            this.fStringBuffer2.clear();
            this.fStringBuffer2.append(xMLString);
            this.normalizeWhitespace(xMLString, n4);
            if (n3 == n) break block28;
            this.fScanningAttribute = true;
            this.fStringBuffer.clear();
            do {
                block31: {
                    block40: {
                        block39: {
                            block38: {
                                block37: {
                                    block29: {
                                        String string3;
                                        block36: {
                                            block35: {
                                                block34: {
                                                    block33: {
                                                        block32: {
                                                            block30: {
                                                                int n6;
                                                                this.fStringBuffer.append(xMLString);
                                                                if (n3 != 38) break block29;
                                                                this.fEntityScanner.skipChar(38);
                                                                if (n2 == this.fEntityDepth) {
                                                                    this.fStringBuffer2.append('&');
                                                                }
                                                                if (!this.fEntityScanner.skipChar(35)) break block30;
                                                                if (n2 == this.fEntityDepth) {
                                                                    this.fStringBuffer2.append('#');
                                                                }
                                                                if ((n6 = this.scanCharReferenceValue(this.fStringBuffer, this.fStringBuffer2)) == -1) {
                                                                    // empty if block
                                                                }
                                                                break block31;
                                                            }
                                                            string3 = this.fEntityScanner.scanName();
                                                            if (string3 == null) {
                                                                this.reportFatalError("NameRequiredInReference", null);
                                                            } else if (n2 == this.fEntityDepth) {
                                                                this.fStringBuffer2.append(string3);
                                                            }
                                                            if (!this.fEntityScanner.skipChar(59)) {
                                                                this.reportFatalError("SemicolonRequiredInReference", new Object[]{string3});
                                                            } else if (n2 == this.fEntityDepth) {
                                                                this.fStringBuffer2.append(';');
                                                            }
                                                            if (string3 != fAmpSymbol) break block32;
                                                            this.fStringBuffer.append('&');
                                                            break block31;
                                                        }
                                                        if (string3 != fAposSymbol) break block33;
                                                        this.fStringBuffer.append('\'');
                                                        break block31;
                                                    }
                                                    if (string3 != fLtSymbol) break block34;
                                                    this.fStringBuffer.append('<');
                                                    break block31;
                                                }
                                                if (string3 != fGtSymbol) break block35;
                                                this.fStringBuffer.append('>');
                                                break block31;
                                            }
                                            if (string3 != fQuotSymbol) break block36;
                                            this.fStringBuffer.append('\"');
                                            break block31;
                                        }
                                        if (this.fEntityManager.isExternalEntity(string3)) {
                                            this.reportFatalError("ReferenceToExternalEntity", new Object[]{string3});
                                            break block31;
                                        } else {
                                            if (!this.fEntityManager.isDeclaredEntity(string3)) {
                                                if (bl) {
                                                    if (this.fValidation) {
                                                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[]{string3}, (short)1);
                                                    }
                                                } else {
                                                    this.reportFatalError("EntityNotDeclared", new Object[]{string3});
                                                }
                                            }
                                            this.fEntityManager.startEntity(string3, true);
                                        }
                                        break block31;
                                    }
                                    if (n3 != 60) break block37;
                                    this.reportFatalError("LessthanInAttValue", new Object[]{string2, string});
                                    this.fEntityScanner.scanChar();
                                    if (n2 == this.fEntityDepth) {
                                        this.fStringBuffer2.append((char)n3);
                                    }
                                    break block31;
                                }
                                if (n3 != 37 && n3 != 93) break block38;
                                this.fEntityScanner.scanChar();
                                this.fStringBuffer.append((char)n3);
                                if (n2 == this.fEntityDepth) {
                                    this.fStringBuffer2.append((char)n3);
                                }
                                break block31;
                            }
                            if (n3 != 10 && n3 != 13) break block39;
                            this.fEntityScanner.scanChar();
                            this.fStringBuffer.append(' ');
                            if (n2 == this.fEntityDepth) {
                                this.fStringBuffer2.append('\n');
                            }
                            break block31;
                        }
                        if (n3 == -1 || !XMLChar.isHighSurrogate(n3)) break block40;
                        this.fStringBuffer3.clear();
                        if (this.scanSurrogates(this.fStringBuffer3)) {
                            this.fStringBuffer.append(this.fStringBuffer3);
                            if (n2 == this.fEntityDepth) {
                                this.fStringBuffer2.append(this.fStringBuffer3);
                            }
                        }
                        break block31;
                    }
                    if (n3 != -1 && this.isInvalidLiteral(n3)) {
                        this.reportFatalError("InvalidCharInAttValue", new Object[]{string2, string, Integer.toString(n3, 16)});
                        this.fEntityScanner.scanChar();
                        if (n2 == this.fEntityDepth) {
                            this.fStringBuffer2.append((char)n3);
                        }
                    }
                }
                n3 = this.fEntityScanner.scanLiteral(n, xMLString);
                if (n2 == this.fEntityDepth) {
                    this.fStringBuffer2.append(xMLString);
                }
                this.normalizeWhitespace(xMLString);
            } while (n3 != n || n2 != this.fEntityDepth);
            this.fStringBuffer.append(xMLString);
            xMLString.setValues(this.fStringBuffer);
            this.fScanningAttribute = false;
        }
        xMLString2.setValues(this.fStringBuffer2);
        int n7 = this.fEntityScanner.scanChar();
        if (n7 != n) {
            this.reportFatalError("CloseQuoteExpected", new Object[]{string2, string});
        }
        return xMLString2.equals(xMLString.ch, xMLString.offset, xMLString.length);
    }

    protected void scanExternalID(String[] stringArray, boolean bl) throws IOException, XNIException {
        String string = null;
        String string2 = null;
        if (this.fEntityScanner.skipString("PUBLIC")) {
            if (!this.fEntityScanner.skipSpaces()) {
                this.reportFatalError("SpaceRequiredAfterPUBLIC", null);
            }
            this.scanPubidLiteral(this.fString);
            string2 = this.fString.toString();
            if (!this.fEntityScanner.skipSpaces() && !bl) {
                this.reportFatalError("SpaceRequiredBetweenPublicAndSystem", null);
            }
        }
        if (string2 != null || this.fEntityScanner.skipString("SYSTEM")) {
            int n;
            if (string2 == null && !this.fEntityScanner.skipSpaces()) {
                this.reportFatalError("SpaceRequiredAfterSYSTEM", null);
            }
            if ((n = this.fEntityScanner.peekChar()) != 39 && n != 34) {
                if (string2 != null && bl) {
                    stringArray[0] = null;
                    stringArray[1] = string2;
                    return;
                }
                this.reportFatalError("QuoteRequiredInSystemID", null);
            }
            this.fEntityScanner.scanChar();
            XMLString xMLString = this.fString;
            if (this.fEntityScanner.scanLiteral(n, xMLString) != n) {
                this.fStringBuffer.clear();
                do {
                    this.fStringBuffer.append(xMLString);
                    int n2 = this.fEntityScanner.peekChar();
                    if (XMLChar.isMarkup(n2) || n2 == 93) {
                        this.fStringBuffer.append((char)this.fEntityScanner.scanChar());
                        continue;
                    }
                    if (XMLChar.isHighSurrogate(n2)) {
                        this.scanSurrogates(this.fStringBuffer);
                        continue;
                    }
                    if (!this.isInvalidLiteral(n2)) continue;
                    this.reportFatalError("InvalidCharInSystemID", new Object[]{Integer.toHexString(n2)});
                    this.fEntityScanner.scanChar();
                } while (this.fEntityScanner.scanLiteral(n, xMLString) != n);
                this.fStringBuffer.append(xMLString);
                xMLString = this.fStringBuffer;
            }
            string = xMLString.toString();
            if (!this.fEntityScanner.skipChar(n)) {
                this.reportFatalError("SystemIDUnterminated", null);
            }
        }
        stringArray[0] = string;
        stringArray[1] = string2;
    }

    protected boolean scanPubidLiteral(XMLString xMLString) throws IOException, XNIException {
        int n = this.fEntityScanner.scanChar();
        if (n != 39 && n != 34) {
            this.reportFatalError("QuoteRequiredInPublicID", null);
            return false;
        }
        this.fStringBuffer.clear();
        boolean bl = true;
        boolean bl2 = true;
        while (true) {
            int n2;
            if ((n2 = this.fEntityScanner.scanChar()) == 32 || n2 == 10 || n2 == 13) {
                if (bl) continue;
                this.fStringBuffer.append(' ');
                bl = true;
                continue;
            }
            if (n2 == n) {
                if (bl) {
                    --this.fStringBuffer.length;
                }
                break;
            }
            if (XMLChar.isPubid(n2)) {
                this.fStringBuffer.append((char)n2);
                bl = false;
                continue;
            }
            if (n2 == -1) {
                this.reportFatalError("PublicIDUnterminated", null);
                return false;
            }
            bl2 = false;
            this.reportFatalError("InvalidCharInPublicID", new Object[]{Integer.toHexString(n2)});
        }
        xMLString.setValues(this.fStringBuffer);
        return bl2;
    }

    protected void normalizeWhitespace(XMLString xMLString) {
        int n = xMLString.offset + xMLString.length;
        for (int i = xMLString.offset; i < n; ++i) {
            char c = xMLString.ch[i];
            if (c >= ' ') continue;
            xMLString.ch[i] = 32;
        }
    }

    protected void normalizeWhitespace(XMLString xMLString, int n) {
        int n2 = xMLString.offset + xMLString.length;
        for (int i = xMLString.offset + n; i < n2; ++i) {
            char c = xMLString.ch[i];
            if (c >= ' ') continue;
            xMLString.ch[i] = 32;
        }
    }

    protected int isUnchangedByNormalization(XMLString xMLString) {
        int n = xMLString.offset + xMLString.length;
        for (int i = xMLString.offset; i < n; ++i) {
            char c = xMLString.ch[i];
            if (c >= ' ') continue;
            return i - xMLString.offset;
        }
        return -1;
    }

    public void startEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        ++this.fEntityDepth;
        this.fEntityScanner = this.fEntityManager.getEntityScanner();
    }

    public void endEntity(String string, Augmentations augmentations) throws XNIException {
        --this.fEntityDepth;
    }

    protected int scanCharReferenceValue(XMLStringBuffer xMLStringBuffer, XMLStringBuffer xMLStringBuffer2) throws IOException, XNIException {
        int n;
        int n2;
        boolean bl = false;
        if (this.fEntityScanner.skipChar(120)) {
            if (xMLStringBuffer2 != null) {
                xMLStringBuffer2.append('x');
            }
            bl = true;
            this.fStringBuffer3.clear();
            n2 = 1;
            n = this.fEntityScanner.peekChar();
            int n3 = n2 = n >= 48 && n <= 57 || n >= 97 && n <= 102 || n >= 65 && n <= 70 ? 1 : 0;
            if (n2 != 0) {
                if (xMLStringBuffer2 != null) {
                    xMLStringBuffer2.append((char)n);
                }
                this.fEntityScanner.scanChar();
                this.fStringBuffer3.append((char)n);
                do {
                    int n4 = n2 = (n = this.fEntityScanner.peekChar()) >= 48 && n <= 57 || n >= 97 && n <= 102 || n >= 65 && n <= 70 ? 1 : 0;
                    if (n2 == 0) continue;
                    if (xMLStringBuffer2 != null) {
                        xMLStringBuffer2.append((char)n);
                    }
                    this.fEntityScanner.scanChar();
                    this.fStringBuffer3.append((char)n);
                } while (n2 != 0);
            } else {
                this.reportFatalError("HexdigitRequiredInCharRef", null);
            }
        } else {
            this.fStringBuffer3.clear();
            n2 = 1;
            n = this.fEntityScanner.peekChar();
            int n5 = n2 = n >= 48 && n <= 57 ? 1 : 0;
            if (n2 != 0) {
                if (xMLStringBuffer2 != null) {
                    xMLStringBuffer2.append((char)n);
                }
                this.fEntityScanner.scanChar();
                this.fStringBuffer3.append((char)n);
                do {
                    int n6 = n2 = (n = this.fEntityScanner.peekChar()) >= 48 && n <= 57 ? 1 : 0;
                    if (n2 == 0) continue;
                    if (xMLStringBuffer2 != null) {
                        xMLStringBuffer2.append((char)n);
                    }
                    this.fEntityScanner.scanChar();
                    this.fStringBuffer3.append((char)n);
                } while (n2 != 0);
            } else {
                this.reportFatalError("DigitRequiredInCharRef", null);
            }
        }
        if (!this.fEntityScanner.skipChar(59)) {
            this.reportFatalError("SemicolonRequiredInCharRef", null);
        }
        if (xMLStringBuffer2 != null) {
            xMLStringBuffer2.append(';');
        }
        n2 = -1;
        try {
            n2 = Integer.parseInt(this.fStringBuffer3.toString(), bl ? 16 : 10);
            if (this.isInvalid(n2)) {
                StringBuffer stringBuffer = new StringBuffer(this.fStringBuffer3.length + 1);
                if (bl) {
                    stringBuffer.append('x');
                }
                stringBuffer.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
                this.reportFatalError("InvalidCharRef", new Object[]{stringBuffer.toString()});
            }
        }
        catch (NumberFormatException numberFormatException) {
            StringBuffer stringBuffer = new StringBuffer(this.fStringBuffer3.length + 1);
            if (bl) {
                stringBuffer.append('x');
            }
            stringBuffer.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
            this.reportFatalError("InvalidCharRef", new Object[]{stringBuffer.toString()});
        }
        if (!XMLChar.isSupplemental(n2)) {
            xMLStringBuffer.append((char)n2);
        } else {
            xMLStringBuffer.append(XMLChar.highSurrogate(n2));
            xMLStringBuffer.append(XMLChar.lowSurrogate(n2));
        }
        if (this.fNotifyCharRefs && n2 != -1) {
            String string = "#" + (bl ? "x" : "") + this.fStringBuffer3.toString();
            if (!this.fScanningAttribute) {
                this.fCharRefLiteral = string;
            }
        }
        return n2;
    }

    protected boolean isInvalid(int n) {
        return XMLChar.isInvalid(n);
    }

    protected boolean isInvalidLiteral(int n) {
        return XMLChar.isInvalid(n);
    }

    protected boolean isValidNameChar(int n) {
        return XMLChar.isName(n);
    }

    protected boolean isValidNameStartChar(int n) {
        return XMLChar.isNameStart(n);
    }

    protected boolean isValidNCName(int n) {
        return XMLChar.isNCName(n);
    }

    protected boolean isValidNameStartHighSurrogate(int n) {
        return false;
    }

    protected boolean versionSupported(String string) {
        return string.equals("1.0");
    }

    protected String getVersionNotSupportedKey() {
        return "VersionNotSupported";
    }

    protected boolean scanSurrogates(XMLStringBuffer xMLStringBuffer) throws IOException, XNIException {
        int n = this.fEntityScanner.scanChar();
        int n2 = this.fEntityScanner.peekChar();
        if (!XMLChar.isLowSurrogate(n2)) {
            this.reportFatalError("InvalidCharInContent", new Object[]{Integer.toString(n, 16)});
            return false;
        }
        this.fEntityScanner.scanChar();
        int n3 = XMLChar.supplemental((char)n, (char)n2);
        if (this.isInvalid(n3)) {
            this.reportFatalError("InvalidCharInContent", new Object[]{Integer.toString(n3, 16)});
            return false;
        }
        xMLStringBuffer.append((char)n);
        xMLStringBuffer.append((char)n2);
        return true;
    }

    protected void reportFatalError(String string, Object[] objectArray) throws XNIException {
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", string, objectArray, (short)2);
    }

    private void init() {
        this.fEntityScanner = null;
        this.fEntityDepth = 0;
        this.fReportEntity = true;
        this.fResourceIdentifier.clear();
    }
}

