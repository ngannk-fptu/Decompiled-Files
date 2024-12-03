/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import java.util.Hashtable;
import org.apache.regexp.REProgram;
import org.apache.regexp.RESyntaxException;

public class RECompiler {
    char[] instruction = new char[128];
    int lenInstruction = 0;
    String pattern;
    int len;
    int idx;
    int parens;
    static final int NODE_NORMAL = 0;
    static final int NODE_NULLABLE = 1;
    static final int NODE_TOPLEVEL = 2;
    static final int ESC_MASK = 1048560;
    static final int ESC_BACKREF = 1048575;
    static final int ESC_COMPLEX = 1048574;
    static final int ESC_CLASS = 1048573;
    int maxBrackets = 10;
    static final int bracketUnbounded = -1;
    int brackets = 0;
    int[] bracketStart = null;
    int[] bracketEnd = null;
    int[] bracketMin = null;
    int[] bracketOpt = null;
    static Hashtable hashPOSIX = new Hashtable();

    void ensure(int n) {
        int n2 = this.instruction.length;
        if (this.lenInstruction + n >= n2) {
            while (this.lenInstruction + n >= n2) {
                n2 *= 2;
            }
            char[] cArray = new char[n2];
            System.arraycopy(this.instruction, 0, cArray, 0, this.lenInstruction);
            this.instruction = cArray;
        }
    }

    void emit(char c) {
        this.ensure(1);
        this.instruction[this.lenInstruction++] = c;
    }

    void nodeInsert(char c, int n, int n2) {
        this.ensure(3);
        System.arraycopy(this.instruction, n2, this.instruction, n2 + 3, this.lenInstruction - n2);
        this.instruction[n2 + 0] = c;
        this.instruction[n2 + 1] = (char)n;
        this.instruction[n2 + 2] = '\u0000';
        this.lenInstruction += 3;
    }

    void setNextOfEnd(int n, int n2) {
        char c = this.instruction[n + 2];
        while (c != '\u0000' && n < this.lenInstruction) {
            if (n == n2) {
                n2 = this.lenInstruction;
            }
            c = this.instruction[(n += c) + 2];
        }
        if (n < this.lenInstruction) {
            this.instruction[n + 2] = (char)(n2 - n);
        }
    }

    int node(char c, int n) {
        this.ensure(3);
        this.instruction[this.lenInstruction + 0] = c;
        this.instruction[this.lenInstruction + 1] = (char)n;
        this.instruction[this.lenInstruction + 2] = '\u0000';
        this.lenInstruction += 3;
        return this.lenInstruction - 3;
    }

    void internalError() throws Error {
        throw new Error("Internal error!");
    }

    void syntaxError(String string) throws RESyntaxException {
        throw new RESyntaxException(string);
    }

    void allocBrackets() {
        if (this.bracketStart == null) {
            this.bracketStart = new int[this.maxBrackets];
            this.bracketEnd = new int[this.maxBrackets];
            this.bracketMin = new int[this.maxBrackets];
            this.bracketOpt = new int[this.maxBrackets];
            int n = 0;
            while (n < this.maxBrackets) {
                this.bracketOpt[n] = -1;
                this.bracketMin[n] = -1;
                this.bracketEnd[n] = -1;
                this.bracketStart[n] = -1;
                ++n;
            }
        }
    }

    synchronized void reallocBrackets() {
        if (this.bracketStart == null) {
            this.allocBrackets();
        }
        int n = this.maxBrackets * 2;
        int[] nArray = new int[n];
        int[] nArray2 = new int[n];
        int[] nArray3 = new int[n];
        int[] nArray4 = new int[n];
        int n2 = this.brackets;
        while (n2 < n) {
            nArray4[n2] = -1;
            nArray3[n2] = -1;
            nArray2[n2] = -1;
            nArray[n2] = -1;
            ++n2;
        }
        System.arraycopy(this.bracketStart, 0, nArray, 0, this.brackets);
        System.arraycopy(this.bracketEnd, 0, nArray2, 0, this.brackets);
        System.arraycopy(this.bracketMin, 0, nArray3, 0, this.brackets);
        System.arraycopy(this.bracketOpt, 0, nArray4, 0, this.brackets);
        this.bracketStart = nArray;
        this.bracketEnd = nArray2;
        this.bracketMin = nArray3;
        this.bracketOpt = nArray4;
        this.maxBrackets = n;
    }

    void bracket() throws RESyntaxException {
        if (this.idx >= this.len || this.pattern.charAt(this.idx++) != '{') {
            this.internalError();
        }
        if (this.idx >= this.len || !Character.isDigit(this.pattern.charAt(this.idx))) {
            this.syntaxError("Expected digit");
        }
        StringBuffer stringBuffer = new StringBuffer();
        while (this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx))) {
            stringBuffer.append(this.pattern.charAt(this.idx++));
        }
        try {
            this.bracketMin[this.brackets] = Integer.parseInt(stringBuffer.toString());
        }
        catch (NumberFormatException numberFormatException) {
            this.syntaxError("Expected valid number");
        }
        if (this.idx >= this.len) {
            this.syntaxError("Expected comma or right bracket");
        }
        if (this.pattern.charAt(this.idx) == '}') {
            ++this.idx;
            this.bracketOpt[this.brackets] = 0;
            return;
        }
        if (this.idx >= this.len || this.pattern.charAt(this.idx++) != ',') {
            this.syntaxError("Expected comma");
        }
        if (this.idx >= this.len) {
            this.syntaxError("Expected comma or right bracket");
        }
        if (this.pattern.charAt(this.idx) == '}') {
            ++this.idx;
            this.bracketOpt[this.brackets] = -1;
            return;
        }
        if (this.idx >= this.len || !Character.isDigit(this.pattern.charAt(this.idx))) {
            this.syntaxError("Expected digit");
        }
        stringBuffer.setLength(0);
        while (this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx))) {
            stringBuffer.append(this.pattern.charAt(this.idx++));
        }
        try {
            this.bracketOpt[this.brackets] = Integer.parseInt(stringBuffer.toString()) - this.bracketMin[this.brackets];
        }
        catch (NumberFormatException numberFormatException) {
            this.syntaxError("Expected valid number");
        }
        if (this.bracketOpt[this.brackets] < 0) {
            this.syntaxError("Bad range");
        }
        if (this.idx >= this.len || this.pattern.charAt(this.idx++) != '}') {
            this.syntaxError("Missing close brace");
        }
    }

    int escape() throws RESyntaxException {
        if (this.pattern.charAt(this.idx) != '\\') {
            this.internalError();
        }
        if (this.idx + 1 == this.len) {
            this.syntaxError("Escape terminates string");
        }
        this.idx += 2;
        char c = this.pattern.charAt(this.idx - 1);
        switch (c) {
            case 'B': 
            case 'b': {
                return 1048574;
            }
            case 'D': 
            case 'S': 
            case 'W': 
            case 'd': 
            case 's': 
            case 'w': {
                return 1048573;
            }
            case 'u': 
            case 'x': {
                int n = c == 'u' ? 4 : 2;
                int n2 = 0;
                while (this.idx < this.len && n-- > 0) {
                    char c2 = this.pattern.charAt(this.idx);
                    if (c2 >= '0' && c2 <= '9') {
                        n2 = (n2 << 4) + c2 - 48;
                    } else if ((c2 = Character.toLowerCase(c2)) >= 'a' && c2 <= 'f') {
                        n2 = (n2 << 4) + (c2 - 97) + 10;
                    } else {
                        this.syntaxError("Expected " + n + " hexadecimal digits after \\" + c);
                    }
                    ++this.idx;
                }
                return n2;
            }
            case 't': {
                return 9;
            }
            case 'n': {
                return 10;
            }
            case 'r': {
                return 13;
            }
            case 'f': {
                return 12;
            }
            case '0': 
            case '1': 
            case '2': 
            case '3': 
            case '4': 
            case '5': 
            case '6': 
            case '7': 
            case '8': 
            case '9': {
                if (this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx)) || c == '0') {
                    int n = c - 48;
                    if (this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx))) {
                        n = (n << 3) + (this.pattern.charAt(this.idx++) - 48);
                        if (this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx))) {
                            n = (n << 3) + (this.pattern.charAt(this.idx++) - 48);
                        }
                    }
                    return n;
                }
                return 1048575;
            }
        }
        return c;
    }

    int characterClass() throws RESyntaxException {
        int n;
        char c;
        int n2;
        if (this.pattern.charAt(this.idx) != '[') {
            this.internalError();
        }
        if (this.idx + 1 >= this.len || this.pattern.charAt(++this.idx) == ']') {
            this.syntaxError("Empty or unterminated class");
        }
        if (this.idx < this.len && this.pattern.charAt(this.idx) == ':') {
            ++this.idx;
            n2 = this.idx;
            while (this.idx < this.len && this.pattern.charAt(this.idx) >= 'a' && this.pattern.charAt(this.idx) <= 'z') {
                ++this.idx;
            }
            if (this.idx + 1 < this.len && this.pattern.charAt(this.idx) == ':' && this.pattern.charAt(this.idx + 1) == ']') {
                String string = this.pattern.substring(n2, this.idx);
                Character c2 = (Character)hashPOSIX.get(string);
                if (c2 != null) {
                    this.idx += 2;
                    return this.node('P', c2.charValue());
                }
                this.syntaxError("Invalid POSIX character class '" + string + "'");
            }
            this.syntaxError("Invalid POSIX character class syntax");
        }
        n2 = this.node('[', 0);
        char c3 = c = '\uffff';
        char c4 = '\u0000';
        boolean bl = true;
        boolean bl2 = false;
        int n3 = this.idx;
        char c5 = '\u0000';
        RERange rERange = new RERange();
        block18: while (this.idx < this.len && this.pattern.charAt(this.idx) != ']') {
            switch (this.pattern.charAt(this.idx)) {
                case '^': {
                    boolean bl3 = bl = !bl;
                    if (this.idx == n3) {
                        rERange.include(0, 65535, true);
                    }
                    ++this.idx;
                    continue block18;
                }
                case '\\': {
                    n = this.escape();
                    switch (n) {
                        case 1048574: 
                        case 1048575: {
                            this.syntaxError("Bad character class");
                        }
                        case 1048573: {
                            if (bl2) {
                                this.syntaxError("Bad character class");
                            }
                            switch (this.pattern.charAt(this.idx - 1)) {
                                case 'S': {
                                    rERange.include(0, 7, bl);
                                    rERange.include('\u000b', bl);
                                    rERange.include(14, 31, bl);
                                    rERange.include(33, 65535, bl);
                                    break;
                                }
                                case 'W': {
                                    rERange.include(0, 47, bl);
                                    rERange.include(58, 64, bl);
                                    rERange.include(91, 94, bl);
                                    rERange.include('`', bl);
                                    rERange.include(123, 65535, bl);
                                    break;
                                }
                                case 'D': {
                                    rERange.include(0, 47, bl);
                                    rERange.include(58, 65535, bl);
                                    break;
                                }
                                case 's': {
                                    rERange.include('\t', bl);
                                    rERange.include('\r', bl);
                                    rERange.include('\f', bl);
                                    rERange.include('\n', bl);
                                    rERange.include('\b', bl);
                                    rERange.include(' ', bl);
                                    break;
                                }
                                case 'w': {
                                    rERange.include(97, 122, bl);
                                    rERange.include(65, 90, bl);
                                    rERange.include('_', bl);
                                }
                                case 'd': {
                                    rERange.include(48, 57, bl);
                                }
                            }
                            c3 = c;
                            continue block18;
                        }
                        default: {
                            c4 = (char)n;
                            break;
                        }
                    }
                    break;
                }
                case '-': {
                    if (bl2) {
                        this.syntaxError("Bad class range");
                    }
                    bl2 = true;
                    char c6 = c5 = c3 == c ? (char)'\u0000' : c3;
                    if (this.idx + 1 >= this.len || this.pattern.charAt(++this.idx) != ']') continue block18;
                    c4 = (char)65535;
                    break;
                }
                default: {
                    c4 = this.pattern.charAt(this.idx++);
                }
            }
            if (bl2) {
                char c7 = c4;
                if (c5 >= c7) {
                    this.syntaxError("Bad character class");
                }
                rERange.include(c5, c7, bl);
                c3 = c;
                bl2 = false;
                continue;
            }
            if (this.idx >= this.len || this.pattern.charAt(this.idx) != '-') {
                rERange.include(c4, bl);
            }
            c3 = c4;
        }
        if (this.idx == this.len) {
            this.syntaxError("Unterminated character class");
        }
        ++this.idx;
        this.instruction[n2 + 1] = (char)rERange.num;
        n = 0;
        while (n < rERange.num) {
            this.emit((char)rERange.minRange[n]);
            this.emit((char)rERange.maxRange[n]);
            ++n;
        }
        return n2;
    }

    /*
     * Unable to fully structure code
     */
    int atom() throws RESyntaxException {
        var1_1 = this.node('A', 0);
        var2_2 = 0;
        block8: while (this.idx < this.len) {
            if (this.idx + 1 >= this.len) ** GOTO lbl-1000
            var3_3 = this.pattern.charAt(this.idx + 1);
            if (this.pattern.charAt(this.idx) == '\\') {
                var4_4 = this.idx;
                this.escape();
                if (this.idx < this.len) {
                    var3_3 = this.pattern.charAt(this.idx);
                }
                this.idx = var4_4;
            }
            block0 : switch (var3_3) {
                case 42: 
                case 43: 
                case 63: 
                case 123: {
                    if (var2_2 != 0) break block8;
                }
                default: lbl-1000:
                // 2 sources

                {
                    switch (this.pattern.charAt(this.idx)) {
                        case '$': 
                        case '(': 
                        case ')': 
                        case '.': 
                        case '[': 
                        case ']': 
                        case '^': 
                        case '|': {
                            break block8;
                        }
                        case '*': 
                        case '+': 
                        case '?': 
                        case '{': {
                            if (var2_2 != 0) break block8;
                            this.syntaxError("Missing operand to closure");
                            break block8;
                        }
                        case '\\': {
                            var3_3 = this.idx;
                            var4_4 = this.escape();
                            if ((var4_4 & 1048560) == 1048560) {
                                this.idx = var3_3;
                                break block8;
                            }
                            this.emit((char)var4_4);
                            ++var2_2;
                            break block0;
                        }
                        default: {
                            this.emit(this.pattern.charAt(this.idx++));
                            ++var2_2;
                        }
                    }
                }
            }
        }
        if (var2_2 == 0) {
            this.internalError();
        }
        this.instruction[var1_1 + 1] = (char)var2_2;
        return var1_1;
    }

    int terminal(int[] nArray) throws RESyntaxException {
        switch (this.pattern.charAt(this.idx)) {
            case '$': 
            case '.': 
            case '^': {
                return this.node(this.pattern.charAt(this.idx++), 0);
            }
            case '[': {
                return this.characterClass();
            }
            case '(': {
                return this.expr(nArray);
            }
            case ')': {
                this.syntaxError("Unexpected close paren");
            }
            case '|': {
                this.internalError();
            }
            case ']': {
                this.syntaxError("Mismatched class");
            }
            case '\u0000': {
                this.syntaxError("Unexpected end of input");
            }
            case '*': 
            case '+': 
            case '?': 
            case '{': {
                this.syntaxError("Missing operand to closure");
            }
            case '\\': {
                int n = this.idx;
                switch (this.escape()) {
                    case 1048573: 
                    case 1048574: {
                        nArray[0] = nArray[0] & 0xFFFFFFFE;
                        return this.node('\\', this.pattern.charAt(this.idx - 1));
                    }
                    case 1048575: {
                        char c = (char)(this.pattern.charAt(this.idx - 1) - 48);
                        if (this.parens <= c) {
                            this.syntaxError("Bad backreference");
                        }
                        nArray[0] = nArray[0] | 1;
                        return this.node('#', c);
                    }
                }
                this.idx = n;
                nArray[0] = nArray[0] & 0xFFFFFFFE;
            }
        }
        nArray[0] = nArray[0] & 0xFFFFFFFE;
        return this.atom();
    }

    /*
     * Unable to fully structure code
     */
    int closure(int[] var1_1) throws RESyntaxException {
        block31: {
            block30: {
                var2_2 = this.idx;
                var3_3 = new int[]{0};
                var4_4 = this.terminal(var3_3);
                var1_1[0] = var1_1[0] | var3_3[0];
                if (this.idx >= this.len) {
                    return var4_4;
                }
                var5_5 = true;
                var6_6 = this.pattern.charAt(this.idx);
                switch (var6_6) {
                    case 42: 
                    case 63: {
                        var1_1[0] = var1_1[0] | 1;
                    }
                    case 43: {
                        ++this.idx;
                    }
                    case 123: {
                        var7_7 = this.instruction[var4_4 + 0];
                        if (var7_7 == '^' || var7_7 == '$') {
                            this.syntaxError("Bad closure operand");
                        }
                        if ((var3_3[0] & 1) == 0) break;
                        this.syntaxError("Closure operand can't be nullable");
                    }
                }
                if (this.idx < this.len && this.pattern.charAt(this.idx) == '?') {
                    ++this.idx;
                    var5_5 = false;
                }
                if (!var5_5) break block30;
                switch (var6_6) {
                    case 123: {
                        var8_8 = 0;
                        this.allocBrackets();
                        var9_10 = 0;
                        while (var9_10 < this.brackets) {
                            if (this.bracketStart[var9_10] == this.idx) {
                                var8_8 = 1;
                                break;
                            }
                            ++var9_10;
                        }
                        if (var8_8 == 0) {
                            if (this.brackets >= this.maxBrackets) {
                                this.reallocBrackets();
                            }
                            this.bracketStart[this.brackets] = this.idx;
                            this.bracket();
                            this.bracketEnd[this.brackets] = this.idx;
                            var9_10 = this.brackets++;
                        }
                        v0 = var9_10;
                        v1 = this.bracketMin[v0];
                        this.bracketMin[v0] = v1 - 1;
                        if (v1 > 0) {
                            if (this.bracketMin[var9_10] > 0 || this.bracketOpt[var9_10] != 0) {
                                var10_11 = 0;
                                while (var10_11 < this.brackets) {
                                    if (var10_11 != var9_10 && this.bracketStart[var10_11] < this.idx && this.bracketStart[var10_11] >= var2_2) {
                                        --this.brackets;
                                        this.bracketStart[var10_11] = this.bracketStart[this.brackets];
                                        this.bracketEnd[var10_11] = this.bracketEnd[this.brackets];
                                        this.bracketMin[var10_11] = this.bracketMin[this.brackets];
                                        this.bracketOpt[var10_11] = this.bracketOpt[this.brackets];
                                    }
                                    ++var10_11;
                                }
                                this.idx = var2_2;
                                break;
                            }
                            this.idx = this.bracketEnd[var9_10];
                            break;
                        }
                        if (this.bracketOpt[var9_10] != -1) ** GOTO lbl66
                        var6_6 = 42;
                        this.bracketOpt[var9_10] = 0;
                        this.idx = this.bracketEnd[var9_10];
                        ** GOTO lbl78
lbl66:
                        // 1 sources

                        v2 = var9_10;
                        v3 = this.bracketOpt[v2];
                        this.bracketOpt[v2] = v3 - 1;
                        if (v3 > 0) {
                            this.idx = this.bracketOpt[var9_10] > 0 ? var2_2 : this.bracketEnd[var9_10];
                            var6_6 = 63;
                        } else {
                            this.lenInstruction = var4_4;
                            this.node('N', 0);
                            this.idx = this.bracketEnd[var9_10];
                            break;
                        }
                    }
lbl78:
                    // 3 sources

                    case 42: 
                    case 63: {
                        if (var5_5) {
                            if (var6_6 == 63) {
                                this.nodeInsert('|', 0, var4_4);
                                this.setNextOfEnd(var4_4, this.node('|', 0));
                                var8_8 = this.node('N', 0);
                                this.setNextOfEnd(var4_4, var8_8);
                                this.setNextOfEnd(var4_4 + 3, var8_8);
                            }
                            if (var6_6 != 42) break;
                            this.nodeInsert('|', 0, var4_4);
                            this.setNextOfEnd(var4_4 + 3, this.node('|', 0));
                            this.setNextOfEnd(var4_4 + 3, this.node('G', 0));
                            this.setNextOfEnd(var4_4 + 3, var4_4);
                            this.setNextOfEnd(var4_4, this.node('|', 0));
                            this.setNextOfEnd(var4_4, this.node('N', 0));
                            break;
                        }
                        break block31;
                    }
                    case 43: {
                        var8_9 = this.node('|', 0);
                        this.setNextOfEnd(var4_4, var8_9);
                        this.setNextOfEnd(this.node('G', 0), var4_4);
                        this.setNextOfEnd(var8_9, this.node('|', 0));
                        this.setNextOfEnd(var4_4, this.node('N', 0));
                    }
                }
                break block31;
            }
            this.setNextOfEnd(var4_4, this.node('E', 0));
            switch (var6_6) {
                case 63: {
                    this.nodeInsert('/', 0, var4_4);
                    break;
                }
                case 42: {
                    this.nodeInsert('8', 0, var4_4);
                    break;
                }
                case 43: {
                    this.nodeInsert('=', 0, var4_4);
                }
            }
            this.setNextOfEnd(var4_4, this.lenInstruction);
        }
        return var4_4;
    }

    int branch(int[] nArray) throws RESyntaxException {
        int n = this.node('|', 0);
        int n2 = -1;
        int[] nArray2 = new int[1];
        boolean bl = true;
        while (this.idx < this.len && this.pattern.charAt(this.idx) != '|' && this.pattern.charAt(this.idx) != ')') {
            nArray2[0] = 0;
            int n3 = this.closure(nArray2);
            if (nArray2[0] == 0) {
                bl = false;
            }
            if (n2 != -1) {
                this.setNextOfEnd(n2, n3);
            }
            n2 = n3;
        }
        if (n2 == -1) {
            this.node('N', 0);
        }
        if (bl) {
            nArray[0] = nArray[0] | 1;
        }
        return n;
    }

    int expr(int[] nArray) throws RESyntaxException {
        int n;
        int n2 = -1;
        int n3 = -1;
        int n4 = this.parens;
        if ((nArray[0] & 2) == 0 && this.pattern.charAt(this.idx) == '(') {
            if (this.idx + 2 < this.len && this.pattern.charAt(this.idx + 1) == '?' && this.pattern.charAt(this.idx + 2) == ':') {
                n2 = 2;
                this.idx += 3;
                n3 = this.node('<', 0);
            } else {
                n2 = 1;
                ++this.idx;
                n3 = this.node('(', this.parens++);
            }
        }
        nArray[0] = nArray[0] & 0xFFFFFFFD;
        int n5 = this.branch(nArray);
        if (n3 == -1) {
            n3 = n5;
        } else {
            this.setNextOfEnd(n3, n5);
        }
        while (this.idx < this.len && this.pattern.charAt(this.idx) == '|') {
            ++this.idx;
            n5 = this.branch(nArray);
            this.setNextOfEnd(n3, n5);
        }
        if (n2 > 0) {
            if (this.idx < this.len && this.pattern.charAt(this.idx) == ')') {
                ++this.idx;
            } else {
                this.syntaxError("Missing close paren");
            }
            n = n2 == 1 ? this.node(')', n4) : this.node('>', 0);
        } else {
            n = this.node('E', 0);
        }
        this.setNextOfEnd(n3, n);
        int n6 = n3;
        char c = this.instruction[n6 + 2];
        while (c != '\u0000' && n6 < this.lenInstruction) {
            if (this.instruction[n6 + 0] == '|') {
                this.setNextOfEnd(n6 + 3, n);
            }
            c = this.instruction[n6 + 2];
            n6 += c;
        }
        return n3;
    }

    public REProgram compile(String string) throws RESyntaxException {
        this.pattern = string;
        this.len = string.length();
        this.idx = 0;
        this.lenInstruction = 0;
        this.parens = 1;
        this.brackets = 0;
        int[] nArray = new int[]{2};
        this.expr(nArray);
        if (this.idx != this.len) {
            if (string.charAt(this.idx) == ')') {
                this.syntaxError("Unmatched close paren");
            }
            this.syntaxError("Unexpected input remains");
        }
        char[] cArray = new char[this.lenInstruction];
        System.arraycopy(this.instruction, 0, cArray, 0, this.lenInstruction);
        return new REProgram(this.parens, cArray);
    }

    static {
        hashPOSIX.put("alnum", new Character('w'));
        hashPOSIX.put("alpha", new Character('a'));
        hashPOSIX.put("blank", new Character('b'));
        hashPOSIX.put("cntrl", new Character('c'));
        hashPOSIX.put("digit", new Character('d'));
        hashPOSIX.put("graph", new Character('g'));
        hashPOSIX.put("lower", new Character('l'));
        hashPOSIX.put("print", new Character('p'));
        hashPOSIX.put("punct", new Character('!'));
        hashPOSIX.put("space", new Character('s'));
        hashPOSIX.put("upper", new Character('u'));
        hashPOSIX.put("xdigit", new Character('x'));
        hashPOSIX.put("javastart", new Character('j'));
        hashPOSIX.put("javapart", new Character('k'));
    }

    class RERange {
        int size = 16;
        int[] minRange = new int[this.size];
        int[] maxRange = new int[this.size];
        int num = 0;

        RERange() {
        }

        /*
         * Unable to fully structure code
         */
        void delete(int var1_1) {
            if (this.num != 0 && var1_1 < this.num) ** GOTO lbl6
            return;
lbl-1000:
            // 1 sources

            {
                if (var1_1 - 1 < 0) continue;
                this.minRange[var1_1 - 1] = this.minRange[var1_1];
                this.maxRange[var1_1 - 1] = this.maxRange[var1_1];
lbl6:
                // 3 sources

                ** while (++var1_1 < this.num)
            }
lbl7:
            // 1 sources

            --this.num;
        }

        void merge(int n, int n2) {
            int n3 = 0;
            while (n3 < this.num) {
                if (n >= this.minRange[n3] && n2 <= this.maxRange[n3]) {
                    return;
                }
                if (n <= this.minRange[n3] && n2 >= this.maxRange[n3]) {
                    this.delete(n3);
                    this.merge(n, n2);
                    return;
                }
                if (n >= this.minRange[n3] && n <= this.maxRange[n3]) {
                    n = this.minRange[n3];
                    this.delete(n3);
                    this.merge(n, n2);
                    return;
                }
                if (n2 >= this.minRange[n3] && n2 <= this.maxRange[n3]) {
                    n2 = this.maxRange[n3];
                    this.delete(n3);
                    this.merge(n, n2);
                    return;
                }
                ++n3;
            }
            if (this.num >= this.size) {
                this.size *= 2;
                int[] nArray = new int[this.size];
                int[] nArray2 = new int[this.size];
                System.arraycopy(this.minRange, 0, nArray, 0, this.num);
                System.arraycopy(this.maxRange, 0, nArray2, 0, this.num);
                this.minRange = nArray;
                this.maxRange = nArray2;
            }
            this.minRange[this.num] = n;
            this.maxRange[this.num] = n2;
            ++this.num;
        }

        void remove(int n, int n2) {
            int n3 = 0;
            while (n3 < this.num) {
                if (this.minRange[n3] >= n && this.maxRange[n3] <= n2) {
                    this.delete(n3);
                    --n3;
                    return;
                }
                if (n >= this.minRange[n3] && n2 <= this.maxRange[n3]) {
                    int n4 = this.minRange[n3];
                    int n5 = this.maxRange[n3];
                    this.delete(n3);
                    if (n4 < n) {
                        this.merge(n4, n - 1);
                    }
                    if (n2 < n5) {
                        this.merge(n2 + 1, n5);
                    }
                    return;
                }
                if (this.minRange[n3] >= n && this.minRange[n3] <= n2) {
                    this.minRange[n3] = n2 + 1;
                    return;
                }
                if (this.maxRange[n3] >= n && this.maxRange[n3] <= n2) {
                    this.maxRange[n3] = n - 1;
                    return;
                }
                ++n3;
            }
        }

        void include(int n, int n2, boolean bl) {
            if (bl) {
                this.merge(n, n2);
            } else {
                this.remove(n, n2);
            }
        }

        void include(char c, boolean bl) {
            this.include(c, c, bl);
        }
    }
}

