/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import java.io.Serializable;
import java.util.Vector;
import org.apache.regexp.CharacterIterator;
import org.apache.regexp.RECompiler;
import org.apache.regexp.REProgram;
import org.apache.regexp.RESyntaxException;
import org.apache.regexp.StringCharacterIterator;

public class RE
implements Serializable {
    public static final int MATCH_NORMAL = 0;
    public static final int MATCH_CASEINDEPENDENT = 1;
    public static final int MATCH_MULTILINE = 2;
    public static final int MATCH_SINGLELINE = 4;
    static final char OP_END = 'E';
    static final char OP_BOL = '^';
    static final char OP_EOL = '$';
    static final char OP_ANY = '.';
    static final char OP_ANYOF = '[';
    static final char OP_BRANCH = '|';
    static final char OP_ATOM = 'A';
    static final char OP_STAR = '*';
    static final char OP_PLUS = '+';
    static final char OP_MAYBE = '?';
    static final char OP_ESCAPE = '\\';
    static final char OP_OPEN = '(';
    static final char OP_OPEN_CLUSTER = '<';
    static final char OP_CLOSE = ')';
    static final char OP_CLOSE_CLUSTER = '>';
    static final char OP_BACKREF = '#';
    static final char OP_GOTO = 'G';
    static final char OP_NOTHING = 'N';
    static final char OP_RELUCTANTSTAR = '8';
    static final char OP_RELUCTANTPLUS = '=';
    static final char OP_RELUCTANTMAYBE = '/';
    static final char OP_POSIXCLASS = 'P';
    static final char E_ALNUM = 'w';
    static final char E_NALNUM = 'W';
    static final char E_BOUND = 'b';
    static final char E_NBOUND = 'B';
    static final char E_SPACE = 's';
    static final char E_NSPACE = 'S';
    static final char E_DIGIT = 'd';
    static final char E_NDIGIT = 'D';
    static final char POSIX_CLASS_ALNUM = 'w';
    static final char POSIX_CLASS_ALPHA = 'a';
    static final char POSIX_CLASS_BLANK = 'b';
    static final char POSIX_CLASS_CNTRL = 'c';
    static final char POSIX_CLASS_DIGIT = 'd';
    static final char POSIX_CLASS_GRAPH = 'g';
    static final char POSIX_CLASS_LOWER = 'l';
    static final char POSIX_CLASS_PRINT = 'p';
    static final char POSIX_CLASS_PUNCT = '!';
    static final char POSIX_CLASS_SPACE = 's';
    static final char POSIX_CLASS_UPPER = 'u';
    static final char POSIX_CLASS_XDIGIT = 'x';
    static final char POSIX_CLASS_JSTART = 'j';
    static final char POSIX_CLASS_JPART = 'k';
    static final int maxNode = 65536;
    static final int MAX_PAREN = 16;
    static final int offsetOpcode = 0;
    static final int offsetOpdata = 1;
    static final int offsetNext = 2;
    static final int nodeSize = 3;
    REProgram program;
    transient CharacterIterator search;
    int matchFlags;
    int maxParen = 16;
    transient int parenCount;
    transient int start0;
    transient int end0;
    transient int start1;
    transient int end1;
    transient int start2;
    transient int end2;
    transient int[] startn;
    transient int[] endn;
    transient int[] startBackref;
    transient int[] endBackref;
    public static final int REPLACE_ALL = 0;
    public static final int REPLACE_FIRSTONLY = 1;
    public static final int REPLACE_BACKREFERENCES = 2;

    public RE(String string) throws RESyntaxException {
        this(string, 0);
    }

    public RE(String string, int n) throws RESyntaxException {
        this(new RECompiler().compile(string));
        this.setMatchFlags(n);
    }

    public RE(REProgram rEProgram, int n) {
        this.setProgram(rEProgram);
        this.setMatchFlags(n);
    }

    public RE(REProgram rEProgram) {
        this(rEProgram, 0);
    }

    public RE() {
        this((REProgram)null, 0);
    }

    public static String simplePatternToFullRegularExpression(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        int n = 0;
        while (n < string.length()) {
            char c = string.charAt(n);
            switch (c) {
                case '*': {
                    stringBuffer.append(".*");
                    break;
                }
                case '$': 
                case '(': 
                case ')': 
                case '+': 
                case '.': 
                case '?': 
                case '[': 
                case '\\': 
                case ']': 
                case '^': 
                case '{': 
                case '|': 
                case '}': {
                    stringBuffer.append('\\');
                }
                default: {
                    stringBuffer.append(c);
                }
            }
            ++n;
        }
        return stringBuffer.toString();
    }

    public void setMatchFlags(int n) {
        this.matchFlags = n;
    }

    public int getMatchFlags() {
        return this.matchFlags;
    }

    public void setProgram(REProgram rEProgram) {
        this.program = rEProgram;
        this.maxParen = rEProgram != null && rEProgram.maxParens != -1 ? rEProgram.maxParens : 16;
    }

    public REProgram getProgram() {
        return this.program;
    }

    public int getParenCount() {
        return this.parenCount;
    }

    public String getParen(int n) {
        int n2;
        if (n < this.parenCount && (n2 = this.getParenStart(n)) >= 0) {
            return this.search.substring(n2, this.getParenEnd(n));
        }
        return null;
    }

    public final int getParenStart(int n) {
        if (n < this.parenCount) {
            switch (n) {
                case 0: {
                    return this.start0;
                }
                case 1: {
                    return this.start1;
                }
                case 2: {
                    return this.start2;
                }
            }
            if (this.startn == null) {
                this.allocParens();
            }
            return this.startn[n];
        }
        return -1;
    }

    public final int getParenEnd(int n) {
        if (n < this.parenCount) {
            switch (n) {
                case 0: {
                    return this.end0;
                }
                case 1: {
                    return this.end1;
                }
                case 2: {
                    return this.end2;
                }
            }
            if (this.endn == null) {
                this.allocParens();
            }
            return this.endn[n];
        }
        return -1;
    }

    public final int getParenLength(int n) {
        if (n < this.parenCount) {
            return this.getParenEnd(n) - this.getParenStart(n);
        }
        return -1;
    }

    protected final void setParenStart(int n, int n2) {
        if (n < this.parenCount) {
            switch (n) {
                case 0: {
                    this.start0 = n2;
                    break;
                }
                case 1: {
                    this.start1 = n2;
                    break;
                }
                case 2: {
                    this.start2 = n2;
                    break;
                }
                default: {
                    if (this.startn == null) {
                        this.allocParens();
                    }
                    this.startn[n] = n2;
                }
            }
        }
    }

    protected final void setParenEnd(int n, int n2) {
        if (n < this.parenCount) {
            switch (n) {
                case 0: {
                    this.end0 = n2;
                    break;
                }
                case 1: {
                    this.end1 = n2;
                    break;
                }
                case 2: {
                    this.end2 = n2;
                    break;
                }
                default: {
                    if (this.endn == null) {
                        this.allocParens();
                    }
                    this.endn[n] = n2;
                }
            }
        }
    }

    protected void internalError(String string) throws Error {
        throw new Error("RE internal error: " + string);
    }

    private final void allocParens() {
        this.startn = new int[this.maxParen];
        this.endn = new int[this.maxParen];
        int n = 0;
        while (n < this.maxParen) {
            this.startn[n] = -1;
            this.endn[n] = -1;
            ++n;
        }
    }

    protected int matchNodes(int n, int n2, int n3) {
        int n4 = n3;
        char[] cArray = this.program.instruction;
        int n5 = n;
        block50: while (n5 < n2) {
            char c = cArray[n5 + 0];
            int n6 = n5 + (short)cArray[n5 + 2];
            short s = cArray[n5 + 1];
            block0 : switch (c) {
                case '/': {
                    char c2 = '\u0000';
                    do {
                        int n7;
                        if ((n7 = this.matchNodes(n6, 65536, n4)) == -1) continue;
                        return n7;
                    } while (c2++ == '\u0000' && (n4 = this.matchNodes(n5 + 3, n6, n4)) != -1);
                    return -1;
                }
                case '=': {
                    while ((n4 = this.matchNodes(n5 + 3, n6, n4)) != -1) {
                        int n8 = this.matchNodes(n6, 65536, n4);
                        if (n8 == -1) continue;
                        return n8;
                    }
                    return -1;
                }
                case '8': {
                    do {
                        int n9;
                        if ((n9 = this.matchNodes(n6, 65536, n4)) == -1) continue;
                        return n9;
                    } while ((n4 = this.matchNodes(n5 + 3, n6, n4)) != -1);
                    return -1;
                }
                case '(': {
                    int n10;
                    if ((this.program.flags & 1) != 0) {
                        this.startBackref[s] = n4;
                    }
                    if ((n10 = this.matchNodes(n6, 65536, n4)) != -1) {
                        if (s + 1 > this.parenCount) {
                            this.parenCount = s + '\u0001';
                        }
                        if (this.getParenStart(s) == -1) {
                            this.setParenStart(s, n4);
                        }
                    }
                    return n10;
                }
                case ')': {
                    int n11;
                    if ((this.program.flags & 1) != 0) {
                        this.endBackref[s] = n4;
                    }
                    if ((n11 = this.matchNodes(n6, 65536, n4)) != -1) {
                        if (s + 1 > this.parenCount) {
                            this.parenCount = s + '\u0001';
                        }
                        if (this.getParenEnd(s) == -1) {
                            this.setParenEnd(s, n4);
                        }
                    }
                    return n11;
                }
                case '<': 
                case '>': {
                    return this.matchNodes(n6, 65536, n4);
                }
                case '#': {
                    char c2 = this.startBackref[s];
                    short s2 = this.endBackref[s];
                    if (c2 == '\uffffffff' || s2 == -1) {
                        return -1;
                    }
                    if (c2 == s2) break;
                    int n12 = s2 - c2;
                    if (this.search.isEnd(n4 + n12 - 1)) {
                        return -1;
                    }
                    int n13 = (this.matchFlags & 1) != 0 ? 1 : 0;
                    int n14 = 0;
                    while (n14 < n12) {
                        if (this.compareChars(this.search.charAt(n4++), this.search.charAt(c2 + n14), n13 != 0) != 0) {
                            return -1;
                        }
                        ++n14;
                    }
                    break;
                }
                case '^': {
                    if (n4 == 0) break;
                    if ((this.matchFlags & 2) == 2) {
                        if (n4 > 0 && this.isNewline(n4 - 1)) break;
                        return -1;
                    }
                    return -1;
                }
                case '$': {
                    if (this.search.isEnd(0) || this.search.isEnd(n4)) break;
                    if ((this.matchFlags & 2) == 2) {
                        if (this.isNewline(n4)) break;
                        return -1;
                    }
                    return -1;
                }
                case '\\': {
                    short s2;
                    char c2;
                    switch (s) {
                        case 66: 
                        case 98: {
                            c2 = n4 == 0 ? (char)'\n' : (char)this.search.charAt(n4 - 1);
                            s2 = this.search.isEnd(n4) ? (short)10 : (short)this.search.charAt(n4);
                            if (Character.isLetterOrDigit(c2) == Character.isLetterOrDigit((char)s2) != (s == 98)) break block0;
                            return -1;
                        }
                        case 68: 
                        case 83: 
                        case 87: 
                        case 100: 
                        case 115: 
                        case 119: {
                            if (this.search.isEnd(n4)) {
                                return -1;
                            }
                            c2 = this.search.charAt(n4);
                            switch (s) {
                                case 87: 
                                case 119: {
                                    if ((Character.isLetterOrDigit(c2) || c2 == '_') == (s == 119)) break;
                                    return -1;
                                }
                                case 68: 
                                case 100: {
                                    if (Character.isDigit(c2) == (s == 100)) break;
                                    return -1;
                                }
                                case 83: 
                                case 115: {
                                    if (Character.isWhitespace(c2) == (s == 115)) break;
                                    return -1;
                                }
                            }
                            ++n4;
                            break;
                        }
                        default: {
                            this.internalError("Unrecognized escape '" + s + "'");
                            break;
                        }
                    }
                    break;
                }
                case '.': {
                    if ((this.matchFlags & 4) == 4 ? this.search.isEnd(n4) : this.search.isEnd(n4) || this.isNewline(n4)) {
                        return -1;
                    }
                    ++n4;
                    break;
                }
                case 'A': {
                    if (this.search.isEnd(n4)) {
                        return -1;
                    }
                    short s2 = s;
                    int n12 = n5 + 3;
                    if (this.search.isEnd(s2 + n4 - 1)) {
                        return -1;
                    }
                    int n13 = (this.matchFlags & 1) != 0 ? 1 : 0;
                    int n14 = 0;
                    while (n14 < s2) {
                        if (this.compareChars(this.search.charAt(n4++), cArray[n12 + n14], n13 != 0) != 0) {
                            return -1;
                        }
                        ++n14;
                    }
                    break;
                }
                case 'P': {
                    short s2;
                    if (this.search.isEnd(n4)) {
                        return -1;
                    }
                    block28 : switch (s) {
                        case 119: {
                            if (Character.isLetterOrDigit(this.search.charAt(n4))) break;
                            return -1;
                        }
                        case 97: {
                            if (Character.isLetter(this.search.charAt(n4))) break;
                            return -1;
                        }
                        case 100: {
                            if (Character.isDigit(this.search.charAt(n4))) break;
                            return -1;
                        }
                        case 98: {
                            if (Character.isSpaceChar(this.search.charAt(n4))) break;
                            return -1;
                        }
                        case 115: {
                            if (Character.isWhitespace(this.search.charAt(n4))) break;
                            return -1;
                        }
                        case 99: {
                            if (Character.getType(this.search.charAt(n4)) == 15) break;
                            return -1;
                        }
                        case 103: {
                            switch (Character.getType(this.search.charAt(n4))) {
                                case 25: 
                                case 26: 
                                case 27: 
                                case 28: {
                                    break block28;
                                }
                            }
                            return -1;
                        }
                        case 108: {
                            if (Character.getType(this.search.charAt(n4)) == 2) break;
                            return -1;
                        }
                        case 117: {
                            if (Character.getType(this.search.charAt(n4)) == 1) break;
                            return -1;
                        }
                        case 112: {
                            if (Character.getType(this.search.charAt(n4)) != 15) break;
                            return -1;
                        }
                        case 33: {
                            s2 = Character.getType(this.search.charAt(n4));
                            switch (s2) {
                                case 20: 
                                case 21: 
                                case 22: 
                                case 23: 
                                case 24: {
                                    break block28;
                                }
                            }
                            return -1;
                        }
                        case 120: {
                            short s3 = s2 = this.search.charAt(n4) >= '0' && this.search.charAt(n4) <= '9' || this.search.charAt(n4) >= 'a' && this.search.charAt(n4) <= 'f' || this.search.charAt(n4) >= 'A' && this.search.charAt(n4) <= 'F' ? (short)1 : 0;
                            if (s2 != 0) break;
                            return -1;
                        }
                        case 106: {
                            if (Character.isJavaIdentifierStart(this.search.charAt(n4))) break;
                            return -1;
                        }
                        case 107: {
                            if (Character.isJavaIdentifierPart(this.search.charAt(n4))) break;
                            return -1;
                        }
                        default: {
                            this.internalError("Bad posix class");
                        }
                    }
                    ++n4;
                    break;
                }
                case '[': {
                    if (this.search.isEnd(n4)) {
                        return -1;
                    }
                    short s2 = (short)this.search.charAt(n4);
                    int n12 = (this.matchFlags & 1) != 0 ? 1 : 0;
                    int n13 = n5 + 3;
                    int n14 = n13 + s * 2;
                    boolean bl = false;
                    int n15 = n13;
                    while (!bl && n15 < n14) {
                        char c3 = cArray[n15++];
                        char c4 = cArray[n15++];
                        boolean bl2 = bl = this.compareChars((char)s2, c3, n12 != 0) >= 0 && this.compareChars((char)s2, c4, n12 != 0) <= 0;
                    }
                    if (!bl) {
                        return -1;
                    }
                    ++n4;
                    break;
                }
                case '|': {
                    short s2;
                    if (cArray[n6 + 0] != '|') {
                        n5 += 3;
                        continue block50;
                    }
                    do {
                        int n16;
                        if ((n16 = this.matchNodes(n5 + 3, 65536, n4)) == -1) continue;
                        return n16;
                    } while ((s2 = (short)cArray[n5 + 2]) != 0 && cArray[(n5 += s2) + 0] == '|');
                    return -1;
                }
                case 'G': 
                case 'N': {
                    break;
                }
                case 'E': {
                    this.setParenEnd(0, n4);
                    return n4;
                }
                default: {
                    this.internalError("Invalid opcode '" + c + "'");
                }
            }
            n5 = n6;
        }
        this.internalError("Corrupt program");
        return -1;
    }

    protected boolean matchAt(int n) {
        int n2;
        this.start0 = -1;
        this.end0 = -1;
        this.start1 = -1;
        this.end1 = -1;
        this.start2 = -1;
        this.end2 = -1;
        this.startn = null;
        this.endn = null;
        this.parenCount = 1;
        this.setParenStart(0, n);
        if ((this.program.flags & 1) != 0) {
            this.startBackref = new int[this.maxParen];
            this.endBackref = new int[this.maxParen];
        }
        if ((n2 = this.matchNodes(0, 65536, n)) != -1) {
            this.setParenEnd(0, n2);
            return true;
        }
        this.parenCount = 0;
        return false;
    }

    public boolean match(String string, int n) {
        return this.match(new StringCharacterIterator(string), n);
    }

    /*
     * Unable to fully structure code
     */
    public boolean match(CharacterIterator var1_1, int var2_2) {
        block9: {
            if (this.program == null) {
                this.internalError("No RE program to run!");
            }
            this.search = var1_1;
            if ((this.program.flags & 2) != 2) break block9;
            if ((this.matchFlags & 2) != 0) ** GOTO lbl15
            return var2_2 == 0 && this.matchAt(var2_2) != false;
lbl-1000:
            // 1 sources

            {
                block10: {
                    if (this.isNewline(var2_2)) break block10;
                    if (!this.matchAt(var2_2)) ** GOTO lbl12
                    return true;
                    while (!this.isNewline(var2_2)) {
                        ++var2_2;
lbl12:
                        // 2 sources

                        if (!var1_1.isEnd(var2_2)) continue;
                    }
                }
                ++var2_2;
lbl15:
                // 2 sources

                ** while (!var1_1.isEnd((int)var2_2))
            }
lbl16:
            // 1 sources

            return false;
        }
        if (this.program.prefix == null) {
            while (!var1_1.isEnd(var2_2 - 1)) {
                if (this.matchAt(var2_2)) {
                    return true;
                }
                ++var2_2;
            }
            return false;
        }
        var3_3 = (this.matchFlags & 1) != 0;
        var4_4 = this.program.prefix;
        while (!var1_1.isEnd(var2_2 + var4_4.length - 1)) {
            var5_5 = var2_2;
            var6_6 = 0;
            do {
                v0 = var7_7 = this.compareChars(var1_1.charAt(var5_5++), var4_4[var6_6++], var3_3) == 0;
            } while (var7_7 && var6_6 < var4_4.length);
            if (var6_6 == var4_4.length && this.matchAt(var2_2)) {
                return true;
            }
            ++var2_2;
        }
        return false;
    }

    public boolean match(String string) {
        return this.match(string, 0);
    }

    public String[] split(String string) {
        Vector<String> vector = new Vector<String>();
        int n = 0;
        int n2 = string.length();
        while (n < n2 && this.match(string, n)) {
            int n3 = this.getParenStart(0);
            int n4 = this.getParenEnd(0);
            if (n4 == n) {
                vector.addElement(string.substring(n, n3 + 1));
            } else {
                vector.addElement(string.substring(n, n3));
            }
            n = ++n4;
        }
        String string2 = string.substring(n);
        if (string2.length() != 0) {
            vector.addElement(string2);
        }
        Object[] objectArray = new String[vector.size()];
        vector.copyInto(objectArray);
        return objectArray;
    }

    public String subst(String string, String string2) {
        return this.subst(string, string2, 0);
    }

    public String subst(String string, String string2, int n) {
        StringBuffer stringBuffer = new StringBuffer();
        int n2 = 0;
        int n3 = string.length();
        while (n2 < n3 && this.match(string, n2)) {
            int n4;
            stringBuffer.append(string.substring(n2, this.getParenStart(0)));
            if ((n & 2) != 0) {
                n4 = 0;
                int n5 = -2;
                int n6 = string2.length();
                boolean bl = false;
                while ((n4 = string2.indexOf("$", n4)) >= 0) {
                    char c;
                    if ((n4 == 0 || string2.charAt(n4 - 1) != '\\') && n4 + 1 < n6 && (c = string2.charAt(n4 + 1)) >= '0' && c <= '9') {
                        if (!bl) {
                            stringBuffer.append(string2.substring(0, n4));
                            bl = true;
                        } else {
                            stringBuffer.append(string2.substring(n5 + 2, n4));
                        }
                        stringBuffer.append(this.getParen(c - 48));
                        n5 = n4;
                    }
                    ++n4;
                }
                stringBuffer.append(string2.substring(n5 + 2, n6));
            } else {
                stringBuffer.append(string2);
            }
            n4 = this.getParenEnd(0);
            if (n4 == n2) {
                // empty if block
            }
            n2 = ++n4;
            if ((n & 1) != 0) break;
        }
        if (n2 < n3) {
            stringBuffer.append(string.substring(n2));
        }
        return stringBuffer.toString();
    }

    public String[] grep(Object[] objectArray) {
        Object object;
        Vector<Object[]> vector = new Vector<Object[]>();
        int n = 0;
        while (n < objectArray.length) {
            object = objectArray[n].toString();
            if (this.match((String)object)) {
                vector.addElement((Object[])object);
            }
            ++n;
        }
        object = new String[vector.size()];
        vector.copyInto((Object[])object);
        return object;
    }

    private boolean isNewline(int n) {
        char c = this.search.charAt(n);
        return c == '\n' || c == '\r' || c == '\u0085' || c == '\u2028' || c == '\u2029';
    }

    private int compareChars(char c, char c2, boolean bl) {
        if (bl) {
            c = Character.toLowerCase(c);
            c2 = Character.toLowerCase(c2);
        }
        return c - c2;
    }
}

