/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.syntax;

import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.syntax.SyntaxException;

public class RuntimeParserException
extends GroovyRuntimeException {
    public RuntimeParserException(String message, ASTNode node) {
        super(message + "\n", node);
    }

    public void throwParserException() throws SyntaxException {
        ASTNode node = this.getNode();
        throw new SyntaxException(this.getMessage(), node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber());
    }
}

