/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xpath.regex;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.xerces.impl.xpath.regex.REUtil;
import org.apache.xerces.impl.xpath.regex.RangeToken;

class Token
implements Serializable {
    private static final long serialVersionUID = 8484976002585487481L;
    static final boolean COUNTTOKENS = true;
    static int tokens = 0;
    static final int CHAR = 0;
    static final int DOT = 11;
    static final int CONCAT = 1;
    static final int UNION = 2;
    static final int CLOSURE = 3;
    static final int RANGE = 4;
    static final int NRANGE = 5;
    static final int PAREN = 6;
    static final int EMPTY = 7;
    static final int ANCHOR = 8;
    static final int NONGREEDYCLOSURE = 9;
    static final int STRING = 10;
    static final int BACKREFERENCE = 12;
    static final int LOOKAHEAD = 20;
    static final int NEGATIVELOOKAHEAD = 21;
    static final int LOOKBEHIND = 22;
    static final int NEGATIVELOOKBEHIND = 23;
    static final int INDEPENDENT = 24;
    static final int MODIFIERGROUP = 25;
    static final int CONDITION = 26;
    static final int UTF16_MAX = 0x10FFFF;
    final int type;
    static Token token_dot;
    static Token token_0to9;
    static Token token_wordchars;
    static Token token_not_0to9;
    static Token token_not_wordchars;
    static Token token_spaces;
    static Token token_not_spaces;
    static Token token_empty;
    static Token token_linebeginning;
    static Token token_linebeginning2;
    static Token token_lineend;
    static Token token_stringbeginning;
    static Token token_stringend;
    static Token token_stringend2;
    static Token token_wordedge;
    static Token token_not_wordedge;
    static Token token_wordbeginning;
    static Token token_wordend;
    static final int FC_CONTINUE = 0;
    static final int FC_TERMINAL = 1;
    static final int FC_ANY = 2;
    private static final Hashtable categories;
    private static final Hashtable categories2;
    private static final String[] categoryNames;
    static final int CHAR_INIT_QUOTE = 29;
    static final int CHAR_FINAL_QUOTE = 30;
    static final int CHAR_LETTER = 31;
    static final int CHAR_MARK = 32;
    static final int CHAR_NUMBER = 33;
    static final int CHAR_SEPARATOR = 34;
    static final int CHAR_OTHER = 35;
    static final int CHAR_PUNCTUATION = 36;
    static final int CHAR_SYMBOL = 37;
    private static final String[] blockNames;
    static final String blockRanges = "\u0000\u007f\u0080\u00ff\u0100\u017f\u0180\u024f\u0250\u02af\u02b0\u02ff\u0300\u036f\u0370\u03ff\u0400\u04ff\u0530\u058f\u0590\u05ff\u0600\u06ff\u0700\u074f\u0780\u07bf\u0900\u097f\u0980\u09ff\u0a00\u0a7f\u0a80\u0aff\u0b00\u0b7f\u0b80\u0bff\u0c00\u0c7f\u0c80\u0cff\u0d00\u0d7f\u0d80\u0dff\u0e00\u0e7f\u0e80\u0eff\u0f00\u0fff\u1000\u109f\u10a0\u10ff\u1100\u11ff\u1200\u137f\u13a0\u13ff\u1400\u167f\u1680\u169f\u16a0\u16ff\u1780\u17ff\u1800\u18af\u1e00\u1eff\u1f00\u1fff\u2000\u206f\u2070\u209f\u20a0\u20cf\u20d0\u20ff\u2100\u214f\u2150\u218f\u2190\u21ff\u2200\u22ff\u2300\u23ff\u2400\u243f\u2440\u245f\u2460\u24ff\u2500\u257f\u2580\u259f\u25a0\u25ff\u2600\u26ff\u2700\u27bf\u2800\u28ff\u2e80\u2eff\u2f00\u2fdf\u2ff0\u2fff\u3000\u303f\u3040\u309f\u30a0\u30ff\u3100\u312f\u3130\u318f\u3190\u319f\u31a0\u31bf\u3200\u32ff\u3300\u33ff\u3400\u4db5\u4e00\u9fff\ua000\ua48f\ua490\ua4cf\uac00\ud7a3\ue000\uf8ff\uf900\ufaff\ufb00\ufb4f\ufb50\ufdff\ufe20\ufe2f\ufe30\ufe4f\ufe50\ufe6f\ufe70\ufefe\ufeff\ufeff\uff00\uffef";
    static final int[] nonBMPBlockRanges;
    private static final int NONBMP_BLOCK_START = 84;
    static Hashtable nonxs;
    static final String viramaString = "\u094d\u09cd\u0a4d\u0acd\u0b4d\u0bcd\u0c4d\u0ccd\u0d4d\u0e3a\u0f84";
    private static Token token_grapheme;
    private static Token token_ccs;

    static ParenToken createLook(int n, Token token) {
        ++tokens;
        return new ParenToken(n, token, 0);
    }

    static ParenToken createParen(Token token, int n) {
        ++tokens;
        return new ParenToken(6, token, n);
    }

    static ClosureToken createClosure(Token token) {
        ++tokens;
        return new ClosureToken(3, token);
    }

    static ClosureToken createNGClosure(Token token) {
        ++tokens;
        return new ClosureToken(9, token);
    }

    static ConcatToken createConcat(Token token, Token token2) {
        ++tokens;
        return new ConcatToken(token, token2);
    }

    static UnionToken createConcat() {
        ++tokens;
        return new UnionToken(1);
    }

    static UnionToken createUnion() {
        ++tokens;
        return new UnionToken(2);
    }

    static Token createEmpty() {
        return token_empty;
    }

    static RangeToken createRange() {
        ++tokens;
        return new RangeToken(4);
    }

    static RangeToken createNRange() {
        ++tokens;
        return new RangeToken(5);
    }

    static CharToken createChar(int n) {
        ++tokens;
        return new CharToken(0, n);
    }

    private static CharToken createAnchor(int n) {
        ++tokens;
        return new CharToken(8, n);
    }

    static StringToken createBackReference(int n) {
        ++tokens;
        return new StringToken(12, null, n);
    }

    static StringToken createString(String string) {
        ++tokens;
        return new StringToken(10, string, 0);
    }

    static ModifierToken createModifierGroup(Token token, int n, int n2) {
        ++tokens;
        return new ModifierToken(token, n, n2);
    }

    static ConditionToken createCondition(int n, Token token, Token token2, Token token3) {
        ++tokens;
        return new ConditionToken(n, token, token2, token3);
    }

    protected Token(int n) {
        this.type = n;
    }

    int size() {
        return 0;
    }

    Token getChild(int n) {
        return null;
    }

    void addChild(Token token) {
        throw new RuntimeException("Not supported.");
    }

    protected void addRange(int n, int n2) {
        throw new RuntimeException("Not supported.");
    }

    protected void sortRanges() {
        throw new RuntimeException("Not supported.");
    }

    protected void compactRanges() {
        throw new RuntimeException("Not supported.");
    }

    protected void mergeRanges(Token token) {
        throw new RuntimeException("Not supported.");
    }

    protected void subtractRanges(Token token) {
        throw new RuntimeException("Not supported.");
    }

    protected void intersectRanges(Token token) {
        throw new RuntimeException("Not supported.");
    }

    static Token complementRanges(Token token) {
        return RangeToken.complementRanges(token);
    }

    void setMin(int n) {
    }

    void setMax(int n) {
    }

    int getMin() {
        return -1;
    }

    int getMax() {
        return -1;
    }

    int getReferenceNumber() {
        return 0;
    }

    String getString() {
        return null;
    }

    int getParenNumber() {
        return 0;
    }

    int getChar() {
        return -1;
    }

    public String toString() {
        return this.toString(0);
    }

    public String toString(int n) {
        return this.type == 11 ? "." : "";
    }

    final int getMinLength() {
        switch (this.type) {
            case 1: {
                int n = 0;
                for (int i = 0; i < this.size(); ++i) {
                    n += this.getChild(i).getMinLength();
                }
                return n;
            }
            case 2: 
            case 26: {
                if (this.size() == 0) {
                    return 0;
                }
                int n = this.getChild(0).getMinLength();
                for (int i = 1; i < this.size(); ++i) {
                    int n2 = this.getChild(i).getMinLength();
                    if (n2 >= n) continue;
                    n = n2;
                }
                return n;
            }
            case 3: 
            case 9: {
                if (this.getMin() >= 0) {
                    return this.getMin() * this.getChild(0).getMinLength();
                }
                return 0;
            }
            case 7: 
            case 8: {
                return 0;
            }
            case 0: 
            case 4: 
            case 5: 
            case 11: {
                return 1;
            }
            case 6: 
            case 24: 
            case 25: {
                return this.getChild(0).getMinLength();
            }
            case 12: {
                return 0;
            }
            case 10: {
                return this.getString().length();
            }
            case 20: 
            case 21: 
            case 22: 
            case 23: {
                return 0;
            }
        }
        throw new RuntimeException("Token#getMinLength(): Invalid Type: " + this.type);
    }

    final int getMaxLength() {
        switch (this.type) {
            case 1: {
                int n = 0;
                for (int i = 0; i < this.size(); ++i) {
                    int n2 = this.getChild(i).getMaxLength();
                    if (n2 < 0) {
                        return -1;
                    }
                    n += n2;
                }
                return n;
            }
            case 2: 
            case 26: {
                if (this.size() == 0) {
                    return 0;
                }
                int n = this.getChild(0).getMaxLength();
                for (int i = 1; n >= 0 && i < this.size(); ++i) {
                    int n3 = this.getChild(i).getMaxLength();
                    if (n3 < 0) {
                        n = -1;
                        break;
                    }
                    if (n3 <= n) continue;
                    n = n3;
                }
                return n;
            }
            case 3: 
            case 9: {
                if (this.getMax() >= 0) {
                    return this.getMax() * this.getChild(0).getMaxLength();
                }
                return -1;
            }
            case 7: 
            case 8: {
                return 0;
            }
            case 0: {
                return 1;
            }
            case 4: 
            case 5: 
            case 11: {
                return 2;
            }
            case 6: 
            case 24: 
            case 25: {
                return this.getChild(0).getMaxLength();
            }
            case 12: {
                return -1;
            }
            case 10: {
                return this.getString().length();
            }
            case 20: 
            case 21: 
            case 22: 
            case 23: {
                return 0;
            }
        }
        throw new RuntimeException("Token#getMaxLength(): Invalid Type: " + this.type);
    }

    private static final boolean isSet(int n, int n2) {
        return (n & n2) == n2;
    }

    final int analyzeFirstCharacter(RangeToken rangeToken, int n) {
        switch (this.type) {
            case 1: {
                int n2 = 0;
                for (int i = 0; i < this.size() && (n2 = this.getChild(i).analyzeFirstCharacter(rangeToken, n)) == 0; ++i) {
                }
                return n2;
            }
            case 2: {
                if (this.size() == 0) {
                    return 0;
                }
                int n3 = 0;
                boolean bl = false;
                for (int i = 0; i < this.size() && (n3 = this.getChild(i).analyzeFirstCharacter(rangeToken, n)) != 2; ++i) {
                    if (n3 != 0) continue;
                    bl = true;
                }
                return bl ? 0 : n3;
            }
            case 26: {
                int n4 = this.getChild(0).analyzeFirstCharacter(rangeToken, n);
                if (this.size() == 1) {
                    return 0;
                }
                if (n4 == 2) {
                    return n4;
                }
                int n5 = this.getChild(1).analyzeFirstCharacter(rangeToken, n);
                if (n5 == 2) {
                    return n5;
                }
                return n4 == 0 || n5 == 0 ? 0 : 1;
            }
            case 3: 
            case 9: {
                this.getChild(0).analyzeFirstCharacter(rangeToken, n);
                return 0;
            }
            case 7: 
            case 8: {
                return 0;
            }
            case 0: {
                int n6 = this.getChar();
                rangeToken.addRange(n6, n6);
                if (n6 < 65536 && Token.isSet(n, 2)) {
                    n6 = Character.toUpperCase((char)n6);
                    rangeToken.addRange(n6, n6);
                    n6 = Character.toLowerCase((char)n6);
                    rangeToken.addRange(n6, n6);
                }
                return 1;
            }
            case 11: {
                return 2;
            }
            case 4: {
                rangeToken.mergeRanges(this);
                return 1;
            }
            case 5: {
                rangeToken.mergeRanges(Token.complementRanges(this));
                return 1;
            }
            case 6: 
            case 24: {
                return this.getChild(0).analyzeFirstCharacter(rangeToken, n);
            }
            case 25: {
                n |= ((ModifierToken)this).getOptions();
                return this.getChild(0).analyzeFirstCharacter(rangeToken, n &= ~((ModifierToken)this).getOptionsMask());
            }
            case 12: {
                rangeToken.addRange(0, 0x10FFFF);
                return 2;
            }
            case 10: {
                char c;
                int n7 = this.getString().charAt(0);
                if (REUtil.isHighSurrogate(n7) && this.getString().length() >= 2 && REUtil.isLowSurrogate(c = this.getString().charAt(1))) {
                    n7 = REUtil.composeFromSurrogates(n7, c);
                }
                rangeToken.addRange(n7, n7);
                if (n7 < 65536 && Token.isSet(n, 2)) {
                    n7 = Character.toUpperCase((char)n7);
                    rangeToken.addRange(n7, n7);
                    n7 = Character.toLowerCase((char)n7);
                    rangeToken.addRange(n7, n7);
                }
                return 1;
            }
            case 20: 
            case 21: 
            case 22: 
            case 23: {
                return 0;
            }
        }
        throw new RuntimeException("Token#analyzeHeadCharacter(): Invalid Type: " + this.type);
    }

    private final boolean isShorterThan(Token token) {
        if (token == null) {
            return false;
        }
        if (this.type != 10) {
            throw new RuntimeException("Internal Error: Illegal type: " + this.type);
        }
        int n = this.getString().length();
        if (token.type != 10) {
            throw new RuntimeException("Internal Error: Illegal type: " + token.type);
        }
        int n2 = token.getString().length();
        return n < n2;
    }

    final void findFixedString(FixedStringContainer fixedStringContainer, int n) {
        switch (this.type) {
            case 1: {
                Token token = null;
                int n2 = 0;
                for (int i = 0; i < this.size(); ++i) {
                    this.getChild(i).findFixedString(fixedStringContainer, n);
                    if (token != null && !token.isShorterThan(fixedStringContainer.token)) continue;
                    token = fixedStringContainer.token;
                    n2 = fixedStringContainer.options;
                }
                fixedStringContainer.token = token;
                fixedStringContainer.options = n2;
                return;
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 7: 
            case 8: 
            case 9: 
            case 11: 
            case 12: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 26: {
                fixedStringContainer.token = null;
                return;
            }
            case 0: {
                fixedStringContainer.token = null;
                return;
            }
            case 10: {
                fixedStringContainer.token = this;
                fixedStringContainer.options = n;
                return;
            }
            case 6: 
            case 24: {
                this.getChild(0).findFixedString(fixedStringContainer, n);
                return;
            }
            case 25: {
                n |= ((ModifierToken)this).getOptions();
                this.getChild(0).findFixedString(fixedStringContainer, n &= ~((ModifierToken)this).getOptionsMask());
                return;
            }
        }
        throw new RuntimeException("Token#findFixedString(): Invalid Type: " + this.type);
    }

    boolean match(int n) {
        throw new RuntimeException("NFAArrow#match(): Internal error: " + this.type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static RangeToken getRange(String string, boolean bl) {
        Serializable serializable;
        if (categories.size() == 0) {
            serializable = categories;
            synchronized (serializable) {
                Object object;
                RangeToken rangeToken;
                int n;
                int n2;
                Token[] tokenArray = new Token[categoryNames.length];
                for (n2 = 0; n2 < tokenArray.length; ++n2) {
                    tokenArray[n2] = Token.createRange();
                }
                for (n = 0; n < 65536; ++n) {
                    n2 = Character.getType((char)n);
                    if (n2 == 21 || n2 == 22) {
                        if (n == 171 || n == 8216 || n == 8219 || n == 8220 || n == 8223 || n == 8249) {
                            n2 = 29;
                        }
                        if (n == 187 || n == 8217 || n == 8221 || n == 8250) {
                            n2 = 30;
                        }
                    }
                    tokenArray[n2].addRange(n, n);
                    switch (n2) {
                        case 1: 
                        case 2: 
                        case 3: 
                        case 4: 
                        case 5: {
                            n2 = 31;
                            break;
                        }
                        case 6: 
                        case 7: 
                        case 8: {
                            n2 = 32;
                            break;
                        }
                        case 9: 
                        case 10: 
                        case 11: {
                            n2 = 33;
                            break;
                        }
                        case 12: 
                        case 13: 
                        case 14: {
                            n2 = 34;
                            break;
                        }
                        case 0: 
                        case 15: 
                        case 16: 
                        case 18: 
                        case 19: {
                            n2 = 35;
                            break;
                        }
                        case 20: 
                        case 21: 
                        case 22: 
                        case 23: 
                        case 24: 
                        case 29: 
                        case 30: {
                            n2 = 36;
                            break;
                        }
                        case 25: 
                        case 26: 
                        case 27: 
                        case 28: {
                            n2 = 37;
                            break;
                        }
                        default: {
                            throw new RuntimeException("org.apache.xerces.utils.regex.Token#getRange(): Unknown Unicode category: " + n2);
                        }
                    }
                    tokenArray[n2].addRange(n, n);
                }
                tokenArray[0].addRange(65536, 0x10FFFF);
                tokenArray[35].addRange(65536, 0x10FFFF);
                for (n = 0; n < tokenArray.length; ++n) {
                    if (categoryNames[n] == null) continue;
                    if (n == 0) {
                        tokenArray[n].addRange(65536, 0x10FFFF);
                    }
                    categories.put(categoryNames[n], tokenArray[n]);
                    categories2.put(categoryNames[n], Token.complementRanges(tokenArray[n]));
                }
                StringBuffer stringBuffer = new StringBuffer(50);
                for (int i = 0; i < blockNames.length; ++i) {
                    int n3;
                    int n4;
                    rangeToken = Token.createRange();
                    if (i < 84) {
                        n4 = i * 2;
                        char c = blockRanges.charAt(n4);
                        n3 = blockRanges.charAt(n4 + 1);
                        ((Token)rangeToken).addRange(c, n3);
                    } else {
                        n4 = (i - 84) * 2;
                        ((Token)rangeToken).addRange(nonBMPBlockRanges[n4], nonBMPBlockRanges[n4 + 1]);
                    }
                    object = blockNames[i];
                    if (((String)object).equals("Specials")) {
                        ((Token)rangeToken).addRange(65520, 65533);
                    }
                    if (((String)object).equals("Private Use")) {
                        ((Token)rangeToken).addRange(983040, 1048573);
                        ((Token)rangeToken).addRange(0x100000, 1114109);
                    }
                    categories.put(object, rangeToken);
                    categories2.put(object, Token.complementRanges(rangeToken));
                    stringBuffer.setLength(0);
                    stringBuffer.append("Is");
                    if (((String)object).indexOf(32) >= 0) {
                        for (n3 = 0; n3 < ((String)object).length(); ++n3) {
                            if (((String)object).charAt(n3) == ' ') continue;
                            stringBuffer.append(((String)object).charAt(n3));
                        }
                    } else {
                        stringBuffer.append((String)object);
                    }
                    Token.setAlias(stringBuffer.toString(), (String)object, true);
                }
                Token.setAlias("ASSIGNED", "Cn", false);
                Token.setAlias("UNASSIGNED", "Cn", true);
                RangeToken rangeToken2 = Token.createRange();
                ((Token)rangeToken2).addRange(0, 0x10FFFF);
                categories.put("ALL", rangeToken2);
                categories2.put("ALL", Token.complementRanges(rangeToken2));
                Token.registerNonXS("ASSIGNED");
                Token.registerNonXS("UNASSIGNED");
                Token.registerNonXS("ALL");
                rangeToken = Token.createRange();
                ((Token)rangeToken).mergeRanges(tokenArray[1]);
                ((Token)rangeToken).mergeRanges(tokenArray[2]);
                ((Token)rangeToken).mergeRanges(tokenArray[5]);
                categories.put("IsAlpha", rangeToken);
                categories2.put("IsAlpha", Token.complementRanges(rangeToken));
                Token.registerNonXS("IsAlpha");
                RangeToken rangeToken3 = Token.createRange();
                ((Token)rangeToken3).mergeRanges(rangeToken);
                ((Token)rangeToken3).mergeRanges(tokenArray[9]);
                categories.put("IsAlnum", rangeToken3);
                categories2.put("IsAlnum", Token.complementRanges(rangeToken3));
                Token.registerNonXS("IsAlnum");
                object = Token.createRange();
                ((Token)object).mergeRanges(token_spaces);
                ((Token)object).mergeRanges(tokenArray[34]);
                categories.put("IsSpace", object);
                categories2.put("IsSpace", Token.complementRanges((Token)object));
                Token.registerNonXS("IsSpace");
                RangeToken rangeToken4 = Token.createRange();
                ((Token)rangeToken4).mergeRanges(rangeToken3);
                ((Token)rangeToken4).addRange(95, 95);
                categories.put("IsWord", rangeToken4);
                categories2.put("IsWord", Token.complementRanges(rangeToken4));
                Token.registerNonXS("IsWord");
                RangeToken rangeToken5 = Token.createRange();
                ((Token)rangeToken5).addRange(0, 127);
                categories.put("IsASCII", rangeToken5);
                categories2.put("IsASCII", Token.complementRanges(rangeToken5));
                Token.registerNonXS("IsASCII");
                RangeToken rangeToken6 = Token.createRange();
                ((Token)rangeToken6).mergeRanges(tokenArray[35]);
                ((Token)rangeToken6).addRange(32, 32);
                categories.put("IsGraph", Token.complementRanges(rangeToken6));
                categories2.put("IsGraph", rangeToken6);
                Token.registerNonXS("IsGraph");
                RangeToken rangeToken7 = Token.createRange();
                ((Token)rangeToken7).addRange(48, 57);
                ((Token)rangeToken7).addRange(65, 70);
                ((Token)rangeToken7).addRange(97, 102);
                categories.put("IsXDigit", Token.complementRanges(rangeToken7));
                categories2.put("IsXDigit", rangeToken7);
                Token.registerNonXS("IsXDigit");
                Token.setAlias("IsDigit", "Nd", true);
                Token.setAlias("IsUpper", "Lu", true);
                Token.setAlias("IsLower", "Ll", true);
                Token.setAlias("IsCntrl", "C", true);
                Token.setAlias("IsPrint", "C", false);
                Token.setAlias("IsPunct", "P", true);
                Token.registerNonXS("IsDigit");
                Token.registerNonXS("IsUpper");
                Token.registerNonXS("IsLower");
                Token.registerNonXS("IsCntrl");
                Token.registerNonXS("IsPrint");
                Token.registerNonXS("IsPunct");
                Token.setAlias("alpha", "IsAlpha", true);
                Token.setAlias("alnum", "IsAlnum", true);
                Token.setAlias("ascii", "IsASCII", true);
                Token.setAlias("cntrl", "IsCntrl", true);
                Token.setAlias("digit", "IsDigit", true);
                Token.setAlias("graph", "IsGraph", true);
                Token.setAlias("lower", "IsLower", true);
                Token.setAlias("print", "IsPrint", true);
                Token.setAlias("punct", "IsPunct", true);
                Token.setAlias("space", "IsSpace", true);
                Token.setAlias("upper", "IsUpper", true);
                Token.setAlias("word", "IsWord", true);
                Token.setAlias("xdigit", "IsXDigit", true);
                Token.registerNonXS("alpha");
                Token.registerNonXS("alnum");
                Token.registerNonXS("ascii");
                Token.registerNonXS("cntrl");
                Token.registerNonXS("digit");
                Token.registerNonXS("graph");
                Token.registerNonXS("lower");
                Token.registerNonXS("print");
                Token.registerNonXS("punct");
                Token.registerNonXS("space");
                Token.registerNonXS("upper");
                Token.registerNonXS("word");
                Token.registerNonXS("xdigit");
            }
        }
        serializable = bl ? (RangeToken)categories.get(string) : (RangeToken)categories2.get(string);
        return serializable;
    }

    protected static RangeToken getRange(String string, boolean bl, boolean bl2) {
        RangeToken rangeToken = Token.getRange(string, bl);
        if (bl2 && rangeToken != null && Token.isRegisterNonXS(string)) {
            rangeToken = null;
        }
        return rangeToken;
    }

    protected static void registerNonXS(String string) {
        if (nonxs == null) {
            nonxs = new Hashtable();
        }
        nonxs.put(string, string);
    }

    protected static boolean isRegisterNonXS(String string) {
        if (nonxs == null) {
            return false;
        }
        return nonxs.containsKey(string);
    }

    private static void setAlias(String string, String string2, boolean bl) {
        Token token = (Token)categories.get(string2);
        Token token2 = (Token)categories2.get(string2);
        if (bl) {
            categories.put(string, token);
            categories2.put(string, token2);
        } else {
            categories2.put(string, token);
            categories.put(string, token2);
        }
    }

    static synchronized Token getGraphemePattern() {
        if (token_grapheme != null) {
            return token_grapheme;
        }
        RangeToken rangeToken = Token.createRange();
        ((Token)rangeToken).mergeRanges(Token.getRange("ASSIGNED", true));
        ((Token)rangeToken).subtractRanges(Token.getRange("M", true));
        ((Token)rangeToken).subtractRanges(Token.getRange("C", true));
        RangeToken rangeToken2 = Token.createRange();
        for (int i = 0; i < viramaString.length(); ++i) {
            ((Token)rangeToken2).addRange(i, i);
        }
        RangeToken rangeToken3 = Token.createRange();
        ((Token)rangeToken3).mergeRanges(Token.getRange("M", true));
        ((Token)rangeToken3).addRange(4448, 4607);
        ((Token)rangeToken3).addRange(65438, 65439);
        UnionToken unionToken = Token.createUnion();
        ((Token)unionToken).addChild(rangeToken);
        ((Token)unionToken).addChild(token_empty);
        Token token = Token.createUnion();
        ((Token)token).addChild(Token.createConcat(rangeToken2, Token.getRange("L", true)));
        ((Token)token).addChild(rangeToken3);
        token = Token.createClosure(token);
        token = Token.createConcat(unionToken, token);
        token_grapheme = token;
        return token_grapheme;
    }

    static synchronized Token getCombiningCharacterSequence() {
        if (token_ccs != null) {
            return token_ccs;
        }
        Token token = Token.createClosure(Token.getRange("M", true));
        token = Token.createConcat(Token.getRange("M", false), token);
        token_ccs = token;
        return token_ccs;
    }

    static {
        token_empty = new Token(7);
        token_linebeginning = Token.createAnchor(94);
        token_linebeginning2 = Token.createAnchor(64);
        token_lineend = Token.createAnchor(36);
        token_stringbeginning = Token.createAnchor(65);
        token_stringend = Token.createAnchor(122);
        token_stringend2 = Token.createAnchor(90);
        token_wordedge = Token.createAnchor(98);
        token_not_wordedge = Token.createAnchor(66);
        token_wordbeginning = Token.createAnchor(60);
        token_wordend = Token.createAnchor(62);
        token_dot = new Token(11);
        token_0to9 = Token.createRange();
        token_0to9.addRange(48, 57);
        token_wordchars = Token.createRange();
        token_wordchars.addRange(48, 57);
        token_wordchars.addRange(65, 90);
        token_wordchars.addRange(95, 95);
        token_wordchars.addRange(97, 122);
        token_spaces = Token.createRange();
        token_spaces.addRange(9, 9);
        token_spaces.addRange(10, 10);
        token_spaces.addRange(12, 12);
        token_spaces.addRange(13, 13);
        token_spaces.addRange(32, 32);
        token_not_0to9 = Token.complementRanges(token_0to9);
        token_not_wordchars = Token.complementRanges(token_wordchars);
        token_not_spaces = Token.complementRanges(token_spaces);
        categories = new Hashtable();
        categories2 = new Hashtable();
        categoryNames = new String[]{"Cn", "Lu", "Ll", "Lt", "Lm", "Lo", "Mn", "Me", "Mc", "Nd", "Nl", "No", "Zs", "Zl", "Zp", "Cc", "Cf", null, "Co", "Cs", "Pd", "Ps", "Pe", "Pc", "Po", "Sm", "Sc", "Sk", "So", "Pi", "Pf", "L", "M", "N", "Z", "C", "P", "S"};
        blockNames = new String[]{"Basic Latin", "Latin-1 Supplement", "Latin Extended-A", "Latin Extended-B", "IPA Extensions", "Spacing Modifier Letters", "Combining Diacritical Marks", "Greek", "Cyrillic", "Armenian", "Hebrew", "Arabic", "Syriac", "Thaana", "Devanagari", "Bengali", "Gurmukhi", "Gujarati", "Oriya", "Tamil", "Telugu", "Kannada", "Malayalam", "Sinhala", "Thai", "Lao", "Tibetan", "Myanmar", "Georgian", "Hangul Jamo", "Ethiopic", "Cherokee", "Unified Canadian Aboriginal Syllabics", "Ogham", "Runic", "Khmer", "Mongolian", "Latin Extended Additional", "Greek Extended", "General Punctuation", "Superscripts and Subscripts", "Currency Symbols", "Combining Marks for Symbols", "Letterlike Symbols", "Number Forms", "Arrows", "Mathematical Operators", "Miscellaneous Technical", "Control Pictures", "Optical Character Recognition", "Enclosed Alphanumerics", "Box Drawing", "Block Elements", "Geometric Shapes", "Miscellaneous Symbols", "Dingbats", "Braille Patterns", "CJK Radicals Supplement", "Kangxi Radicals", "Ideographic Description Characters", "CJK Symbols and Punctuation", "Hiragana", "Katakana", "Bopomofo", "Hangul Compatibility Jamo", "Kanbun", "Bopomofo Extended", "Enclosed CJK Letters and Months", "CJK Compatibility", "CJK Unified Ideographs Extension A", "CJK Unified Ideographs", "Yi Syllables", "Yi Radicals", "Hangul Syllables", "Private Use", "CJK Compatibility Ideographs", "Alphabetic Presentation Forms", "Arabic Presentation Forms-A", "Combining Half Marks", "CJK Compatibility Forms", "Small Form Variants", "Arabic Presentation Forms-B", "Specials", "Halfwidth and Fullwidth Forms", "Old Italic", "Gothic", "Deseret", "Byzantine Musical Symbols", "Musical Symbols", "Mathematical Alphanumeric Symbols", "CJK Unified Ideographs Extension B", "CJK Compatibility Ideographs Supplement", "Tags"};
        nonBMPBlockRanges = new int[]{66304, 66351, 66352, 66383, 66560, 66639, 118784, 119039, 119040, 119295, 119808, 120831, 131072, 173782, 194560, 195103, 917504, 917631};
        nonxs = null;
        token_grapheme = null;
        token_ccs = null;
    }

    static class UnionToken
    extends Token
    implements Serializable {
        private static final long serialVersionUID = -2568843945989489861L;
        Vector children;

        UnionToken(int n) {
            super(n);
        }

        @Override
        void addChild(Token token) {
            int n;
            StringBuffer stringBuffer;
            int n2;
            if (token == null) {
                return;
            }
            if (this.children == null) {
                this.children = new Vector();
            }
            if (this.type == 2) {
                this.children.addElement(token);
                return;
            }
            if (token.type == 1) {
                for (int i = 0; i < token.size(); ++i) {
                    this.addChild(token.getChild(i));
                }
                return;
            }
            int n3 = this.children.size();
            if (n3 == 0) {
                this.children.addElement(token);
                return;
            }
            Token token2 = (Token)this.children.elementAt(n3 - 1);
            if (token2.type != 0 && token2.type != 10 || token.type != 0 && token.type != 10) {
                this.children.addElement(token);
                return;
            }
            int n4 = n2 = token.type == 0 ? 2 : token.getString().length();
            if (token2.type == 0) {
                stringBuffer = new StringBuffer(2 + n2);
                n = token2.getChar();
                if (n >= 65536) {
                    stringBuffer.append(REUtil.decomposeToSurrogates(n));
                } else {
                    stringBuffer.append((char)n);
                }
                token2 = Token.createString(null);
                this.children.setElementAt(token2, n3 - 1);
            } else {
                stringBuffer = new StringBuffer(token2.getString().length() + n2);
                stringBuffer.append(token2.getString());
            }
            if (token.type == 0) {
                n = token.getChar();
                if (n >= 65536) {
                    stringBuffer.append(REUtil.decomposeToSurrogates(n));
                } else {
                    stringBuffer.append((char)n);
                }
            } else {
                stringBuffer.append(token.getString());
            }
            ((StringToken)token2).string = new String(stringBuffer);
        }

        @Override
        int size() {
            return this.children == null ? 0 : this.children.size();
        }

        @Override
        Token getChild(int n) {
            return (Token)this.children.elementAt(n);
        }

        @Override
        public String toString(int n) {
            String string;
            if (this.type == 1) {
                String string2;
                if (this.children.size() == 2) {
                    Token token = this.getChild(0);
                    Token token2 = this.getChild(1);
                    string2 = token2.type == 3 && token2.getChild(0) == token ? token.toString(n) + "+" : (token2.type == 9 && token2.getChild(0) == token ? token.toString(n) + "+?" : token.toString(n) + token2.toString(n));
                } else {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < this.children.size(); ++i) {
                        stringBuffer.append(((Token)this.children.elementAt(i)).toString(n));
                    }
                    string2 = new String(stringBuffer);
                }
                return string2;
            }
            if (this.children.size() == 2 && this.getChild((int)1).type == 7) {
                string = this.getChild(0).toString(n) + "?";
            } else if (this.children.size() == 2 && this.getChild((int)0).type == 7) {
                string = this.getChild(1).toString(n) + "??";
            } else {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(((Token)this.children.elementAt(0)).toString(n));
                for (int i = 1; i < this.children.size(); ++i) {
                    stringBuffer.append('|');
                    stringBuffer.append(((Token)this.children.elementAt(i)).toString(n));
                }
                string = new String(stringBuffer);
            }
            return string;
        }
    }

    static class ModifierToken
    extends Token
    implements Serializable {
        private static final long serialVersionUID = -9114536559696480356L;
        final Token child;
        final int add;
        final int mask;

        ModifierToken(Token token, int n, int n2) {
            super(25);
            this.child = token;
            this.add = n;
            this.mask = n2;
        }

        @Override
        int size() {
            return 1;
        }

        @Override
        Token getChild(int n) {
            return this.child;
        }

        int getOptions() {
            return this.add;
        }

        int getOptionsMask() {
            return this.mask;
        }

        @Override
        public String toString(int n) {
            return "(?" + (this.add == 0 ? "" : REUtil.createOptionString(this.add)) + (this.mask == 0 ? "" : REUtil.createOptionString(this.mask)) + ":" + this.child.toString(n) + ")";
        }
    }

    static class ConditionToken
    extends Token
    implements Serializable {
        private static final long serialVersionUID = 4353765277910594411L;
        final int refNumber;
        final Token condition;
        final Token yes;
        final Token no;

        ConditionToken(int n, Token token, Token token2, Token token3) {
            super(26);
            this.refNumber = n;
            this.condition = token;
            this.yes = token2;
            this.no = token3;
        }

        @Override
        int size() {
            return this.no == null ? 1 : 2;
        }

        @Override
        Token getChild(int n) {
            if (n == 0) {
                return this.yes;
            }
            if (n == 1) {
                return this.no;
            }
            throw new RuntimeException("Internal Error: " + n);
        }

        @Override
        public String toString(int n) {
            String string = this.refNumber > 0 ? "(?(" + this.refNumber + ")" : (this.condition.type == 8 ? "(?(" + this.condition + ")" : "(?" + this.condition);
            string = this.no == null ? string + this.yes + ")" : string + this.yes + "|" + this.no + ")";
            return string;
        }
    }

    static class ParenToken
    extends Token
    implements Serializable {
        private static final long serialVersionUID = -5938014719827987704L;
        final Token child;
        final int parennumber;

        ParenToken(int n, Token token, int n2) {
            super(n);
            this.child = token;
            this.parennumber = n2;
        }

        @Override
        int size() {
            return 1;
        }

        @Override
        Token getChild(int n) {
            return this.child;
        }

        @Override
        int getParenNumber() {
            return this.parennumber;
        }

        @Override
        public String toString(int n) {
            String string = null;
            switch (this.type) {
                case 6: {
                    if (this.parennumber == 0) {
                        string = "(?:" + this.child.toString(n) + ")";
                        break;
                    }
                    string = "(" + this.child.toString(n) + ")";
                    break;
                }
                case 20: {
                    string = "(?=" + this.child.toString(n) + ")";
                    break;
                }
                case 21: {
                    string = "(?!" + this.child.toString(n) + ")";
                    break;
                }
                case 22: {
                    string = "(?<=" + this.child.toString(n) + ")";
                    break;
                }
                case 23: {
                    string = "(?<!" + this.child.toString(n) + ")";
                    break;
                }
                case 24: {
                    string = "(?>" + this.child.toString(n) + ")";
                }
            }
            return string;
        }
    }

    static class ClosureToken
    extends Token
    implements Serializable {
        private static final long serialVersionUID = 1308971930673997452L;
        int min;
        int max;
        final Token child;

        ClosureToken(int n, Token token) {
            super(n);
            this.child = token;
            this.setMin(-1);
            this.setMax(-1);
        }

        @Override
        int size() {
            return 1;
        }

        @Override
        Token getChild(int n) {
            return this.child;
        }

        @Override
        final void setMin(int n) {
            this.min = n;
        }

        @Override
        final void setMax(int n) {
            this.max = n;
        }

        @Override
        final int getMin() {
            return this.min;
        }

        @Override
        final int getMax() {
            return this.max;
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public String toString(int n) {
            if (this.type == 3) {
                if (this.getMin() < 0 && this.getMax() < 0) {
                    return this.child.toString(n) + "*";
                }
                if (this.getMin() == this.getMax()) {
                    return this.child.toString(n) + "{" + this.getMin() + "}";
                }
                if (this.getMin() >= 0 && this.getMax() >= 0) {
                    return this.child.toString(n) + "{" + this.getMin() + "," + this.getMax() + "}";
                }
                if (this.getMin() < 0) throw new RuntimeException("Token#toString(): CLOSURE " + this.getMin() + ", " + this.getMax());
                if (this.getMax() >= 0) throw new RuntimeException("Token#toString(): CLOSURE " + this.getMin() + ", " + this.getMax());
                return this.child.toString(n) + "{" + this.getMin() + ",}";
            }
            if (this.getMin() < 0 && this.getMax() < 0) {
                return this.child.toString(n) + "*?";
            }
            if (this.getMin() == this.getMax()) {
                return this.child.toString(n) + "{" + this.getMin() + "}?";
            }
            if (this.getMin() >= 0 && this.getMax() >= 0) {
                return this.child.toString(n) + "{" + this.getMin() + "," + this.getMax() + "}?";
            }
            if (this.getMin() < 0) throw new RuntimeException("Token#toString(): NONGREEDYCLOSURE " + this.getMin() + ", " + this.getMax());
            if (this.getMax() >= 0) throw new RuntimeException("Token#toString(): NONGREEDYCLOSURE " + this.getMin() + ", " + this.getMax());
            return this.child.toString(n) + "{" + this.getMin() + ",}?";
        }
    }

    static class CharToken
    extends Token
    implements Serializable {
        private static final long serialVersionUID = -4394272816279496989L;
        final int chardata;

        CharToken(int n, int n2) {
            super(n);
            this.chardata = n2;
        }

        @Override
        int getChar() {
            return this.chardata;
        }

        @Override
        public String toString(int n) {
            String string;
            block0 : switch (this.type) {
                case 0: {
                    switch (this.chardata) {
                        case 40: 
                        case 41: 
                        case 42: 
                        case 43: 
                        case 46: 
                        case 63: 
                        case 91: 
                        case 92: 
                        case 123: 
                        case 124: {
                            string = "\\" + (char)this.chardata;
                            break block0;
                        }
                        case 12: {
                            string = "\\f";
                            break block0;
                        }
                        case 10: {
                            string = "\\n";
                            break block0;
                        }
                        case 13: {
                            string = "\\r";
                            break block0;
                        }
                        case 9: {
                            string = "\\t";
                            break block0;
                        }
                        case 27: {
                            string = "\\e";
                            break block0;
                        }
                    }
                    if (this.chardata >= 65536) {
                        String string2 = "0" + Integer.toHexString(this.chardata);
                        string = "\\v" + string2.substring(string2.length() - 6, string2.length());
                        break;
                    }
                    string = "" + (char)this.chardata;
                    break;
                }
                case 8: {
                    if (this == token_linebeginning || this == token_lineend) {
                        string = "" + (char)this.chardata;
                        break;
                    }
                    string = "\\" + (char)this.chardata;
                    break;
                }
                default: {
                    string = null;
                }
            }
            return string;
        }

        @Override
        boolean match(int n) {
            if (this.type == 0) {
                return n == this.chardata;
            }
            throw new RuntimeException("NFAArrow#match(): Internal error: " + this.type);
        }
    }

    static class ConcatToken
    extends Token
    implements Serializable {
        private static final long serialVersionUID = 8717321425541346381L;
        final Token child;
        final Token child2;

        ConcatToken(Token token, Token token2) {
            super(1);
            this.child = token;
            this.child2 = token2;
        }

        @Override
        int size() {
            return 2;
        }

        @Override
        Token getChild(int n) {
            return n == 0 ? this.child : this.child2;
        }

        @Override
        public String toString(int n) {
            String string = this.child2.type == 3 && this.child2.getChild(0) == this.child ? this.child.toString(n) + "+" : (this.child2.type == 9 && this.child2.getChild(0) == this.child ? this.child.toString(n) + "+?" : this.child.toString(n) + this.child2.toString(n));
            return string;
        }
    }

    static class StringToken
    extends Token
    implements Serializable {
        private static final long serialVersionUID = -4614366944218504172L;
        String string;
        final int refNumber;

        StringToken(int n, String string, int n2) {
            super(n);
            this.string = string;
            this.refNumber = n2;
        }

        @Override
        int getReferenceNumber() {
            return this.refNumber;
        }

        @Override
        String getString() {
            return this.string;
        }

        @Override
        public String toString(int n) {
            if (this.type == 12) {
                return "\\" + this.refNumber;
            }
            return REUtil.quoteMeta(this.string);
        }
    }

    static class FixedStringContainer {
        Token token = null;
        int options = 0;

        FixedStringContainer() {
        }
    }
}

