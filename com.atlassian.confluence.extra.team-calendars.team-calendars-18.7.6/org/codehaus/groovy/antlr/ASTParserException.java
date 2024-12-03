/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr;

import groovyjarjarantlr.collections.AST;
import org.codehaus.groovy.antlr.ASTRuntimeException;
import org.codehaus.groovy.antlr.SourceInfo;
import org.codehaus.groovy.syntax.ParserException;

public class ASTParserException
extends ParserException {
    private final AST ast;

    public ASTParserException(ASTRuntimeException e) {
        super(e.getMessage(), e, e.getLine(), e.getColumn(), ASTParserException.getLineLast(e), ASTParserException.getColumnLast(e));
        this.ast = e.getAst();
    }

    public ASTParserException(String message, ASTRuntimeException e) {
        super(message, e, e.getLine(), e.getColumn(), ASTParserException.getLineLast(e), ASTParserException.getColumnLast(e));
        this.ast = e.getAst();
    }

    public AST getAst() {
        return this.ast;
    }

    private static int getLineLast(ASTRuntimeException e) {
        AST ast = e.getAst();
        return ast instanceof SourceInfo ? ((SourceInfo)((Object)ast)).getLineLast() : ast.getLine();
    }

    private static int getColumnLast(ASTRuntimeException e) {
        AST ast = e.getAst();
        return ast instanceof SourceInfo ? ((SourceInfo)((Object)ast)).getColumnLast() : ast.getColumn() + 1;
    }
}

