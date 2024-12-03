/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr;

import groovyjarjarantlr.collections.AST;

public class ASTRuntimeException
extends RuntimeException {
    private final AST ast;

    public ASTRuntimeException(AST ast, String message) {
        super(message + ASTRuntimeException.description(ast));
        this.ast = ast;
    }

    public ASTRuntimeException(AST ast, String message, Throwable throwable) {
        super(message + ASTRuntimeException.description(ast), throwable);
        this.ast = null;
    }

    protected static String description(AST node) {
        return node != null ? " at line: " + node.getLine() + " column: " + node.getColumn() : "";
    }

    public AST getAst() {
        return this.ast;
    }

    public int getLine() {
        return this.ast != null ? this.ast.getLine() : -1;
    }

    public int getColumn() {
        return this.ast != null ? this.ast.getColumn() : -1;
    }
}

