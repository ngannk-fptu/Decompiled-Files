/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import java.util.Vector;
import org.apache.regexp.CharacterIterator;
import org.apache.regexp.RECompiler;
import org.apache.regexp.REProgram;
import org.apache.regexp.RESyntaxException;
import org.apache.regexp.StringCharacterIterator;

public class RE {
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
    static final char OP_CLOSE = ')';
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
    static final int maxParen = 16;
    static final int offsetOpcode = 0;
    static final int offsetOpdata = 1;
    static final int offsetNext = 2;
    static final int nodeSize = 3;
    static final String NEWLINE = System.getProperty("line.separator");
    REProgram program;
    CharacterIterator search;
    int idx;
    int matchFlags;
    int parenCount;
    int start0;
    int end0;
    int start1;
    int end1;
    int start2;
    int end2;
    int[] startn;
    int[] endn;
    int[] startBackref;
    int[] endBackref;
    public static final int REPLACE_ALL = 0;
    public static final int REPLACE_FIRSTONLY = 1;

    public RE() {
        this((REProgram)null, 0);
    }

    public RE(String string) throws RESyntaxException {
        this(string, 0);
    }

    public RE(String string, int n) throws RESyntaxException {
        this(new RECompiler().compile(string));
        this.setMatchFlags(n);
    }

    public RE(REProgram rEProgram) {
        this(rEProgram, 0);
    }

    public RE(REProgram rEProgram, int n) {
        this.setProgram(rEProgram);
        this.setMatchFlags(n);
    }

    private final void allocParens() {
        this.startn = new int[16];
        this.endn = new int[16];
        int n = 0;
        while (n < 16) {
            this.startn[n] = -1;
            this.endn[n] = -1;
            ++n;
        }
    }

    public int getMatchFlags() {
        return this.matchFlags;
    }

    public String getParen(int n) {
        int n2;
        if (n < this.parenCount && (n2 = this.getParenStart(n)) >= 0) {
            return this.search.substring(n2, this.getParenEnd(n));
        }
        return null;
    }

    public int getParenCount() {
        return this.parenCount;
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

    public REProgram getProgram() {
        return this.program;
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

    protected void internalError(String string) throws Error {
        throw new Error("RE internal error: " + string);
    }

    private boolean isNewline(int n) {
        if (n < NEWLINE.length() - 1) {
            return false;
        }
        if (this.search.charAt(n) == '\n') {
            return true;
        }
        int n2 = NEWLINE.length() - 1;
        while (n2 >= 0) {
            if (NEWLINE.charAt(n2) != this.search.charAt(n)) {
                return false;
            }
            --n2;
            --n;
        }
        return true;
    }

    public boolean match(String string) {
        return this.match(string, 0);
    }

    public boolean match(String string, int n) {
        return this.match(new StringCharacterIterator(string), n);
    }

    public boolean match(CharacterIterator characterIterator, int n) {
        if (this.program == null) {
            this.internalError("No RE program to run!");
        }
        this.search = characterIterator;
        if (this.program.prefix == null) {
            while (!characterIterator.isEnd(n - 1)) {
                if (this.matchAt(n)) {
                    return true;
                }
                ++n;
            }
            return false;
        }
        boolean bl = (this.matchFlags & 1) != 0;
        char[] cArray = this.program.prefix;
        while (!characterIterator.isEnd(n + cArray.length - 1)) {
            boolean bl2 = false;
            if (bl) {
                bl2 = Character.toLowerCase(characterIterator.charAt(n)) == Character.toLowerCase(cArray[0]);
            } else {
                boolean bl3 = bl2 = characterIterator.charAt(n) == cArray[0];
            }
            if (bl2) {
                int n2 = n++;
                int n3 = 1;
                while (n3 < cArray.length) {
                    if (bl) {
                        bl2 = Character.toLowerCase(characterIterator.charAt(n++)) == Character.toLowerCase(cArray[n3++]);
                    } else {
                        boolean bl4 = bl2 = characterIterator.charAt(n++) == cArray[n3++];
                    }
                    if (!bl2) break;
                }
                if (n3 == cArray.length && this.matchAt(n2)) {
                    return true;
                }
                n = n2;
            }
            ++n;
        }
        return false;
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
            this.startBackref = new int[16];
            this.endBackref = new int[16];
        }
        if ((n2 = this.matchNodes(0, 65536, n)) != -1) {
            this.setParenEnd(0, n2);
            return true;
        }
        this.parenCount = 0;
        return false;
    }

    /*
     * Exception decompiling
     */
    protected int matchNodes(int var1_1, int var2_2, int var3_3) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Extractable last case doesn't follow previous, and can't clone.
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.examineSwitchContiguity(SwitchReplacer.java:611)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.replaceRawSwitches(SwitchReplacer.java:94)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:517)
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

    public void setMatchFlags(int n) {
        this.matchFlags = n;
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
                    break;
                }
            }
        }
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
                    break;
                }
            }
        }
    }

    public void setProgram(REProgram rEProgram) {
        this.program = rEProgram;
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
                    break;
                }
            }
            ++n;
        }
        return stringBuffer.toString();
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
            stringBuffer.append(string.substring(n2, this.getParenStart(0)));
            stringBuffer.append(string2);
            int n4 = this.getParenEnd(0);
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
}

