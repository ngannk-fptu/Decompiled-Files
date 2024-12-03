/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ANTLRHashString;
import antlr.ANTLRStringBuffer;
import antlr.CharStreamException;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.MismatchedCharException;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.Utils;
import antlr.collections.impl.BitSet;
import java.util.Hashtable;

public abstract class CharScanner
implements TokenStream {
    static final char NO_CHAR = '\u0000';
    public static final char EOF_CHAR = '\uffff';
    protected ANTLRStringBuffer text = new ANTLRStringBuffer();
    protected boolean saveConsumedInput = true;
    protected Class tokenObjectClass;
    protected boolean caseSensitive = true;
    protected boolean caseSensitiveLiterals = true;
    protected Hashtable literals;
    protected int tabsize = 8;
    protected Token _returnToken = null;
    protected ANTLRHashString hashString = new ANTLRHashString(this);
    protected LexerSharedInputState inputState;
    protected boolean commitToPath = false;
    protected int traceDepth = 0;

    public CharScanner() {
        this.setTokenObjectClass("antlr.CommonToken");
    }

    public CharScanner(InputBuffer inputBuffer) {
        this();
        this.inputState = new LexerSharedInputState(inputBuffer);
    }

    public CharScanner(LexerSharedInputState lexerSharedInputState) {
        this();
        this.inputState = lexerSharedInputState;
    }

    public void append(char c) {
        if (this.saveConsumedInput) {
            this.text.append(c);
        }
    }

    public void append(String string) {
        if (this.saveConsumedInput) {
            this.text.append(string);
        }
    }

    public void commit() {
        this.inputState.input.commit();
    }

    public void consume() throws CharStreamException {
        if (this.inputState.guessing == 0) {
            char c = this.LA(1);
            if (this.caseSensitive) {
                this.append(c);
            } else {
                this.append(this.inputState.input.LA(1));
            }
            if (c == '\t') {
                this.tab();
            } else {
                ++this.inputState.column;
            }
        }
        this.inputState.input.consume();
    }

    public void consumeUntil(int n) throws CharStreamException {
        while (this.LA(1) != '\uffff' && this.LA(1) != n) {
            this.consume();
        }
    }

    public void consumeUntil(BitSet bitSet) throws CharStreamException {
        while (this.LA(1) != '\uffff' && !bitSet.member(this.LA(1))) {
            this.consume();
        }
    }

    public boolean getCaseSensitive() {
        return this.caseSensitive;
    }

    public final boolean getCaseSensitiveLiterals() {
        return this.caseSensitiveLiterals;
    }

    public int getColumn() {
        return this.inputState.column;
    }

    public void setColumn(int n) {
        this.inputState.column = n;
    }

    public boolean getCommitToPath() {
        return this.commitToPath;
    }

    public String getFilename() {
        return this.inputState.filename;
    }

    public InputBuffer getInputBuffer() {
        return this.inputState.input;
    }

    public LexerSharedInputState getInputState() {
        return this.inputState;
    }

    public void setInputState(LexerSharedInputState lexerSharedInputState) {
        this.inputState = lexerSharedInputState;
    }

    public int getLine() {
        return this.inputState.line;
    }

    public String getText() {
        return this.text.toString();
    }

    public Token getTokenObject() {
        return this._returnToken;
    }

    public char LA(int n) throws CharStreamException {
        if (this.caseSensitive) {
            return this.inputState.input.LA(n);
        }
        return this.toLower(this.inputState.input.LA(n));
    }

    protected Token makeToken(int n) {
        try {
            Token token = (Token)this.tokenObjectClass.newInstance();
            token.setType(n);
            token.setColumn(this.inputState.tokenStartColumn);
            token.setLine(this.inputState.tokenStartLine);
            return token;
        }
        catch (InstantiationException instantiationException) {
            this.panic("can't instantiate token: " + this.tokenObjectClass);
        }
        catch (IllegalAccessException illegalAccessException) {
            this.panic("Token class is not accessible" + this.tokenObjectClass);
        }
        return Token.badToken;
    }

    public int mark() {
        return this.inputState.input.mark();
    }

    public void match(char c) throws MismatchedCharException, CharStreamException {
        if (this.LA(1) != c) {
            throw new MismatchedCharException(this.LA(1), c, false, this);
        }
        this.consume();
    }

    public void match(BitSet bitSet) throws MismatchedCharException, CharStreamException {
        if (!bitSet.member(this.LA(1))) {
            throw new MismatchedCharException(this.LA(1), bitSet, false, this);
        }
        this.consume();
    }

    public void match(String string) throws MismatchedCharException, CharStreamException {
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            if (this.LA(1) != string.charAt(i)) {
                throw new MismatchedCharException(this.LA(1), string.charAt(i), false, this);
            }
            this.consume();
        }
    }

    public void matchNot(char c) throws MismatchedCharException, CharStreamException {
        if (this.LA(1) == c) {
            throw new MismatchedCharException(this.LA(1), c, true, this);
        }
        this.consume();
    }

    public void matchRange(char c, char c2) throws MismatchedCharException, CharStreamException {
        if (this.LA(1) < c || this.LA(1) > c2) {
            throw new MismatchedCharException(this.LA(1), c, c2, false, this);
        }
        this.consume();
    }

    public void newline() {
        ++this.inputState.line;
        this.inputState.column = 1;
    }

    public void tab() {
        int n = this.getColumn();
        int n2 = ((n - 1) / this.tabsize + 1) * this.tabsize + 1;
        this.setColumn(n2);
    }

    public void setTabSize(int n) {
        this.tabsize = n;
    }

    public int getTabSize() {
        return this.tabsize;
    }

    public void panic() {
        System.err.println("CharScanner: panic");
        Utils.error("");
    }

    public void panic(String string) {
        System.err.println("CharScanner; panic: " + string);
        Utils.error(string);
    }

    public void reportError(RecognitionException recognitionException) {
        System.err.println(recognitionException);
    }

    public void reportError(String string) {
        if (this.getFilename() == null) {
            System.err.println("error: " + string);
        } else {
            System.err.println(this.getFilename() + ": error: " + string);
        }
    }

    public void reportWarning(String string) {
        if (this.getFilename() == null) {
            System.err.println("warning: " + string);
        } else {
            System.err.println(this.getFilename() + ": warning: " + string);
        }
    }

    public void resetText() {
        this.text.setLength(0);
        this.inputState.tokenStartColumn = this.inputState.column;
        this.inputState.tokenStartLine = this.inputState.line;
    }

    public void rewind(int n) {
        this.inputState.input.rewind(n);
    }

    public void setCaseSensitive(boolean bl) {
        this.caseSensitive = bl;
    }

    public void setCommitToPath(boolean bl) {
        this.commitToPath = bl;
    }

    public void setFilename(String string) {
        this.inputState.filename = string;
    }

    public void setLine(int n) {
        this.inputState.line = n;
    }

    public void setText(String string) {
        this.resetText();
        this.text.append(string);
    }

    public void setTokenObjectClass(String string) {
        try {
            this.tokenObjectClass = Utils.loadClass(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            this.panic("ClassNotFoundException: " + string);
        }
    }

    public int testLiteralsTable(int n) {
        this.hashString.setBuffer(this.text.getBuffer(), this.text.length());
        Integer n2 = (Integer)this.literals.get(this.hashString);
        if (n2 != null) {
            n = n2;
        }
        return n;
    }

    public int testLiteralsTable(String string, int n) {
        ANTLRHashString aNTLRHashString = new ANTLRHashString(string, this);
        Integer n2 = (Integer)this.literals.get(aNTLRHashString);
        if (n2 != null) {
            n = n2;
        }
        return n;
    }

    public char toLower(char c) {
        return Character.toLowerCase(c);
    }

    public void traceIndent() {
        for (int i = 0; i < this.traceDepth; ++i) {
            System.out.print(" ");
        }
    }

    public void traceIn(String string) throws CharStreamException {
        ++this.traceDepth;
        this.traceIndent();
        System.out.println("> lexer " + string + "; c==" + this.LA(1));
    }

    public void traceOut(String string) throws CharStreamException {
        this.traceIndent();
        System.out.println("< lexer " + string + "; c==" + this.LA(1));
        --this.traceDepth;
    }

    public void uponEOF() throws TokenStreamException, CharStreamException {
    }
}

