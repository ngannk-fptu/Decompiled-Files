/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.CharStream
 *  org.antlr.v4.runtime.Lexer
 *  org.antlr.v4.runtime.LexerNoViableAltException
 *  org.antlr.v4.runtime.Token
 *  org.antlr.v4.runtime.atn.LexerATNSimulator
 *  org.antlr.v4.runtime.misc.IntervalSet
 *  org.slf4j.Logger
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.csskit.antlr4.CSSLexerState;
import cz.vutbr.web.csskit.antlr4.CSSToken;
import java.util.Stack;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.slf4j.Logger;

public class CSSTokenRecovery {
    private final Lexer lexer;
    private final CharStream input;
    private final CSSLexerState ls;
    private final Logger log;
    private final Stack<Integer> expectedToken;
    private boolean eof;
    public static final int APOS = 1;
    public static final int QUOT = 2;
    public static final int RPAREN = 3;
    public static final int RCURLY = 4;
    public static final int IMPORT = 5;
    public static final int CHARSET = 6;
    public static final int STRING = 7;
    public static final int INVALID_STRING = 8;
    public static final int RBRACKET = 9;
    private final CSSToken.TypeMapper typeMapper;
    private final CSSToken.TypeMapper lexerTypeMapper;

    public CSSTokenRecovery(Lexer lexer, CharStream input, CSSLexerState ls, Logger log) {
        this.lexer = lexer;
        this.input = input;
        this.ls = ls;
        this.log = log;
        this.expectedToken = new Stack();
        this.eof = false;
        this.lexerTypeMapper = CSSToken.createDefaultTypeMapper(lexer.getClass());
        this.typeMapper = new CSSToken.TypeMapper(CSSTokenRecovery.class, lexer.getClass(), "APOS", "QUOT", "RPAREN", "RCURLY", "IMPORT", "CHARSET", "STRING", "INVALID_STRING", "RBRACKET");
    }

    public boolean isAtEof() {
        return this.eof;
    }

    public void expecting(int token) {
        this.expectedToken.push(token);
    }

    public void end() {
        this.expectedToken.pop();
    }

    public boolean recover() {
        int t;
        if (this.expectedToken.isEmpty()) {
            return false;
        }
        try {
            t = this.typeMapper.inverse().get(this.expectedToken.pop());
        }
        catch (NullPointerException e) {
            return false;
        }
        switch (t) {
            case 5: 
            case 6: {
                IntervalSet charsetFollow = new IntervalSet(new int[]{125, 59});
                this.consumeUntilBalanced(charsetFollow);
                break;
            }
            case 7: {
                if (this.consumeAnyButEOF()) {
                    this.ls.quotOpen = false;
                    this.ls.aposOpen = false;
                    this.lexer.setToken((Token)new CSSToken(this.typeMapper.get(8), this.ls, this.lexerTypeMapper));
                    ((CSSToken)this.lexer.getToken()).setText("INVALID_STRING");
                    break;
                }
                char enclosing = this.ls.quotOpen ? (char)'\"' : '\'';
                this.ls.quotOpen = false;
                this.ls.aposOpen = false;
                this.lexer.setToken((Token)new CSSToken(this.typeMapper.get(7), this.ls, this.lexer._tokenStartCharIndex, this.input.index() - 1, this.lexerTypeMapper));
                ((CSSToken)this.lexer.getToken()).setText(this.input.toString().substring(this.lexer._tokenStartCharIndex, this.input.index() - 1) + enclosing);
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Token nextToken() {
        if (this.lexer._input == null) {
            throw new IllegalStateException("nextToken requires a non-null input stream.");
        }
        int tokenStartMarker = this.lexer._input.mark();
        try {
            Token ttype1;
            block7: while (!this.lexer._hitEOF) {
                Token ttype12;
                this.lexer._token = null;
                this.lexer._channel = 0;
                this.lexer._tokenStartCharIndex = this.lexer._input.index();
                this.lexer._tokenStartCharPositionInLine = ((LexerATNSimulator)this.lexer.getInterpreter()).getCharPositionInLine();
                this.lexer._tokenStartLine = ((LexerATNSimulator)this.lexer.getInterpreter()).getLine();
                this.lexer._text = null;
                do {
                    int ttype;
                    this.lexer._type = 0;
                    try {
                        ttype = ((LexerATNSimulator)this.lexer.getInterpreter()).match(this.lexer._input, this.lexer._mode);
                    }
                    catch (LexerNoViableAltException var7) {
                        this.lexer.notifyListeners(var7);
                        this.lexer.recover(var7);
                        ttype = -3;
                    }
                    if (this.lexer._input.LA(1) == -1) {
                        this.lexer._hitEOF = true;
                    }
                    if (this.lexer._type == 0) {
                        this.lexer._type = ttype;
                    }
                    if (this.lexer._type == -3) continue block7;
                } while (this.lexer._type == -2);
                if (this.lexer._token == null) {
                    this.lexer.emit();
                }
                Token token = ttype12 = this.lexer._token;
                return token;
            }
            this.eof = true;
            if (!this.ls.isBalanced()) {
                CSSToken cSSToken = this.generateEOFRecover();
                return cSSToken;
            }
            this.log.trace("lexer state is balanced - emitEOF");
            this.lexer.emitEOF();
            Token token = ttype1 = this.lexer._token;
            return token;
        }
        finally {
            this.lexer._input.release(tokenStartMarker);
        }
    }

    public CSSToken generateEOFRecover() {
        CSSToken t = null;
        if (this.ls.aposOpen) {
            this.ls.aposOpen = false;
            t = new CSSToken(this.typeMapper.get(1), this.ls, this.lexerTypeMapper);
            t.setText("'");
        } else if (this.ls.quotOpen) {
            this.ls.quotOpen = false;
            t = new CSSToken(this.typeMapper.get(2), this.ls, this.lexerTypeMapper);
            t.setText("\"");
        } else if (this.ls.parenNest != 0) {
            this.ls.parenNest = (short)(this.ls.parenNest - 1);
            t = new CSSToken(this.typeMapper.get(3), this.ls, this.lexerTypeMapper);
            t.setText(")");
        } else if (this.ls.curlyNest != 0) {
            this.ls.curlyNest = (short)(this.ls.curlyNest - 1);
            t = new CSSToken(this.typeMapper.get(4), this.ls, this.lexerTypeMapper);
            t.setText("}");
        } else if (this.ls.sqNest != 0) {
            this.ls.sqNest = (short)(this.ls.sqNest - 1);
            t = new CSSToken(this.typeMapper.get(9), this.ls, this.lexerTypeMapper);
            t.setText("]");
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Recovering from EOF by {}", (Object)t.getText());
        }
        return t;
    }

    private void consumeUntilBalanced(IntervalSet follow) {
        int c;
        if (this.log.isDebugEnabled()) {
            this.log.debug("Lexer entered consumeUntilBalanced with {} and follow {}", (Object)this.ls, (Object)follow);
        }
        do {
            if ((c = this.input.LA(1)) == 39 && !this.ls.quotOpen) {
                this.ls.aposOpen = !this.ls.aposOpen;
            } else if (c == 34 && !this.ls.aposOpen) {
                this.ls.quotOpen = !this.ls.quotOpen;
            } else if (c == 40) {
                this.ls.parenNest = (short)(this.ls.parenNest + 1);
            } else if (c == 41 && this.ls.parenNest > 0) {
                this.ls.parenNest = (short)(this.ls.parenNest - 1);
            } else if (c == 123) {
                this.ls.curlyNest = (short)(this.ls.curlyNest + 1);
            } else if (c == 125 && this.ls.curlyNest > 0) {
                this.ls.curlyNest = (short)(this.ls.curlyNest - 1);
            } else if (c == 10) {
                if (this.ls.quotOpen) {
                    this.ls.quotOpen = false;
                } else if (this.ls.aposOpen) {
                    this.ls.aposOpen = false;
                }
            } else if (c == -1) {
                this.log.info("Unexpected EOF during consumeUntilBalanced, EOF not consumed");
                return;
            }
            this.input.consume();
            if (!this.log.isTraceEnabled()) continue;
            this.log.trace("Lexer consumes '{}'({}) until balanced ({}).", new Object[]{Character.toString((char)c), Integer.toString(c), this.ls});
        } while (!this.ls.isBalanced() || !follow.contains(c));
    }

    private boolean consumeAnyButEOF() {
        int c = this.input.LA(1);
        if (c == -1) {
            return false;
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace("Lexer consumes '{}' while consumeButEOF", (Object)Character.toString((char)c));
        }
        this.input.consume();
        return true;
    }
}

