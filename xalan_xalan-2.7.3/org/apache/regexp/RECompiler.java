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
    static final char ESC_MASK = '\ufff0';
    static final char ESC_BACKREF = '\uffff';
    static final char ESC_COMPLEX = '\ufffe';
    static final char ESC_CLASS = '\ufffd';
    static final int maxBrackets = 10;
    static int brackets = 0;
    static int[] bracketStart = null;
    static int[] bracketEnd = null;
    static int[] bracketMin = null;
    static int[] bracketOpt = null;
    static final int bracketUnbounded = -1;
    static final int bracketFinished = -2;
    static Hashtable hashPOSIX = new Hashtable();

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

    void allocBrackets() {
        if (bracketStart == null) {
            bracketStart = new int[10];
            bracketEnd = new int[10];
            bracketMin = new int[10];
            bracketOpt = new int[10];
            int n = 0;
            while (n < 10) {
                RECompiler.bracketOpt[n] = -1;
                RECompiler.bracketMin[n] = -1;
                RECompiler.bracketEnd[n] = -1;
                RECompiler.bracketStart[n] = -1;
                ++n;
            }
        }
    }

    /*
     * Exception decompiling
     */
    int atom() throws RESyntaxException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[SWITCH], 2[CASE]], but top level block is 6[CASE]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
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
            RECompiler.bracketMin[RECompiler.brackets] = Integer.parseInt(stringBuffer.toString());
        }
        catch (NumberFormatException numberFormatException) {
            this.syntaxError("Expected valid number");
        }
        if (this.idx >= this.len) {
            this.syntaxError("Expected comma or right bracket");
        }
        if (this.pattern.charAt(this.idx) == '}') {
            ++this.idx;
            RECompiler.bracketOpt[RECompiler.brackets] = 0;
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
            RECompiler.bracketOpt[RECompiler.brackets] = -1;
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
            RECompiler.bracketOpt[RECompiler.brackets] = Integer.parseInt(stringBuffer.toString()) - bracketMin[brackets];
        }
        catch (NumberFormatException numberFormatException) {
            this.syntaxError("Expected valid number");
        }
        if (bracketOpt[brackets] <= 0) {
            this.syntaxError("Bad range");
        }
        if (this.idx >= this.len || this.pattern.charAt(this.idx++) != '}') {
            this.syntaxError("Missing close brace");
        }
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

    int characterClass() throws RESyntaxException {
        int n;
        int n2;
        int n3;
        if (this.pattern.charAt(this.idx) != '[') {
            this.internalError();
        }
        if (this.idx + 1 >= this.len || this.pattern.charAt(++this.idx) == ']') {
            this.syntaxError("Empty or unterminated class");
        }
        if (this.idx < this.len && this.pattern.charAt(this.idx) == ':') {
            ++this.idx;
            n3 = this.idx;
            while (this.idx < this.len && this.pattern.charAt(this.idx) >= 'a' && this.pattern.charAt(this.idx) <= 'z') {
                ++this.idx;
            }
            if (this.idx + 1 < this.len && this.pattern.charAt(this.idx) == ':' && this.pattern.charAt(this.idx + 1) == ']') {
                String string = this.pattern.substring(n3, this.idx);
                Character c = (Character)hashPOSIX.get(string);
                if (c != null) {
                    this.idx += 2;
                    return this.node('P', c.charValue());
                }
                this.syntaxError("Invalid POSIX character class '" + string + "'");
            }
            this.syntaxError("Invalid POSIX character class syntax");
        }
        n3 = this.node('[', 0);
        int n4 = n2 = 65535;
        int n5 = 0;
        boolean bl = true;
        boolean bl2 = false;
        int n6 = this.idx;
        int n7 = 0;
        RERange rERange = new RERange();
        block16: while (this.idx < this.len && this.pattern.charAt(this.idx) != ']') {
            switch (this.pattern.charAt(this.idx)) {
                case '^': {
                    bl ^= true;
                    if (this.idx == n6) {
                        rERange.include(0, 65535, true);
                    }
                    ++this.idx;
                    continue block16;
                }
                case '\\': {
                    n = this.escape();
                    switch (n) {
                        case 65534: 
                        case 65535: {
                            this.syntaxError("Bad character class");
                        }
                        case 65533: {
                            if (bl2) {
                                this.syntaxError("Bad character class");
                            }
                            switch (this.pattern.charAt(this.idx - 1)) {
                                case 'D': 
                                case 'S': 
                                case 'W': {
                                    this.syntaxError("Bad character class");
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
                                    break;
                                }
                            }
                            n4 = n2;
                            continue block16;
                        }
                        default: {
                            n5 = n;
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
                    int n8 = n7 = n4 == n2 ? 0 : n4;
                    if (this.idx + 1 >= this.len || this.pattern.charAt(++this.idx) != ']') continue block16;
                    n5 = 65535;
                    break;
                }
                default: {
                    n5 = this.pattern.charAt(this.idx++);
                }
            }
            if (bl2) {
                int n9 = n5;
                if (n7 >= n9) {
                    this.syntaxError("Bad character class");
                }
                rERange.include(n7, n9, bl);
                n4 = n2;
                bl2 = false;
                continue;
            }
            if (this.idx + 1 >= this.len || this.pattern.charAt(this.idx + 1) != '-') {
                rERange.include((char)n5, bl);
            }
            n4 = n5;
        }
        if (this.idx == this.len) {
            this.syntaxError("Unterminated character class");
        }
        ++this.idx;
        this.instruction[n3 + 1] = (char)rERange.num;
        n = 0;
        while (n < rERange.num) {
            this.emit((char)rERange.minRange[n]);
            this.emit((char)rERange.maxRange[n]);
            ++n;
        }
        return n3;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    int closure(int[] nArray) throws RESyntaxException {
        int n;
        int n2 = this.idx;
        int[] nArray2 = new int[1];
        int n3 = this.terminal(nArray2);
        nArray[0] = nArray[0] | nArray2[0];
        if (this.idx >= this.len) {
            return n3;
        }
        boolean bl = true;
        int n4 = this.pattern.charAt(this.idx);
        switch (n4) {
            case 42: 
            case 63: {
                nArray[0] = nArray[0] | 1;
            }
            case 43: {
                ++this.idx;
            }
            case 123: {
                n = this.instruction[n3];
                if (n == 94 || n == 36) {
                    this.syntaxError("Bad closure operand");
                }
                if ((nArray2[0] & 1) == 0) break;
                this.syntaxError("Closure operand can't be nullable");
                break;
            }
        }
        if (this.idx < this.len && this.pattern.charAt(this.idx) == '?') {
            ++this.idx;
            bl = false;
        }
        if (bl) {
            switch (n4) {
                case 123: {
                    n = 0;
                    this.allocBrackets();
                    int n5 = 0;
                    while (n5 < brackets) {
                        if (bracketStart[n5] == this.idx) {
                            n = 1;
                            break;
                        }
                        ++n5;
                    }
                    if (n == 0) {
                        if (brackets >= 10) {
                            this.syntaxError("Too many bracketed closures (limit is 10)");
                        }
                        RECompiler.bracketStart[RECompiler.brackets] = this.idx;
                        this.bracket();
                        RECompiler.bracketEnd[RECompiler.brackets] = this.idx;
                        n5 = brackets++;
                    }
                    int n6 = n5;
                    bracketMin[n6] = bracketMin[n6] - 1;
                    if (bracketMin[n6] > 0) {
                        this.idx = n2;
                        return n3;
                    }
                    if (bracketOpt[n5] == -2) {
                        n4 = 42;
                        RECompiler.bracketOpt[n5] = 0;
                        this.idx = bracketEnd[n5];
                    } else {
                        if (bracketOpt[n5] == -1) {
                            this.idx = n2;
                            RECompiler.bracketOpt[n5] = -2;
                            return n3;
                        }
                        int n7 = n5;
                        int n8 = bracketOpt[n7];
                        bracketOpt[n7] = n8 - 1;
                        if (n8 <= 0) {
                            this.idx = bracketEnd[n5];
                            return n3;
                        }
                        this.idx = n2;
                        n4 = 63;
                    }
                }
                case 42: 
                case 63: {
                    if (!bl) return n3;
                    if (n4 == 63) {
                        this.nodeInsert('|', 0, n3);
                        this.setNextOfEnd(n3, this.node('|', 0));
                        n = this.node('N', 0);
                        this.setNextOfEnd(n3, n);
                        this.setNextOfEnd(n3 + 3, n);
                    }
                    if (n4 != 42) return n3;
                    this.nodeInsert('|', 0, n3);
                    this.setNextOfEnd(n3 + 3, this.node('|', 0));
                    this.setNextOfEnd(n3 + 3, this.node('G', 0));
                    this.setNextOfEnd(n3 + 3, n3);
                    this.setNextOfEnd(n3, this.node('|', 0));
                    this.setNextOfEnd(n3, this.node('N', 0));
                    return n3;
                }
                case 43: {
                    n = this.node('|', 0);
                    this.setNextOfEnd(n3, n);
                    this.setNextOfEnd(this.node('G', 0), n3);
                    this.setNextOfEnd(n, this.node('|', 0));
                    this.setNextOfEnd(n3, this.node('N', 0));
                    return n3;
                }
                default: {
                    return n3;
                }
            }
        }
        this.setNextOfEnd(n3, this.node('E', 0));
        switch (n4) {
            case 63: {
                this.nodeInsert('/', 0, n3);
                break;
            }
            case 42: {
                this.nodeInsert('8', 0, n3);
                break;
            }
            case 43: {
                this.nodeInsert('=', 0, n3);
                break;
            }
        }
        this.setNextOfEnd(n3, this.lenInstruction);
        return n3;
    }

    public REProgram compile(String string) throws RESyntaxException {
        this.pattern = string;
        this.len = string.length();
        this.idx = 0;
        this.lenInstruction = 0;
        this.parens = 1;
        brackets = 0;
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
        return new REProgram(cArray);
    }

    void emit(char c) {
        this.ensure(1);
        this.instruction[this.lenInstruction++] = c;
    }

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

    char escape() throws RESyntaxException {
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
                return '\ufffe';
            }
            case 'D': 
            case 'S': 
            case 'W': 
            case 'd': 
            case 's': 
            case 'w': {
                return '\ufffd';
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
                return (char)n2;
            }
            case 't': {
                return '\t';
            }
            case 'n': {
                return '\n';
            }
            case 'r': {
                return '\r';
            }
            case 'f': {
                return '\f';
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
                    return (char)n;
                }
                return '\uffff';
            }
        }
        return c;
    }

    int expr(int[] nArray) throws RESyntaxException {
        int n;
        boolean bl = false;
        int n2 = -1;
        int n3 = this.parens;
        if ((nArray[0] & 2) == 0 && this.pattern.charAt(this.idx) == '(') {
            ++this.idx;
            bl = true;
            n2 = this.node('(', this.parens++);
        }
        nArray[0] = nArray[0] & 0xFFFFFFFD;
        int n4 = this.branch(nArray);
        if (n2 == -1) {
            n2 = n4;
        } else {
            this.setNextOfEnd(n2, n4);
        }
        while (this.idx < this.len && this.pattern.charAt(this.idx) == '|') {
            ++this.idx;
            n4 = this.branch(nArray);
            this.setNextOfEnd(n2, n4);
        }
        if (bl) {
            if (this.idx < this.len && this.pattern.charAt(this.idx) == ')') {
                ++this.idx;
            } else {
                this.syntaxError("Missing close paren");
            }
            n = this.node(')', n3);
        } else {
            n = this.node('E', 0);
        }
        this.setNextOfEnd(n2, n);
        int n5 = -1;
        int n6 = n2;
        while (n5 != 0) {
            if (this.instruction[n6] == '|') {
                this.setNextOfEnd(n6 + 3, n);
            }
            n5 = this.instruction[n6 + 2];
            n6 += n5;
        }
        return n2;
    }

    void internalError() throws Error {
        throw new Error("Internal error!");
    }

    int node(char c, int n) {
        this.ensure(3);
        this.instruction[this.lenInstruction] = c;
        this.instruction[this.lenInstruction + 1] = (char)n;
        this.instruction[this.lenInstruction + 2] = '\u0000';
        this.lenInstruction += 3;
        return this.lenInstruction - 3;
    }

    void nodeInsert(char c, int n, int n2) {
        this.ensure(3);
        System.arraycopy(this.instruction, n2, this.instruction, n2 + 3, this.lenInstruction - n2);
        this.instruction[n2] = c;
        this.instruction[n2 + 1] = (char)n;
        this.instruction[n2 + 2] = '\u0000';
        this.lenInstruction += 3;
    }

    void setNextOfEnd(int n, int n2) {
        char c;
        while ((c = this.instruction[n + 2]) != '\u0000') {
            n += c;
        }
        this.instruction[n + 2] = (char)(n2 - n);
    }

    void syntaxError(String string) throws RESyntaxException {
        throw new RESyntaxException(string);
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
                    case '\ufffd': 
                    case '\ufffe': {
                        nArray[0] = nArray[0] & 0xFFFFFFFE;
                        return this.node('\\', this.pattern.charAt(this.idx - 1));
                    }
                    case '\uffff': {
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
                break;
            }
        }
        nArray[0] = nArray[0] & 0xFFFFFFFE;
        return this.atom();
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

                ** while (var1_1++ < this.num)
            }
lbl7:
            // 1 sources

            --this.num;
        }

        void include(char c, boolean bl) {
            this.include(c, c, bl);
        }

        void include(int n, int n2, boolean bl) {
            if (bl) {
                this.merge(n, n2);
            } else {
                this.remove(n, n2);
            }
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
                    this.delete(n3);
                    n = this.minRange[n3];
                    this.merge(n, n2);
                    return;
                }
                if (n2 >= this.minRange[n3] && n2 <= this.maxRange[n3]) {
                    this.delete(n3);
                    n2 = this.maxRange[n3];
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
                    if (n4 < n - 1) {
                        this.merge(n4, n - 1);
                    }
                    if (n2 + 1 < n5) {
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
    }
}

