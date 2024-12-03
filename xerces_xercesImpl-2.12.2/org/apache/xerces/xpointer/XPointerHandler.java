/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xpointer;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xinclude.XIncludeHandler;
import org.apache.xerces.xinclude.XIncludeNamespaceSupport;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xpointer.ElementSchemePointer;
import org.apache.xerces.xpointer.ShortHandPointer;
import org.apache.xerces.xpointer.XPointerErrorHandler;
import org.apache.xerces.xpointer.XPointerMessageFormatter;
import org.apache.xerces.xpointer.XPointerPart;
import org.apache.xerces.xpointer.XPointerProcessor;

public final class XPointerHandler
extends XIncludeHandler
implements XPointerProcessor {
    protected ArrayList fXPointerParts = new ArrayList();
    protected XPointerPart fXPointerPart = null;
    protected boolean fFoundMatchingPtrPart = false;
    protected XMLErrorReporter fXPointerErrorReporter;
    protected XMLErrorHandler fErrorHandler;
    protected SymbolTable fSymbolTable = null;
    private final String ELEMENT_SCHEME_NAME = "element";
    protected boolean fIsXPointerResolved = false;
    protected boolean fFixupBase = false;
    protected boolean fFixupLang = false;

    public XPointerHandler() {
        this.fSymbolTable = new SymbolTable();
    }

    public XPointerHandler(SymbolTable symbolTable, XMLErrorHandler xMLErrorHandler, XMLErrorReporter xMLErrorReporter) {
        this.fSymbolTable = symbolTable;
        this.fErrorHandler = xMLErrorHandler;
        this.fXPointerErrorReporter = xMLErrorReporter;
    }

    @Override
    public void setDocumentHandler(XMLDocumentHandler xMLDocumentHandler) {
        this.fDocumentHandler = xMLDocumentHandler;
    }

    @Override
    public void parseXPointer(String string) throws XNIException {
        this.init();
        Tokens tokens = new Tokens(this.fSymbolTable);
        Scanner scanner = new Scanner(this.fSymbolTable){

            @Override
            protected void addToken(Tokens tokens, int n) throws XNIException {
                if (n == 0 || n == 1 || n == 3 || n == 4 || n == 2) {
                    super.addToken(tokens, n);
                    return;
                }
                XPointerHandler.this.reportError("InvalidXPointerToken", new Object[]{tokens.getTokenString(n)});
            }
        };
        int n = string.length();
        boolean bl = scanner.scanExpr(this.fSymbolTable, tokens, string, 0, n);
        if (!bl) {
            this.reportError("InvalidXPointerExpression", new Object[]{string});
        }
        while (tokens.hasMore()) {
            int n2 = tokens.nextToken();
            switch (n2) {
                case 2: {
                    n2 = tokens.nextToken();
                    String string2 = tokens.getTokenString(n2);
                    if (string2 == null) {
                        this.reportError("InvalidXPointerExpression", new Object[]{string});
                    }
                    Object object = new ShortHandPointer(this.fSymbolTable);
                    object.setSchemeName(string2);
                    this.fXPointerParts.add(object);
                    break;
                }
                case 3: {
                    n2 = tokens.nextToken();
                    String string2 = tokens.getTokenString(n2);
                    n2 = tokens.nextToken();
                    Object object = tokens.getTokenString(n2);
                    String string3 = string2 + (String)object;
                    int n3 = 0;
                    int n4 = 0;
                    n2 = tokens.nextToken();
                    String string4 = tokens.getTokenString(n2);
                    if (string4 != "XPTRTOKEN_OPEN_PAREN") {
                        if (n2 == 2) {
                            this.reportError("MultipleShortHandPointers", new Object[]{string});
                        } else {
                            this.reportError("InvalidXPointerExpression", new Object[]{string});
                        }
                    }
                    ++n3;
                    String string5 = null;
                    while (tokens.hasMore() && (string5 = tokens.getTokenString(n2 = tokens.nextToken())) == "XPTRTOKEN_OPEN_PAREN") {
                        ++n3;
                    }
                    n2 = tokens.nextToken();
                    string5 = tokens.getTokenString(n2);
                    n2 = tokens.nextToken();
                    String string6 = tokens.getTokenString(n2);
                    if (string6 != "XPTRTOKEN_CLOSE_PAREN") {
                        this.reportError("SchemeDataNotFollowedByCloseParenthesis", new Object[]{string});
                    }
                    ++n4;
                    while (tokens.hasMore() && tokens.getTokenString(tokens.peekToken()) == "XPTRTOKEN_OPEN_PAREN") {
                        ++n4;
                    }
                    if (n3 != n4) {
                        this.reportError("UnbalancedParenthesisInXPointerExpression", new Object[]{string, new Integer(n3), new Integer(n4)});
                    }
                    if (string3.equals("element")) {
                        ElementSchemePointer elementSchemePointer = new ElementSchemePointer(this.fSymbolTable, this.fErrorReporter);
                        elementSchemePointer.setSchemeName(string3);
                        elementSchemePointer.setSchemeData(string5);
                        try {
                            elementSchemePointer.parseXPointer(string5);
                            this.fXPointerParts.add(elementSchemePointer);
                            break;
                        }
                        catch (XNIException xNIException) {
                            throw new XNIException(xNIException);
                        }
                    }
                    this.reportWarning("SchemeUnsupported", new Object[]{string3});
                    break;
                }
                default: {
                    this.reportError("InvalidXPointerExpression", new Object[]{string});
                }
            }
        }
    }

    @Override
    public boolean resolveXPointer(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations, int n) throws XNIException {
        boolean bl = false;
        if (!this.fFoundMatchingPtrPart) {
            for (int i = 0; i < this.fXPointerParts.size(); ++i) {
                this.fXPointerPart = (XPointerPart)this.fXPointerParts.get(i);
                if (!this.fXPointerPart.resolveXPointer(qName, xMLAttributes, augmentations, n)) continue;
                this.fFoundMatchingPtrPart = true;
                bl = true;
            }
        } else if (this.fXPointerPart.resolveXPointer(qName, xMLAttributes, augmentations, n)) {
            bl = true;
        }
        if (!this.fIsXPointerResolved) {
            this.fIsXPointerResolved = bl;
        }
        return bl;
    }

    @Override
    public boolean isFragmentResolved() throws XNIException {
        boolean bl;
        boolean bl2 = bl = this.fXPointerPart != null ? this.fXPointerPart.isFragmentResolved() : false;
        if (!this.fIsXPointerResolved) {
            this.fIsXPointerResolved = bl;
        }
        return bl;
    }

    public boolean isChildFragmentResolved() throws XNIException {
        boolean bl = this.fXPointerPart != null ? this.fXPointerPart.isChildFragmentResolved() : false;
        return bl;
    }

    @Override
    public boolean isXPointerResolved() throws XNIException {
        return this.fIsXPointerResolved;
    }

    public XPointerPart getXPointerPart() {
        return this.fXPointerPart;
    }

    private void reportError(String string, Object[] objectArray) throws XNIException {
        throw new XNIException(this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/XPTR").formatMessage(this.fErrorReporter.getLocale(), string, objectArray));
    }

    private void reportWarning(String string, Object[] objectArray) throws XNIException {
        this.fXPointerErrorReporter.reportError("http://www.w3.org/TR/XPTR", string, objectArray, (short)0);
    }

    protected void initErrorReporter() {
        if (this.fXPointerErrorReporter == null) {
            this.fXPointerErrorReporter = new XMLErrorReporter();
        }
        if (this.fErrorHandler == null) {
            this.fErrorHandler = new XPointerErrorHandler();
        }
        this.fXPointerErrorReporter.putMessageFormatter("http://www.w3.org/TR/XPTR", new XPointerMessageFormatter());
    }

    protected void init() {
        this.fXPointerParts.clear();
        this.fXPointerPart = null;
        this.fFoundMatchingPtrPart = false;
        this.fIsXPointerResolved = false;
        this.initErrorReporter();
    }

    public ArrayList getPointerParts() {
        return this.fXPointerParts;
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.comment(xMLString, augmentations);
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.processingInstruction(string, xMLString, augmentations);
    }

    @Override
    public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        if (!this.resolveXPointer(qName, xMLAttributes, augmentations, 0)) {
            if (this.fFixupBase) {
                this.processXMLBaseAttributes(xMLAttributes);
            }
            if (this.fFixupLang) {
                this.processXMLLangAttributes(xMLAttributes);
            }
            this.fNamespaceContext.setContextInvalid();
            return;
        }
        super.startElement(qName, xMLAttributes, augmentations);
    }

    @Override
    public void emptyElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        if (!this.resolveXPointer(qName, xMLAttributes, augmentations, 2)) {
            if (this.fFixupBase) {
                this.processXMLBaseAttributes(xMLAttributes);
            }
            if (this.fFixupLang) {
                this.processXMLLangAttributes(xMLAttributes);
            }
            this.fNamespaceContext.setContextInvalid();
            return;
        }
        super.emptyElement(qName, xMLAttributes, augmentations);
    }

    @Override
    public void characters(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.characters(xMLString, augmentations);
    }

    @Override
    public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.ignorableWhitespace(xMLString, augmentations);
    }

    @Override
    public void endElement(QName qName, Augmentations augmentations) throws XNIException {
        if (!this.resolveXPointer(qName, null, augmentations, 1)) {
            return;
        }
        super.endElement(qName, augmentations);
    }

    @Override
    public void startCDATA(Augmentations augmentations) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.startCDATA(augmentations);
    }

    @Override
    public void endCDATA(Augmentations augmentations) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.endCDATA(augmentations);
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        if (string == "http://apache.org/xml/properties/internal/error-reporter") {
            this.fXPointerErrorReporter = object != null ? (XMLErrorReporter)object : null;
        }
        if (string == "http://apache.org/xml/properties/internal/error-handler") {
            this.fErrorHandler = object != null ? (XMLErrorHandler)object : null;
        }
        if (string == "http://apache.org/xml/features/xinclude/fixup-language") {
            this.fFixupLang = object != null ? (Boolean)object : false;
        }
        if (string == "http://apache.org/xml/features/xinclude/fixup-base-uris") {
            this.fFixupBase = object != null ? (Boolean)object : false;
        }
        if (string == "http://apache.org/xml/properties/internal/namespace-context") {
            this.fNamespaceContext = (XIncludeNamespaceSupport)object;
        }
        super.setProperty(string, object);
    }

    private class Scanner {
        private static final byte CHARTYPE_INVALID = 0;
        private static final byte CHARTYPE_OTHER = 1;
        private static final byte CHARTYPE_WHITESPACE = 2;
        private static final byte CHARTYPE_CARRET = 3;
        private static final byte CHARTYPE_OPEN_PAREN = 4;
        private static final byte CHARTYPE_CLOSE_PAREN = 5;
        private static final byte CHARTYPE_MINUS = 6;
        private static final byte CHARTYPE_PERIOD = 7;
        private static final byte CHARTYPE_SLASH = 8;
        private static final byte CHARTYPE_DIGIT = 9;
        private static final byte CHARTYPE_COLON = 10;
        private static final byte CHARTYPE_EQUAL = 11;
        private static final byte CHARTYPE_LETTER = 12;
        private static final byte CHARTYPE_UNDERSCORE = 13;
        private static final byte CHARTYPE_NONASCII = 14;
        private final byte[] fASCIICharMap = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, 1, 1, 1, 4, 5, 1, 1, 1, 6, 7, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 1, 1, 11, 1, 1, 1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 1, 1, 1, 3, 13, 1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 1, 1, 1, 1, 1};
        private SymbolTable fSymbolTable;

        private Scanner(SymbolTable symbolTable) {
            this.fSymbolTable = symbolTable;
        }

        private boolean scanExpr(SymbolTable symbolTable, Tokens tokens, String string, int n, int n2) throws XNIException {
            int n3 = 0;
            int n4 = 0;
            boolean bl = false;
            String string2 = null;
            String string3 = null;
            String string4 = null;
            StringBuffer stringBuffer = new StringBuffer();
            while (n != n2) {
                int n5 = string.charAt(n);
                while ((n5 == 32 || n5 == 10 || n5 == 9 || n5 == 13) && ++n != n2) {
                    n5 = string.charAt(n);
                }
                if (n == n2) break;
                int n6 = n5 >= 128 ? 14 : this.fASCIICharMap[n5];
                switch (n6) {
                    case 4: {
                        this.addToken(tokens, 0);
                        ++n3;
                        ++n;
                        break;
                    }
                    case 5: {
                        this.addToken(tokens, 1);
                        ++n4;
                        ++n;
                        break;
                    }
                    case 1: 
                    case 2: 
                    case 3: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 9: 
                    case 10: 
                    case 11: 
                    case 12: 
                    case 13: 
                    case 14: {
                        if (n3 == 0) {
                            int n7 = n;
                            if ((n = this.scanNCName(string, n2, n)) == n7) {
                                XPointerHandler.this.reportError("InvalidShortHandPointer", new Object[]{string});
                                return false;
                            }
                            n5 = n < n2 ? (int)string.charAt(n) : -1;
                            string2 = symbolTable.addSymbol(string.substring(n7, n));
                            string3 = XMLSymbols.EMPTY_STRING;
                            if (n5 == 58) {
                                if (++n == n2) {
                                    return false;
                                }
                                n5 = string.charAt(n);
                                string3 = string2;
                                n7 = n;
                                if ((n = this.scanNCName(string, n2, n)) == n7) {
                                    return false;
                                }
                                n5 = n < n2 ? (int)string.charAt(n) : -1;
                                bl = true;
                                string2 = symbolTable.addSymbol(string.substring(n7, n));
                            }
                            if (n != n2) {
                                this.addToken(tokens, 3);
                                tokens.addToken(string3);
                                tokens.addToken(string2);
                                bl = false;
                            } else if (n == n2) {
                                this.addToken(tokens, 2);
                                tokens.addToken(string2);
                                bl = false;
                            }
                            n4 = 0;
                            break;
                        }
                        if (n3 > 0 && n4 == 0 && string2 != null) {
                            int n8 = n;
                            if ((n = this.scanData(string, stringBuffer, n2, n)) == n8) {
                                XPointerHandler.this.reportError("InvalidSchemeDataInXPointer", new Object[]{string});
                                return false;
                            }
                            n5 = n < n2 ? (int)string.charAt(n) : -1;
                            string4 = symbolTable.addSymbol(stringBuffer.toString());
                            this.addToken(tokens, 4);
                            tokens.addToken(string4);
                            n3 = 0;
                            stringBuffer.delete(0, stringBuffer.length());
                            break;
                        }
                        return false;
                    }
                }
            }
            return true;
        }

        private int scanNCName(String string, int n, int n2) {
            byte by;
            char c = string.charAt(n2);
            if (c >= '\u0080' ? !XMLChar.isNameStart(c) : (by = this.fASCIICharMap[c]) != 12 && by != 13) {
                return n2;
            }
            while (++n2 < n && !((c = string.charAt(n2)) >= '\u0080' ? !XMLChar.isName(c) : (by = this.fASCIICharMap[c]) != 12 && by != 9 && by != 7 && by != 6 && by != 13)) {
            }
            return n2;
        }

        private int scanData(String string, StringBuffer stringBuffer, int n, int n2) {
            while (n2 != n) {
                int n3;
                char c = string.charAt(n2);
                int n4 = n3 = c >= '\u0080' ? 14 : this.fASCIICharMap[c];
                if (n3 == 4) {
                    stringBuffer.append((int)c);
                    ++n2;
                    n2 = this.scanData(string, stringBuffer, n, n2);
                    if (n2 == n) {
                        return n2;
                    }
                    c = string.charAt(n2);
                    int n5 = n3 = c >= '\u0080' ? 14 : this.fASCIICharMap[c];
                    if (n3 != 5) {
                        return n;
                    }
                    stringBuffer.append(c);
                    ++n2;
                    continue;
                }
                if (n3 == 5) {
                    return n2;
                }
                if (n3 == 3) {
                    int n6 = n3 = (c = string.charAt(++n2)) >= '\u0080' ? 14 : this.fASCIICharMap[c];
                    if (n3 != 3 && n3 != 4 && n3 != 5) break;
                    stringBuffer.append(c);
                    ++n2;
                    continue;
                }
                stringBuffer.append(c);
                ++n2;
            }
            return n2;
        }

        protected void addToken(Tokens tokens, int n) throws XNIException {
            tokens.addToken(n);
        }
    }

    private final class Tokens {
        private static final int XPTRTOKEN_OPEN_PAREN = 0;
        private static final int XPTRTOKEN_CLOSE_PAREN = 1;
        private static final int XPTRTOKEN_SHORTHAND = 2;
        private static final int XPTRTOKEN_SCHEMENAME = 3;
        private static final int XPTRTOKEN_SCHEMEDATA = 4;
        private final String[] fgTokenNames = new String[]{"XPTRTOKEN_OPEN_PAREN", "XPTRTOKEN_CLOSE_PAREN", "XPTRTOKEN_SHORTHAND", "XPTRTOKEN_SCHEMENAME", "XPTRTOKEN_SCHEMEDATA"};
        private static final int INITIAL_TOKEN_COUNT = 256;
        private int[] fTokens = new int[256];
        private int fTokenCount = 0;
        private int fCurrentTokenIndex;
        private SymbolTable fSymbolTable;
        private HashMap fTokenNames = new HashMap();

        private Tokens(SymbolTable symbolTable) {
            this.fSymbolTable = symbolTable;
            this.fTokenNames.put(new Integer(0), "XPTRTOKEN_OPEN_PAREN");
            this.fTokenNames.put(new Integer(1), "XPTRTOKEN_CLOSE_PAREN");
            this.fTokenNames.put(new Integer(2), "XPTRTOKEN_SHORTHAND");
            this.fTokenNames.put(new Integer(3), "XPTRTOKEN_SCHEMENAME");
            this.fTokenNames.put(new Integer(4), "XPTRTOKEN_SCHEMEDATA");
        }

        private String getTokenString(int n) {
            return (String)this.fTokenNames.get(new Integer(n));
        }

        private void addToken(String string) {
            Integer n = (Integer)this.fTokenNames.get(string);
            if (n == null) {
                n = new Integer(this.fTokenNames.size());
                this.fTokenNames.put(n, string);
            }
            this.addToken(n);
        }

        private void addToken(int n) {
            try {
                this.fTokens[this.fTokenCount] = n;
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                int[] nArray = this.fTokens;
                this.fTokens = new int[this.fTokenCount << 1];
                System.arraycopy(nArray, 0, this.fTokens, 0, this.fTokenCount);
                this.fTokens[this.fTokenCount] = n;
            }
            ++this.fTokenCount;
        }

        private void rewind() {
            this.fCurrentTokenIndex = 0;
        }

        private boolean hasMore() {
            return this.fCurrentTokenIndex < this.fTokenCount;
        }

        private int nextToken() throws XNIException {
            if (this.fCurrentTokenIndex == this.fTokenCount) {
                XPointerHandler.this.reportError("XPointerProcessingError", null);
            }
            return this.fTokens[this.fCurrentTokenIndex++];
        }

        private int peekToken() throws XNIException {
            if (this.fCurrentTokenIndex == this.fTokenCount) {
                XPointerHandler.this.reportError("XPointerProcessingError", null);
            }
            return this.fTokens[this.fCurrentTokenIndex];
        }

        private String nextTokenAsString() throws XNIException {
            String string = this.getTokenString(this.nextToken());
            if (string == null) {
                XPointerHandler.this.reportError("XPointerProcessingError", null);
            }
            return string;
        }
    }
}

