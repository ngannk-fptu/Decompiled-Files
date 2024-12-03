/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xpath.regex;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import org.apache.xerces.impl.xpath.regex.CaseInsensitiveMap;
import org.apache.xerces.impl.xpath.regex.ParseException;
import org.apache.xerces.impl.xpath.regex.REUtil;
import org.apache.xerces.impl.xpath.regex.RangeToken;
import org.apache.xerces.impl.xpath.regex.Token;

class RegexParser {
    static final int T_CHAR = 0;
    static final int T_EOF = 1;
    static final int T_OR = 2;
    static final int T_STAR = 3;
    static final int T_PLUS = 4;
    static final int T_QUESTION = 5;
    static final int T_LPAREN = 6;
    static final int T_RPAREN = 7;
    static final int T_DOT = 8;
    static final int T_LBRACKET = 9;
    static final int T_BACKSOLIDUS = 10;
    static final int T_CARET = 11;
    static final int T_DOLLAR = 12;
    static final int T_LPAREN2 = 13;
    static final int T_LOOKAHEAD = 14;
    static final int T_NEGATIVELOOKAHEAD = 15;
    static final int T_LOOKBEHIND = 16;
    static final int T_NEGATIVELOOKBEHIND = 17;
    static final int T_INDEPENDENT = 18;
    static final int T_SET_OPERATIONS = 19;
    static final int T_POSIX_CHARCLASS_START = 20;
    static final int T_COMMENT = 21;
    static final int T_MODIFIERS = 22;
    static final int T_CONDITION = 23;
    static final int T_XMLSCHEMA_CC_SUBTRACTION = 24;
    int offset;
    String regex;
    int regexlen;
    int options;
    ResourceBundle resources;
    int chardata;
    int nexttoken;
    protected static final int S_NORMAL = 0;
    protected static final int S_INBRACKETS = 1;
    protected static final int S_INXBRACKETS = 2;
    int context = 0;
    int parenOpened = 1;
    int parennumber = 1;
    boolean hasBackReferences;
    Vector references = null;

    public RegexParser() {
        this.setLocale(Locale.getDefault());
    }

    public RegexParser(Locale locale) {
        this.setLocale(locale);
    }

    public void setLocale(Locale locale) {
        try {
            this.resources = locale != null ? ResourceBundle.getBundle("org.apache.xerces.impl.xpath.regex.message", locale) : ResourceBundle.getBundle("org.apache.xerces.impl.xpath.regex.message");
        }
        catch (MissingResourceException missingResourceException) {
            throw new RuntimeException("Installation Problem???  Couldn't load messages: " + missingResourceException.getMessage());
        }
    }

    final ParseException ex(String string, int n) {
        return new ParseException(this.resources.getString(string), n);
    }

    protected final boolean isSet(int n) {
        return (this.options & n) == n;
    }

    synchronized Token parse(String string, int n) throws ParseException {
        this.options = n;
        this.offset = 0;
        this.setContext(0);
        this.parennumber = 1;
        this.parenOpened = 1;
        this.hasBackReferences = false;
        this.regex = string;
        if (this.isSet(16)) {
            this.regex = REUtil.stripExtendedComment(this.regex);
        }
        this.regexlen = this.regex.length();
        this.next();
        Token token = this.parseRegex();
        if (this.offset != this.regexlen) {
            throw this.ex("parser.parse.1", this.offset);
        }
        if (this.read() != 1) {
            throw this.ex("parser.parse.1", this.offset - 1);
        }
        if (this.references != null) {
            for (int i = 0; i < this.references.size(); ++i) {
                ReferencePosition referencePosition = (ReferencePosition)this.references.elementAt(i);
                if (this.parennumber > referencePosition.refNumber) continue;
                throw this.ex("parser.parse.2", referencePosition.position);
            }
            this.references.removeAllElements();
        }
        return token;
    }

    protected final void setContext(int n) {
        this.context = n;
    }

    final int read() {
        return this.nexttoken;
    }

    final void next() {
        int n;
        if (this.offset >= this.regexlen) {
            this.chardata = -1;
            this.nexttoken = 1;
            return;
        }
        char c = this.regex.charAt(this.offset++);
        this.chardata = c;
        if (this.context == 1) {
            int n2;
            switch (c) {
                case '\\': {
                    n2 = 10;
                    if (this.offset >= this.regexlen) {
                        throw this.ex("parser.next.1", this.offset - 1);
                    }
                    this.chardata = this.regex.charAt(this.offset++);
                    break;
                }
                case '-': {
                    if (this.offset < this.regexlen && this.regex.charAt(this.offset) == '[') {
                        ++this.offset;
                        n2 = 24;
                        break;
                    }
                    n2 = 0;
                    break;
                }
                case '[': {
                    if (!this.isSet(512) && this.offset < this.regexlen && this.regex.charAt(this.offset) == ':') {
                        ++this.offset;
                        n2 = 20;
                        break;
                    }
                }
                default: {
                    char c2;
                    if (REUtil.isHighSurrogate(c) && this.offset < this.regexlen && REUtil.isLowSurrogate(c2 = this.regex.charAt(this.offset))) {
                        this.chardata = REUtil.composeFromSurrogates(c, c2);
                        ++this.offset;
                    }
                    n2 = 0;
                }
            }
            this.nexttoken = n2;
            return;
        }
        block5 : switch (c) {
            case '|': {
                n = 2;
                break;
            }
            case '*': {
                n = 3;
                break;
            }
            case '+': {
                n = 4;
                break;
            }
            case '?': {
                n = 5;
                break;
            }
            case ')': {
                n = 7;
                break;
            }
            case '.': {
                n = 8;
                break;
            }
            case '[': {
                n = 9;
                break;
            }
            case '^': {
                if (this.isSet(512)) {
                    n = 0;
                    break;
                }
                n = 11;
                break;
            }
            case '$': {
                if (this.isSet(512)) {
                    n = 0;
                    break;
                }
                n = 12;
                break;
            }
            case '(': {
                n = 6;
                if (this.offset >= this.regexlen || this.regex.charAt(this.offset) != '?') break;
                if (++this.offset >= this.regexlen) {
                    throw this.ex("parser.next.2", this.offset - 1);
                }
                c = this.regex.charAt(this.offset++);
                switch (c) {
                    case ':': {
                        n = 13;
                        break block5;
                    }
                    case '=': {
                        n = 14;
                        break block5;
                    }
                    case '!': {
                        n = 15;
                        break block5;
                    }
                    case '[': {
                        n = 19;
                        break block5;
                    }
                    case '>': {
                        n = 18;
                        break block5;
                    }
                    case '<': {
                        if (this.offset >= this.regexlen) {
                            throw this.ex("parser.next.2", this.offset - 3);
                        }
                        if ((c = this.regex.charAt(this.offset++)) == '=') {
                            n = 16;
                            break block5;
                        }
                        if (c == '!') {
                            n = 17;
                            break block5;
                        }
                        throw this.ex("parser.next.3", this.offset - 3);
                    }
                    case '#': {
                        while (this.offset < this.regexlen && (c = this.regex.charAt(this.offset++)) != ')') {
                        }
                        if (c != ')') {
                            throw this.ex("parser.next.4", this.offset - 1);
                        }
                        n = 21;
                        break block5;
                    }
                }
                if (c == '-' || 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z') {
                    --this.offset;
                    n = 22;
                    break;
                }
                if (c == '(') {
                    n = 23;
                    break;
                }
                throw this.ex("parser.next.2", this.offset - 2);
            }
            case '\\': {
                n = 10;
                if (this.offset >= this.regexlen) {
                    throw this.ex("parser.next.1", this.offset - 1);
                }
                this.chardata = this.regex.charAt(this.offset++);
                break;
            }
            default: {
                n = 0;
            }
        }
        this.nexttoken = n;
    }

    Token parseRegex() throws ParseException {
        Token token = this.parseTerm();
        Token.UnionToken unionToken = null;
        while (this.read() == 2) {
            this.next();
            if (unionToken == null) {
                unionToken = Token.createUnion();
                ((Token)unionToken).addChild(token);
                token = unionToken;
            }
            token.addChild(this.parseTerm());
        }
        return token;
    }

    Token parseTerm() throws ParseException {
        int n = this.read();
        if (n == 2 || n == 7 || n == 1) {
            return Token.createEmpty();
        }
        Token token = this.parseFactor();
        Token.UnionToken unionToken = null;
        while ((n = this.read()) != 2 && n != 7 && n != 1) {
            if (unionToken == null) {
                unionToken = Token.createConcat();
                ((Token)unionToken).addChild(token);
                token = unionToken;
            }
            ((Token)unionToken).addChild(this.parseFactor());
        }
        return token;
    }

    Token processCaret() throws ParseException {
        this.next();
        return Token.token_linebeginning;
    }

    Token processDollar() throws ParseException {
        this.next();
        return Token.token_lineend;
    }

    Token processLookahead() throws ParseException {
        this.next();
        Token.ParenToken parenToken = Token.createLook(20, this.parseRegex());
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return parenToken;
    }

    Token processNegativelookahead() throws ParseException {
        this.next();
        Token.ParenToken parenToken = Token.createLook(21, this.parseRegex());
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return parenToken;
    }

    Token processLookbehind() throws ParseException {
        this.next();
        Token.ParenToken parenToken = Token.createLook(22, this.parseRegex());
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return parenToken;
    }

    Token processNegativelookbehind() throws ParseException {
        this.next();
        Token.ParenToken parenToken = Token.createLook(23, this.parseRegex());
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return parenToken;
    }

    Token processBacksolidus_A() throws ParseException {
        this.next();
        return Token.token_stringbeginning;
    }

    Token processBacksolidus_Z() throws ParseException {
        this.next();
        return Token.token_stringend2;
    }

    Token processBacksolidus_z() throws ParseException {
        this.next();
        return Token.token_stringend;
    }

    Token processBacksolidus_b() throws ParseException {
        this.next();
        return Token.token_wordedge;
    }

    Token processBacksolidus_B() throws ParseException {
        this.next();
        return Token.token_not_wordedge;
    }

    Token processBacksolidus_lt() throws ParseException {
        this.next();
        return Token.token_wordbeginning;
    }

    Token processBacksolidus_gt() throws ParseException {
        this.next();
        return Token.token_wordend;
    }

    Token processStar(Token token) throws ParseException {
        this.next();
        if (this.read() == 5) {
            this.next();
            return Token.createNGClosure(token);
        }
        return Token.createClosure(token);
    }

    Token processPlus(Token token) throws ParseException {
        this.next();
        if (this.read() == 5) {
            this.next();
            return Token.createConcat(token, Token.createNGClosure(token));
        }
        return Token.createConcat(token, Token.createClosure(token));
    }

    Token processQuestion(Token token) throws ParseException {
        this.next();
        Token.UnionToken unionToken = Token.createUnion();
        if (this.read() == 5) {
            this.next();
            ((Token)unionToken).addChild(Token.createEmpty());
            ((Token)unionToken).addChild(token);
        } else {
            ((Token)unionToken).addChild(token);
            ((Token)unionToken).addChild(Token.createEmpty());
        }
        return unionToken;
    }

    boolean checkQuestion(int n) {
        return n < this.regexlen && this.regex.charAt(n) == '?';
    }

    Token processParen() throws ParseException {
        this.next();
        int n = this.parenOpened++;
        Token.ParenToken parenToken = Token.createParen(this.parseRegex(), n);
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        ++this.parennumber;
        this.next();
        return parenToken;
    }

    Token processParen2() throws ParseException {
        this.next();
        Token.ParenToken parenToken = Token.createParen(this.parseRegex(), 0);
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return parenToken;
    }

    Token processCondition() throws ParseException {
        if (this.offset + 1 >= this.regexlen) {
            throw this.ex("parser.factor.4", this.offset);
        }
        int n = -1;
        Token token = null;
        char c = this.regex.charAt(this.offset);
        if ('1' <= c && c <= '9') {
            int n2 = n = c - 48;
            if (this.parennumber <= n) {
                throw this.ex("parser.parse.2", this.offset);
            }
            while (this.offset + 1 < this.regexlen && '0' <= (c = this.regex.charAt(this.offset + 1)) && c <= '9' && (n = n * 10 + (c - 48)) < this.parennumber) {
                n2 = n;
                ++this.offset;
            }
            this.hasBackReferences = true;
            if (this.references == null) {
                this.references = new Vector();
            }
            this.references.addElement(new ReferencePosition(n2, this.offset));
            ++this.offset;
            if (this.regex.charAt(this.offset) != ')') {
                throw this.ex("parser.factor.1", this.offset);
            }
            ++this.offset;
        } else {
            if (c == '?') {
                --this.offset;
            }
            this.next();
            token = this.parseFactor();
            switch (token.type) {
                case 20: 
                case 21: 
                case 22: 
                case 23: {
                    break;
                }
                case 8: {
                    if (this.read() == 7) break;
                    throw this.ex("parser.factor.1", this.offset - 1);
                }
                default: {
                    throw this.ex("parser.factor.5", this.offset);
                }
            }
        }
        this.next();
        Token token2 = this.parseRegex();
        Token token3 = null;
        if (token2.type == 2) {
            if (token2.size() != 2) {
                throw this.ex("parser.factor.6", this.offset);
            }
            token3 = token2.getChild(1);
            token2 = token2.getChild(0);
        }
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return Token.createCondition(n, token, token2, token3);
    }

    Token processModifiers() throws ParseException {
        Token.ModifierToken modifierToken;
        int n;
        int n2 = 0;
        int n3 = 0;
        int n4 = -1;
        while (this.offset < this.regexlen && (n = REUtil.getOptionValue(n4 = (int)this.regex.charAt(this.offset))) != 0) {
            n2 |= n;
            ++this.offset;
        }
        if (this.offset >= this.regexlen) {
            throw this.ex("parser.factor.2", this.offset - 1);
        }
        if (n4 == 45) {
            ++this.offset;
            while (this.offset < this.regexlen && (n = REUtil.getOptionValue(n4 = (int)this.regex.charAt(this.offset))) != 0) {
                n3 |= n;
                ++this.offset;
            }
            if (this.offset >= this.regexlen) {
                throw this.ex("parser.factor.2", this.offset - 1);
            }
        }
        if (n4 == 58) {
            ++this.offset;
            this.next();
            modifierToken = Token.createModifierGroup(this.parseRegex(), n2, n3);
            if (this.read() != 7) {
                throw this.ex("parser.factor.1", this.offset - 1);
            }
            this.next();
        } else if (n4 == 41) {
            ++this.offset;
            this.next();
            modifierToken = Token.createModifierGroup(this.parseRegex(), n2, n3);
        } else {
            throw this.ex("parser.factor.3", this.offset);
        }
        return modifierToken;
    }

    Token processIndependent() throws ParseException {
        this.next();
        Token.ParenToken parenToken = Token.createLook(24, this.parseRegex());
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return parenToken;
    }

    Token processBacksolidus_c() throws ParseException {
        char c;
        if (this.offset >= this.regexlen || ((c = this.regex.charAt(this.offset++)) & 0xFFE0) != 64) {
            throw this.ex("parser.atom.1", this.offset - 1);
        }
        this.next();
        return Token.createChar(c - 64);
    }

    Token processBacksolidus_C() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }

    Token processBacksolidus_i() throws ParseException {
        Token.CharToken charToken = Token.createChar(105);
        this.next();
        return charToken;
    }

    Token processBacksolidus_I() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }

    Token processBacksolidus_g() throws ParseException {
        this.next();
        return Token.getGraphemePattern();
    }

    Token processBacksolidus_X() throws ParseException {
        this.next();
        return Token.getCombiningCharacterSequence();
    }

    Token processBackreference() throws ParseException {
        char c;
        int n;
        int n2 = n = this.chardata - 48;
        if (this.parennumber <= n) {
            throw this.ex("parser.parse.2", this.offset - 2);
        }
        while (this.offset < this.regexlen && '0' <= (c = this.regex.charAt(this.offset)) && c <= '9' && (n = n * 10 + (c - 48)) < this.parennumber) {
            ++this.offset;
            n2 = n;
            this.chardata = c;
        }
        Token.StringToken stringToken = Token.createBackReference(n2);
        this.hasBackReferences = true;
        if (this.references == null) {
            this.references = new Vector();
        }
        this.references.addElement(new ReferencePosition(n2, this.offset - 2));
        this.next();
        return stringToken;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    Token parseFactor() throws ParseException {
        int n = this.read();
        switch (n) {
            case 11: {
                return this.processCaret();
            }
            case 12: {
                return this.processDollar();
            }
            case 14: {
                return this.processLookahead();
            }
            case 15: {
                return this.processNegativelookahead();
            }
            case 16: {
                return this.processLookbehind();
            }
            case 17: {
                return this.processNegativelookbehind();
            }
            case 21: {
                this.next();
                return Token.createEmpty();
            }
            case 10: {
                switch (this.chardata) {
                    case 65: {
                        return this.processBacksolidus_A();
                    }
                    case 90: {
                        return this.processBacksolidus_Z();
                    }
                    case 122: {
                        return this.processBacksolidus_z();
                    }
                    case 98: {
                        return this.processBacksolidus_b();
                    }
                    case 66: {
                        return this.processBacksolidus_B();
                    }
                    case 60: {
                        return this.processBacksolidus_lt();
                    }
                    case 62: {
                        return this.processBacksolidus_gt();
                    }
                }
            }
        }
        Token token = this.parseAtom();
        n = this.read();
        switch (n) {
            case 3: {
                return this.processStar(token);
            }
            case 4: {
                return this.processPlus(token);
            }
            case 5: {
                return this.processQuestion(token);
            }
            case 0: {
                if (this.chardata != 123 || this.offset >= this.regexlen) return token;
                int n2 = this.offset;
                int n3 = 0;
                int n4 = -1;
                char c = this.regex.charAt(n2++);
                n = c;
                if (c < '0' || n > 57) throw this.ex("parser.quantifier.1", this.offset);
                n3 = n - 48;
                while (n2 < this.regexlen) {
                    char c2 = this.regex.charAt(n2++);
                    n = c2;
                    if (c2 < '0' || n > 57) break;
                    if ((n3 = n3 * 10 + n - 48) >= 0) continue;
                    throw this.ex("parser.quantifier.5", this.offset);
                }
                n4 = n3;
                if (n == 44) {
                    if (n2 >= this.regexlen) {
                        throw this.ex("parser.quantifier.3", this.offset);
                    }
                    char c3 = this.regex.charAt(n2++);
                    n = c3;
                    if (c3 >= '0' && n <= 57) {
                        n4 = n - 48;
                        while (n2 < this.regexlen) {
                            char c4 = this.regex.charAt(n2++);
                            n = c4;
                            if (c4 < '0' || n > 57) break;
                            if ((n4 = n4 * 10 + n - 48) >= 0) continue;
                            throw this.ex("parser.quantifier.5", this.offset);
                        }
                        if (n3 > n4) {
                            throw this.ex("parser.quantifier.4", this.offset);
                        }
                    } else {
                        n4 = -1;
                    }
                }
                if (n != 125) {
                    throw this.ex("parser.quantifier.2", this.offset);
                }
                if (this.checkQuestion(n2)) {
                    token = Token.createNGClosure(token);
                    this.offset = n2 + 1;
                } else {
                    token = Token.createClosure(token);
                    this.offset = n2;
                }
                token.setMin(n3);
                token.setMax(n4);
                this.next();
            }
        }
        return token;
    }

    Token parseAtom() throws ParseException {
        int n = this.read();
        Token token = null;
        switch (n) {
            case 6: {
                return this.processParen();
            }
            case 13: {
                return this.processParen2();
            }
            case 23: {
                return this.processCondition();
            }
            case 22: {
                return this.processModifiers();
            }
            case 18: {
                return this.processIndependent();
            }
            case 8: {
                this.next();
                token = Token.token_dot;
                break;
            }
            case 9: {
                return this.parseCharacterClass(true);
            }
            case 19: {
                return this.parseSetOperations();
            }
            case 10: {
                switch (this.chardata) {
                    case 68: 
                    case 83: 
                    case 87: 
                    case 100: 
                    case 115: 
                    case 119: {
                        token = this.getTokenForShorthand(this.chardata);
                        this.next();
                        return token;
                    }
                    case 101: 
                    case 102: 
                    case 110: 
                    case 114: 
                    case 116: 
                    case 117: 
                    case 118: 
                    case 120: {
                        int n2 = this.decodeEscaped();
                        if (n2 < 65536) {
                            token = Token.createChar(n2);
                            break;
                        }
                        token = Token.createString(REUtil.decomposeToSurrogates(n2));
                        break;
                    }
                    case 99: {
                        return this.processBacksolidus_c();
                    }
                    case 67: {
                        return this.processBacksolidus_C();
                    }
                    case 105: {
                        return this.processBacksolidus_i();
                    }
                    case 73: {
                        return this.processBacksolidus_I();
                    }
                    case 103: {
                        return this.processBacksolidus_g();
                    }
                    case 88: {
                        return this.processBacksolidus_X();
                    }
                    case 49: 
                    case 50: 
                    case 51: 
                    case 52: 
                    case 53: 
                    case 54: 
                    case 55: 
                    case 56: 
                    case 57: {
                        return this.processBackreference();
                    }
                    case 80: 
                    case 112: {
                        int n3 = this.offset;
                        token = this.processBacksolidus_pP(this.chardata);
                        if (token != null) break;
                        throw this.ex("parser.atom.5", n3);
                    }
                    default: {
                        token = Token.createChar(this.chardata);
                    }
                }
                this.next();
                break;
            }
            case 0: {
                if (this.chardata == 93 || this.chardata == 123 || this.chardata == 125) {
                    throw this.ex("parser.atom.4", this.offset - 1);
                }
                token = Token.createChar(this.chardata);
                int n4 = this.chardata;
                this.next();
                if (!REUtil.isHighSurrogate(n4) || this.read() != 0 || !REUtil.isLowSurrogate(this.chardata)) break;
                char[] cArray = new char[]{(char)n4, (char)this.chardata};
                token = Token.createParen(Token.createString(new String(cArray)), 0);
                this.next();
                break;
            }
            default: {
                throw this.ex("parser.atom.4", this.offset - 1);
            }
        }
        return token;
    }

    protected RangeToken processBacksolidus_pP(int n) throws ParseException {
        this.next();
        if (this.read() != 0 || this.chardata != 123) {
            throw this.ex("parser.atom.2", this.offset - 1);
        }
        boolean bl = n == 112;
        int n2 = this.offset;
        int n3 = this.regex.indexOf(125, n2);
        if (n3 < 0) {
            throw this.ex("parser.atom.3", this.offset);
        }
        String string = this.regex.substring(n2, n3);
        this.offset = n3 + 1;
        return Token.getRange(string, bl, this.isSet(512));
    }

    int processCIinCharacterClass(RangeToken rangeToken, int n) {
        return this.decodeEscaped();
    }

    protected RangeToken parseCharacterClass(boolean bl) throws ParseException {
        int n;
        RangeToken rangeToken;
        this.setContext(1);
        this.next();
        boolean bl2 = false;
        RangeToken rangeToken2 = null;
        if (this.read() == 0 && this.chardata == 94) {
            bl2 = true;
            this.next();
            if (bl) {
                rangeToken = Token.createNRange();
            } else {
                rangeToken2 = Token.createRange();
                rangeToken2.addRange(0, 0x10FFFF);
                rangeToken = Token.createRange();
            }
        } else {
            rangeToken = Token.createRange();
        }
        boolean bl3 = true;
        while ((n = this.read()) != 1 && (n != 0 || this.chardata != 93 || bl3)) {
            int n2;
            boolean bl4;
            int n3;
            block41: {
                block40: {
                    n3 = this.chardata;
                    bl4 = false;
                    if (n != 10) break block40;
                    switch (n3) {
                        case 68: 
                        case 83: 
                        case 87: 
                        case 100: 
                        case 115: 
                        case 119: {
                            rangeToken.mergeRanges(this.getTokenForShorthand(n3));
                            bl4 = true;
                            break;
                        }
                        case 67: 
                        case 73: 
                        case 99: 
                        case 105: {
                            n3 = this.processCIinCharacterClass(rangeToken, n3);
                            if (n3 < 0) {
                                bl4 = true;
                                break;
                            }
                            break block41;
                        }
                        case 80: 
                        case 112: {
                            n2 = this.offset;
                            RangeToken rangeToken3 = this.processBacksolidus_pP(n3);
                            if (rangeToken3 == null) {
                                throw this.ex("parser.atom.5", n2);
                            }
                            rangeToken.mergeRanges(rangeToken3);
                            bl4 = true;
                            break;
                        }
                        default: {
                            n3 = this.decodeEscaped();
                            break;
                        }
                    }
                    break block41;
                }
                if (n == 20) {
                    String string;
                    RangeToken rangeToken4;
                    n2 = this.regex.indexOf(58, this.offset);
                    if (n2 < 0) {
                        throw this.ex("parser.cc.1", this.offset);
                    }
                    boolean bl5 = true;
                    if (this.regex.charAt(this.offset) == '^') {
                        ++this.offset;
                        bl5 = false;
                    }
                    if ((rangeToken4 = Token.getRange(string = this.regex.substring(this.offset, n2), bl5, this.isSet(512))) == null) {
                        throw this.ex("parser.cc.3", this.offset);
                    }
                    rangeToken.mergeRanges(rangeToken4);
                    bl4 = true;
                    if (n2 + 1 >= this.regexlen || this.regex.charAt(n2 + 1) != ']') {
                        throw this.ex("parser.cc.1", n2);
                    }
                    this.offset = n2 + 2;
                } else if (n == 24 && !bl3) {
                    if (bl2) {
                        bl2 = false;
                        if (bl) {
                            rangeToken = (RangeToken)Token.complementRanges(rangeToken);
                        } else {
                            rangeToken2.subtractRanges(rangeToken);
                            rangeToken = rangeToken2;
                        }
                    }
                    RangeToken rangeToken5 = this.parseCharacterClass(false);
                    rangeToken.subtractRanges(rangeToken5);
                    if (this.read() == 0 && this.chardata == 93) break;
                    throw this.ex("parser.cc.5", this.offset);
                }
            }
            this.next();
            if (!bl4) {
                if (this.read() != 0 || this.chardata != 45) {
                    if (!this.isSet(2) || n3 > 65535) {
                        rangeToken.addRange(n3, n3);
                    } else {
                        RegexParser.addCaseInsensitiveChar(rangeToken, n3);
                    }
                } else {
                    if (n == 24) {
                        throw this.ex("parser.cc.8", this.offset - 1);
                    }
                    this.next();
                    n = this.read();
                    if (n == 1) {
                        throw this.ex("parser.cc.2", this.offset);
                    }
                    if (n == 0 && this.chardata == 93) {
                        if (!this.isSet(2) || n3 > 65535) {
                            rangeToken.addRange(n3, n3);
                        } else {
                            RegexParser.addCaseInsensitiveChar(rangeToken, n3);
                        }
                        rangeToken.addRange(45, 45);
                    } else {
                        n2 = this.chardata;
                        if (n == 10) {
                            n2 = this.decodeEscaped();
                        }
                        this.next();
                        if (n3 > n2) {
                            throw this.ex("parser.ope.3", this.offset - 1);
                        }
                        if (!this.isSet(2) || n3 > 65535 && n2 > 65535) {
                            rangeToken.addRange(n3, n2);
                        } else {
                            RegexParser.addCaseInsensitiveCharRange(rangeToken, n3, n2);
                        }
                    }
                }
            }
            if (this.isSet(1024) && this.read() == 0 && this.chardata == 44) {
                this.next();
            }
            bl3 = false;
        }
        if (this.read() == 1) {
            throw this.ex("parser.cc.2", this.offset);
        }
        if (!bl && bl2) {
            rangeToken2.subtractRanges(rangeToken);
            rangeToken = rangeToken2;
        }
        rangeToken.sortRanges();
        rangeToken.compactRanges();
        this.setContext(0);
        this.next();
        return rangeToken;
    }

    protected RangeToken parseSetOperations() throws ParseException {
        int n;
        RangeToken rangeToken = this.parseCharacterClass(false);
        while ((n = this.read()) != 7) {
            int n2 = this.chardata;
            if (n == 0 && (n2 == 45 || n2 == 38) || n == 4) {
                this.next();
                if (this.read() != 9) {
                    throw this.ex("parser.ope.1", this.offset - 1);
                }
                RangeToken rangeToken2 = this.parseCharacterClass(false);
                if (n == 4) {
                    rangeToken.mergeRanges(rangeToken2);
                    continue;
                }
                if (n2 == 45) {
                    rangeToken.subtractRanges(rangeToken2);
                    continue;
                }
                if (n2 == 38) {
                    rangeToken.intersectRanges(rangeToken2);
                    continue;
                }
                throw new RuntimeException("ASSERT");
            }
            throw this.ex("parser.ope.2", this.offset - 1);
        }
        this.next();
        return rangeToken;
    }

    Token getTokenForShorthand(int n) {
        Token token;
        switch (n) {
            case 100: {
                token = this.isSet(32) ? Token.getRange("Nd", true) : Token.token_0to9;
                break;
            }
            case 68: {
                token = this.isSet(32) ? Token.getRange("Nd", false) : Token.token_not_0to9;
                break;
            }
            case 119: {
                token = this.isSet(32) ? Token.getRange("IsWord", true) : Token.token_wordchars;
                break;
            }
            case 87: {
                token = this.isSet(32) ? Token.getRange("IsWord", false) : Token.token_not_wordchars;
                break;
            }
            case 115: {
                token = this.isSet(32) ? Token.getRange("IsSpace", true) : Token.token_spaces;
                break;
            }
            case 83: {
                token = this.isSet(32) ? Token.getRange("IsSpace", false) : Token.token_not_spaces;
                break;
            }
            default: {
                throw new RuntimeException("Internal Error: shorthands: \\u" + Integer.toString(n, 16));
            }
        }
        return token;
    }

    int decodeEscaped() throws ParseException {
        if (this.read() != 10) {
            throw this.ex("parser.next.1", this.offset - 1);
        }
        int n = this.chardata;
        switch (n) {
            case 101: {
                n = 27;
                break;
            }
            case 102: {
                n = 12;
                break;
            }
            case 110: {
                n = 10;
                break;
            }
            case 114: {
                n = 13;
                break;
            }
            case 116: {
                n = 9;
                break;
            }
            case 120: {
                this.next();
                if (this.read() != 0) {
                    throw this.ex("parser.descape.1", this.offset - 1);
                }
                if (this.chardata == 123) {
                    int n2 = 0;
                    int n3 = 0;
                    while (true) {
                        this.next();
                        if (this.read() != 0) {
                            throw this.ex("parser.descape.1", this.offset - 1);
                        }
                        n2 = RegexParser.hexChar(this.chardata);
                        if (n2 < 0) break;
                        if (n3 > n3 * 16) {
                            throw this.ex("parser.descape.2", this.offset - 1);
                        }
                        n3 = n3 * 16 + n2;
                    }
                    if (this.chardata != 125) {
                        throw this.ex("parser.descape.3", this.offset - 1);
                    }
                    if (n3 > 0x10FFFF) {
                        throw this.ex("parser.descape.4", this.offset - 1);
                    }
                    n = n3;
                    break;
                }
                int n4 = 0;
                if (this.read() != 0 || (n4 = RegexParser.hexChar(this.chardata)) < 0) {
                    throw this.ex("parser.descape.1", this.offset - 1);
                }
                int n5 = n4;
                this.next();
                if (this.read() != 0 || (n4 = RegexParser.hexChar(this.chardata)) < 0) {
                    throw this.ex("parser.descape.1", this.offset - 1);
                }
                n = n5 = n5 * 16 + n4;
                break;
            }
            case 117: {
                int n6 = 0;
                this.next();
                if (this.read() != 0 || (n6 = RegexParser.hexChar(this.chardata)) < 0) {
                    throw this.ex("parser.descape.1", this.offset - 1);
                }
                int n7 = n6;
                this.next();
                if (this.read() != 0 || (n6 = RegexParser.hexChar(this.chardata)) < 0) {
                    throw this.ex("parser.descape.1", this.offset - 1);
                }
                n7 = n7 * 16 + n6;
                this.next();
                if (this.read() != 0 || (n6 = RegexParser.hexChar(this.chardata)) < 0) {
                    throw this.ex("parser.descape.1", this.offset - 1);
                }
                n7 = n7 * 16 + n6;
                this.next();
                if (this.read() != 0 || (n6 = RegexParser.hexChar(this.chardata)) < 0) {
                    throw this.ex("parser.descape.1", this.offset - 1);
                }
                n = n7 = n7 * 16 + n6;
                break;
            }
            case 118: {
                int n8;
                this.next();
                if (this.read() != 0 || (n8 = RegexParser.hexChar(this.chardata)) < 0) {
                    throw this.ex("parser.descape.1", this.offset - 1);
                }
                int n9 = n8;
                this.next();
                if (this.read() != 0 || (n8 = RegexParser.hexChar(this.chardata)) < 0) {
                    throw this.ex("parser.descape.1", this.offset - 1);
                }
                n9 = n9 * 16 + n8;
                this.next();
                if (this.read() != 0 || (n8 = RegexParser.hexChar(this.chardata)) < 0) {
                    throw this.ex("parser.descape.1", this.offset - 1);
                }
                n9 = n9 * 16 + n8;
                this.next();
                if (this.read() != 0 || (n8 = RegexParser.hexChar(this.chardata)) < 0) {
                    throw this.ex("parser.descape.1", this.offset - 1);
                }
                n9 = n9 * 16 + n8;
                this.next();
                if (this.read() != 0 || (n8 = RegexParser.hexChar(this.chardata)) < 0) {
                    throw this.ex("parser.descape.1", this.offset - 1);
                }
                n9 = n9 * 16 + n8;
                this.next();
                if (this.read() != 0 || (n8 = RegexParser.hexChar(this.chardata)) < 0) {
                    throw this.ex("parser.descape.1", this.offset - 1);
                }
                if ((n9 = n9 * 16 + n8) > 0x10FFFF) {
                    throw this.ex("parser.descappe.4", this.offset - 1);
                }
                n = n9;
                break;
            }
            case 65: 
            case 90: 
            case 122: {
                throw this.ex("parser.descape.5", this.offset - 2);
            }
        }
        return n;
    }

    private static final int hexChar(int n) {
        if (n < 48) {
            return -1;
        }
        if (n > 102) {
            return -1;
        }
        if (n <= 57) {
            return n - 48;
        }
        if (n < 65) {
            return -1;
        }
        if (n <= 70) {
            return n - 65 + 10;
        }
        if (n < 97) {
            return -1;
        }
        return n - 97 + 10;
    }

    protected static final void addCaseInsensitiveChar(RangeToken rangeToken, int n) {
        int[] nArray = CaseInsensitiveMap.get(n);
        rangeToken.addRange(n, n);
        if (nArray != null) {
            for (int i = 0; i < nArray.length; i += 2) {
                rangeToken.addRange(nArray[i], nArray[i]);
            }
        }
    }

    protected static final void addCaseInsensitiveCharRange(RangeToken rangeToken, int n, int n2) {
        int n3;
        int n4;
        if (n <= n2) {
            n4 = n;
            n3 = n2;
        } else {
            n4 = n2;
            n3 = n;
        }
        rangeToken.addRange(n4, n3);
        for (int i = n4; i <= n3; ++i) {
            int[] nArray = CaseInsensitiveMap.get(i);
            if (nArray == null) continue;
            for (int j = 0; j < nArray.length; j += 2) {
                rangeToken.addRange(nArray[j], nArray[j]);
            }
        }
    }

    static class ReferencePosition {
        int refNumber;
        int position;

        ReferencePosition(int n, int n2) {
            this.refNumber = n;
            this.position = n2;
        }
    }
}

