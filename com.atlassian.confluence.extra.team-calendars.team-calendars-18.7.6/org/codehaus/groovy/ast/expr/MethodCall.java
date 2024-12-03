/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.Expression;

public interface MethodCall {
    public ASTNode getReceiver();

    public String getMethodAsString();

    public Expression getArguments();

    public String getText();
}

