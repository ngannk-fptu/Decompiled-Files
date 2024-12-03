/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xpointer;

import java.util.HashMap;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xpointer.ShortHandPointer;
import org.apache.xerces.xpointer.XPointerErrorHandler;
import org.apache.xerces.xpointer.XPointerMessageFormatter;
import org.apache.xerces.xpointer.XPointerPart;

final class ElementSchemePointer
implements XPointerPart {
    private String fSchemeName;
    private String fSchemeData;
    private String fShortHandPointerName;
    private boolean fIsResolveElement = false;
    private boolean fIsElementFound = false;
    private boolean fWasOnlyEmptyElementFound = false;
    boolean fIsShortHand = false;
    int fFoundDepth = 0;
    private int[] fChildSequence;
    private int fCurrentChildPosition = 1;
    private int fCurrentChildDepth = 0;
    private int[] fCurrentChildSequence;
    private boolean fIsFragmentResolved = false;
    private ShortHandPointer fShortHandPointer;
    protected XMLErrorReporter fErrorReporter;
    protected XMLErrorHandler fErrorHandler;
    private SymbolTable fSymbolTable;

    public ElementSchemePointer() {
    }

    public ElementSchemePointer(SymbolTable symbolTable) {
        this.fSymbolTable = symbolTable;
    }

    public ElementSchemePointer(SymbolTable symbolTable, XMLErrorReporter xMLErrorReporter) {
        this.fSymbolTable = symbolTable;
        this.fErrorReporter = xMLErrorReporter;
    }

    @Override
    public void parseXPointer(String string) throws XNIException {
        this.init();
        Tokens tokens = new Tokens(this.fSymbolTable);
        Scanner scanner = new Scanner(this.fSymbolTable){

            @Override
            protected void addToken(Tokens tokens, int n) throws XNIException {
                if (n == 1 || n == 0) {
                    super.addToken(tokens, n);
                    return;
                }
                ElementSchemePointer.this.reportError("InvalidElementSchemeToken", new Object[]{tokens.getTokenString(n)});
            }
        };
        int n = string.length();
        boolean bl = scanner.scanExpr(this.fSymbolTable, tokens, string, 0, n);
        if (!bl) {
            this.reportError("InvalidElementSchemeXPointer", new Object[]{string});
        }
        int[] nArray = new int[tokens.getTokenCount() / 2 + 1];
        int n2 = 0;
        block4: while (tokens.hasMore()) {
            int n3 = tokens.nextToken();
            switch (n3) {
                case 0: {
                    n3 = tokens.nextToken();
                    this.fShortHandPointerName = tokens.getTokenString(n3);
                    this.fShortHandPointer = new ShortHandPointer(this.fSymbolTable);
                    this.fShortHandPointer.setSchemeName(this.fShortHandPointerName);
                    continue block4;
                }
                case 1: {
                    nArray[n2] = tokens.nextToken();
                    ++n2;
                    continue block4;
                }
            }
            this.reportError("InvalidElementSchemeXPointer", new Object[]{string});
        }
        this.fChildSequence = new int[n2];
        this.fCurrentChildSequence = new int[n2];
        System.arraycopy(nArray, 0, this.fChildSequence, 0, n2);
    }

    @Override
    public String getSchemeName() {
        return this.fSchemeName;
    }

    @Override
    public String getSchemeData() {
        return this.fSchemeData;
    }

    @Override
    public void setSchemeName(String string) {
        this.fSchemeName = string;
    }

    @Override
    public void setSchemeData(String string) {
        this.fSchemeData = string;
    }

    @Override
    public boolean resolveXPointer(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations, int n) throws XNIException {
        boolean bl = false;
        if (this.fShortHandPointerName != null) {
            bl = this.fShortHandPointer.resolveXPointer(qName, xMLAttributes, augmentations, n);
            if (bl) {
                this.fIsResolveElement = true;
                this.fIsShortHand = true;
            } else {
                this.fIsResolveElement = false;
            }
        } else {
            this.fIsResolveElement = true;
        }
        this.fIsFragmentResolved = this.fChildSequence.length > 0 ? this.matchChildSequence(qName, n) : (bl && this.fChildSequence.length <= 0 ? bl : false);
        return this.fIsFragmentResolved;
    }

    protected boolean matchChildSequence(QName qName, int n) throws XNIException {
        if (this.fCurrentChildDepth >= this.fCurrentChildSequence.length) {
            int[] nArray = new int[this.fCurrentChildSequence.length];
            System.arraycopy(this.fCurrentChildSequence, 0, nArray, 0, this.fCurrentChildSequence.length);
            this.fCurrentChildSequence = new int[this.fCurrentChildDepth * 2];
            System.arraycopy(nArray, 0, this.fCurrentChildSequence, 0, nArray.length);
        }
        if (this.fIsResolveElement) {
            if (n == 0) {
                this.fCurrentChildSequence[this.fCurrentChildDepth] = this.fCurrentChildPosition;
                ++this.fCurrentChildDepth;
                this.fCurrentChildPosition = 1;
                if (this.fCurrentChildDepth <= this.fFoundDepth || this.fFoundDepth == 0) {
                    if (this.checkMatch()) {
                        this.fIsElementFound = true;
                        this.fFoundDepth = this.fCurrentChildDepth;
                    } else {
                        this.fIsElementFound = false;
                        this.fFoundDepth = 0;
                    }
                }
            } else if (n == 1) {
                if (this.fCurrentChildDepth == this.fFoundDepth) {
                    this.fIsElementFound = true;
                } else if (this.fCurrentChildDepth < this.fFoundDepth && this.fFoundDepth != 0 || this.fCurrentChildDepth > this.fFoundDepth && this.fFoundDepth == 0) {
                    this.fIsElementFound = false;
                }
                this.fCurrentChildSequence[this.fCurrentChildDepth] = 0;
                --this.fCurrentChildDepth;
                this.fCurrentChildPosition = this.fCurrentChildSequence[this.fCurrentChildDepth] + 1;
            } else if (n == 2) {
                this.fCurrentChildSequence[this.fCurrentChildDepth] = this.fCurrentChildPosition++;
                if (this.checkMatch()) {
                    this.fWasOnlyEmptyElementFound = !this.fIsElementFound;
                    this.fIsElementFound = true;
                } else {
                    this.fIsElementFound = false;
                    this.fWasOnlyEmptyElementFound = false;
                }
            }
        }
        return this.fIsElementFound;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected boolean checkMatch() {
        if (!this.fIsShortHand) {
            if (this.fChildSequence.length > this.fCurrentChildDepth + 1) return false;
            for (int i = 0; i < this.fChildSequence.length; ++i) {
                if (this.fChildSequence[i] == this.fCurrentChildSequence[i]) continue;
                return false;
            }
            return true;
        } else {
            if (this.fChildSequence.length > this.fCurrentChildDepth + 1) return false;
            for (int i = 0; i < this.fChildSequence.length; ++i) {
                if (this.fCurrentChildSequence.length < i + 2) {
                    return false;
                }
                if (this.fChildSequence[i] == this.fCurrentChildSequence[i + 1]) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isFragmentResolved() throws XNIException {
        return this.fIsFragmentResolved;
    }

    @Override
    public boolean isChildFragmentResolved() {
        if (this.fIsShortHand && this.fShortHandPointer != null && this.fChildSequence.length <= 0) {
            return this.fShortHandPointer.isChildFragmentResolved();
        }
        return this.fWasOnlyEmptyElementFound ? !this.fWasOnlyEmptyElementFound : this.fIsFragmentResolved && this.fCurrentChildDepth >= this.fFoundDepth;
    }

    protected void reportError(String string, Object[] objectArray) throws XNIException {
        throw new XNIException(this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/XPTR").formatMessage(this.fErrorReporter.getLocale(), string, objectArray));
    }

    protected void initErrorReporter() {
        if (this.fErrorReporter == null) {
            this.fErrorReporter = new XMLErrorReporter();
        }
        if (this.fErrorHandler == null) {
            this.fErrorHandler = new XPointerErrorHandler();
        }
        this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/XPTR", new XPointerMessageFormatter());
    }

    protected void init() {
        this.fSchemeName = null;
        this.fSchemeData = null;
        this.fShortHandPointerName = null;
        this.fIsResolveElement = false;
        this.fIsElementFound = false;
        this.fWasOnlyEmptyElementFound = false;
        this.fFoundDepth = 0;
        this.fCurrentChildPosition = 1;
        this.fCurrentChildDepth = 0;
        this.fIsFragmentResolved = false;
        this.fShortHandPointer = null;
        this.initErrorReporter();
    }

    private class Scanner {
        private static final byte CHARTYPE_INVALID = 0;
        private static final byte CHARTYPE_OTHER = 1;
        private static final byte CHARTYPE_MINUS = 2;
        private static final byte CHARTYPE_PERIOD = 3;
        private static final byte CHARTYPE_SLASH = 4;
        private static final byte CHARTYPE_DIGIT = 5;
        private static final byte CHARTYPE_LETTER = 6;
        private static final byte CHARTYPE_UNDERSCORE = 7;
        private static final byte CHARTYPE_NONASCII = 8;
        private final byte[] fASCIICharMap = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 1, 1, 1, 1, 1, 1, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 1, 1, 1, 1, 7, 1, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 1, 1, 1, 1, 1};
        private SymbolTable fSymbolTable;

        private Scanner(SymbolTable symbolTable) {
            this.fSymbolTable = symbolTable;
        }

        private boolean scanExpr(SymbolTable symbolTable, Tokens tokens, String string, int n, int n2) throws XNIException {
            String string2 = null;
            while (n != n2) {
                int n3 = string.charAt(n);
                int n4 = n3 >= 128 ? 8 : this.fASCIICharMap[n3];
                switch (n4) {
                    case 4: {
                        if (++n == n2) {
                            return false;
                        }
                        this.addToken(tokens, 1);
                        n3 = string.charAt(n);
                        int n5 = 0;
                        while (n3 >= 48 && n3 <= 57) {
                            n5 = n5 * 10 + (n3 - 48);
                            if (++n == n2) break;
                            n3 = string.charAt(n);
                        }
                        if (n5 == 0) {
                            ElementSchemePointer.this.reportError("InvalidChildSequenceCharacter", new Object[]{new Character((char)n3)});
                            return false;
                        }
                        tokens.addToken(n5);
                        break;
                    }
                    case 1: 
                    case 2: 
                    case 3: 
                    case 5: 
                    case 6: 
                    case 7: 
                    case 8: {
                        int n6 = n;
                        n = this.scanNCName(string, n2, n);
                        if (n == n6) {
                            ElementSchemePointer.this.reportError("InvalidNCNameInElementSchemeData", new Object[]{string});
                            return false;
                        }
                        n3 = n < n2 ? (int)string.charAt(n) : -1;
                        string2 = symbolTable.addSymbol(string.substring(n6, n));
                        this.addToken(tokens, 0);
                        tokens.addToken(string2);
                    }
                }
            }
            return true;
        }

        private int scanNCName(String string, int n, int n2) {
            byte by;
            char c = string.charAt(n2);
            if (c >= '\u0080' ? !XMLChar.isNameStart(c) : (by = this.fASCIICharMap[c]) != 6 && by != 7) {
                return n2;
            }
            while (++n2 < n && !((c = string.charAt(n2)) >= '\u0080' ? !XMLChar.isName(c) : (by = this.fASCIICharMap[c]) != 6 && by != 5 && by != 3 && by != 2 && by != 7)) {
            }
            return n2;
        }

        protected void addToken(Tokens tokens, int n) throws XNIException {
            tokens.addToken(n);
        }
    }

    private final class Tokens {
        private static final int XPTRTOKEN_ELEM_NCNAME = 0;
        private static final int XPTRTOKEN_ELEM_CHILD = 1;
        private final String[] fgTokenNames = new String[]{"XPTRTOKEN_ELEM_NCNAME", "XPTRTOKEN_ELEM_CHILD"};
        private static final int INITIAL_TOKEN_COUNT = 256;
        private int[] fTokens = new int[256];
        private int fTokenCount = 0;
        private int fCurrentTokenIndex;
        private SymbolTable fSymbolTable;
        private HashMap fTokenNames = new HashMap();

        private Tokens(SymbolTable symbolTable) {
            this.fSymbolTable = symbolTable;
            this.fTokenNames.put(new Integer(0), "XPTRTOKEN_ELEM_NCNAME");
            this.fTokenNames.put(new Integer(1), "XPTRTOKEN_ELEM_CHILD");
        }

        private String getTokenString(int n) {
            return (String)this.fTokenNames.get(new Integer(n));
        }

        private Integer getToken(int n) {
            return (Integer)this.fTokenNames.get(new Integer(n));
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
                ElementSchemePointer.this.reportError("XPointerElementSchemeProcessingError", null);
            }
            return this.fTokens[this.fCurrentTokenIndex++];
        }

        private int peekToken() throws XNIException {
            if (this.fCurrentTokenIndex == this.fTokenCount) {
                ElementSchemePointer.this.reportError("XPointerElementSchemeProcessingError", null);
            }
            return this.fTokens[this.fCurrentTokenIndex];
        }

        private String nextTokenAsString() throws XNIException {
            String string = this.getTokenString(this.nextToken());
            if (string == null) {
                ElementSchemePointer.this.reportError("XPointerElementSchemeProcessingError", null);
            }
            return string;
        }

        private int getTokenCount() {
            return this.fTokenCount;
        }
    }
}

