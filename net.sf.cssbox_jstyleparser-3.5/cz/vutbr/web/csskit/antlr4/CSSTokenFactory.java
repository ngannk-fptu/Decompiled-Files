/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.CharStream
 *  org.antlr.v4.runtime.Lexer
 *  org.antlr.v4.runtime.TokenSource
 *  org.antlr.v4.runtime.misc.Pair
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.csskit.antlr4.CSSInputStream;
import cz.vutbr.web.csskit.antlr4.CSSLexerState;
import cz.vutbr.web.csskit.antlr4.CSSToken;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

public class CSSTokenFactory {
    private final Pair<TokenSource, CharStream> input;
    private final Lexer lexer;
    private final CSSLexerState ls;
    private final CSSToken.TypeMapper typeMapper;

    public CSSTokenFactory(Pair<TokenSource, CharStream> input, Lexer lexer, CSSLexerState ls, Class<? extends Lexer> lexerClass) {
        this.input = input;
        this.lexer = lexer;
        this.ls = ls;
        this.typeMapper = CSSToken.createDefaultTypeMapper(lexerClass);
    }

    public CSSToken make() {
        CSSToken t = new CSSToken(this.input, this.lexer._type, this.lexer._channel, this.lexer._tokenStartCharIndex, ((CharStream)this.input.b).index() - 1, this.typeMapper);
        t.setLine(this.lexer._tokenStartLine);
        t.setText(this.lexer._text);
        t.setCharPositionInLine(this.lexer._tokenStartCharPositionInLine);
        t.setBase(((CSSInputStream)((Object)this.input.b)).getBase());
        t.setLexerState(new CSSLexerState(this.ls));
        return t;
    }
}

